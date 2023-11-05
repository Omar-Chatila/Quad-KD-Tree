module application.quadkdtrees {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires jfxtras.common;
    requires jfxtras.labs;


    opens application to javafx.fxml;
    exports application;
    exports controller;
    exports model.kdTree;
    exports model.quadTree;
    exports model;
    exports util;
    opens controller to javafx.fxml;
}