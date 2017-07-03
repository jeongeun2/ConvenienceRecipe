package com.conveniencerecipe.RecipeListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conveniencerecipe.R;

/**
 * Created by ccei on 2016-08-02.
 */
public class NewestTopFragment extends Fragment {
    public int image;

    public NewestTopFragment() {}

    public static NewestTopFragment newInstance(int image) {
        NewestTopFragment f = new NewestTopFragment();
        f.image = image;

        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipelist_newest_top, container, false);
        ImageView top_image = (ImageView)view.findViewById(R.id.top_image);
        top_image.setImageResource(image);
        //Glide.with(MyApplication.RecipeContext()).load(Uri.parse(image)).into(top_image);

        return view;
    }
}
