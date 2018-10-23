package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.model.Recipe.Ingredient;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    Context context;
    private ArrayList<Ingredient> ingredients;

    public IngredientAdapter(Context context) {
        this.context = context;
    }

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientItem;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ingredientItem = (TextView) itemView.findViewById(R.id.ingredient_item_tv);
        }
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int ingredientItem = R.layout.recipe_ingredient_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(ingredientItem, viewGroup, false);
        IngredientViewHolder viewHolder = new IngredientViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientItem.setText(formatIngredient(ingredient, context));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void setData(List<Ingredient> data) {
        if (null != data && !data.isEmpty()) {
            ingredients.clear();
            ingredients.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (ingredients != null) {
            int size = getItemCount();
            if (size > 0) {
                ingredients.clear();
            }
        }
    }

    public String formatIngredient(Ingredient ingredient, Context context) {
        StringBuilder ingredientsListBuilder = new StringBuilder();
        ingredientsListBuilder.append('\u2022' + " " + ingredient.getQuantity());
        String measure = ingredient.getMeasure().toLowerCase();
        String tablespoon = context.getString(R.string.tablespoon);
        String teaspoon= context.getString(R.string.teaspoon);

        switch (measure) {
            case "g":
                ingredientsListBuilder.append(measure + " ");
                break;
            case "unit":
                ingredientsListBuilder.append(" ");
                break;
            case "tblsp":
                ingredientsListBuilder.append(" " + tablespoon + " ");
                break;
            case "tsp":
                ingredientsListBuilder.append(" " + teaspoon + " ");
                break;
            default:
                ingredientsListBuilder.append(" " + measure + " ");
                break;
        }

        String ingredientName = ingredient.getIngredient();
        ingredientName = Character.toUpperCase(ingredientName.charAt(0)) + ingredientName.substring(1);
        ingredientsListBuilder.append(ingredientName);

        return ingredientsListBuilder.toString();
    }
}