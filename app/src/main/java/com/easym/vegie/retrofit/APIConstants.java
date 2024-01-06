package com.easym.vegie.retrofit;

/**
 * Created by Arti Kumari on 05/10/2020.
 */
public class APIConstants {
    private static APIConstants apiConstants = null;

    private APIConstants() {
    }

    public static APIConstants getInstance() {
        return (apiConstants == null) ? apiConstants = new APIConstants() : apiConstants;
    }

    public String getBaseUrl() {
//        return "http://103.154.2.115/~maxget/freshtohotel/webservie/auth/";
        return "https://easyvegie.com/webservie/auth/";
       // return "https://182.76.237.238/~maxget/freshtohotel/webservie/auth/";
    }


  //  http://182.76.237.227/~maxget/grocery/webservie/auth/
}