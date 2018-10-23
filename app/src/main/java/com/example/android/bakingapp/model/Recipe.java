package com.example.android.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable{

    private String id;
    private String name;
    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private ArrayList<Step> steps = new ArrayList<Step>();
    private int servings;
    private String image;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public Recipe(){
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    public Recipe(String name, int servings) {
        this.name = name;
        this.servings = servings;
    }

    public Recipe(String id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Recipe {" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                '}';
    }

    //Parcelable implementation taken from:
    //https://stackoverflow.com/questions/14178736/how-to-make-a-class-with-nested-objects-parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    public Recipe(Parcel in) {
        this();
        id = in.readString();
        name = in.readString();
        in.readTypedList(ingredients, Ingredient.CREATOR);
        in.readTypedList(steps, Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public static class Ingredient implements Parcelable {
        private String measure;
        private String quantity;
        private String ingredient;

        Ingredient(){}

        public Ingredient(String measure, String quantity, String ingredient) {
            this.measure = measure;
            this.quantity = quantity;
            this.ingredient = ingredient;
        }

        @Override
        public String toString() {
            return "Ingredient{" +
                    "measure='" + measure + '\'' +
                    ", quantity='" + quantity + '\'' +
                    ", ingredient='" + ingredient + '\'' +
                    '}';
        }

        public String getMeasure() {
            return measure;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getIngredient() {
            return ingredient;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(measure);
            dest.writeString(quantity);
            dest.writeString(ingredient);
        }

        private Ingredient(Parcel in) {
            measure = in.readString();
            quantity = in.readString();
            ingredient = in.readString();
        }

        public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
            @Override
            public Ingredient createFromParcel(Parcel in) {
                return new Ingredient(in);
            }

            @Override
            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };
    }

    public static class Step implements Parcelable {
        private int id;
        private String shortDescription;
        private String description;
        private String videoURL;
        private String thumbnailURL;

        public Step(){}

        public int getId() {
            return id;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }


        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
        }

        @Override
        public String toString() {
            return "Step{" +
                    "id=" + id +
                    ", shortDescription='" + shortDescription + '\'' +
                    ", description='" + description + '\'' +
                    ", videoURL='" + videoURL + '\'' +
                    ", thumbnailURL='" + thumbnailURL + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(shortDescription);
            parcel.writeString(description);
            parcel.writeString(videoURL);
            parcel.writeString(thumbnailURL);
        }

        protected Step(Parcel in) {
            id = in.readInt();
            shortDescription = in.readString();
            description = in.readString();
            videoURL = in.readString();
            thumbnailURL = in.readString();
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }
}