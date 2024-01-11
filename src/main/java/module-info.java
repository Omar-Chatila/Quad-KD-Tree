module application.quadkdtrees {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires com.jfoenix;
    requires jfxtras.common;
    requires jfxtras.labs;
    requires java.sql;
    requires java.desktop;
    requires junit;
    requires org.testng;
    requires jmh.core;


    opens application to javafx.fxml;
    exports application;
    exports controller;
    exports model.kdTree;
    exports model.quadTree;
    exports model;
    exports tests;
    exports util;
    opens controller to javafx.fxml;
    opens tests to org.testng;
}