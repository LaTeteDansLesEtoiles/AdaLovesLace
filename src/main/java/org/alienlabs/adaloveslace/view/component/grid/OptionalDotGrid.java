package org.alienlabs.adaloveslace.view.component.grid;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.domain.enumeration.PatternOrTextMode;
import org.alienlabs.adaloveslace.util.NodeUtil;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.component.GuideLinesUtil;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.alienlabs.adaloveslace.App.CANVAS_TEXT_FONT_SIZE;
import static org.alienlabs.adaloveslace.domain.Diagram.newStep;
import static org.alienlabs.adaloveslace.domain.Knot.NEW_TEXT;

/**
 * A grid (= coordinate system) with dots (= used as landmarks for lace).
 */
public class OptionalDotGrid extends Pane {

  public static final Color BLUE_HANDLE = Color.rgb(0, 0, 255, 0.5);
  double GRID_WIDTH                     = 1400d;
  double GRID_HEIGHT                    = 600d;

  private final App app;
  private boolean showHideGrid = true;
  private Diagram diagram;

  private final Pane root;
  private final Pane gridPane;

  public static final PauseTransition moveKnotPause = new PauseTransition(Duration.millis(750));

  private final SimpleBooleanProperty showHideGridProperty;
  private final SimpleObjectProperty<Pattern> currentPatternProperty;
  private final SimpleObjectProperty<PatternOrTextMode> currentPatternOrTextModeProperty;

  private static final Logger logger = LoggerFactory.getLogger(OptionalDotGrid.class);
  private GridUtil gridUtil;

