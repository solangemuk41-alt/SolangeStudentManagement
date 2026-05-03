module org.example.studentmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    exports org.example.studentmanagement;
    exports org.example.studentmanagement.ui;
    exports org.example.studentmanagement.model;

    opens org.example.studentmanagement to javafx.fxml;
    opens org.example.studentmanagement.ui to javafx.fxml;
}
