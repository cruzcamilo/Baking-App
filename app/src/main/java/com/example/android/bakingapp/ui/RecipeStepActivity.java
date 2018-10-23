package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;

import java.util.ArrayList;

import static com.example.android.bakingapp.model.Recipe.Step;

public class RecipeStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        if (getIntent() != null) {
            Intent stepIntent = getIntent();
            ArrayList<Step> steps = stepIntent.getParcelableArrayListExtra("steps");
            String recipeName = stepIntent.getStringExtra("recipeName");
            int pos = stepIntent.getIntExtra("position", 0);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("steps", steps);
            bundle.putString("recipeName", recipeName);
            bundle.putInt("position", pos);
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            recipeStepFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, recipeStepFragment).commit();
        }
    }
}