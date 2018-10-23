package com.example.android.bakingapp.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.loader.RecipeLoader;
import com.example.android.bakingapp.adapters.RecipeCardAdapter;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>> {

    private RecipeCardAdapter mAdapter;
    private List<Recipe> recipes;
    private static final int RECIPE_LOADER_ID = 1;
    private View circle;
    private RecyclerView recipeCardsRV;
    public static final String LOG_TAG = MainActivity.class.getName();
    private String queryURL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_activity_title));
        TextView mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        recipeCardsRV = (RecyclerView) findViewById(R.id.recipe_cards_rv);
        circle = findViewById(R.id.loading_spinner);

        if (isOnline()) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(RECIPE_LOADER_ID, null, this);
            } else {
                circle.setVisibility(View.GONE);
                recipeCardsRV.setVisibility(View.INVISIBLE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.no_internet);
            }

        setLayoutBasedOnOrientation(getScreenOrientation());
        mAdapter = new RecipeCardAdapter(this, new ArrayList<Recipe>(),
                new RecipeCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Recipe selectedRecipe = recipes.get(pos);
                launchDetailActivity(selectedRecipe);
            }
        });
        recipeCardsRV.setAdapter(mAdapter);
    }

    private void getRecipeCardList (List<Recipe> recipes){
        ArrayList<Recipe> recipeCardList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Recipe displayRecipe = new Recipe(recipe.getName(), recipe.getServings());
            recipeCardList.add(displayRecipe);
        }
        mAdapter.setData(recipeCardList);
    }

    private void launchDetailActivity(Recipe selectedRecipe) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra("recipe", selectedRecipe);
        startActivity(detailActivity);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //taken from: https://stackoverflow.com/questions/2795833/check-orientation-on-android-phone
    public int getScreenOrientation(){
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = ORIENTATION_PORTRAIT;
            }else {
                orientation = ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    private void setLayoutBasedOnOrientation (int orientation){
        if(orientation == ORIENTATION_PORTRAIT){
            recipeCardsRV.setLayoutManager(new LinearLayoutManager(this));
            recipeCardsRV.setHasFixedSize(true);
        } else {
            recipeCardsRV.setLayoutManager(new GridLayoutManager(this, 2));
            recipeCardsRV.setHasFixedSize(true);
        }
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int i, Bundle bundle) {
        return new RecipeLoader(this, queryURL);
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> recipes) {
        circle.setVisibility(View.GONE);
        if (recipes!= null && !recipes.isEmpty()) {
            this.recipes = recipes;
            getRecipeCardList(recipes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {
        mAdapter.clear();
    }
}