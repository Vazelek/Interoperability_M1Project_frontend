module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.web;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports hr.algebra.valentinherve.productapp.model;
}
