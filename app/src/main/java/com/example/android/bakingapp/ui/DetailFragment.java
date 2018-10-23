package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.content.res.Configuration.ORIENTATION_SQUARE;
import static com.example.android.bakingapp.model.Recipe.Ingredient;
import static com.example.android.bakingapp.model.Recipe.Step;

public class DetailFragment extends Fragment {

    private RecipeStepAdapter mAdapter;
    private IngredientAdapter ingredientAdapter;
    private FragmentRecipeDetailBinding mBinding;
    private Boolean mTablet;
    public DetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_recipe_detail, container, false);
        View rootView = mBinding.getRoot();

        int currentOrientation = getScreenOrientation();
         boolean isTablet = getResources().getBoolean(R.bool.isTablet);

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
        Intent intent = getActivity().getIntent();
        final Recipe currentRecipe = intent.getParcelableExtra("recipe");
        getActivity().setTitle(currentRecipe.getName());
        ArrayList<Ingredient> ingredients = currentRecipe.getIngredients();
        final ArrayList<Step> steps = currentRecipe.getSteps();

        RecyclerView ingredientsRV = (RecyclerView) rootView.findViewById(R.id.ingredients_rv);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsRV.setHasFixedSize(true);
        ingredientAdapter = new IngredientAdapter(getActivity(), ingredients);
        ingredientsRV.setAdapter(ingredientAdapter);

        RecyclerView recipeStepsRV = (RecyclerView) rootView.findViewById(R.id.recipe_steps_rv);
        recipeStepsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeStepsRV.setHasFixedSize(true);
        mAdapter = new RecipeStepAdapter(getActivity(), steps, new RecipeStepAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mTablet = ((DetailActivity) getActivity()).isTablet();
                if(!mTablet){
                    launchRecipeStepActivity(steps, currentRecipe.getName(), pos);
                } else {
                    ((DetailActivity) getActivity()).replaceStepFragment(steps,
                            currentRecipe.getName(), pos);
                }
            }
        });
        recipeStepsRV.setAdapter(mAdapter);
        IngredientService.startActionUpdateIngredientList(getActivity(), ingredients);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchRecipeStepActivity(ArrayList<Step> steps, String recipeName, int pos) {
        Intent detailActivity = new Intent(getActivity(), RecipeStepActivity.class);
        detailActivity.putParcelableArrayListExtra("steps", steps);
        detailActivity.putExtra("recipeName", recipeName);
        detailActivity.putExtra("position", pos);
        startActivity(detailActivity);
    }
    //taken from https://stackoverflow.com/questions/3663665/
    public int getScreenOrientation(){
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
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
}