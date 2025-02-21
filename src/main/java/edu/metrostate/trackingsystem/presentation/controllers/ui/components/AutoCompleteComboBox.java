package edu.metrostate.trackingsystem.presentation.controllers.ui.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

/*
* I found out that ControlFX supports that, but I couldn't
* get it to work. So I had to implement it myself.
* Apparently is a version issue, if you can figure that out, feel free to use
* their solution instead.
*/
public class AutoCompleteComboBox {

    /**
     * Enables auto-complete feature on a JavaFX ComboBox.
     */
    public static <T> void enableAutoComplete(ComboBox<T> comboBox) {
        ObservableList<T> originalItems = FXCollections.observableArrayList(comboBox.getItems());

        comboBox.setEditable(true);

        comboBox.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            String input = comboBox.getEditor().getText();

            if (input == null || input.isEmpty()) {
                comboBox.setItems(originalItems);
                comboBox.hide();
                return;
            }

            ObservableList<T> filteredItems = FXCollections.observableArrayList();
            for (T item : originalItems) {
                if (item.toString().toLowerCase().contains(input.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            comboBox.setItems(filteredItems);
            comboBox.show();
        });

        comboBox.setOnHidden(event -> {
            T selectedItem = comboBox.getSelectionModel().getSelectedItem();
            comboBox.setItems(originalItems);
            comboBox.getSelectionModel().select(selectedItem);
        });
    }
}