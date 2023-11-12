package org.alienlabs.adaloveslace.view.window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.ShareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static org.alienlabs.adaloveslace.App.resourceBundle;

public class DiagramShareWindow {

  public static final double DIAGRAM_SHARE_WINDOW_WIDTH     = 550d;

  public static final String SHARE_DIAGRAM_WINDOW_TITLE     = "SHARE_DIAGRAM_WINDOW_TITLE";
  public static final String SHARE_DIAGRAM_HEADER_TEXT      = "SHARE_DIAGRAM_HEADER_TEXT";
  public static final String SHARE_DIAGRAM_CONTENT_TEXT     = "SHARE_DIAGRAM_CONTENT_TEXT";
  public static final String SHARE_BUTTON_TEXT              = "SHARE_BUTTON_TEXT";
  public static final String CANCEL_BUTTON_TEXT             = "CANCEL_BUTTON_TEXT";
  public static final String FILENAME_LABEL                 = "FILENAME_LABEL";
  public static final String USERNAME_LABEL                 = "USERNAME_LABEL";
  public static final String CLIENT_ID_LABEL                = "CLIENT_ID_LABEL";
  public static final String CLIENT_SECRET_LABEL            = "CLIENT_SECRET_LABEL";

  public static final String FILENAME_PREFERENCE            = "FILENAME_PREFERENCE";
  public static final String USERNAME_PREFERENCE            = "USERNAME_PREFERENCE";
  public static final String CLIENT_ID_PREFERENCE           = "CLIENT_ID_PREFERENCE";
  public static final String CLIENT_SECRET_PREFERENCE       = "CLIENT_SECRET_PREFERENCE";

  private TextField filenameText;
  private TextField userText;
  private TextField clientIdText;
  private TextField clientSecretText;

  private static final Logger logger = LoggerFactory.getLogger(DiagramShareWindow.class);

  public DiagramShareWindow(App app) {
    Alert alert = new Alert(CONFIRMATION);
    Preferences preferences = new Preferences();

    ButtonType shareButton = buildAlertWindow(alert);
    GridPane gridPane = buildGridPane();
    alert.getDialogPane().setExpandableContent(gridPane);

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == shareButton) {
      preferences.setStringValue(FILENAME_PREFERENCE,       filenameText.getText());
      preferences.setStringValue(USERNAME_PREFERENCE,       userText.getText());
      preferences.setStringValue(CLIENT_ID_PREFERENCE,      clientIdText.getText());
      preferences.setStringValue(CLIENT_SECRET_PREFERENCE,  clientSecretText.getText());

      new ShareUtil(app, filenameText.getText(), userText.getText(), clientIdText.getText(), clientSecretText.getText());
    } else {
      logger.debug("Sharing cancelled");
    }

    alert.close();
  }

  private ButtonType buildAlertWindow(Alert alert) {
    alert.setTitle(resourceBundle.getString(SHARE_DIAGRAM_WINDOW_TITLE));
    alert.setHeaderText(resourceBundle.getString(SHARE_DIAGRAM_HEADER_TEXT));
    alert.setContentText(resourceBundle.getString(SHARE_DIAGRAM_CONTENT_TEXT));

    ButtonType shareButton  = new ButtonType(resourceBundle.getString(SHARE_BUTTON_TEXT));
    ButtonType cancelButton = new ButtonType(resourceBundle.getString(CANCEL_BUTTON_TEXT), CANCEL_CLOSE);

    alert.getButtonTypes().setAll(shareButton, cancelButton);
    return shareButton;
  }

  private GridPane buildGridPane() {
    Preferences prefs = new Preferences();
    Label filenameLabel = new Label(resourceBundle.getString(FILENAME_LABEL));

    filenameText = new TextField(prefs.getStringValue(FILENAME_PREFERENCE));
    filenameText.setEditable(true);

    GridPane.setVgrow(filenameText, Priority.ALWAYS);
    GridPane.setHgrow(filenameText, Priority.ALWAYS);

    GridPane gridPane = new GridPane();
    gridPane.setPrefWidth(DIAGRAM_SHARE_WINDOW_WIDTH);
    gridPane.setMaxWidth(Double.MAX_VALUE);
    gridPane.add(filenameLabel, 0, 0);
    gridPane.add(filenameText, 1, 0);

    Label userLabel = new Label(resourceBundle.getString(USERNAME_LABEL));

    userText = new TextField(prefs.getStringValue(USERNAME_PREFERENCE));
    userText.setEditable(true);

    GridPane.setVgrow(userText, Priority.ALWAYS);
    GridPane.setHgrow(userText, Priority.ALWAYS);

    gridPane.add(userLabel, 0, 1);
    gridPane.add(userText, 1, 1);

    Label clientIdLabel = new Label(resourceBundle.getString(CLIENT_ID_LABEL));

    clientIdText = new TextField(prefs.getStringValue(CLIENT_ID_PREFERENCE));
    clientIdText.setEditable(true);

    GridPane.setVgrow(clientIdText, Priority.ALWAYS);
    GridPane.setHgrow(clientIdText, Priority.ALWAYS);

    gridPane.add(clientIdLabel, 0, 2);
    gridPane.add(clientIdText, 1, 2);

    Label clientSecretLabel = new Label(resourceBundle.getString(CLIENT_SECRET_LABEL));

    clientSecretText = new TextField(prefs.getStringValue(CLIENT_SECRET_PREFERENCE));
    clientSecretText.setEditable(true);

    GridPane.setVgrow(clientSecretText, Priority.ALWAYS);
    GridPane.setHgrow(clientSecretText, Priority.ALWAYS);

    gridPane.add(clientSecretLabel, 0, 3);
    gridPane.add(clientSecretText, 1, 3);
    return gridPane;
  }

}
