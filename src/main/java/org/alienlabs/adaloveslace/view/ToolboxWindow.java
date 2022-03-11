package org.alienlabs.adaloveslace.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
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

  public Diagram createToolboxPane(TilePane toolboxPane, String resourcesPath, Object app, Diagram diagram) {
    List<String> resourceFiles = loadPatternsResourcesFiles(resourcesPath, app);

    for (int i = 0; i < resourceFiles.size(); i++) {
      String filename = resourceFiles.get(i);
      Button button;

      try (FileInputStream fis = new FileInputStream(filename)) {
        String name = new File(filename).getName();
        button = new Button(name, new ImageView(new Image(fis)));
        button.setId(TOOLBOX_BUTTON + (i + 1));

        toolboxPane.getChildren().addAll(button);

        diagram.addPattern(new org.alienlabs.adaloveslace.business.model.Pattern(filename));
      } catch (IOException e) {
        logger.error("Exception reading toolbox file!", e);
      }
    }

    return diagram;
  }

  public List<String> loadPatternsResourcesFiles(String resourcesPath, Object app) {
    List<String> resourceFiles = new FileUtil().getResources(app, Pattern.compile(resourcesPath));
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
