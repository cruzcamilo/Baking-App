package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipes;
    // Implementation of OnItemClickListener taken from:
    // https://antonioleiva.com/recyclerview-listener/
    private OnItemClickListener listener;

    public RecipeCardAdapter(Context context, ArrayList<Recipe> recipes, OnItemClickListener listener) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
    }

    class RecipeCardViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, servingsAmount;

        public RecipeCardViewHolder(View itemView) {
            super(itemView);
            recipeName = (TextView) itemView.findViewById(R.id.recipe_name_tv);
            servingsAmount = (TextView) itemView.findViewById(R.id.servings_amount_tv);
        }
    }

    @Override
    public RecipeCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int recipeCard = R.layout.recipe_card;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(recipeCard, viewGroup, false);
        final RecipeCardViewHolder viewHolder = new RecipeCardViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeCardViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.servingsAmount.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setData(List<Recipe> data) {
        if (null != data && !data.isEmpty()) {
            recipes.clear();
            recipes.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (recipes != null) {
            int size = getItemCount();
            if (size > 0) {
                recipes.clear();
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}