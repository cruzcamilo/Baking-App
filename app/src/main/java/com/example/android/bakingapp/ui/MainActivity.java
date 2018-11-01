package com.example.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.example.android.bakingapp.retrofit.BakingClient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.retrofit.ServiceGenerator;
import com.example.android.bakingapp.adapters.RecipeCardAdapter;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;

public class MainActivity extends AppCompatActivity {

    private static final String SAVED_RECIPES_KEY = "savedRecipes";
    private static final String RETROFIT_ERROR = "Retrofit error";
    public static final String RECIPE_KEY = "recipe";
    private RecipeCardAdapter mAdapter;
    private List<Recipe> mRecipes;
    private View mCircle;
    private RecyclerView mRecipeCardsRV;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVED_RECIPES_KEY, (ArrayList<? extends Parcelable>) mRecipes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_activity_title));
        final TextView mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        mRecipeCardsRV = (RecyclerView) findViewById(R.id.recipe_cards_rv);
        mCircle = findViewById(R.id.loading_spinner);

        setLayoutBasedOnOrientation(getScreenOrientation());
        mAdapter = new RecipeCardAdapter(this, new ArrayList<Recipe>(),
                new RecipeCardAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos) {
                        Recipe selectedRecipe = mRecipes.get(pos);
                        launchDetailActivity(selectedRecipe);
                    }
                });

        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mCircle.setVisibility(View.GONE);
            mRecipes = savedInstanceState.getParcelableArrayList(SAVED_RECIPES_KEY);
            getRecipesToCardList(mRecipes);
        } else if (isOnline() && mRecipes == null) {
            BakingClient client = ServiceGenerator.createService();
            Call<List<Recipe>> call = client.getRecipes();

            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    mRecipes = response.body();
                    mCircle.setVisibility(View.GONE);
                    if (mRecipes != null && !mRecipes.isEmpty()) {
                        getRecipesToCardList(mRecipes);
                    }
                }
                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.error_message);
                    Log.e(RETROFIT_ERROR, t.getMessage());
                }
            });
        } else if (!isOnline()){
            mCircle.setVisibility(View.GONE);
            mRecipeCardsRV.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }
        mRecipeCardsRV.setAdapter(mAdapter);
    }

    private void getRecipesToCardList(List<Recipe> recipes) {
        ArrayList<Recipe> recipeCardList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Recipe displayRecipe = new Recipe(recipe.getName(), recipe.getServings());
            recipeCardList.add(displayRecipe);
        }
        mAdapter.setData(recipeCardList);
    }

    private void launchDetailActivity(Recipe selectedRecipe) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra(RECIPE_KEY, selectedRecipe);
        startActivity(detailActivity);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //taken from: https://stackoverflow.com/questions/2795833/check-orientation-on-android-phone
    public int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = ORIENTATION_PORTRAIT;
            } else {
                orientation = ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    private void setLayoutBasedOnOrientation (int orientation){
        if(orientation == ORIENTATION_PORTRAIT){
            mRecipeCardsRV.setLayoutManager(new LinearLayoutManager(this));
            mRecipeCardsRV.setHasFixedSize(true);
        } else {
            mRecipeCardsRV.setLayoutManager(new GridLayoutManager(this, 2));
            mRecipeCardsRV.setHasFixedSize(true);
        }
    }
}