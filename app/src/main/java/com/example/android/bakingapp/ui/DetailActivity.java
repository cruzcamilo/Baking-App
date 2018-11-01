package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;

import java.util.ArrayList;

import static com.example.android.bakingapp.model.Recipe.Step;
import static com.example.android.bakingapp.ui.DetailFragment.POSITION_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.RECIPE_NAME_KEY;
import static com.example.android.bakingapp.ui.DetailFragment.STEPS_KEY;

public class DetailActivity extends AppCompatActivity {
    public Boolean mTabletMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(findViewById(R.id.container)!= null){
            mTabletMode = true;
        }
    }

    public boolean isTablet() {
        return mTabletMode;
    }

    public void replaceStepFragment(ArrayList<Step> steps, String recipeName, int pos) {
        RecipeStepFragment stepFragment = new RecipeStepFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS_KEY, steps);
        args.putString(RECIPE_NAME_KEY, recipeName);
        args.putInt(POSITION_KEY, pos);
        stepFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, stepFragment).commit();
    }
}