package com.conveniencerecipe.SearchFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.conveniencerecipe.R;
import com.conveniencerecipe.SearchResultActivity;


/**
 * Created by ccei 34 on 2016-07-29.
 */
public class PriceFragment extends Fragment {
    public static RadioButton price0,price1,price2,price3,price4;
    RecyclerView recyclerView;
    public PriceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_price,null);

        price0 = (RadioButton) view.findViewById(R.id.price0);
        price1 = (RadioButton) view.findViewById(R.id.price1);
        price2 = (RadioButton) view.findViewById(R.id.price2);
        price3 = (RadioButton) view.findViewById(R.id.price3);
        price4 = (RadioButton) view.findViewById(R.id.price4);

        price0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","0");
                i.putExtra("option","~ 3000");
                i.putExtra("sort","price");
                startActivity(i);
            }
        });
        price1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","1");
                i.putExtra("option","3000 ~ 5000");
                i.putExtra("sort","price");
                startActivity(i);
            }
        });
        price2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","2");
                i.putExtra("option","5000 ~ 10000");
                i.putExtra("sort","price");
                startActivity(i);
            }
        });
        price3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","3");
                i.putExtra("option","10000 ~ 15000");
                i.putExtra("sort","price");
                startActivity(i);
            }
        });
        price4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SearchResultActivity.class);
                i.putExtra("id","4");
                i.putExtra("option","15000 ~ ");
                i.putExtra("sort","price");
                startActivity(i);
            }
        });
        return view;
    }
}