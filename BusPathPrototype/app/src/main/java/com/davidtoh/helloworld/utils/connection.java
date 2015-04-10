package com.davidtoh.helloworld.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class connection {
    String connectionURL;
    String JSON;
    public connection(String connectionURL) {
        this.connectionURL = connectionURL;
        try {
            JSON = makeConnection(connectionURL);
        }
        catch (IOException e) {
        }
    }

    public String getJSON() {
        return JSON;
    }
    public String makeConnection(String urlString) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            inputStream = conn.getInputStream();
            return readIt(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    public String readIt(InputStream stream) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        StringBuilder JSONResult = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            JSONResult.append(line);
        }
        reader.close();
        return JSONResult.toString();
    }
}
