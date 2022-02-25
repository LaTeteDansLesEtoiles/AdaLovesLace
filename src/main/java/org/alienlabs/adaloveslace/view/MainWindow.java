package org.alienlabs.adaloveslace.view;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow {

  private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

  public void createMenuBar(GridPane root) {
    MenuBar menuBar = new MenuBar();
    Menu menu = new Menu("File");
    MenuItem menuItem = new MenuItem("Quit");
    menuItem.setOnAction(actionEvent -> onQuitAction());
    menuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
    menu.getItems().add(menuItem);
    menuBar.getMenus().add(menu);
    VBox vBox = new VBox(menuBar); //Gives vertical box
    root.getChildren().add(vBox);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    TilePane footer           = new TilePane(Orientation.VERTICAL);
    footer.getChildren().add(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().add(new Label("© 2022 AlienLabs"));
    footer.getChildren().add(new Label("This is Free Software under GPL license"));
    footer.getChildren().add(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
    footer.setAlignment(Pos.BOTTOM_CENTER);
    return footer;
  }

  public GridPane createGridPane(StackPane grid, TilePane footer) {
    GridPane root             = new GridPane();
    GridPane.setConstraints(grid, 0, 0);
    GridPane.setConstraints(footer, 0, 1);
    root.getChildren().addAll(grid, footer);
    return root;
  }

  public StackPane createGrid() {
    StackPane grid            = new StackPane(new DotGrid());
    grid.setAlignment(Pos.TOP_LEFT);
    return grid;
  }

  private void onQuitAction() {
    logger.info("Exiting app");
    System.exit(0);
  }

}
