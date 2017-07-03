package com.conveniencerecipe.Recipe;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccei 34 on 2016-08-22.
 */
public class FirstFragment extends Fragment {
    ImageView firstmain;
    TextView firsttitle;
    TextView firstprice;
    TextView firstNickname;
    TextView firstTheme;
    TextView firstTime;

    RecyclerView ingrediListView;
    IngredientAdapter ingredientAdapter;

    public FirstFragment() {}

    public static FirstFragment newInstance(){
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }

    boolean flag;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail_first, container, false);

        firstprice = (TextView)view.findViewById(R.id.recipe_detail_first_price);
        firstprice.setText(String.valueOf(RecipeActivity.recipeDetailData.price)+"원");

        firstTheme = (TextView)view.findViewById(R.id.recipe_detail_first_theme);
        switch (RecipeActivity.recipeDetailData.theme) {
            case "0": firstTheme.setText("이렇게 싸게?!"); break;
            case "1": firstTheme.setText("편의점도 럭셔리하게"); break;
            case "2": firstTheme.setText("어제 달렸어요"); break;
            case "3": firstTheme.setText("우중충 비오는 날"); break;
            case "4": firstTheme.setText("오늘 하늘 맑음"); break;
            case "5": firstTheme.setText("스트레스 만땅"); break;
        }

        firstNickname = (TextView)view.findViewById(R.id.first_nickname);
        firstNickname.setText("작성자 | "+RecipeActivity.recipeDetailData.nickname);

        firstTime = (TextView)view.findViewById(R.id.recipe_detail_first_time);
        firstTime.setText(String.valueOf(RecipeActivity.recipeDetailData.time)+"분");

        firstmain = (ImageView)view.findViewById(R.id.recipe_detail_first);
        Glide.with(MyApplication.RecipeContext()).load(Uri.parse(RecipeActivity.recipeDetailData.recipeImg.get(RecipeActivity.recipeDetailData.recipeImg.size()-1))).override(1080,1080).into(firstmain);
        firstmain.setColorFilter(Color.rgb(178, 178, 178), android.graphics.PorterDuff.Mode.MULTIPLY);

        firsttitle = (TextView)view.findViewById(R.id.recipe_detail_first_title);
        firsttitle.setText(RecipeActivity.recipeDetailData.title);

        ingrediListView = (RecyclerView)view.findViewById(R.id.recipe_detail_first_ingredilist);
        ingredientAdapter = new IngredientAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ingrediListView.setLayoutManager(layoutManager);
        ingrediListView.setAdapter(ingredientAdapter);
        for(int i = 0; i < RecipeActivity.recipeDetailData.ingredient.size(); i++) {
            ingredientAdapter.add(RecipeActivity.recipeDetailData.ingredient.get(i));
        }
        ingredientAdapter.notifyDataSetChanged();

        return view;
    }

    public class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<RecipeActivity.IngredientData> items = new ArrayList<>();

        public void add(RecipeActivity.IngredientData data) {
            items.add(data);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_recipe_ingredient, parent, false);

            return new ingredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ingredientViewHolder)holder).setMyData(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ingredientViewHolder extends RecyclerView.ViewHolder {
            ImageView eventImage;
            TextView ingrediTitle;

            public ingredientViewHolder(final View itemView) {
                super(itemView);
                eventImage = (ImageView)itemView.findViewById(R.id.event_image);
                ingrediTitle = (TextView)itemView.findViewById(R.id.ingredient_title);
            }

            public void setMyData(RecipeActivity.IngredientData data) {
                ingrediTitle.setText(data.name);

                if(data.event != null) {
                    switch (data.event) {
                        case "1+1": eventImage.setImageResource(R.drawable.event1); break;
                        case "2+1": eventImage.setImageResource(R.drawable.event2); break;
                        case "3+1": eventImage.setImageResource(R.drawable.event3); break;
                    }
                }
            }
        }
    }
}