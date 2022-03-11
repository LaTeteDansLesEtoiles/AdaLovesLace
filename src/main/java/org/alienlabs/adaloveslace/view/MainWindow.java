package org.alienlabs.adaloveslace.view;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.alienlabs.adaloveslace.business.model.Diagram;
import org.alienlabs.adaloveslace.business.model.Knot;
import org.alienlabs.adaloveslace.business.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class MainWindow {

  public static final String MOUSE_CLICKED = "MOUSE_CLICKED";

  private CanvasWithOptionalDotGrid canvasWithOptionalDotGrid;
  private Diagram diagram;

  private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

  public void createMenuBar(GridPane root) {
    MenuBar menuBar = new MenuBar();
    Menu menu = new Menu("File");
    MenuItem menuItem = new MenuItem("Quit");
    menuItem.setOnAction(actionEvent -> onQuitAction());
    menuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
    menu.getItems().addAll(menuItem);
    menuBar.getMenus().addAll(menu);
    VBox vBox = new VBox(menuBar); //Gives vertical box
    root.getChildren().addAll(vBox);
  }

  public TilePane createFooter(String javafxVersion, String javaVersion) {
    TilePane footer           = new TilePane(Orientation.VERTICAL);
    footer.getChildren().addAll(new Label("Ada Loves Lace - A tatting lace patterns creation software"));
    footer.getChildren().addAll(new Label("© 2022 AlienLabs"));
    footer.getChildren().addAll(new Label("This is Free Software under GPL license"));
    footer.getChildren().addAll(new Label("JavaFX " + javafxVersion + ", running on Java " + javaVersion));
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

  public StackPane createGrid(final Diagram diagram) {
    this.diagram = diagram;
    this.canvasWithOptionalDotGrid = new CanvasWithOptionalDotGrid(this.diagram);
    StackPane grid = new StackPane(this.canvasWithOptionalDotGrid);
    grid.setAlignment(Pos.TOP_LEFT);
    return grid;
  }

  public void onMainWindowClicked(final GridPane root) {
    root.setOnMouseClicked(event -> {
      String eType = event.getEventType().toString();
      logger.info("Event type -> {}", eType);

      if (eType.equals(MOUSE_CLICKED)) {
        double x                = event.getX();
        double y                = event.getY();
        double yMinusTop        = y - CanvasWithOptionalDotGrid.TOP_MARGIN;
        Pattern currentPattern  = this.diagram.getCurrentPattern();

        logger.info("Coordinate X     -> {}",                 x);
        logger.info("Coordinate Y     -> {}, Y - TOP -> {}",  y, yMinusTop);
        logger.info("Current pattern  -> {}",                 currentPattern);


        try (FileInputStream fis = new FileInputStream(currentPattern.filename())) {

          this.canvasWithOptionalDotGrid.getCanvas().getGraphicsContext2D().drawImage(
            new Image(fis), x, yMinusTop);
          this.diagram.addKnot(new Knot(x, yMinusTop, currentPattern));

        } catch (IOException e) {
          logger.error("Problem with resource file!", e);
        }
      }
    });
  }

  private void onQuitAction() {
    logger.info("Exiting app");
    System.exit(0);
  }

}
