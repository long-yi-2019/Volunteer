package com.example.myapplication.api;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Header;

public interface DeepSeekApiService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    Call<ChatResponse> chat(@Header("Authorization") String token, @Body ChatRequest request);
}