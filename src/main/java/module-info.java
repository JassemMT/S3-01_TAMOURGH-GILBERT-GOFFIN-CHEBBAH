module com.example.trello {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.trello to javafx.fxml;
    exports com.example.trello;
}