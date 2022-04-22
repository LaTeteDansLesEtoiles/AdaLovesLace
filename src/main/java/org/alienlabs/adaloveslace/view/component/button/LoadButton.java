package org.alienlabs.adaloveslace.view.component.button;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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

public class LoadButton extends Button {

  public static final String LOAD_BUTTON_NAME         = "Load";

  public static final String LOAD_FILE_DIALOG_TITLE   = "Load diagram";
  public static final String DIAGRAM_FILES            = "XML files (*.xml)";
  public static final String DIAGRAM_FILE_FILTER      = "*.xml";

  private static final Logger logger                  = LoggerFactory.getLogger(LoadButton.class);

  public LoadButton(App app, Pane root, String buttonLabel) {
    super(buttonLabel);
    this.setOnMouseClicked(event -> onLoadAction(app, root));
  }

  public static void onLoadAction(App app, Pane root) {
    logger.info("Load file");

    FileChooser load = new FileChooser();
    load.setTitle(LOAD_FILE_DIALOG_TITLE);

    Preferences preferences = new Preferences();
    File xmlFile      = preferences.getPathWithFileValue(SAVED_XML_FILE);
    File xmlFilePath  = preferences.getPathWithFileValue(XML_FILE_FOLDER_SAVE_PATH);

    if (xmlFilePath == null || !xmlFilePath.canRead() || !xmlFile.canRead()) {
      // We don't know from where to load
      load.setInitialDirectory(new File(System.getProperty("user.home")));
    } else {
      // We do know
      load.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
    load.getExtensionFilters().add(filter);

    File file = load.showOpenDialog(root.getScene().getWindow());

    if (file != null) {
      try {
        JAXBContext context = JAXBContext.newInstance(Diagram.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        Diagram diagram = (Diagram) jaxbUnmarshaller.unmarshal(file);
        app.getCanvasWithOptionalDotGrid().getDiagramProperty().set(diagram);
        app.getCanvasWithOptionalDotGrid().layoutChildren();
      } catch (JAXBException e) {
        logger.error("Error unmarshalling loaded file: " + file.getAbsolutePath(), e);
      }
    }
  }

}
