package com.conveniencerecipe.WriteRecipe;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.R;

/**
 * Created by ccei on 2016-08-08.
 */
public class RecipePreviewFragment extends Fragment {
    public Object image;
    public String num;
    public String text;

    public RecipePreviewFragment() {}

    public static RecipePreviewFragment newInstance(Object image, String num, String text) {
        RecipePreviewFragment f = new RecipePreviewFragment();
        f.image = image;
        f.num = num;
        f.text = text;

        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.write_recipe_preview_fragment, container, false);
        ImageView recipe_image = (ImageView)view.findViewById(R.id.recipe_image);
        TextView sequence_num = (TextView)view.findViewById(R.id.sequence_num);
        TextView recipe_text = (TextView)view.findViewById(R.id.recipe_text);

        if(image instanceof Bitmap) {
            recipe_image.setImageBitmap((Bitmap)image);
        } else if(image instanceof String) {
            Glide.with(MyApplication.RecipeContext()).load(Uri.parse((String)image)).override(1080,1080).into(recipe_image);
        }
        if(Integer.parseInt(num) < 10) {
            sequence_num.setText("0"+num);
        } else {
            sequence_num.setText(num);
        }
        recipe_text.setText(text);

        return view;
    }
}
