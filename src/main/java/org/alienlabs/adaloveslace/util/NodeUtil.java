package org.alienlabs.adaloveslace.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.alienlabs.adaloveslace.business.model.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.alienlabs.adaloveslace.App.CANVAS_TEXT_FONT_SIZE;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.business.model.Diagram.isNewText;
import static org.alienlabs.adaloveslace.business.model.Knot.NEW_TEXT;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.app;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

public class NodeUtil {

  public static final int HANDLE_SIZE   = 25;

  private static final Logger logger = LoggerFactory.getLogger(NodeUtil.class);

  public NodeUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public boolean isMouseOverKnot(Knot knot) {
    return (knot.getImageView().isHover())
            || ((knot.getHovered() != null) && (knot.getHovered().isHover()))
            || ((knot.getSelection() != null) && (knot.getSelection().isHover()));
  }

  public Knot copyKnot(Knot knot) {
    Knot copy = new Knot(
            knot.getX(),
            knot.getY(),
            knot.getPattern(),
            knot.getText(),
            knot.getImageView()
    );
    copy(knot, copy);

    return copy;
  }

  public Knot copyKnotCloningImageView(Knot knot) {
    Knot copy = new Knot(knot.getX(), knot.getY(), knot.getPattern(), knot.getText(), new ImageView(knot.getImageView().getImage()));
    copy(knot, copy);
    copy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
    copy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));

    return copy;
  }

  private static void copy(Knot knot, Knot copy) {
    copy.setRotationAngle(knot.getRotationAngle());
    copy.setZoomFactor(knot.getZoomFactor());
    copy.setVisible(knot.isVisible());
    copy.setSelectable(knot.isSelectable());
    copy.setFlippedVertically(knot.isFlippedVertically());
    copy.setFlippedHorizontally(knot.isFlippedHorizontally());
    copy.setTextId(knot.getTextId());
    copy.setTypedText(knot.getTypedText());
    copy.setText(knot.getText());

    if (knot.getHovered() != null) {
      copy.setHovered(knot.getHovered());
    }
    if (knot.getSelection() != null) {
      copy.setSelection(knot.getSelection());
    }
    if (knot.getHandle() != null) {
      copy.setHandle(knot.getHandle());
    }
  }

  public ImageView drawPattern(double x, double y, Pattern currentPattern) {
    ImageView iv = null;

    try (FileInputStream fis = new FileInputStream(new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME, currentPattern.getFilename()))) {
      Image image = new Image(fis);
      iv = new ImageView(image);

      iv.setLayoutX(x);
      iv.setLayoutY(y);
      iv.setRotate(0d);

      logger.debug("Top left corner of the knot {} is ({},{})", currentPattern.getFilename(), x, y);
    } catch (IOException e) {
      logger.error("Problem with pattern resource file!", e);
    }

    return iv;
  }

  public void drawText(Diagram diagram, double x, double y) {
    diagram.setX(x);
    diagram.setY(y);

    app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
    app.getOptionalDotGrid().getDiagram()           .setCurrentMode(MouseMode.SELECTION);
    app.getGeometryWindow().getDrawingButton()      .setSelected(false);
    app.getGeometryWindow().getSelectionButton()    .setSelected(true);
    app.getGeometryWindow().getDeletionButton()     .setSelected(false);
    app.getGeometryWindow().getDuplicationButton()  .setSelected(false);

    app.getOptionalDotGrid().getDiagram().setCurrentKnot(null);
    diagram.getUpdateImage().run();
  }

  public void clearTechnicalElements(App app) {
    app.getOptionalDotGrid().clearSelections();
    app.getOptionalDotGrid().clearAllGuideLines();
    app.getOptionalDotGrid().clearHovered();
    app.getOptionalDotGrid().clearHandles();
    app.getOptionalDotGrid().layoutChildren();
  }


  public Knot newKnot(double x, double y, ImageView imageView, Pattern pattern, Knot currentKnot) {
    return new Knot(
            isNewText || currentKnot == null || pattern != null ? x : currentKnot.getX(),
            isNewText || currentKnot == null || pattern != null ? y : currentKnot.getY(),
            pattern == null ? Optional.empty() : Optional.of(pattern),
            pattern == null && !isNewText
                    ? Optional.of(currentKnot == null ? NEW_TEXT.toString() : currentKnot.getTypedText().toString())
                    : Optional.of(NEW_TEXT.toString()),
            imageView
    );
  }

  public ImageView createText(double x, double y, ImageView imageView, Knot currentKnot) {
    if (null == imageView) {
      imageView = new ImageView();

      final Text text = new Text();
      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);

      text.setText(currentKnot == null ? NEW_TEXT.toString() : currentKnot.getTypedText().toString());
      text.setFont(new Font(CANVAS_TEXT_FONT_SIZE));
      text.setFill(Color.BLACK);
      text.setScaleX(currentKnot == null ? 1d : new GridUtil(app.getMovablePane()).computeZoomFactor(currentKnot.getZoomFactor()));
      text.setScaleY(currentKnot == null ? 1d : new GridUtil(app.getMovablePane()).computeZoomFactor(currentKnot.getZoomFactor()));
      text.setRotate(currentKnot == null ? 0d : currentKnot.getRotationAngle());
      text.setLayoutX(currentKnot == null ? x : currentKnot.getX());
      text.setLayoutY(currentKnot == null ? y : currentKnot.getY());

      WritableImage s = text.snapshot(params, null);
      imageView.setImage(s);
    }
    return imageView;
  }

}
