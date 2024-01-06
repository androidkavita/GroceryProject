package com.easym.vegie.retrofit;


/**
 * Created by Arti Kumari on 05/10/2020.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {

    private APIService() {
    }

    private static APIService apiService = null;

    public static APIService getInstance() {
        return (apiService == null) ? apiService = new APIService() : apiService;
    }

    private Retrofit retrofit = null;

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public APIInterface init() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout((60 * 5), TimeUnit.SECONDS)
                .readTimeout((60 * 5), TimeUnit.SECONDS).addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.getInstance().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(APIInterface.class);
    }
}