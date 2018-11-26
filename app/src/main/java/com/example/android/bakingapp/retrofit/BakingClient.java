package com.example.android.bakingapp.retrofit;

import com.example.android.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingClient {

    @GET("baking.json")
    Call<List<Recipe>> getRecipes();
}