package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientAdapter;
import com.example.android.bakingapp.adapters.RecipeStepAdapter;
import com.example.android.bakingapp.databinding.FragmentRecipeDetailBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.widget.IngredientService;

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;
import static com.example.android.bakingapp.model.Recipe.Ingredient;
import static com.example.android.bakingapp.model.Recipe.Step;
import static com.example.android.bakingapp.ui.MainActivity.RECIPE_KEY;

public class DetailFragment extends Fragment {

    public static final String STEPS_KEY = "mSteps";
    public static final String RECIPE_NAME_KEY = "recipeName";
    public static final String POSITION_KEY = "position";
    private static final String INGREDIENTS_KEY = "ingredientsKey";
    private RecipeStepAdapter mAdapter;
    private IngredientAdapter mIngredientAdapter;
    private FragmentRecipeDetailBinding mBinding;
    private Boolean mTablet;
    private Recipe mCurrentRecipe;
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<Step> mSteps = new ArrayList<>();

    public DetailFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_KEY, mCurrentRecipe);
        outState.putParcelableArrayList(INGREDIENTS_KEY, (ArrayList<? extends Parcelable>) mIngredients);
        outState.putParcelableArrayList(STEPS_KEY, (ArrayList<? extends Parcelable>) mSteps);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_recipe_detail, container, false);
        View rootView = mBinding.getRoot();

        int currentOrientation = getScreenOrientation();
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        setPhoneLandscapeLayout(currentOrientation, isTablet);
        setTabletLandscapeLayout(currentOrientation, isTablet);

        if(savedInstanceState!=null && !savedInstanceState.isEmpty()){
            mCurrentRecipe = savedInstanceState.getParcelable(RECIPE_KEY);
            mIngredients = savedInstanceState.getParcelableArrayList(INGREDIENTS_KEY);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_KEY);
        } else {
            Intent intent = getActivity().getIntent();
            mCurrentRecipe = intent.getParcelableExtra(RECIPE_KEY);
            mIngredients = mCurrentRecipe.getIngredients();
            mSteps = mCurrentRecipe.getSteps();
        }

        getActivity().setTitle(mCurrentRecipe.getName());
        RecyclerView ingredientsRV = (RecyclerView) rootView.findViewById(R.id.ingredients_rv);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsRV.setHasFixedSize(true);
        mIngredientAdapter = new IngredientAdapter(getActivity(), (ArrayList<Ingredient>) mIngredients);
        ingredientsRV.setAdapter(mIngredientAdapter);

        RecyclerView recipeStepsRV = (RecyclerView) rootView.findViewById(R.id.recipe_steps_rv);
        recipeStepsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeStepsRV.setHasFixedSize(true);
        mAdapter = new RecipeStepAdapter(getActivity(), (ArrayList<Step>) mSteps, new RecipeStepAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mTablet = ((DetailActivity) getActivity()).isTablet();
                if (!mTablet) {
                    launchRecipeStepActivity((ArrayList<Step>) mSteps, mCurrentRecipe.getName(), pos);
                } else {
                    ((DetailActivity) getActivity()).replaceStepFragment( (ArrayList<Step>) mSteps,
                            mCurrentRecipe.getName(), pos);
                }
            }
        });
        recipeStepsRV.setAdapter(mAdapter);
        String recipeName = mCurrentRecipe.getName();
        IngredientService.startActionUpdateIngredientList(getActivity(),
                (ArrayList<Ingredient>) mIngredients, recipeName);
        return rootView;
    }

    private void setTabletLandscapeLayout(int currentOrientation, boolean isTablet) {
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE && isTablet){
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 2
            );
            LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 3
            );
            mBinding.recipeDetailCardview.setLayoutParams(cardParams);
            mBinding.recipeDetailScrollview.setLayoutParams(scrollParams);
        }
    }

    private void setPhoneLandscapeLayout(int currentOrientation, boolean isTablet) {
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE &&
                !isTablet){
            mBinding.linearLayoutRecipeDetail.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1
            );
            LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
            );
            mBinding.recipeDetailCardview.setLayoutParams(cardParams);
            mBinding.recipeDetailScrollview.setLayoutParams(scrollParams);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchRecipeStepActivity(ArrayList<Step> steps, String recipeName, int pos) {
        Intent detailActivity = new Intent(getActivity(), RecipeStepActivity.class);
        detailActivity.putParcelableArrayListExtra(STEPS_KEY, steps);
        detailActivity.putExtra(RECIPE_NAME_KEY, recipeName);
        detailActivity.putExtra(POSITION_KEY, pos);
        startActivity(detailActivity);
    }

    //taken from https://stackoverflow.com/questions/3663665/
    public int getScreenOrientation() {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
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
}