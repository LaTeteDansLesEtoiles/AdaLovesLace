package org.alienlabs.adaloveslace.view.window;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Picture;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.alienlabs.adaloveslace.util.ImageUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.util.ShareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;

public class DiagramShareWithImagesWindow extends Dialog<DiagramShareWithImagesWindow.Result> {

  public static final String SHARE_DIAGRAM_WINDOW_TITLE     = "ShareDiagramWindowTitle";
  public static final String SHARE_BUTTON_TEXT              = "ShareButtonText";
  public static final String USERNAME_PROMPT                = "UsernamePrompt";
  public static final String FILENAME_PROMPT                = "FilenamePrompt";
  public static final String DESCRIPTION_PROMPT             = "DescriptionPrompt";
  public static final String CLIENT_ID_PROMPT               = "ClientIdPrompt";
  public static final String CLIENT_SECRET_PROMPT           = "ClientSecretPrompt";
  public static final String HIDE_CREDENTIALS               = "HideCredentials";
  public static final String SHOW_CREDENTIALS               = "ShowCredentials";
  public static final String USERNAME_LABEL                 = "UsernameLabel";
  public static final String FILENAME_LABEL                 = "FilenameLabel";
  public static final String DESCRIPTION_LABEL              = "DescriptionLabel";
  public static final String CLIENT_ID_LABEL                = "ClientIdLabel";
  public static final String CLIENT_SECRET_LABEL            = "ClientSecretLabel";
  public static final String CHOOSE_TECHNIQUE_LABEL         = "ChooseTechniqueLabel";
  public static final String CHOOSE_SUB_TECHNIQUE_LABEL     = "ChooseSubTechniqueLabel";
  public static final String TECHNIQUE_LABEL                = "TechniqueLabel";
  public static final String SUB_TECHNIQUE_LABEL            = "SubTechniqueLabel";
  public static final String LOAD_IMAGE                     = "LoadImage";
  public static final String CHOOSE_IMAGE                   = "ChooseImage";
  public static final String ADDITIONAL_IMAGES_LABEL        = "AdditionalImagesLabel";

  public static final String USERNAME_PREFERENCE            = "USERNAME_PREFERENCE";
  public static final String CLIENT_ID_PREFERENCE           = "CLIENT_ID_PREFERENCE";
  public static final String CLIENT_SECRET_PREFERENCE       = "CLIENT_SECRET_PREFERENCE";
  public static final String SHARED_IMAGES_FOLDER_PREFERENCE= "SHARED_IMAGES_FOLDER_PREFERENCE";

  private final ObservableList<Image> imageList             = FXCollections.observableArrayList();

  private final TextField nameField                         = new TextField();
  private final TextField descriptionField                  = new TextField();

  private final ComboBox<Technique> techniqueCombo             = new ComboBox<>();
  private final ComboBox<SubTechnique> subTechniqueCombo          = new ComboBox<>();

  private static final Logger logger = LoggerFactory.getLogger(DiagramShareWithImagesWindow.class);

