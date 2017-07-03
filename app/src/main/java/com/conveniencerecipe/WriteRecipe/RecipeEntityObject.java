package com.conveniencerecipe.WriteRecipe;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-07-25.
 */
public class RecipeEntityObject {
    public String title;
    public int theme;
    public ArrayList<WriteRecipeMainActivity.IngredientData> ingredientArray = new ArrayList<>();
    public String price;
    public String time;

    public RecipeEntityObject(){}
}
