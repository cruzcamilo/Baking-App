package com.example.android.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.model.Recipe.Ingredient;
import static com.example.android.bakingapp.widget.IngredientService.INGREDIENT_LIST_KEY;
import static com.example.android.bakingapp.widget.IngredientService.RECIPE_NAME_KEY;
import static com.example.android.bakingapp.widget.IngredientService.UPDATE_WIDGET;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidgetProvider extends AppWidgetProvider {

    static List<Ingredient> mIngredientArrayList = new ArrayList<>();
    static String mRecipeName;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, ArrayList<Ingredient> ingredients,
                                String recipeName, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.empty_widget, pendingIntent);

        mIngredientArrayList = ingredients;
        mRecipeName = recipeName;
        if(mRecipeName == null || mRecipeName.isEmpty()){
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.app_name));
            views.setViewVisibility(R.id.empty_widget, View.VISIBLE);
        }

        if(mRecipeName != null &&!mRecipeName.isEmpty()){
            views.setTextViewText(R.id.appwidget_text, mRecipeName + " " +
                    context.getString(R.string.ingredients_label));
            views.setViewVisibility(R.id.empty_widget, View.INVISIBLE);
        }

        Intent intentRemoteView = new Intent(context, IngredientsRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_listview, intentRemoteView);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, (ArrayList<Ingredient>) mIngredientArrayList,
                    mRecipeName, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateIngredientsList(Context context, AppWidgetManager appWidgetManager,
                                             ArrayList<Ingredient> ingredients, String recipeName, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, ingredients, recipeName, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, IngredientWidgetProvider.class);
        int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context, IngredientWidgetProvider.class));
        final String action = intent.getAction();

        if (action.equals(UPDATE_WIDGET)) {
            // refresh all your widgets
            mIngredientArrayList = intent.getExtras().getParcelableArrayList(INGREDIENT_LIST_KEY);
            mRecipeName = intent.getStringExtra(RECIPE_NAME_KEY);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_listview);
            IngredientWidgetProvider.updateIngredientsList(context, mgr,
                    (ArrayList<Ingredient>) mIngredientArrayList, mRecipeName, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_listview,
                new Intent(context, IngredientsRemoteViewsService.class));
    }

    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_listview,
                new Intent(context, IngredientsRemoteViewsService.class));
    }
}