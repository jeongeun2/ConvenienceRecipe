package com.conveniencerecipe;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by ccei on 2016-07-25.
 */
public class RecipeFragment extends Fragment {
    public String image;
    public String num;
    public String text;

    public RecipeFragment() {}

    public static RecipeFragment newInstance(String imagee, String num, String text) {
        RecipeFragment f = new RecipeFragment();
        f.image = imagee;
        f.num = num;
        f.text = text;

        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_detail_fragment, container, false);
        ImageView recipe_image = (ImageView)view.findViewById(R.id.recipe_image);
        TextView sequence_num = (TextView)view.findViewById(R.id.sequence_num);
        TextView recipe_text = (TextView)view.findViewById(R.id.recipe_text);

        Glide.with(MyApplication.RecipeContext()).load(Uri.parse(image))
                .override(1080,1080).into(recipe_image);
        if(Integer.parseInt(num) < 10) {
            sequence_num.setText("0" + num);
        } else {
            sequence_num.setText(num);
        }
        recipe_text.setText(text);

        return view;
    }
}