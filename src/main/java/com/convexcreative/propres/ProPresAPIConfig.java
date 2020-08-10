package com.convexcreative.propres;

public class ProPresAPIConfig {

    private static final String DEFAULT_HOST6 = "127.0.0.1:59213";
    private static final String DEFAULT_HOST7 = "127.0.0.1:64759";
    private static final String DEFAULT_PW = "1234";

    public static final int V6 = 6;
    public static final int V7 = 7;

    private String host = DEFAULT_HOST6;
    private String pw = DEFAULT_PW;
    private int apiVersion = V6;

    public ProPresAPIConfig(String host, String pw, int apiVersion){
        this.host = host;
        this.pw = pw;
        this.apiVersion = apiVersion;
    }

    public ProPresAPIConfig(){}

    public String getHost() {
        return host;
    }

    public String getPassword(){
        return pw;
    }

    public int getApiVersion(){
        return apiVersion;
    }
}
