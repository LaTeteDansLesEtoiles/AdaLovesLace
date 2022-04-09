package org.alienlabs.adaloveslace.view;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.alienlabs.adaloveslace.App.*;
import static org.alienlabs.adaloveslace.view.QuitButton.QUIT_APP;
import static org.alienlabs.adaloveslace.view.ShowHideGridButton.SHOW_HIDE_GRID_BUTTON_NAME;

public class ToolboxWindow {

  private List<String> resourceFiles;

  public static final double TOOLBOX_WINDOW_X             = 750d;
  public static final double TOOLBOX_WINDOW_WIDTH         = 200d;
  public static final double TILE_HEIGHT                  = 50d;
  public static final double TILE_PADDING                 = 15d;
  public static final double VERTICAL_PADDING             = 50d;
  public static final double VERTICAL_GAP_BETWEEN_BUTTONS = 10d;

  private static final Logger logger = LoggerFactory.getLogger(ToolboxWindow.class);

  public Diagram createToolboxPane(TilePane toolboxPane, Object classpathBase, String resourcesPath, App app, final Diagram diagram) {
    this.resourceFiles = loadPatternsResourcesFiles(resourcesPath, classpathBase);

    for (int i = 0; i < this.resourceFiles.size(); i++) {
      String filename = this.resourceFiles.get(i);

      try (FileInputStream fis = new FileInputStream(filename)) {
        String label = new File(filename).getName();
        org.alienlabs.adaloveslace.business.model.Pattern pattern = new org.alienlabs.adaloveslace.business.model.Pattern(filename);

        if (i == 0) {
          diagram.setCurrentPattern(pattern);
        }

        Button button = new PatternButton(app, label, new ImageView(new Image(fis)), pattern);
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
    List<String> classpathResourceFiles = new FileUtil().getResources(classpathBase, Pattern.compile(resourcesPath));
    Collections.sort(classpathResourceFiles);

    return classpathResourceFiles;
  }

  public void createToolboxStage(Stage toolboxStage, TilePane showHideGridPanePane, TilePane toolboxPane) {
    Scene toolboxScene = new Scene(toolboxPane, TOOLBOX_WINDOW_WIDTH,
      this.resourceFiles.size() * (TILE_HEIGHT + TILE_PADDING) + VERTICAL_PADDING + VERTICAL_GAP_BETWEEN_BUTTONS);

    toolboxStage.setTitle(TOOLBOX_TITLE);
    toolboxStage.setX(TOOLBOX_WINDOW_X);
    toolboxStage.setY(MAIN_WINDOW_Y);
    toolboxStage.setScene(toolboxScene);
    toolboxStage.show();

    toolboxPane.getChildren().addAll(showHideGridPanePane);
  }

  public TilePane createShowHideGridAndQuitButtons(App app) {
    TilePane showHideGridPanePane  = new TilePane(Orientation.HORIZONTAL);
    showHideGridPanePane.setAlignment(Pos.CENTER);
    showHideGridPanePane.setPrefColumns(1);
    showHideGridPanePane.setVgap(VERTICAL_GAP_BETWEEN_BUTTONS);

    ShowHideGridButton showHideGridButton = new ShowHideGridButton(SHOW_HIDE_GRID_BUTTON_NAME, app);
    showHideGridPanePane.getChildren().add(showHideGridButton);

    QuitButton showQuitButton = new QuitButton(QUIT_APP);
    showHideGridPanePane.getChildren().add(showQuitButton);

    return showHideGridPanePane;
  }

  public List<String> getResourceFiles() {
    return new ArrayList<>(this.resourceFiles);
  }

}
