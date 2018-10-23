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
import android.widget.RemoteViews;

import com.example.android.bakingapp.ui.MainActivity;
import com.example.android.bakingapp.R;

import java.util.ArrayList;

import static com.example.android.bakingapp.widget.IngredientService.UPDATE_WIDGET;
import static com.example.android.bakingapp.model.Recipe.Ingredient;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidgetProvider extends AppWidgetProvider {

    static ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, ArrayList<Ingredient> ingredients,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        ingredientArrayList = ingredients;
        /*if (ingredientArrayList != null) {
            for (Ingredient ingredient : ingredientArrayList) {
                RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
                        R.layout.widget_ingredient_item);

                IngredientAdapter ingredientAdapter = new IngredientAdapter();
                String ingredientFormatted = ingredientAdapter.formatIngredient(ingredient);
                ingredientView.setTextViewText(R.id.widget_item_tv, ingredientFormatted);
                views.addView(R.id.widget_listview, ingredientView);
            }
        }*/

        //appWidgetManager.updateAppWidget(appWidgetId, views);

        Intent intentRemoteView = new Intent(context, IngredientsRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_listview, intentRemoteView);
        appWidgetManager.updateAppWidget(appWidgetId, views);
/*
        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }*/
        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, ingredientArrayList, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateIngredientsList(Context context, AppWidgetManager appWidgetManager,
                                             ArrayList<Ingredient> ingredients, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, ingredients, appWidgetId);
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
            ingredientArrayList = intent.getExtras().getParcelableArrayList("ingredientsList");
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_listview);
            IngredientWidgetProvider.updateIngredientsList(context, mgr, ingredientArrayList, appWidgetIds);
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