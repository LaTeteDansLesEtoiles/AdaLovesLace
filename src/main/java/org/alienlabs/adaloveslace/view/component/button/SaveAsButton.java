package org.alienlabs.adaloveslace.view.component.button;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.alienlabs.adaloveslace.util.Preferences.SAVED_XML_FILE;
import static org.alienlabs.adaloveslace.util.Preferences.XML_FILE_FOLDER_SAVE_PATH;

public class SaveAsButton extends Button {

  public static final String SAVE_FILE_AS_BUTTON_NAME = "Save as";
  public static final String SAVE_FILE_DIALOG_TITLE   = "Save diagram as";
  public static final String DIAGRAM_FILES            = "XML files (*.xml)";
  public static final String DIAGRAM_FILE_FILTER      = "*.xml";

  private static final Logger logger                  = LoggerFactory.getLogger(SaveAsButton.class);

  public SaveAsButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onSaveAsAction(app, root));
  }

  public static void onSaveAsAction(App app, Pane root) {
    logger.info("Saving file as");

    FileChooser saveAs = new FileChooser();
    saveAs.setTitle(SAVE_FILE_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File xmlFilePath = preferences.getPathWithFileValue(XML_FILE_FOLDER_SAVE_PATH);
    if (xmlFilePath == null) {
      File userHome = new File(System.getProperty("user.home"));
      saveAs.setInitialDirectory(userHome);
      preferences.setPathWithFileValue(userHome, XML_FILE_FOLDER_SAVE_PATH);
    } else {
      saveAs.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    saveAs.getExtensionFilters().add(filter);

    File file = saveAs.showSaveDialog(root.getScene().getWindow());

    if (file != null) {
      preferences.setPathWithFileValue(file,                  SAVED_XML_FILE);
      preferences.setPathWithFileValue(file.getParentFile(),  XML_FILE_FOLDER_SAVE_PATH);

      try {
        JAXBContext context = JAXBContext.newInstance(Diagram.class);
        Marshaller jaxbMarshaller = context.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxbMarshaller.marshal(app.getCanvasWithOptionalDotGrid().getDiagram(), file);
      } catch (JAXBException e) {
        logger.error("Error marshalling save as file: " + file.getAbsolutePath(), e);
      }
    }
  }

}
