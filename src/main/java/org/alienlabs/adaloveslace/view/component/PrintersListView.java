package org.alienlabs.adaloveslace.view.component;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class PrintersListView extends ListView<String> {

    public PrintersListView() {
        super();

        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getStyleClass().add("styled-list-view");
        this.setPrefHeight(150);

        this.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty || item == null ? null : item);
                    this.getStyleClass().remove("selected-line");
                    if (isSelected() && !empty && item != null) {
                        this.getStyleClass().add("selected-line");
                    }
                }
            };

            // Ensure the class updates when selection changes
            cell.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    if (!cell.getStyleClass().contains("selected-line")) {
                        cell.getStyleClass().add("selected-line");
                    }
                } else {
                    cell.getStyleClass().remove("selected-line");
                }
            });

            return cell;
        });
    }
}
