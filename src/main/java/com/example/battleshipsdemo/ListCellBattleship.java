package com.example.battleshipsdemo;

import javafx.scene.control.ListCell;

public class ListCellBattleship extends ListCell<Battleship> {
    @Override
    protected void updateItem(Battleship ship, boolean empty) {
        super.updateItem(ship, empty);

        if (empty || ship == null) {
            setText(null);  // If the item is empty or null, set the text to null
        } else {
            setText(ship.getName());  // Display the name of the ship
        }
    }
}
