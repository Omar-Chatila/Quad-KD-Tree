module application.quadkdtrees {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens application to javafx.fxml;
    exports application;
    exports controller;
    exports model.kdTree;
    exports model.quadTree;
    exports model;
    opens controller to javafx.fxml;
}