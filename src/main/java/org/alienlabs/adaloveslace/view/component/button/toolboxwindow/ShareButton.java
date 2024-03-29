package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.alienlabs.adaloveslace.view.window.DiagramShareWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareButton extends ImageButton {

  public static final String SHARE_BUTTON_NAME         = "SHARE_BUTTON_NAME";

  private static final Logger logger                  = LoggerFactory.getLogger(ShareButton.class);

  public ShareButton(App app, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onShareAction(app));
    buildButtonImage("share.png");
  }

  public static void onShareAction(App app) {
    logger.debug("Share file");
    new DiagramShareWindow(app);
  }

}
