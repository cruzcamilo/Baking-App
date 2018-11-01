package com.example.android.bakingapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    //Taken from https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client
    private static final String BASE_URL = "http://go.udacity.com/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static BakingClient createService(){
        return retrofit.create(BakingClient.class);
    }
}
