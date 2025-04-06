module edu.metrostate.dealership {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;

    exports edu.metrostate.dealership;
    exports edu.metrostate.dealership.application.exceptions;
    exports edu.metrostate.dealership.presentation.controllers;
    exports edu.metrostate.dealership.domain.models;
    exports edu.metrostate.dealership.domain.repositories;
    exports edu.metrostate.dealership.application.services;
    exports edu.metrostate.dealership.infrastructure.utils;
    exports edu.metrostate.dealership.infrastructure.logging;
    exports edu.metrostate.dealership.infrastructure.database;
    exports edu.metrostate.dealership.infrastructure.imports.models;
    exports edu.metrostate.dealership.infrastructure.imports.models.xml;
    exports edu.metrostate.dealership.infrastructure.imports.models.json;

    opens edu.metrostate.dealership to javafx.fxml;
    opens edu.metrostate.dealership.presentation.controllers to javafx.fxml;
    opens edu.metrostate.dealership.infrastructure.utils to com.google.gson;
    opens edu.metrostate.dealership.domain.models to javafx.base, com.google.gson;
    opens edu.metrostate.dealership.infrastructure.imports.models to com.google.gson, javafx.base;
    opens edu.metrostate.dealership.infrastructure.imports.models.json to com.google.gson, javafx.base;
    opens edu.metrostate.dealership.infrastructure.imports.models.xml to com.google.gson, javafx.base;
}