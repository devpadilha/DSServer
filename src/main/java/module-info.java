module augustopadilha.serverdistributedsystems {
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.codec;
    requires java.sql;
    requires jjwt.api;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens augustopadilha.serverdistributedsystems to javafx.fxml;
    opens augustopadilha.serverdistributedsystems.models to com.fasterxml.jackson.databind;
    opens augustopadilha.serverdistributedsystems.views to javafx.fxml;
    opens augustopadilha.serverdistributedsystems.controllers.UI to javafx.fxml;
    opens augustopadilha.serverdistributedsystems.controllers.system to javafx.fxml;
    exports augustopadilha.serverdistributedsystems;
    exports augustopadilha.serverdistributedsystems.models;
    exports augustopadilha.serverdistributedsystems.views;
    opens augustopadilha.serverdistributedsystems.system.utilities to javafx.fxml;
}