package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static com.example.android.bakingapp.model.Recipe.Ingredient;

public class IngredientService extends IntentService {

    public static final String ACTION_UPDATE_WIDGET_INGREDIENTS_LIST =
            "android.appwidget.action.update_ingredients_list";

    public static final String UPDATE_WIDGET =
            "android.appwidget.action.ACTION_UPDATE_WIDGET_BROADCAST";

    public IngredientService() {
        super("IngredientService");
    }

    public static void startActionUpdateIngredientList(Context context, ArrayList<Ingredient> ingredients) {
        Intent intent = new Intent(context, IngredientService.class);
        intent.setAction(ACTION_UPDATE_WIDGET_INGREDIENTS_LIST);
        intent.putParcelableArrayListExtra("ingredientList", ingredients);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET_INGREDIENTS_LIST.equals(action)) {
                ArrayList<Ingredient> ingredients =
                        intent.getParcelableArrayListExtra("ingredientList");
                handleActionUpdateIngredientsList(ingredients);
            }
        }
    }

    private void handleActionUpdateIngredientsList(ArrayList<Ingredient> ingredients) {
        Intent intent = new Intent(UPDATE_WIDGET);
        intent.setAction(UPDATE_WIDGET);
        intent.putExtra("ingredientsList",ingredients);
        sendBroadcast(intent);
    }
}