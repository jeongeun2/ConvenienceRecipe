package com.conveniencerecipe.WriteRecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conveniencerecipe.R;

/**
 * Created by ccei on 2016-08-03.
 */
public class CookSpinnerAdapter extends ArrayAdapter<Integer> {
    Context context;
    Integer[] image_items = {R.drawable.microwave, R.drawable.hotwater, R.drawable.frypan, R.drawable.pot};
    String[] text_items = {"전자레인지", "끓는물", "프라이팬", "냄비"};

    public CookSpinnerAdapter(Context context) {
        super(context,0);
        this.context = context;
    }

    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cook_spinner_dropdown, parent, false);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.cook_image);
        imageView.setImageResource(image_items[position]);
        TextView textView = (TextView)convertView.findViewById(R.id.cook_text);
        textView.setText(text_items[position]);

        return convertView;
    }

    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cook_spinner, parent, false);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.cook_image);
        imageView.setImageResource(image_items[position]);

        return convertView;
    }
}