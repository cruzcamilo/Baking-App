package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientAdapter;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.widget.IngredientWidgetProvider.mIngredientArrayList;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Recipe.Ingredient> mIngredientsList = new ArrayList<>();
    private Intent intent;

    public WidgetDataProvider(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        this.intent = intent;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        mIngredientsList = mIngredientArrayList;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);
        IngredientAdapter ingredientAdapter = new IngredientAdapter(mContext);
        if (mIngredientsList !=null){
            String ingredient = ingredientAdapter.formatIngredient(mIngredientsList.get(position), mContext);
            rv.setTextViewText(R.id.widget_item_tv, ingredient);
        }
        return rv;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