  public DiagramShareWithImagesWindow(App app) {
    setTitle(resourceBundle.getString(SHARE_DIAGRAM_WINDOW_TITLE));
    initOwner(app.getPrimaryStage().getOwner());
    Preferences preferences = new Preferences();

    UUID uuid           = UUID.randomUUID();
    Image initialImage  = new ImageUtil(app).buildWritableImageWithoutTechnicalElements(
            APP_FOLDER_IN_USER_HOME + uuid + EXPORT_IMAGE_FILE_TYPE);

    ButtonType okType = new ButtonType(resourceBundle.getString(SHARE_BUTTON_TEXT), ButtonBar.ButtonData.OK_DONE);
    getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

    VBox content = new VBox(10d);
    content.setPadding(new Insets(15d));

    ImageView iv = new ImageView(initialImage);
    iv.setFitWidth(300d);
    iv.setPreserveRatio(true);
    content.getChildren().add(iv);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(8);

    TextField usernameField = new TextField(preferences.getStringValue(USERNAME_PREFERENCE));
    usernameField.setPromptText(resourceBundle.getString(USERNAME_PROMPT));
    nameField.setPromptText(resourceBundle.getString(FILENAME_PROMPT));
    descriptionField.setPromptText(resourceBundle.getString(DESCRIPTION_PROMPT));

    grid.add(new Label(resourceBundle.getString(USERNAME_LABEL)),       0, 0);
    grid.add(usernameField,                                             1, 0);
    grid.add(new Label(resourceBundle.getString(FILENAME_LABEL)),       0, 1);
    grid.add(nameField,                                                 1, 1);
    grid.add(new Label(resourceBundle.getString(DESCRIPTION_LABEL)),    0, 2);
    grid.add(descriptionField,                                          1, 2);

    TextField clientIdField = new TextField(preferences.getStringValue(CLIENT_ID_PREFERENCE));
    TextField clientSecretField = new TextField(preferences.getStringValue(CLIENT_SECRET_PREFERENCE));
    clientIdField.setPromptText(resourceBundle.getString(CLIENT_ID_PROMPT));
    clientSecretField.setPromptText(resourceBundle.getString(CLIENT_SECRET_PROMPT));
    Label clientIdLabel = new Label(resourceBundle.getString(CLIENT_ID_LABEL));
    Label clientSecretLabel = new Label(resourceBundle.getString(CLIENT_SECRET_LABEL));

    grid.add(new Label(resourceBundle.getString(TECHNIQUE_LABEL)),      0, 5);
    grid.add(techniqueCombo,                                            1, 5);
    grid.add(new Label(resourceBundle.getString(SUB_TECHNIQUE_LABEL)),  0, 6);
    grid.add(subTechniqueCombo,                                         1, 6);

    VBox credentialsBox = new VBox(5,
            clientIdLabel,    clientIdField,
            clientSecretLabel, clientSecretField
    );
    credentialsBox.setPadding(new Insets(0, 0, 0, 20)); // un peu de marge à gauche
    credentialsBox.setVisible(false);
    credentialsBox.managedProperty().bind(credentialsBox.visibleProperty());

    Button toggleCredButton = new Button(resourceBundle.getString(SHOW_CREDENTIALS));
    toggleCredButton.setOnAction(evt -> {
      boolean nowVisible = !credentialsBox.isVisible();
      credentialsBox.setVisible(nowVisible);
      toggleCredButton.setText(nowVisible
              ? resourceBundle.getString(HIDE_CREDENTIALS)
              : resourceBundle.getString(SHOW_CREDENTIALS));
    });

    techniqueCombo.setPromptText(resourceBundle.getString(CHOOSE_TECHNIQUE_LABEL));
    subTechniqueCombo.setPromptText(resourceBundle.getString(CHOOSE_SUB_TECHNIQUE_LABEL));
    subTechniqueCombo.setDisable(true);

    techniqueCombo.setItems(FXCollections.observableArrayList(Technique.values()));
    subTechniqueCombo.setDisable(true);

    techniqueCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(Technique t) {
        return t == null ? "" : resourceBundle.getString(t.getMessageKey());
      }
      @Override
      public Technique fromString(String s) { return null; }
    });
    techniqueCombo.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(Technique item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null
                ? null
                : resourceBundle.getString(item.getMessageKey()));
      }
    });

    subTechniqueCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(SubTechnique st) {
        return st == null ? "" : resourceBundle.getString(st.getMessageKey());
      }
      @Override
      public SubTechnique fromString(String s) { return null; }
    });

    subTechniqueCombo.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(SubTechnique item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null
                ? null
                : resourceBundle.getString(item.getMessageKey()));
      }
    });

    techniqueCombo.valueProperty().addListener((obs, oldV, newV) -> {
      if (newV != null) {
        subTechniqueCombo.getItems().setAll(newV.getSubTechniques());
        subTechniqueCombo.setDisable(false);
        subTechniqueCombo.getSelectionModel().clearSelection();
      } else {
        subTechniqueCombo.getItems().clear();
        subTechniqueCombo.setDisable(true);
      }
    });

    content.getChildren().addAll(
            grid,
            new Separator(),
            toggleCredButton,
            credentialsBox,
            new Separator()
    );

    ListView<Image> listView = getImageListView();
    Button loadButton = new Button(resourceBundle.getString(LOAD_IMAGE));

    loadButton.setOnAction(_ -> {
      FileChooser chooser = new FileChooser();
      chooser.setTitle(resourceBundle.getString(CHOOSE_IMAGE));
      File lastDir = new File(preferences.getStringValue(SHARED_IMAGES_FOLDER_PREFERENCE));
      if (lastDir.exists() && lastDir.isDirectory()) {
        chooser.setInitialDirectory(lastDir);
      }
      chooser.getExtensionFilters().add(
              new FileChooser.ExtensionFilter(
                      "Images (*.png,*.jpg,*.jpeg,*.gif)",
                      "*.png","*.jpg","*.jpeg","*.gif"
              )
      );
      List<File> selectedFiles;

      try {
        selectedFiles = chooser.showOpenMultipleDialog(getOwner());
      } catch (IllegalArgumentException e) {
        logger.error("Error opening file selector for sharing, retrying", e);
        chooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
        selectedFiles = chooser.showOpenMultipleDialog(getOwner());
      }

      if (selectedFiles != null) {
        preferences.setStringValue(SHARED_IMAGES_FOLDER_PREFERENCE, selectedFiles.getFirst().getAbsolutePath());

        for (File file : selectedFiles) {
          imageList.add(new Image(file.toURI().toString()));
        }
      }
    });

    content.getChildren().addAll(
            new Label(resourceBundle.getString(ADDITIONAL_IMAGES_LABEL)), listView, loadButton
    );
    getDialogPane().setContent(content);

    Node okButton = getDialogPane().lookupButton(okType);
    okButton.disableProperty().bind(
            Bindings.createBooleanBinding(
                    () ->
                            usernameField.getText().length()            < 3 ||
                                    nameField.getText().length()        < 5 ||
                                    descriptionField.getText().length() < 5 ||
                                    clientIdField.getText().length()    < 36 ||
                                    clientSecretField.getText().length()< 36 ||
                                    techniqueCombo.getValue() == null ||
                                    subTechniqueCombo.getValue() == null ||
                                    imageList.isEmpty(),
                    usernameField.textProperty(),
                    nameField.textProperty(),
                    descriptionField.textProperty(),
                    clientIdField.textProperty(),
                    clientSecretField.textProperty(),
                    techniqueCombo.valueProperty(),
                    subTechniqueCombo.valueProperty(),
                    imageList
            )
    );

    setResultConverter(btn -> {
      if (btn == okType) {
        preferences.setStringValue(USERNAME_PREFERENCE,       usernameField.getText());
        preferences.setStringValue(CLIENT_ID_PREFERENCE,      clientIdField.getText());
        preferences.setStringValue(CLIENT_SECRET_PREFERENCE,  clientSecretField.getText());

        List<Picture> pictures = new ArrayList<>();

        try {
            for (Image image : imageList) {
                pictures.add(new Picture().picture(new ImageUtil(app).imageToPngString(image)).
                        pictureContentType("image/png").
                        showcase("").
                        preview(null)
                );
            }

          imageList.add(1, initialImage);
          pictures.add(1, new Picture().picture(new ImageUtil(app).imageToPngString(initialImage)).
                  pictureContentType("image/png").
                  showcase("").
                  preview(null)
          );

          Image showcase = imageList.getFirst();
          pictures.removeFirst();
          pictures.addFirst(
                  new Picture().picture("").
                          pictureContentType("image/png").
                          showcase(new ImageUtil(app).imageToPngString(showcase)).
                          preview(null)
          );

        } catch (IOException e) {
          throw new IllegalArgumentException(e);
        }

        new ShareUtil(
                app,
                nameField.getText(),
                usernameField.getText(),
                clientIdField.getText(),
                clientSecretField.getText(),
                pictures
        );
      }

      logger.debug("Sharing cancelled");
      return null;
    });
  }

  private ListView<Image> getImageListView() {
    ListView<Image> listView = new ListView<>(imageList);
    listView.setCellFactory(lv -> new ListCell<>() {
      private final ImageView thumb = new ImageView();
      {
        thumb.setFitWidth(100d);
        thumb.setPreserveRatio(true);
      }
      @Override
      protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty || item == null ? null : thumb);
        if (!empty && item != null) thumb.setImage(item);
      }
    });
    listView.setPrefHeight(200d);
    return listView;
  }

  public static Optional<Result> show(App app) {
    return new DiagramShareWithImagesWindow(app).showAndWait();
  }

  public static class Result {
    public final String       username;
    public final String       name;
    public final String       description;
    public final String       clientId;
    public final String       clientSecret;
    public final Technique    technique;
    public final SubTechnique subTechnique;
    public final List<Image>  images;

    public Result(
            String username,
            String name,
            String description,
            String clientId,
            String clientSecret,
            Technique technique,
            SubTechnique subTechnique,
            List<Image> images
    ) {
      this.username     = username;
      this.name         = name;
      this.description  = description;
      this.clientId     = clientId;
      this.clientSecret = clientSecret;
      this.technique    = technique;
      this.subTechnique = subTechnique;
      this.images       = images;
    }
  }

}
