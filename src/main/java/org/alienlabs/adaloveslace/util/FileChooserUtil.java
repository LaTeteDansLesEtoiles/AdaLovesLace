package org.alienlabs.adaloveslace.util;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;

import java.io.File;

import static org.alienlabs.adaloveslace.App.USER_HOME;
import static org.alienlabs.adaloveslace.util.FileUtil.CLASSPATH_RESOURCES_PATH;
import static org.alienlabs.adaloveslace.view.component.button.toolboxwindow.grid.AddKnotButton.*;

public class FileChooserUtil {

  public FileChooserUtil() {
    // Nothing to do here, that's just to avoid an all-static class
  }

  public FileChooser getFileChooser(String dialogTitle, String file, String folderSavePath,
                                            String filteredFiles, String fileFilter) {
    FileChooser export = new FileChooser();
    export.setTitle(dialogTitle);

    Preferences preferences = new Preferences();
    File xmlFile      = preferences.getPathWithFileValue(file);
    File xmlFilePath  = preferences.getPathWithFileValue(folderSavePath);

    if (xmlFilePath == null || xmlFile == null || !xmlFilePath.canRead() || !xmlFile.canRead()) {
      // We don't know from where to export
      export.setInitialDirectory(new File(System.getProperty(USER_HOME)));
    } else {
      // We do know
      export.setInitialDirectory(xmlFilePath);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(filteredFiles, fileFilter);
    export.getExtensionFilters().add(filter);
    return export;
  }

  public FileChooser getAddKnotFileChooser(String dialogTitle, String knotFolderPath,
                                            String filteredFiles) {
    FileChooser addKnot = new FileChooser();
    addKnot.setTitle(dialogTitle);

    Preferences preferences = new Preferences();
    File folder  = preferences.getPathWithFileValue(knotFolderPath);

    if (folder == null || !folder.canRead()) {
      // We don't know from where to export
      addKnot.setInitialDirectory(new File(System.getProperty(USER_HOME)));
    } else {
      // We do know
        addKnot.setInitialDirectory(folder);
    }

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(filteredFiles, KNOT_FILES_FILTER1, KNOT_FILES_FILTER2, KNOT_FILES_FILTER3);
    addKnot.getExtensionFilters().add(filter);
    return addKnot;
  }

  public void restartGui(App app, Diagram... diagram) {
    app.getPrimaryStage().close();

    if (diagram.length > 0) {
      app.setDiagram(diagram[0]);
      app.getOptionalDotGrid().setDiagram(diagram[0]);
    }

    app.showMainWindow(
            app.getResizes().getMainWindowWidth(),
            app.getResizes().getMainWindowHeight(),
            app.getResizes().getGridWidth(),
            app.getResizes().getGridHeight(),
            app.getPrimaryStage(),
            diagram.length > 0 ? diagram[0] : app.getOptionalDotGrid().getDiagram()
    );
    app.getToolboxStage().close();
    app.showToolboxWindow(app, app, CLASSPATH_RESOURCES_PATH);
    // GeometryStage n'existe plus
    // app.getGeometryStage().close();
    // GeometryWindow et StateWindow sont maintenant intégrées dans ToolboxWindow
    // app.showGeometryWindow(app);
    // app.getStateStage().close();
    // app.showStateWindow(app);
    app.getPrimaryStage().requestFocus();
  }

}
