package org.alienlabs.adaloveslace.util;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.*;
import org.alienlabs.adaloveslace.view.window.CreatePatternWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.UUID;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;

public class ImageUtil {

    public static final String NEW_PATTERN = "pattern";

    public static final File OUTPUT = new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator +
        NEW_PATTERN + UUID.randomUUID().toString().substring(0, 8) + EXPORT_IMAGE_FILE_TYPE);

    public static File PATH_NAME;

    private final App app;

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);


    public ImageUtil(App app) {
        this.app = app;
    }

    public File buildWritableImageWithoutTechnicalElements(String pathname) {
        this.hideTechnicalElementsFromRootGroup();

        File image = buildImage(pathname);

        this.showTechnicalElementsFromRootGroup();
        logger.debug("Snapshot done!");

        return image;
    }

    public WritableImage buildWritableImageWithTechnicalElements(String pathname) {
        WritableImage snapshot = buildWritableImage(pathname);
        logger.debug("Snapshot done!");

        return snapshot;
    }

    public DiagramDTO getDiagram(String diagramFilename, String username, String clientId, String clientSecret) throws IOException {
        UUID uuid         = UUID.randomUUID();
        new ImageUtil(app).buildWritableImageWithoutTechnicalElements(
                APP_FOLDER_IN_USER_HOME + uuid + EXPORT_IMAGE_FILE_TYPE);
        File laceFilePath = new File(APP_FOLDER_IN_USER_HOME + diagramFilename + LACE_FILE_EXTENSION);

        File previewFile  = ImageUtil.PATH_NAME;
        DiagramDTO diagramDTO = new DiagramDTO().uuid(uuid).name(diagramFilename).
                preview(Files.readAllBytes(previewFile.toPath())).previewContentType(EXPORT_IMAGE_CONTENT_TYPE).
                technique(Technique.LACE).subTechnique(SubTechnique.TATTING_LACE).
                language(Language.FRENCH).diagram(Files.readAllBytes(
                        new FileUtil(app).saveFile(
                                laceFilePath,
                                new Diagram(app.getOptionalDotGrid().getDiagram(),
                                        app),
                                app.getOptionalDotGrid().getDiagram().getCurrentStepIndex()
                        ).toPath())).
                diagramContentType(LACE_FILE_MIME_TYPE).username(username).
                clientId(UUID.fromString(clientId)).clientSecret(UUID.fromString(clientSecret));

        Files.delete(laceFilePath.toPath());
        Files.delete(previewFile.toPath());

        return diagramDTO;
    }

    private WritableImage  buildWritableImage(String pathname) {
        WritableImage wi = new WritableImage(Double.valueOf(app.getPrimaryStage().getX() + GRID_WIDTH).intValue(),
            Double.valueOf(app.getPrimaryStage().getY() + GRID_HEIGHT).intValue());
        WritableImage snapshot = app.getRoot().snapshot(new SnapshotParameters(), wi);

        File output = new File(pathname);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), EXPORT_IMAGE_FILE_FORMAT, output);
        } catch (IOException e) {
            logger.error("Problem writing root group image file!", e);
        }
        return snapshot;
    }

    private File buildImage(String pathname) {
        WritableImage wi = new WritableImage(Double.valueOf(app.getPrimaryStage().getX() + GRID_WIDTH).intValue(),
            Double.valueOf(app.getPrimaryStage().getY() + GRID_HEIGHT).intValue());
        WritableImage snapshot = app.getRoot().snapshot(new SnapshotParameters(), wi);

        PATH_NAME = new File(pathname);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), EXPORT_IMAGE_FILE_FORMAT, PATH_NAME);
        } catch (IOException e) {
            logger.error("Problem writing root group image file!", e);
        }

    return PATH_NAME;
    }

    public void buildImage(double x, double y, double width, double height) {
        if (app.getOptionalDotGrid().isShowHideGrid()) {
            app.getOptionalDotGrid().setShowHideGrid(false);
            app.getOptionalDotGrid().setGridNeedsToBeRedrawn(true);
            app.getOptionalDotGrid().layoutChildren();
        }

        Platform.runLater(() -> {
            try {
                WritableImage snapshot = app.getPrimaryStage().getScene().snapshot(null);
                WritableImage croppedImage = new WritableImage(snapshot.getPixelReader(),
                    Double.valueOf(x).intValue(),
                    Double.valueOf(y).intValue(),
                    Double.valueOf(width).intValue(),
                    Double.valueOf(height).intValue());
                ImageIO.write(SwingFXUtils.fromFXImage(croppedImage, null), EXPORT_IMAGE_FILE_FORMAT, OUTPUT);
            } catch (IOException e) {
                logger.error("Problem writing new pattern image file!", e);
            }

            app.getOptionalDotGrid().setShowHideGrid(true);
            app.getOptionalDotGrid().setGridNeedsToBeRedrawn(true);
            app.getOptionalDotGrid().layoutChildren();

            new CreatePatternWindow(app);
        });
    }

    public void hideTechnicalElementsFromRootGroup() {
        manageTechnicalElementsFromRootGroup(false);
    }

    public void showTechnicalElementsFromRootGroup() {
        manageTechnicalElementsFromRootGroup(true);
    }

    private void manageTechnicalElementsFromRootGroup(boolean show) {
        this.app.getMainWindow().getMenuBar().setVisible(show);
        this.app.getMainWindow().getFooter().setVisible(show);

        if (!show) {
            app.getOptionalDotGrid().clearSelections();
            app.getOptionalDotGrid().clearAllGuideLines();
            app.getOptionalDotGrid().clearHovered();
        }
    }

    public void getImageView(String pathname, ButtonBase button, boolean isSelected) {
        try {
            Image buttonImage = new Image(ClassLoader.getSystemResource(ASSETS_DIRECTORY + pathname) != null ?
                    ClassLoader.getSystemResource(ASSETS_DIRECTORY + pathname).toURI().toURL().toExternalForm() :
                    new File(ASSETS_DIRECTORY + pathname).toURI().toURL().toExternalForm());

            ImageView buttonImageView  = new ImageView(buttonImage);
            buttonImageView.setFitHeight(ICON_SIZE);
            buttonImageView.setPreserveRatio(true);
            button.setGraphic(buttonImageView);

            if (isSelected) {
                ((ToggleButton)button).setSelected(true);
            }
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error("Error loading button image!", e);
        }
    }

}
