package org.alienlabs.adaloveslace.functionaltest.domain.enumeration;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.alienlabs.adaloveslace.domain.enumeration.GridType;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exercises domain enums through JavaFX controls as a user would (selection in a {@link ComboBox}).
 */
@Tag("functional")
@ExtendWith(ApplicationExtension.class)
class EnumerationComboBoxSelectionFunctionalTest {

  private ComboBox<GridType> gridTypeCombo;
  private ComboBox<Technique> techniqueCombo;

  @Start
  void start(Stage stage) {
    gridTypeCombo = new ComboBox<>(FXCollections.observableArrayList(GridType.values()));
    gridTypeCombo.setId("grid-type-combo");

    techniqueCombo = new ComboBox<>(FXCollections.observableArrayList(Technique.values()));
    techniqueCombo.setId("technique-combo");

    VBox root = new VBox(12, gridTypeCombo, techniqueCombo);
    stage.setScene(new Scene(root, 320, 200));
    stage.show();
  }

  @Test
  void grid_type_combo_box_selects_enum_constant(FxRobot robot) {
    robot.clickOn("#grid-type-combo");
    robot.clickOn(GridType.HIDDEN.name());
    robot.interact(() -> assertEquals(GridType.HIDDEN, gridTypeCombo.getValue()));
  }

  @Test
  void technique_combo_box_selects_enum_constant(FxRobot robot) {
    robot.clickOn("#technique-combo");
    robot.clickOn(Technique.EMBROIDERY.name());
    robot.interact(() -> assertEquals(Technique.EMBROIDERY, techniqueCombo.getValue()));
  }
}
