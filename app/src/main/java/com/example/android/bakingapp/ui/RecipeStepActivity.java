package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;

import java.util.ArrayList;

import static com.example.android.bakingapp.model.Recipe.Step;
import static com.example.android.bakingapp.ui.DetailFragment.POSITION_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.RECIPE_NAME_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.STEPS_KEY;

public class RecipeStepActivity extends AppCompatActivity {

    private static final String SAVED_FRAGMENT = "recipeStepFragment";
    RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        if (savedInstanceState != null) {
            recipeStepFragment = (RecipeStepFragment) getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAGMENT);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, recipeStepFragment).commit();
        }

        if (getIntent() != null) {
            Intent stepIntent = getIntent();
            ArrayList<Step> steps = stepIntent.getParcelableArrayListExtra(STEPS_KEY);
            String recipeName = stepIntent.getStringExtra(RECIPE_NAME_KEY);
            int pos = stepIntent.getIntExtra(POSITION_KEY, 0);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(STEPS_KEY, steps);
            bundle.putString(RECIPE_NAME_KEY, recipeName);
            bundle.putInt(POSITION_KEY, pos);
            recipeStepFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, recipeStepFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, SAVED_FRAGMENT, recipeStepFragment);
    }
}