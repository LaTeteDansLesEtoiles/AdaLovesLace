package org.alienlabs.adaloveslace;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.alienlabs.adaloveslace.util.FileUtil;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.DotGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * JavaFX App
 */
public class App extends Application {

  @Override
  public void start(Stage primaryStage) {
    showMainWindow(primaryStage);
    showToolboxWindow();
  }

  private void showMainWindow(Stage primaryStage) {
    StackPane grid            = new StackPane(new DotGrid());
    grid.setAlignment(Pos.TOP_LEFT);

    var javafxVersion = SystemInfo.javafxVersion();
    var javaVersion   = SystemInfo.javaVersion();

    TilePane footer           = new TilePane(Orientation.VERTICAL);
    footer.getChildren().add(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().add(new Label("© 2022 AlienLabs"));
    footer.getChildren().add(new Label("This is Free Software under GPL license"));
    footer.getChildren().add(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
    footer.setAlignment(Pos.BOTTOM_CENTER);

    GridPane root             = new GridPane();
    GridPane.setConstraints(grid, 0, 0);
    GridPane.setConstraints(footer, 0, 1);
    root.getChildren().addAll(grid, footer);

    var scene = new Scene(root, 800d, 720d);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ada Loves Lace");
    primaryStage.show();
  }

  private void showToolboxWindow() {
    Stage toolboxStage = new Stage(StageStyle.DECORATED);

    TilePane toolboxPane = new TilePane(Orientation.VERTICAL);
    toolboxPane.setAlignment(Pos.TOP_CENTER);

    String ps = File.separator;
    List<String> resourceFiles = FileUtil.getResources(Pattern.compile(".*org" + ps + "alienlabs" + ps + "adaloveslace" + ps + ".*.jpg"));

    for (int i = 0; i < resourceFiles.size(); i++) {
      String filename = resourceFiles.get(i);
      Button button = null;

      try (FileInputStream fis = new FileInputStream(filename)) {
        button = new Button(Integer.toString(i + 1), new ImageView(new Image(fis)));
        toolboxPane.getChildren().add(button);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    var toolboxScene = new Scene(toolboxPane, 150, 400);
    toolboxStage.setTitle("Toolbox");
    toolboxStage.setX(1400d);
    toolboxStage.setY(175d);
    toolboxStage.setScene(toolboxScene);
    toolboxStage.show();
  }

  public static void main(String[] args) {
    launch();
  }

}
