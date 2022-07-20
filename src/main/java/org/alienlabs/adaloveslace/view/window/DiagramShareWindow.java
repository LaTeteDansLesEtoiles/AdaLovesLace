package org.alienlabs.adaloveslace.view.window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.ShareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;

public class DiagramShareWindow {

  public static final String SHARE_DIAGRAM_WINDOW_TITLE = "Share diagram on Ada Loves Lace community website";
  public static final String SHARE_DIAGRAM_HEADER_TEXT = "Will you share your diagram?";
  public static final String SHARE_DIAGRAM_CONTENT_TEXT = "Please fill in information below";
  public static final String SHARE_BUTTON_TEXT = "Share";
  public static final String CANCEL_BUTTON_TEXT = "Cancel";
  public static final String FILENAME_LABEL = "Diagram name: ";
  public static final String USERNAME_LABEL = "Username: ";
  public static final String CLIENT_ID_LABEL = "Client ID: ";
  public static final String CLIENT_SECRET_LABEL = "Client secret: ";

  private TextField filenameText;
  private TextField userText;
  private TextField clientIdText;
  private TextField clientSecretText;

  private static final Logger logger = LoggerFactory.getLogger(DiagramShareWindow.class);

  public DiagramShareWindow(App app) {
    Alert alert = new Alert(CONFIRMATION);

    ButtonType shareButton = buildAlertWindow(alert);
    GridPane gridPane = buildGridPane();
    alert.getDialogPane().setExpandableContent(gridPane);

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == shareButton){
      new ShareUtil(app, filenameText.getText(), userText.getText(), clientIdText.getText(), clientSecretText.getText());
    } else {
      logger.info("Sharing cancelled");
    }

    alert.close();
  }

  private ButtonType buildAlertWindow(Alert alert) {
    alert.setTitle(SHARE_DIAGRAM_WINDOW_TITLE);
    alert.setHeaderText(SHARE_DIAGRAM_HEADER_TEXT);
    alert.setContentText(SHARE_DIAGRAM_CONTENT_TEXT);

    ButtonType shareButton  = new ButtonType(SHARE_BUTTON_TEXT);
    ButtonType cancelButton = new ButtonType(CANCEL_BUTTON_TEXT, CANCEL_CLOSE);

    alert.getButtonTypes().setAll(shareButton, cancelButton);
    return shareButton;
  }

  private GridPane buildGridPane() {
    Label filenameLabel = new Label(FILENAME_LABEL);

    filenameText = new TextField("");
    filenameText.setEditable(true);

    GridPane.setVgrow(filenameText, Priority.ALWAYS);
    GridPane.setHgrow(filenameText, Priority.ALWAYS);

    GridPane gridPane = new GridPane();
    gridPane.setMaxWidth(Double.MAX_VALUE);
    gridPane.add(filenameLabel, 0, 0);
    gridPane.add(filenameText, 1, 0);

    Label userLabel = new Label(USERNAME_LABEL);

    userText = new TextField("");
    userText.setEditable(true);

    GridPane.setVgrow(userText, Priority.ALWAYS);
    GridPane.setHgrow(userText, Priority.ALWAYS);

    gridPane.add(userLabel, 0, 1);
    gridPane.add(userText, 1, 1);

    Label clientIdLabel = new Label(CLIENT_ID_LABEL);

    clientIdText = new TextField("");
    clientIdText.setEditable(true);

    GridPane.setVgrow(clientIdText, Priority.ALWAYS);
    GridPane.setHgrow(clientIdText, Priority.ALWAYS);

    gridPane.add(clientIdLabel, 0, 2);
    gridPane.add(clientIdText, 1, 2);

    Label clientSecretLabel = new Label(CLIENT_SECRET_LABEL);

    clientSecretText = new TextField("");
    clientSecretText.setEditable(true);

    GridPane.setVgrow(clientSecretText, Priority.ALWAYS);
    GridPane.setHgrow(clientSecretText, Priority.ALWAYS);

    gridPane.add(clientSecretLabel, 0, 3);
    gridPane.add(clientSecretText, 1, 3);
    return gridPane;
  }

}
