package com.lsenseney.btmemorize.model;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class APIBible {
    private static APIBible instance;
    private static String key = "7b49bb79780270ec2aa314396b820813";

    private String apiKey;
    private RequestQueue queue;
    private Map<String, String> versionAbbrToID;

    private APIBible(String key, Context context) {
        apiKey = key;
        queue = Volley.newRequestQueue(context);

        versionAbbrToID = new TreeMap<>();
        versionAbbrToID.put("WEBBE", "7142879509583d59-04");
        versionAbbrToID.put("WEB", "9879dbb7cfe39e4d-04");
        versionAbbrToID.put("ASV", "06125adad2d5898a-01");
        versionAbbrToID.put("RV", "40072c4a5aba4022-01");
        versionAbbrToID.put("KJV", "de4e12af7f28f599-02");
    }

    public static APIBible getInstance(Context context) {
        if (instance == null) {
            instance = new APIBible(key, context);
        }
        return instance;
    }

    public Set<String> getVersions() {
        return versionAbbrToID.keySet();
    }
}
