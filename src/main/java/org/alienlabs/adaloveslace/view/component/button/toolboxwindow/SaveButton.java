package org.alienlabs.adaloveslace.view.component.button.toolboxwindow;

import javafx.stage.FileChooser;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Step;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.Preferences;
import org.alienlabs.adaloveslace.view.component.button.ImageButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.alienlabs.adaloveslace.App.USER_HOME;
import static org.alienlabs.adaloveslace.util.Preferences.LACE_FILE_FOLDER_SAVE_PATH;
import static org.alienlabs.adaloveslace.util.Preferences.SAVED_LACE_FILE;

public class SaveButton extends ImageButton {

    public static final String SAVE_FILE_DIALOG_TITLE   = "Save diagram as";
    public static final String DIAGRAM_FILES            = ".lace files (*.lace)";
    public static final String DIAGRAM_FILE_FILTER      = "*.lace";

    private static final Logger logger                  = LoggerFactory.getLogger(SaveButton.class);

    public SaveButton(App app, String buttonLabel) {
        super(buttonLabel);
        this.setOnMouseClicked(event -> onSaveAction(app));
        buildButtonImage("save.png");
    }

    public static void onSaveAction(App app) {
        logger.debug("Saving file");

        File file;

        Preferences preferences = new Preferences();
        File laceFilePath   = preferences.getPathWithFileValue(SAVED_LACE_FILE);
        File laceFileParent = preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH);

        if (laceFileParent == null || laceFilePath == null ||
            !laceFileParent.exists() || !laceFilePath.exists() ||
            !laceFileParent.isDirectory() || !laceFileParent.canWrite() || !laceFilePath.canWrite()) {
            // Save as anyway, since we don't know where to save
            FileChooser saveAs = new FileChooser();
            saveAs.setTitle(SAVE_FILE_DIALOG_TITLE);

            File userHome = new File(System.getProperty(USER_HOME));
            saveAs.setInitialDirectory(userHome);
            preferences.setPathWithFileValue(userHome, LACE_FILE_FOLDER_SAVE_PATH);

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(DIAGRAM_FILES, DIAGRAM_FILE_FILTER);
            saveAs.getExtensionFilters().add(filter);
            saveAs.setInitialDirectory(preferences.getPathWithFileValue(LACE_FILE_FOLDER_SAVE_PATH));

            file = saveAs.showSaveDialog(app.getScene().getWindow());
        } else {
            // We know where to save
            file = new File(laceFilePath.getAbsolutePath());
        }

        if (file != null) {
            // Save
            preferences.setPathWithFileValue(file, SAVED_LACE_FILE);
            preferences.setPathWithFileValue(file.getParentFile(), LACE_FILE_FOLDER_SAVE_PATH);

            if (file.exists()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    logger.error("Error deleting file during diagram save!", e);
                }
            }

            for (Step step : app.getOptionalDotGrid().getDiagram().getAllSteps()) {
                step.getDisplayedKnots().removeAll(
                        step.getSelectedKnots()
                );
            }

            new FileUtil(app).saveFile(file,
                    app.getOptionalDotGrid().getDiagram(),
                    app.getOptionalDotGrid().getDiagram().getCurrentStepIndex()
            );
        }
    }

}
