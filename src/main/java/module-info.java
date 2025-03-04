module com.example.battleshipsdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    opens com.example.battleshipsdemo to javafx.fxml;
    exports com.example.battleshipsdemo;
}