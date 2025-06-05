package com.example.myapplication.api;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static DeepSeekApiService deepSeekApi;

    public static DeepSeekApiService getDeepSeekApi() {
        if (deepSeekApi == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.deepseek.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            deepSeekApi = retrofit.create(DeepSeekApiService.class);
        }
        return deepSeekApi;
    }
}