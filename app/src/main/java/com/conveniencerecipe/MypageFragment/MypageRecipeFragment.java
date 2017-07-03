package com.conveniencerecipe.MypageFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conveniencerecipe.MypageActivity;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.RecipeRecyclerViewAdapter;
import com.conveniencerecipe.UserPageActivity;


/**
 * Created by ccei 34 on 2016-07-29.
 */
public class MypageRecipeFragment extends Fragment {
    RecyclerView rv;
    RecipeRecyclerViewAdapter activityAdapter;
    int i;
    String s;
    public MypageRecipeFragment() {}

    public static MypageRecipeFragment newInstance(int i, String s){
        MypageRecipeFragment mypageRecipeFragment = new MypageRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("i", i);
        bundle.putString("s", s);
        mypageRecipeFragment.setArguments(bundle);
        return mypageRecipeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage_activity,container,false);
        if (getArguments() != null) {
            i = getArguments().getInt("i");
            s = getArguments().getString("s");
        }

        rv = (RecyclerView)view.findViewById(R.id.ma_recyclerview);
        if(i == 0) {
            if(s.equals("M")) {
                activityAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,MypageActivity.userpagedata.activity);
            } else {
                activityAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,UserPageActivity.userpagedata.activity);
            }
        } else {
            if(s.equals("M")) {
                activityAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,MypageActivity.userpagedata.scrap);
            } else {
                activityAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,UserPageActivity.userpagedata.scrap);
            }
        }

        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(activityAdapter);

        return view;
    }
}