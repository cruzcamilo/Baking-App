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

import static com.example.android.bakingapp.model.Recipe.Step;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    Context context;
    private ArrayList<Step> steps;
    OnItemClickListener listener;

    public RecipeStepAdapter(Context context, ArrayList<Step> steps, OnItemClickListener listener) {
        this.context = context;
        this.steps = steps;
        this.listener = listener;
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder{
        TextView recipeStep;
        public RecipeStepViewHolder(View itemView) {
            super(itemView);
            recipeStep = (TextView) itemView.findViewById(R.id.step_content_tv);
        }
    }

    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int recipeStep = R.layout.recipe_step_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(recipeStep, viewGroup, false);
        final RecipeStepViewHolder viewHolder = new RecipeStepViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        Step step = steps.get(position);
        int stepNum = step.getId();
        String displayedStep = "";
        if(stepNum == 0){
            displayedStep = step.getShortDescription();
        } else {
            displayedStep = stepNum + ". " + step.getShortDescription();
        }
        holder.recipeStep.setText(displayedStep);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void setData(List<Step> data) {
        if (null != data && !data.isEmpty()) {
            steps.clear();
            steps.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (steps != null) {
            int size = getItemCount();
            if (size > 0) {
                steps.clear();
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
