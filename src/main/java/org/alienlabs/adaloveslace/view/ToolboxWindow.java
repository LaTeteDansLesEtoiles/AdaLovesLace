package org.alienlabs.adaloveslace.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.alienlabs.adaloveslace.App.TOOLBOX_BUTTON;
import static org.alienlabs.adaloveslace.App.TOOLBOX_TITLE;

public class ToolboxWindow {

  private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);

  public Diagram createToolboxPane(TilePane toolboxPane, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
    List<String> resourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);

    for (int i = 0; i < resourceFiles.size(); i++) {
      String filename = resourceFiles.get(i);
      Button button;

      try (FileInputStream fis = new FileInputStream(filename)) {
        String name = new File(filename).getName();
        org.alienlabs.adaloveslace.business.model.Pattern pattern = new org.alienlabs.adaloveslace.business.model.Pattern(filename);

        if (i == 0) {
          diagram.setCurrentPattern(pattern);
        }

        button = new PatternButton(app, name, new ImageView(new Image(fis)), pattern);
        button.setId(TOOLBOX_BUTTON + (i + 1));
        toolboxPane.getChildren().addAll(button);

        diagram.addPattern(pattern);
      } catch (IOException e) {
        logger.error("Exception reading toolbox file!", e);
      }
    }

    return diagram;
  }

  /**
   * Gets sorted (by String's default sort) Pattern list resources from classpath.
   *
   * @param resourcesPath the classpath resource pattern to load
   * @param classpathBase the main app, needed for tests
   * @return the sorted Pattern list from classpath, by name
   */
  public List<String> loadPatternsResourcesFiles(String resourcesPath, Object classpathBase) {
    List<String> resourceFiles = new FileUtil().getResources(classpathBase, Pattern.compile(resourcesPath));
    Collections.sort(resourceFiles);

    return resourceFiles;
  }

  public void createToolboxStage(Stage toolboxStage, TilePane toolboxPane) {
    Scene toolboxScene = new Scene(toolboxPane, 150, 400);
    toolboxStage.setTitle(TOOLBOX_TITLE);
    toolboxStage.setX(1400d);
    toolboxStage.setY(175d);
    toolboxStage.setScene(toolboxScene);
    toolboxStage.show();
  }

}
