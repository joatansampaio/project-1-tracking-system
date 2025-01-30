package com.metrostate.projectone.utils;

import org.json.simple.JSONObject;

public interface IFileHandler {
    JSONObject read(String fileName);
    void write(JSONObject jsonObject);
}
