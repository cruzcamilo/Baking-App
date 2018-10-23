package com.example.android.bakingapp.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utils.QueryUtils;

import java.util.List;

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    private static final String LOG_TAG = RecipeLoader.class.getName();

    /** Query URL */
    private String mUrl;
    private List<Recipe> savedRecipes;

    public RecipeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if(savedRecipes !=null){
            deliverResult(savedRecipes);
            Log.v(LOG_TAG, " using cache");
        } else if (takeContentChanged() || savedRecipes == null) {
            forceLoad();
        }
    }

    @Override
    public List<Recipe> loadInBackground() {
        List<Recipe> recipes = QueryUtils.fetchRecipeData(mUrl);
        return recipes;
    }

    @Override
    public void deliverResult(List<Recipe> recipes) {
        savedRecipes = recipes;
        super.deliverResult(savedRecipes);
    }
}