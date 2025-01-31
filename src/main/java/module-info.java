module com.example.battleshipsdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.battleshipsdemo to javafx.fxml;
    exports com.example.battleshipsdemo;
}