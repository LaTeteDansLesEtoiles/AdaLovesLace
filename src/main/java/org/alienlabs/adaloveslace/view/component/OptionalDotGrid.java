package org.alienlabs.adaloveslace.view.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.MouseMode;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNullElseGet;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.ZOOM_SPINNER_NEGATIVE_ZOOM_DIVISION_FACTOR;
import static org.alienlabs.adaloveslace.view.window.GeometryWindow.ZOOM_SPINNER_POSITIVE_ZOOM_MULTIPLY_FACTOR;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class OptionalDotGrid extends Pane {

  public static final Color GRID_COLOR  = Color.gray(0d, 0.2d);
  private static final double RADIUS    = 0.5d; // The dots are ellipses, this is their radius
  double GRID_WIDTH                     = 1240d;
  double GRID_HEIGHT                    = 600d;
  public static final double TOP_MARGIN = 10d;

  private static final double SPACING_X = 25d; // The X space between the dots
  private static final double SPACING_Y = 10d; // The Y space between the dots

  private final SimpleBooleanProperty   showHideGridProperty;
  private SimpleObjectProperty<Pattern> currentPatternProperty;
  private SimpleObjectProperty<Diagram> diagramProperty;
  private boolean showHideGrid          = true;
  private boolean gridNeedsToBeRedrawn;

  private double desiredRadius;

  private Diagram diagram;

  private final Set<Shape> grid = new HashSet<>();

  private static final Logger logger = LoggerFactory.getLogger(OptionalDotGrid.class);
  private final Group root;
  private Node firstNonGridNode;

  private final List<Knot> allSelectedKnots = new ArrayList<>();

  private final List<Knot> allHoveredKnots = new ArrayList<>();

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public OptionalDotGrid(Diagram diagram, Group root) {
    this.root = root;
    this.diagram = requireNonNullElseGet(diagram, Diagram::new);

    this.desiredRadius = RADIUS;

    if (!this.diagram.getPatterns().isEmpty()) {
      this.diagram.setCurrentPattern(this.diagram.getPatterns().get(0));
      currentPatternProperty = new SimpleObjectProperty<>(this.diagram.getCurrentPattern());
    } else {
      currentPatternProperty = new SimpleObjectProperty<>();
    }

    diagramProperty = new SimpleObjectProperty<>(this.diagram);
    diagramProperty.addListener(observable -> this.setDiagram(diagramProperty.getValue()));
    currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));

    showHideGridProperty = new SimpleBooleanProperty(this.showHideGrid);
    showHideGridProperty.addListener(observable -> {
      this.showHideGrid = showHideGridProperty.getValue();
      setNeedsLayout(true);
    });

    this.gridNeedsToBeRedrawn = true;
  }

  public OptionalDotGrid(double width, double height, double desiredRadius, Diagram diagram, Group root) {
    this(diagram, root);
    GRID_WIDTH = width;
    GRID_HEIGHT = height;
    this.desiredRadius = desiredRadius;
  }

  @Override
  public void layoutChildren() {
    drawDiagram();

    if (this.gridNeedsToBeRedrawn) {
      drawGrid();
    }
  }

  private void drawDiagram() {
    // We whall not display the undone knots => delete them from canvas, then draw the grid again
    clearAllGuideLines();
    clearHovered();
    clearSelections();
    deleteKnotsFromCanvas();

    // If there are knots on the diagram, we must display them at each window refresh
   for (Knot knot : this.diagram.getKnots().subList(0, this.diagram.getCurrentKnotIndex())) {
     if (knot.isVisible()) {
       drawKnotWithRotationAndZoom(knot);
     }
    }
  }

  // We whall not display the undone knots => delete them from canvas, then draw the grid again
  private void deleteKnotsFromCanvas() {
    ArrayList<Node> listOfAllNodes  = new ArrayList<>(root.getChildren());
    ArrayList<Node> listToRemove    = new ArrayList<>(root.getChildren());

    for (Node node : listOfAllNodes) {
      if (node instanceof ImageView) {
        listToRemove.remove(node);
      }
    }

    root.getChildren().clear();
    root.getChildren().addAll(listToRemove);
  }

  public Knot drawSelectedKnot(Knot knot) {
    // If selected and not hovered: blue
    if (allSelectedKnots.contains(knot) && !allHoveredKnots.contains(knot)) {
      Rectangle rec = new Rectangle(knot.getX(), knot.getY(), knot.getPattern().getWidth(), knot.getPattern().getHeight());
      rec.setStroke(Color.BLUE);
      rec.setStrokeWidth(2d);
      rec.setFill(Color.TRANSPARENT);
      rec.setScaleX(computeZoomFactor(knot));
      rec.setScaleY(computeZoomFactor(knot));
      rec.setRotate(knot.getRotationAngle());

      if (knot.getSelection() != null) {
        root.getChildren().remove(knot.getSelection());
      }

      if (knot.getHovered() != null) {
        root.getChildren().remove(knot.getHovered());
      }

      root.getChildren().add(rec);
      knot.setSelection(rec);
    }

    return knot;
  }

  public void drawHovered(Knot knot) {
    if (allHoveredKnots.contains(knot)) {
      allHoveredKnots.remove(knot);
      drawHoveredKnot(knot);
    }
  }
  public Knot drawHoveredKnot(Knot knot) {
    Rectangle rec;

    // If selected & hovered: red
    if (allSelectedKnots.contains(knot)) {
      rec = new Rectangle(knot.getX(), knot.getY(), knot.getPattern().getWidth(), knot.getPattern().getHeight());
      rec.setStroke(Color.RED);
      rec.setStrokeWidth(2d);
      rec.setFill(Color.TRANSPARENT);
      rec.setScaleX(computeZoomFactor(knot));
      rec.setScaleY(computeZoomFactor(knot));
      rec.setRotate(knot.getRotationAngle());
    } else {
      // If hovered & not selected: gray
      rec = new Rectangle(knot.getX(), knot.getY(), knot.getPattern().getWidth(), knot.getPattern().getHeight());
      rec.setStroke(Color.GRAY);
      rec.setStrokeWidth(2d);
      rec.setFill(Color.TRANSPARENT);
      rec.setScaleX(computeZoomFactor(knot));
      rec.setScaleY(computeZoomFactor(knot));
      rec.setRotate(knot.getRotationAngle());
    }

    for (Knot k : this.getAllHoveredKnots()) {
      if (k.getSelection() != null) {
        root.getChildren().remove(k.getSelection());
      }
      if (k.getHovered() != null) {
        root.getChildren().remove(k.getHovered());
      }
    }

    root.getChildren().add(rec);
    allHoveredKnots.add(knot);
    knot.setHovered(rec);

    return knot;
  }

  public Knot drawGuideLines(final Knot knot) {
    clearGuideLines(knot);

    // The black, thick lines that we use as guides
    for (Knot otherKnot : getDiagram().getKnots()) {
      if (!otherKnot.equals(knot) && otherKnot.isVisible()) {
        new GuideLinesUtil(knot, otherKnot, root);
      }
    }

    return knot;
  }

  public void clearGuideLines(final Knot knot) {
    root.getChildren().removeAll(knot.getGuideLines());
    knot.getGuideLines().clear();
  }

  public void clearSelections() {
    allSelectedKnots.stream().forEach(knot -> root.getChildren().remove(knot.getSelection()));
  }

  public void clearHovered() {
    diagram.getKnots().stream().forEach(knot -> root.getChildren().remove(knot.getHovered()));
  }

  public void clearSelection(Knot knot) {
    if (knot.getSelection() != null) {
      root.getChildren().remove(knot.getSelection());
    }

    allSelectedKnots.remove(knot);
  }

  public void clearAllGuideLines() {
    for (Knot knot : getDiagram().getKnots()) {
      clearGuideLines(knot);
    }
  }

  public List<Knot> getAllSelectedKnots() {
    return this.allSelectedKnots;
  }

  public List<Knot> getAllHoveredKnots() {
    return this.allHoveredKnots;
  }

  private void drawKnotWithRotationAndZoom(Knot knot) {
    ImageView iv = rotateKnot(knot);

    if (this.diagram.getKnots().indexOf(knot) == 0) {
      this.firstNonGridNode = iv;
    }

    zoomAndFlipKnot(knot, iv);

    double x = knot.getX();
    double y = knot.getY();

    iv.setX(x);
    iv.setY(y);

    knot.setImageView(iv);

    if (((diagram.getCurrentMode() == MouseMode.SELECTION) || (diagram.getCurrentMode() == MouseMode.DUPLICATION))
      && (allSelectedKnots.contains(knot))) {
      drawGuideLines(knot);
    }
    if (allSelectedKnots.contains(knot)) {
      drawHovered(knot);
      drawSelectedKnot(knot);
    }

    logger.debug("drawing top left corner of knot {} to ({},{})", knot.getPattern().getFilename(), x, y);
  }

  // Zoom factor goes from -10 to 10, 0 being don't zoom knot, < 0 being shrink knot, > 0 being enlarge knot
  public double zoomAndFlipKnot(Knot knot, ImageView iv) {
    flip(knot.isFlippedVertically(), Rotate.Y_AXIS, knot);
    flip(knot.isFlippedHorizontally(), Rotate.X_AXIS, knot);

    return zoom(knot, iv);
  }

  private double zoom(Knot knot, ImageView iv) {
    if (knot.getZoomFactor() != 0) {
      double zoomFactor = computeZoomFactor(knot);

      if (iv != null) {
        iv.setScaleX(zoomFactor);
        iv.setScaleY(zoomFactor);
      }

      return zoomFactor;
    } else {
      double zoomFactor = 1d;

      if (iv != null) {
        iv.setScaleX(zoomFactor);
        iv.setScaleY(zoomFactor);
      }

      return 1d;
    }
  }
  private void flip(boolean flip, Point3D yAxis, Knot knot) {
    if (flip) {
      Rotate rot = new Rotate(180d, yAxis);
      rot.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
      rot.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);
      knot.getImageView().getTransforms().add(rot);

      Translate translate = new Translate(0d, 0d, knot.getPattern().getWidth() / 2.0);
      knot.getImageView().getTransforms().add(translate);
    } else {
      Rotate rot = new Rotate(0, yAxis);
      rot.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
      rot.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);
      knot.getImageView().getTransforms().add(rot);

      Translate translate = new Translate(0d, 0d, knot.getPattern().getWidth() / 2.0);
      knot.getImageView().getTransforms().add(translate);
    }
  }

  public double computeZoomFactor(Knot knot) {
    return knot.getZoomFactor() == 0 ? 1 :
      (knot.getZoomFactor() > 0 ?
      (1d + knot.getZoomFactor() * ZOOM_SPINNER_POSITIVE_ZOOM_MULTIPLY_FACTOR) :
        ZOOM_SPINNER_NEGATIVE_ZOOM_DIVISION_FACTOR / -(knot.getZoomFactor() - 1d));
  }

  // Rotate knot with an angle in degrees
  private ImageView rotateKnot(Knot knot) {
    if (!root.getChildren().contains(knot.getImageView())) {
      root.getChildren().add(knot.getImageView());
      knot.getImageView().toBack();
    }

    knot.getImageView().getTransforms().clear();

    Rotate rot = new Rotate(knot.getRotationAngle(), Rotate.Z_AXIS);
    rot.setPivotX(knot.getImageView().getX() + knot.getPattern().getWidth() / 2d);
    rot.setPivotY(knot.getImageView().getY() + knot.getPattern().getHeight() / 2d);

    knot.getImageView().getTransforms().add(rot);
    logger.debug("rotating knot {} at angle {}", knot.getPattern().getFilename(), knot.getRotationAngle());

    return knot.getImageView();
  }

  private void drawGrid() {
    double top = (int) snappedTopInset() + TOP_MARGIN;
    double right = (int) snappedRightInset();
    double bottom = (int) snappedBottomInset();
    double left = (int) snappedLeftInset();
    double width = (int) getWidth() - left - right;
    double height = (int) getHeight() - top - bottom - 20d;
    root.setLayoutX(left);
    root.setLayoutY(top);

    if (this.showHideGrid && this.gridNeedsToBeRedrawn) {
      drawGrid(width, height);
    } else {
      hideGrid();
    }

    this.gridNeedsToBeRedrawn = false;
  }

  private void hideGrid() {
    for (Shape shape : grid) {
      root.getChildren().remove(shape);
    }
  }

  private void drawGrid(double w, double h) {
    hideGrid();

    for (double x = 40d; x < (w - 185d); x += SPACING_X) {
      for (double y = 60d; y < (h - 50d); y += SPACING_Y) {
        double offsetY = (y % (2d * SPACING_Y)) == 0d ? SPACING_X / 2d : 0d;
        Ellipse ell = new Ellipse(x - this.desiredRadius + offsetY,y - this.desiredRadius,this.desiredRadius,this.desiredRadius); // A dot
        ell.setFill(GRID_COLOR);
        ell.toFront();

        grid.add(ell);
        root.getChildren().add(ell);
      }
    }
  }

  public Knot addKnot(double x, double y) {
    Pattern currentPattern = this.diagram.getCurrentPattern();
    logger.info("Current pattern  -> {}", currentPattern);
    Knot currentKnot = null;

    try (FileInputStream fis = new FileInputStream(currentPattern.getAbsoluteFilename())) {
      Image image = new Image(fis);
      ImageView iv = new ImageView(image);

      iv.setX(x);
      iv.setY(y);
      iv.setRotate(0d);

      logger.debug("Top left corner of the knot {} is ({},{})", currentPattern.getFilename(), x, y);

      root.getChildren().add(iv);
      currentKnot = new Knot(x, y, currentPattern, iv);
      this.diagram.addKnot(currentKnot);
      layoutChildren();
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }

    return currentKnot;
  }

  public Node getFirstNonGridNode() {
    return this.firstNonGridNode;
  }

  public void setGridNeedsToBeRedrawn(boolean gridNeedsToBeRedrawn) {
    this.gridNeedsToBeRedrawn = gridNeedsToBeRedrawn;
  }

  public Group getRoot() {
    return root;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleObjectProperty<Pattern> getCurrentPatternProperty() {
    return this.currentPatternProperty;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleObjectProperty<Diagram> getDiagramProperty() {
    return this.diagramProperty;
  }

  public Diagram getDiagram() {
    return this.diagram;
  }

  /** Use JavaFX property
   * @see OptionalDotGrid#getDiagramProperty()
   * */
  public void setDiagram(Diagram diagram) {
    this.diagram = diagram;
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "A JavaFX property is meant to be modified from the outside")
  public SimpleBooleanProperty isShowHideGridProperty() {
    return this.showHideGridProperty;
  }

  public boolean isShowHideGrid() {
    return showHideGrid;
  }

  public void setShowHideGrid(boolean showHideGrid) {
    this.showHideGrid = showHideGrid;
  }

}
