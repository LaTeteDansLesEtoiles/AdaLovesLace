package org.alienlabs.adaloveslace;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.alienlabs.adaloveslace.view.DotGrid;

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
    footer.getChildren().add(new Label("Ada Loves Lace - A lace pattern creation software"));
    footer.getChildren().add(new Label("© 2022 AlienLabs"));
    footer.getChildren().add(new Label("This is Free Software under GPL license"));
    footer.getChildren().add(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
    footer.setAlignment(Pos.BOTTOM_LEFT);

    TilePane root             = new TilePane(Orientation.VERTICAL, grid, footer);
    root.setAlignment(Pos.BOTTOM_CENTER);

    var scene = new Scene(root, 800d, 720d);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ada Loves Lace");
    primaryStage.show();
  }

  private void showToolboxWindow() {
    Stage toolboxStage = new Stage(StageStyle.DECORATED);

    TilePane toolboxPane = new TilePane(Orientation.VERTICAL);

    Button snowflakeButton  = new Button("1", new ImageView("snowflake_small.jpg"));
    Button mandalaButton    = new Button("2", new ImageView("mandala_small.jpg"));

    toolboxPane.getChildren().add(snowflakeButton);
    toolboxPane.getChildren().add(mandalaButton);
    toolboxPane.setAlignment(Pos.TOP_CENTER);

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