  /**
   * We draw the dots on the grid using a Canvas.
   *
   * @see Canvas
   *
   */
  public OptionalDotGrid(App app, Diagram diagram, Pane root) {
    this.app = app;
    this.root = root;
    this.root.toFront();
    this.diagram = Objects.requireNonNullElseGet(diagram, () -> new Diagram(app));

    this.gridPane = new Pane();
    this.gridPane.toBack();
    this.gridPane.getStyleClass().add("grid");
    this.gridPane.setBackground(null);

    this.root.getChildren().add(this.gridPane);
    this.gridUtil = new GridUtil(this.root);


    if (!this.diagram.getPatterns().isEmpty()) {
      this.diagram.setCurrentPattern(this.diagram.getPatterns().stream().findFirst().get());
      currentPatternProperty = new SimpleObjectProperty<>(this.diagram.getCurrentPattern());
    } else {
      currentPatternProperty = new SimpleObjectProperty<>();
    }

    currentPatternProperty.addListener(observable -> this.diagram.setCurrentPattern(currentPatternProperty.getValue()));
    currentPatternOrTextModeProperty = new SimpleObjectProperty<>(PatternOrTextMode.PATTERN);

    showHideGridProperty = new SimpleBooleanProperty(this.showHideGrid);
    showHideGridProperty.addListener(observable -> {
      this.showHideGrid = showHideGridProperty.getValue();
      setNeedsLayout(true);
    });

    moveKnotPause.setOnFinished(e -> {
      List<Knot> displayedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getDisplayedKnots());
      List<Knot> selectedKnots = new ArrayList<>(app.getOptionalDotGrid().getDiagram().getCurrentStep().getSelectedKnots());
      List<Knot> copiedKnots = new ArrayList<>();

      for (Knot knot : selectedKnots) {
        Knot copiedKnot = new NodeUtil().copyKnot(knot);

        displayedKnots.remove(knot);
        copiedKnots.add(copiedKnot);
      }

      newStep(displayedKnots, copiedKnots, true);
      app.getOptionalDotGrid().getDiagram().setCurrentMode(app.getOptionalDotGrid().getDiagram().getOldMode());
    });
  }

  public OptionalDotGrid(App app, double width, double height, Diagram diagram, Pane root) {
    this(app, diagram, root);
    GRID_WIDTH = width;
    GRID_HEIGHT = height;
  }

  @Override
  public void layoutChildren() {
    drawDiagram();
    app.getGridStrategy().drawGrid();
  }

  private void drawDiagram() {
    // We shall not display the undone knots => delete them from canvas, then draw the grid again
    deleteKnotsFromCanvas();

    this.getDiagram().deleteKnotDecorationsFromFollowingSteps(root);

    // If there are knots on the diagram, we must display them at each window refresh
    if (!this.diagram.getAllSteps().isEmpty() && this.diagram.getCurrentStepIndex() >= 0) {
      for (Knot knot : this.diagram.getCurrentStep().getDisplayedKnots()) {
        if (knot.isVisible()) {
          drawDisplayedKnot(knot);
        }
      }

      for (Knot knot : this.diagram.getCurrentStep().getSelectedKnots()) {
        if (knot.isVisible()) {
          drawSelectedKnot(this.diagram.getCurrentStep(), knot);
        }
      }

      drawHoveredOverOrSelectedDecorations(this.diagram.getCurrentStep().getAllVisibleKnots());
    }
  }

  // We shall not display the undone knots => delete them from canvas, then draw the grid again
  public void deleteKnotsFromCanvas() {
    this.diagram.deleteKnotDecorationsFromFollowingSteps(root);
    List<Node> nodeListToRemove = new ArrayList<>();
    Step step = this.diagram.getCurrentStep();

    for (Knot k : step.getSelectedKnots()) {
      if (app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.DRAG_AND_DROP) {
        app.getOptionalDotGrid().getDiagram().removeKnotDecorations(nodeListToRemove, k);
        root.getChildren().removeAll(nodeListToRemove);
        app.getOptionalDotGrid().getDiagram().removeAllHandles();
      }
    }

    nodeListToRemove = new ArrayList<>();
    Step s = this.diagram.getCurrentStep();
    for (Knot k : s.getAllVisibleKnots()) {
      nodeListToRemove.add(k.getImageView());
    }

    root.getChildren().removeAll(nodeListToRemove);
  }

  public void drawHoveredOverOrSelectedDecorations(List<Knot> knots) {
    for (Knot knot : knots) {

      if (!knot.isSelectable()) {
        continue;
      }

      boolean hovered = new NodeUtil().isMouseOverKnot(knot);

      // If selected & hovered: red
      if (hovered
              && getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
        Optional<Knot> firstKnot = getDiagram().getCurrentStep().getSelectedKnots().stream()
                .min(Comparator.comparing(Knot::getX)
                        .thenComparing(Knot::getY));

        if (firstKnot.isPresent()) {
          logger.debug("Adding red rectangle for Knot {}", knot);
          addSelectionAndHandleToAKnot(knot, Color.rgb(255, 0, 0, 0.5));
        } else {
          Platform.runLater(() -> {
            Rectangle rec = this.gridUtil.newRectangle(knot, Color.BLUE);
            knot.setSelection(rec);
            logger.debug("Adding hover {} for Knot {}", rec, knot);
            root.getChildren().add(rec);
          });
        }
      } else if (hovered) {
        // If hovered & not selected: gray
        Platform.runLater(() -> {
          Rectangle rec = newHoverRectangle(knot);
          knot.setHovered(rec);
          logger.debug("Adding hover {} for Knot {}", rec, knot);
          root.getChildren().add(rec);
        });
      } else if (getDiagram().getCurrentStep().getSelectedKnots().contains(knot)) {
        addSelectionAndHandleToAKnot(knot, BLUE_HANDLE);
      } else {
        Platform.runLater(() -> {
          logger.debug("Removing node {} and hover {}", knot, knot.getHovered());

          if (knot.getHovered() != null) {
            root.getChildren().remove(knot.getHovered());
            knot.setHovered(null);
            layoutChildren();
          }
        });
      }
    }
  }

  public void addSelectionAndHandleToAKnot(Knot knot, Color rgba) {
    Platform.runLater(() -> {
      Rectangle rec = this.gridUtil.newRectangle(knot, rgba);
      rec.addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));

      knot.setHovered(rec);
      knot.setSelection(rec);
      root.getChildren().add(rec);

      if (root.getChildren().contains(knot.getHandle()) && app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.DRAG_AND_DROP) {
        root.getChildren().remove(knot.getHandle());
        knot.setHandle(null);
      }

      if (app.getOptionalDotGrid().getDiagram().getCurrentMode() != MouseMode.DRAG_AND_DROP) {
        Circle handle;

        if (knot.getPattern().isPresent()) {
          handle = this.gridUtil.newHandleForPattern(knot, rec);
        } else {
          handle = this.gridUtil.newHandleForText(knot, rec);
        }

        knot.setHandle(handle);
        root.getChildren().add(handle);

        handle.setOnMousePressed(GridEvents.getDragInitiatedOverHandleEventHandler());
        handle.setOnMouseDragged(GridEvents.getMouseDragOverHandleEventHandler());
        handle.setOnMouseReleased(GridEvents.getMouseDragDroppedHandleEventHandler());
      }
    });
  }

  private Rectangle newHoverRectangle(Knot knot) {
    return this.gridUtil.newRectangle(knot, Color.GRAY);
  }

  public void drawGuideLines(final Step step, final Knot knot) {
    Platform.runLater(() -> {
      if ((diagram.getCurrentMode() != MouseMode.CREATE_PATTERN) && (diagram.getCurrentMode() != MouseMode.MIRROR)) {
        for (Knot otherKnot : step.getAllVisibleKnots()) {
          if (!otherKnot.equals(knot) && otherKnot.isVisible()) {
            new GuideLinesUtil(knot, otherKnot, root);
          }
        }
      }
    });
  }

  public void removeKnotDecorations() {
    this.clearSelections();
    this.clearAllGuideLines();
    this.clearHovered();
    this.clearHandles();
  }

  public void clearGuideLines(final Knot knot) {
    root.getChildren().removeAll(knot.getGuideLines());
    knot.getGuideLines().clear();
  }

  public void clearSelections() {
    getDiagram().getCurrentStep().getSelectedKnots().stream().forEach(knot -> root.getChildren().remove(knot.getSelection()));
  }

  public void clearHovered() {
    getDiagram().getCurrentStep().getSelectedKnots().stream().forEach(knot -> root.getChildren().remove(knot.getHovered()));
  }

  public void clearHandles() {
    root.getChildren().removeAll(root.getChildren().stream().filter(Circle.class::isInstance).toList());
  }

  public void clearAllGuideLines() {
    for (Knot knot : getDiagram().getCurrentStep().getSelectedKnots()) {
      clearGuideLines(knot);
    }
  }

  public void clearCreatePatternRectangle() {
    root.getChildren().removeAll(root.getChildren().stream()
            .filter(Rectangle.class::isInstance)
            .map((Node node) -> {
              node.setOnMouseMoved(null);
              node.setOnMouseClicked(null);
              return node;
            })
            .toList());
  }

  private void drawDisplayedKnot(Knot knot) {
    ImageView imageView = null;
    double x = knot.getX();
    double y = knot.getY();

    if (knot.getText().isPresent() && !knot.getText().get().contentEquals(NEW_TEXT)) {
      drawTextImageView(knot, x, y);
      imageView = rotateTextKnot(knot);
      this.gridUtil.zoomTextKnot(knot);
    } else if (knot.getPattern().isPresent()) {

      imageView = this.gridUtil.rotatePatternKnot(knot);
      this.gridUtil.zoomAndFlipPatternKnot(knot);
    }

    if (null != imageView) {
      imageView.setLayoutX(x);
      imageView.setLayoutY(y);

      logger.debug("drawing top left corner of knot {} to ({},{})", knot, x, y);
    }
  }

  public ImageView drawTextImageView(Knot knot, double x, double y) {
    ImageView imageView;
    Text text = new Text();
    text.setText(knot.getText().get());
    text.setFont(new Font(CANVAS_TEXT_FONT_SIZE));
    text.setFill(knot.getColor().isEmpty() ? Color.BLACK : knot.getColor().get());
    SnapshotParameters params = new SnapshotParameters();
    params.setFill(Color.TRANSPARENT);
    text.setLayoutX(x);
    text.setLayoutY(y);

    imageView = new ImageView();
    WritableImage snapshot = text.snapshot(params, null);
    imageView.setImage(snapshot);

    knot.setImageView(imageView);
    root.getChildren().add(knot.getImageView());
    return imageView;
  }

  private void drawSelectedKnot(Step step, Knot knot) {
    ImageView imageView;
    double x = knot.getX();
    double y = knot.getY();

    if (knot.getPattern().isEmpty()) {
      drawTextImageView(knot, x, y);
      imageView = rotateTextKnot(knot);
      this.gridUtil.zoomTextKnot(knot);

      imageView.setLayoutX(x);
      imageView.setLayoutY(y);
      knot.setImageView(imageView);

      drawGuideLines(step, knot);
    } else if (knot.getPattern().isPresent()) {
      imageView = this.gridUtil.rotatePatternKnot(knot);
      this.gridUtil.zoomAndFlipPatternKnot(knot);

      imageView.setLayoutX(x);
      imageView.setLayoutY(y);
      knot.setImageView(imageView);

      drawGuideLines(step, knot);
    }

    logger.debug("drawing top left corner of knot {} to ({},{})",
            knot.getPattern().isPresent() ?
                    knot.getPattern().get().getFilename() :
                    knot.getText().toString(),
            x, y);
  }

  // Rotate Text knot with an angle in degrees
  private ImageView rotateTextKnot(Knot knot) {
    knot.getImageView().getTransforms().clear();
    knot.getImageView().setRotate(knot.getRotationAngle());

    if (!root.getChildren().contains(knot.getImageView())) {
      root.getChildren().add(knot.getImageView());
    }

    logger.debug("rotated knot {} at angle {}",
            knot.getText(),
            knot.getRotationAngle());

    return knot.getImageView();
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
  public SimpleObjectProperty<PatternOrTextMode> getCurrentPatternOrTextModeProperty() {
    return this.currentPatternOrTextModeProperty;
  }

  public Diagram getDiagram() {
    return this.diagram;
  }

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
    return this.showHideGrid;
  }

  public void setShowHideGrid(boolean showHideGrid) {
    this.showHideGrid = showHideGrid;
  }

  public Pane getGridPane() {
    return this.gridPane;
  }

}
