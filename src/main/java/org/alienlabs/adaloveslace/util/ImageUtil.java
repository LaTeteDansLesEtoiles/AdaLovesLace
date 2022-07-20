package org.alienlabs.adaloveslace.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.DiagramDTO;
import org.alienlabs.adaloveslace.business.model.Language;
import org.alienlabs.adaloveslace.business.model.SubTechnique;
import org.alienlabs.adaloveslace.business.model.Technique;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;

public class ImageUtil {

  private final App app;

  private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);


  public ImageUtil(App app) {
    this.app = app;
  }

  public void buildWritableImageWithoutTechnicalElements(String pathname) {
    this.hideTechnicalElementsFromRootGroup();

    buildWritableImage(pathname);

    this.showTechnicalElementsFromRootGroup();
    logger.info("Snapshot done!");
  }

  public WritableImage buildWritableImageWithTechnicalElements(String pathname) {
    WritableImage snapshot = buildWritableImage(pathname);
    logger.info("Snapshot done!");

    return snapshot;
  }

  public DiagramDTO getDiagram() throws IOException {
    File laceFilePath = new File(APP_FOLDER_IN_USER_HOME + "my diagram.lace");

    return new DiagramDTO().uuid(UUID.randomUUID()).name("my diagram").
      preview("my diagram.lace").technique(Technique.LACE).subTechnique(SubTechnique.TATTING_LACE).
      language(Language.FRENCH).diagram(Files.readAllBytes(new FileUtil().saveFile(this.app, laceFilePath).toPath())).
      diagramContentType("application/lace").username("user").
      clientId(UUID.fromString("80f9f2d3-327b-4b58-9ec9-53121d75a8f3")).clientSecret(UUID.fromString("df655bfe-b599-4027-aed1-1cf03393c0f4"));
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

  public void hideTechnicalElementsFromRootGroup() {
    manageTechnicalElementsFromRootGroup(false);
  }

  public void showTechnicalElementsFromRootGroup() {
    manageTechnicalElementsFromRootGroup(true);
  }

  private void manageTechnicalElementsFromRootGroup(boolean show) {
    this.app.getMainWindow().getMenuBar().setVisible(show);
    this.app.getMainWindow().getFooter().setVisible(show);
  }

}
