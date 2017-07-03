package com.conveniencerecipe.SearchFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.conveniencerecipe.MypageActivity;
import com.conveniencerecipe.NoticeActivity;
import com.conveniencerecipe.R;
import com.conveniencerecipe.SearchResultActivity;
import com.conveniencerecipe.ThemeActivity;
import com.conveniencerecipe.WriteRecipe.WriteRecipeMainActivity;


/**
 * Created by ccei 34 on 2016-07-29.
 */
public class IngrediFragment extends Fragment {
    ImageView ingredient0,ingredient1,ingredient2,ingredient3,ingredient4,ingredient5,ingredient6
            ,ingredient7,ingredient8,ingredient9,ingredient10,ingredient11;
    RecyclerView recyclerView;
    public IngrediFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_ingredi,null);

        ingredient0 = (ImageView) view.findViewById(R.id.ingredient0);
        ingredient1 = (ImageView) view.findViewById(R.id.ingredient1);
        ingredient2 = (ImageView) view.findViewById(R.id.ingredient2);
        ingredient3 = (ImageView) view.findViewById(R.id.ingredient3);
        ingredient4 = (ImageView) view.findViewById(R.id.ingredient4);
        ingredient5 = (ImageView) view.findViewById(R.id.ingredient5);
        ingredient6 = (ImageView) view.findViewById(R.id.ingredient6);
        ingredient7 = (ImageView) view.findViewById(R.id.ingredient7);
        ingredient8 = (ImageView) view.findViewById(R.id.ingredient8);
        ingredient9 = (ImageView) view.findViewById(R.id.ingredient9);
        ingredient10 = (ImageView) view.findViewById(R.id.ingredient10);
        ingredient11 = (ImageView) view.findViewById(R.id.ingredient11);

        ingredient0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","0");
                i.putExtra("option","면류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","1");
                i.putExtra("option","김밥류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","2");
                i.putExtra("option","핫바류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","3");
                i.putExtra("option","통조림");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","4");
                i.putExtra("option","빵류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","5");
                i.putExtra("option","육류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","group&id=6");
                i.putExtra("option","유제품");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","7");
                i.putExtra("option","음료/주류");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","8");
                i.putExtra("option","가공식품&냉동");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","9");
                i.putExtra("option","스낵");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });

        ingredient10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","10");
                i.putExtra("option","도시락");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        ingredient11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","11");
                i.putExtra("option","식재료");
                i.putExtra("sort","group");
                startActivity(i);
            }
        });
        return view;
    }
}