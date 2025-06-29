package org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid;

import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.FileChooserUtil;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alienlabs.adaloveslace.App.RESET_KNOTS_BUTTON_NAME;
import static org.alienlabs.adaloveslace.App.resourceBundle;
import static org.alienlabs.adaloveslace.view.window.ToolboxWindow.PATTERN_TEXT_AND_COLOR_BUTTON;

public class ResetKnotsButton extends ToggleButton {

  private static App app;

  private static final Logger logger                  = LoggerFactory.getLogger(ResetKnotsButton.class);

  public ResetKnotsButton(App app) {
    super(resourceBundle.getString(RESET_KNOTS_BUTTON_NAME));

    ResetKnotsButton.app = app;

    this.getStyleClass().add(PATTERN_TEXT_AND_COLOR_BUTTON);
    this.setSelected(false);

    this.setOnMouseClicked(onResetKnotsButtonClicked);
  }

  private final EventHandler<MouseEvent> onResetKnotsButtonClicked = _ -> {
    app.unselectPatternsAndTextButtons();
    app.getMovablePane().setOnKeyPressed(null);

    new ImageUtil(app).backupKnots();
    app.getOptionalDotGrid().getDiagram().getPatterns().clear();
    app.getOptionalDotGrid().getDiagram().resetDiagram(app);
    new FileChooserUtil().restartGui(app);
  };

}
