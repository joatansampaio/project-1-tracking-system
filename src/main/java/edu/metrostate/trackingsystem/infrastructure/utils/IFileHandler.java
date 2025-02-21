package edu.metrostate.trackingsystem.infrastructure.utils;

import java.io.File;

public interface IFileHandler {
    boolean importFile(File file);
    boolean exportFile(File file);
}
