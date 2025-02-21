module edu.metrostate.trackingsystem {

    requires javafx.fxml;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires javafx.controls;
    requires java.logging;

    opens edu.metrostate.trackingsystem to javafx.fxml;
    opens edu.metrostate.trackingsystem.presentation.controllers to javafx.fxml;
    opens edu.metrostate.trackingsystem.domain.models to javafx.base, com.google.gson;
    opens edu.metrostate.trackingsystem.application.dto to com.google.gson;

    exports edu.metrostate.trackingsystem;
    exports edu.metrostate.trackingsystem.application.exceptions;
    exports edu.metrostate.trackingsystem.presentation.controllers;
    exports edu.metrostate.trackingsystem.domain.models;
    exports edu.metrostate.trackingsystem.domain.repositories;
    exports edu.metrostate.trackingsystem.application.services;
    exports edu.metrostate.trackingsystem.application.dto;
    exports edu.metrostate.trackingsystem.infrastructure.utils;
    exports edu.metrostate.trackingsystem.infrastructure.logging;
    exports edu.metrostate.trackingsystem.infrastructure.database;
}