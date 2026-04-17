package org.alienlabs.adaloveslace.util;

import edu.umd.cs.findbugs.annotations.NonNull;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Picture;
import org.alienlabs.adaloveslace.domain.dto.DiagramDTO;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.Language;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.alienlabs.adaloveslace.view.component.grid.gridstrategy.ParentGridStrategy;
import org.alienlabs.adaloveslace.view.window.CreatePatternWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;

public class ImageUtil {

    public static final String NEW_PATTERN = "pattern";

    private static File lastExportedImageFile;

    public static File getLastExportedImageFile() {
        return lastExportedImageFile;
    }

    private final App app;

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);


    public ImageUtil(App app) {
        this.app = app;
    }

    public File buildFileImageWithoutTechnicalElements(String pathname) {
        boolean isGridDisplayed = app.getOptionalDotGrid().isShowHideGrid();
        this.hideTechnicalElementsFromRootGroup(false);

        File image = buildImage(pathname);

        this.showTechnicalElementsFromRootGroup(isGridDisplayed);
        logger.info("Snapshot done!");

        return image;
    }

    public WritableImage buildWritableImageWithoutTechnicalElements(String pathname) {
        boolean isGridDisplayed = app.getOptionalDotGrid().isShowHideGrid();
        this.hideTechnicalElementsFromRootGroup(false);

        WritableImage snapshot = buildWritableImage(pathname);
        logger.info("Snapshot done!");

        this.showTechnicalElementsFromRootGroup(isGridDisplayed);
        return snapshot;
    }

    public WritableImage buildWritableImageWithTechnicalElements(String pathname) {
        WritableImage snapshot = buildWritableImage(pathname);
        logger.info("Snapshot done!");

        return snapshot;
    }

    public DiagramDTO getDiagram(String diagramFilename,
                                 String username,
                                 String clientId,
                                 String clientSecret,
                                 String initialImage,
                                 List<Picture> imageList) throws IOException {
        UUID uuid         = UUID.randomUUID();
        new ImageUtil(app).
                buildFileImageWithoutTechnicalElements(
                        APP_FOLDER_IN_USER_HOME + uuid + EXPORT_IMAGE_FILE_TYPE
                );
        File laceFilePath = new File(APP_FOLDER_IN_USER_HOME + diagramFilename + LACE_FILE_EXTENSION);

        return new DiagramDTO().
                uuid(uuid).
                name(diagramFilename).
                showcase(imageList.getFirst().getShowcase()).previewContentType(EXPORT_IMAGE_CONTENT_TYPE).
                technique(Technique.LACE).
                subTechnique(SubTechnique.TATTING_LACE).
                language(Language.FRENCH).
                diagram(Base64.getEncoder().encodeToString(Files.readAllBytes(
                        new FileUtil(app).saveFile(
                                        laceFilePath,
                                        app.getOptionalDotGrid().getDiagram(),
                                        true
                                )
                                .toPath()
                        )
                )).
                diagramContentType(LACE_FILE_MIME_TYPE).
                username(username).
                clientId(UUID.fromString(clientId)).
                clientSecret(UUID.fromString(clientSecret)).
                diagramPreview(initialImage).
                previews(imageList.stream()
                        .filter(picture -> picture.getShowcase() == null || picture.getShowcase().isEmpty())
                        .map(Picture::getPicture).toList());
    }

    public WritableImage buildWritableImage(String pathname) {
        WritableImage wi = new WritableImage((int)app.getMovablePane().getWidth(),
                (int)app.getMovablePane().getHeight());
        WritableImage snapshot = app.getMovablePane().snapshot(newSnapshotParameters(), wi);

        File output = new File(pathname);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), EXPORT_IMAGE_FILE_FORMAT, output);
        } catch (IOException e) {
            logger.error("Problem writing root group image file!", e);
        }
        return snapshot;
    }

    private File buildImage(String pathname) {
        WritableImage wi = new WritableImage(
                (int)app.getMovablePane().getWidth(),
                (int)app.getMovablePane().getHeight()
        );
        WritableImage snapshot = app.getMovablePane().snapshot(newSnapshotParameters(), wi);
        lastExportedImageFile = new File(pathname);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), EXPORT_IMAGE_FILE_FORMAT, lastExportedImageFile);
        } catch (IOException e) {
            logger.error("Problem writing root group image file!", e);
        }

        return lastExportedImageFile;
    }

    private SnapshotParameters newSnapshotParameters() {
        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(
                new Rectangle2D(
                        app.getMovablePane().getLayoutX(),
                        app.getMovablePane().getLayoutY(),
                        app.getMovablePane().getWidth(),
                        app.getMovablePane().getHeight()
                )
        );
        return params;
    }

    /**
     * Rasterizes the diagram for printing: crops to visible knot bounds (plus padding) so the page is filled
     * by the drawn area, and matches {@link Pane#getScaleX()}/{@code scaleY} as print magnification.
     * <p>
     * The pane is snapshotted at scale {@code 1} using a full-pane raster and an explicit pixel crop so
     * relayout + zoom combinations stay deterministic; the result is nearest-neighbour upscaled by the saved
     * zoom factors (viewport-only snapshots at pane zoom can miss diagram pixels on some platforms).
     * Call after {@link #hideTechnicalElementsFromRootGroup(boolean)} when matching the print workflow.
     */
    public WritableImage snapshotMovablePaneForPrint() {
        Pane pane = app.getMovablePane();
        double savedScaleX = pane.getScaleX();
        double savedScaleY = pane.getScaleY();
        double zoomX = savedScaleX == 0d ? 1d : Math.abs(savedScaleX);
        double zoomY = savedScaleY == 0d ? 1d : Math.abs(savedScaleY);

        try {
            pane.setScaleX(1d);
            pane.setScaleY(1d);
            pane.applyCss();
            pane.layout();
            Diagram diagram = null;
            var mainWindow = app.getMainWindow();
            if (mainWindow != null && mainWindow.getOptionalDotGrid() != null) {
                mainWindow.getOptionalDotGrid().layoutChildren();
                diagram = mainWindow.getOptionalDotGrid().getDiagram();
            }
            Rectangle2D viewport = DiagramPrintLayout.viewportForPrint(pane, diagram, DiagramPrintLayout.DEFAULT_CONTENT_PADDING);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE);
            WritableImage fullPane = pane.snapshot(params, null);
            WritableImage logicalCrop = cropPrintSnapshotToViewport(fullPane, pane, viewport);
            if (zoomX == 1d && zoomY == 1d) {
                return logicalCrop;
            }
            return upscalePrintSnapshotNearest(logicalCrop, zoomX, zoomY);
        } finally {
            pane.setScaleX(savedScaleX);
            pane.setScaleY(savedScaleY);
        }
    }

    /**
     * Maps a layout-space print viewport onto pixel coordinates of a full-pane snapshot and copies that region.
     */
    private static WritableImage cropPrintSnapshotToViewport(WritableImage full, Pane pane, Rectangle2D viewport) {
        PixelReader reader = full.getPixelReader();
        if (reader == null) {
            return full;
        }
        double paneW = DiagramPrintLayout.effectivePaneWidth(pane);
        double paneH = DiagramPrintLayout.effectivePaneHeight(pane);
        if (paneW <= 0 || paneH <= 0) {
            return full;
        }
        double mapX = full.getWidth() / paneW;
        double mapY = full.getHeight() / paneH;
        int fw = (int) full.getWidth();
        int fh = (int) full.getHeight();
        int x0 = (int) Math.floor(viewport.getMinX() * mapX);
        int y0 = (int) Math.floor(viewport.getMinY() * mapY);
        int cw = (int) Math.ceil(viewport.getWidth() * mapX);
        int ch = (int) Math.ceil(viewport.getHeight() * mapY);
        x0 = Math.max(0, Math.min(x0, Math.max(0, fw - 1)));
        y0 = Math.max(0, Math.min(y0, Math.max(0, fh - 1)));
        cw = Math.max(1, Math.min(cw, fw - x0));
        ch = Math.max(1, Math.min(ch, fh - y0));
        WritableImage out = new WritableImage(cw, ch);
        PixelWriter writer = out.getPixelWriter();
        for (int y = 0; y < ch; y++) {
            for (int x = 0; x < cw; x++) {
                writer.setColor(x, y, reader.getColor(x0 + x, y0 + y));
            }
        }
        return out;
    }

    /**
     * Nearest-neighbour upscale in JavaFX pixel space (avoids Swing conversion quirks on some GTK runs).
     */
    private static WritableImage upscalePrintSnapshotNearest(WritableImage src, double zoomX, double zoomY) {
        PixelReader reader = src.getPixelReader();
        if (reader == null) {
            return src;
        }
        int sw = (int) src.getWidth();
        int sh = (int) src.getHeight();
        if (sw <= 0 || sh <= 0) {
            return src;
        }
        int outW = (int) Math.ceil(sw * zoomX);
        int outH = (int) Math.ceil(sh * zoomY);
        WritableImage out = new WritableImage(outW, outH);
        PixelWriter writer = out.getPixelWriter();
        for (int y = 0; y < outH; y++) {
            int sy = Math.min(sh - 1, (int) Math.floor(y / zoomY));
            for (int x = 0; x < outW; x++) {
                int sx = Math.min(sw - 1, (int) Math.floor(x / zoomX));
                writer.setColor(x, y, reader.getColor(sx, sy));
            }
        }
        return out;
    }

    public void buildImage(double xMin, double yMin, double wLog, double hLog) {
        if (app.getOptionalDotGrid().isShowHideGrid()) {
            ParentGridStrategy.setGridHasBeenDrawn(false);
            app.getOptionalDotGrid().layoutChildren();
        }

        createPattern(xMin, yMin, wLog, hLog);
    }

    private void createPattern(double xMin, double yMin, double wLog, double hLog) {
        Platform.runLater(() -> {
            logger.info(
                    "Create Pattern => ImageView: X= {}, Y= {}, width= {}, height= {}",
                    xMin,
                    yMin,
                    wLog,
                    hLog
            );

            ParentGridStrategy parentGridStrategy = new ParentGridStrategy(app, app.getOptionalDotGrid().getGridPane());
            GridType before = parentGridStrategy.getCurrentGridType();
            ParentGridStrategy.hideGrid();

            double scale = app.getMovablePane().getScaleX();
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            WritableImage fullSnap = app.getMovablePane().snapshot(sp, null);
            BufferedImage buffered = SwingFXUtils.fromFXImage(fullSnap, null);

            int cropX = (int) Math.round(xMin * scale);
            int cropY = (int) Math.round(yMin * scale);
            int cropW = (int) Math.round(wLog * scale);
            int cropH = (int) Math.round(hLog * scale);

            try {
                BufferedImage croppedBI = buffered.getSubimage(
                        getX(cropX, cropW, fullSnap),
                        getY(cropY, cropH, fullSnap),
                        cropW,
                        cropH
                );
                File previewFile = new File(
                        APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + File.separator +
                        NEW_PATTERN + UUID.randomUUID().toString().substring(0, 8) + EXPORT_IMAGE_FILE_TYPE
                );
                ImageIO.write(
                        croppedBI,
                        EXPORT_IMAGE_FILE_FORMAT,
                        previewFile
                );

                new CreatePatternWindow(app, previewFile);
            } catch (Exception e) {
                logger.error("Problem writing new pattern image file!", e);
            }

            parentGridStrategy.setCurrentGridType(before);
            ParentGridStrategy.setGridHasBeenDrawn(false);
            app.getOptionalDotGrid().layoutChildren();
        });
    }

    private int getX(int cropX, int cropW, WritableImage fullSnap) {
        if (cropX + cropW < 0) {
            return 0;
        }

        return cropX + cropW >= fullSnap.getWidth() ? (int) fullSnap.getWidth() - cropW : cropX;
    }

    private int getY(int cropY, int cropH, WritableImage fullSnap) {
        if (cropY + cropH < 0) {
            return 0;
        }

        return cropY + cropH >= fullSnap.getHeight() ? (int) fullSnap.getHeight() - cropH : cropY;
    }

    public void hideTechnicalElementsFromRootGroup(boolean showGrid) {
        manageTechnicalElementsFromRootGroup(false, showGrid);
    }

    public void showTechnicalElementsFromRootGroup(boolean showGrid) {
        manageTechnicalElementsFromRootGroup(true, showGrid);
    }

    private void manageTechnicalElementsFromRootGroup(boolean showElements, boolean showGrid) {
        if (!showElements) {
            new NodeUtil().clearTechnicalElements(app);
        }

        if (showGrid) {
            ParentGridStrategy.setGridHasBeenDrawn(false);
            app.getOptionalDotGrid().layoutChildren();
        } else {
            ParentGridStrategy.hideGrid();
        }
    }

    public void getImageView(String pathname, ButtonBase button, boolean isSelected) {
        Image buttonImage = new Image(getClass()
                        .getResource(ASSETS_DIRECTORY + pathname).toExternalForm());

        ImageView buttonImageView  = new ImageView(buttonImage);
        buttonImageView.setFitHeight(ICON_SIZE);
        buttonImageView.setPreserveRatio(true);
        button.setGraphic(buttonImageView);

        if (isSelected && button instanceof ToggleButton toggleButton) {
            toggleButton.setSelected(true);
        }
    }

    public void backupKnots() {
        Path root = Paths.get(APP_FOLDER_IN_USER_HOME);
        Path knotsDirectory = root.resolve(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME );
        Path backupDirectory = root.resolve(APP_FOLDER_IN_USER_HOME + BACKUP_DIRECTORY_NAME );

        try {
            createBackupDirectory(backupDirectory);

            // Parcourt l'arborescence du dossier source
            Files.walkFileTree(knotsDirectory, new SimpleFileVisitor<>() {

                @Override
                @NonNull
                public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs) throws IOException {
                    Path leaf = file.getFileName();
                    if (leaf == null) {
                        return FileVisitResult.CONTINUE;
                    }
                    String filename = leaf.toString().toLowerCase(Locale.ROOT);
                    moveKnot(file, filename, backupDirectory, knotsDirectory);

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error during file backup!", e);
        }
    }

    private void moveKnot(Path file, String filename, Path backupDirectory, Path knotsDirectory) throws IOException {
        if (isCorrectFileType(filename)) {
            Path destination = backupDirectory.resolve(knotsDirectory.relativize(file));
            Path parent = destination.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Copied: {} -> {}", file, destination);
        }
    }

    private boolean isCorrectFileType(String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png");
    }

    private void createBackupDirectory(Path targetDir) throws IOException {
        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
        }
    }

    public String imageToPngString(Image fxImage) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

}
