package com.gvbrain.appApi;
import java.util.HashMap;

public class Restful {

    public String url;
    public String method;
    public HashMap<String, Object> header = new HashMap<>();
    public HashMap<String, Object> query = new HashMap<>();
    public String body;
}

