package com.conveniencerecipe;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by ccei on 2016-08-09.
 */
public class QnaDetailFragment extends Fragment {
    public String image;

    public QnaDetailFragment() {}

    public static QnaDetailFragment newInstance(String image) {
        QnaDetailFragment f = new QnaDetailFragment();
        f.image = image;

        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qna_detail, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.image);
        Glide.with(MyApplication.RecipeContext()).load(Uri.parse(image)).override(984,984).into(imageView);

        return view;
    }
}