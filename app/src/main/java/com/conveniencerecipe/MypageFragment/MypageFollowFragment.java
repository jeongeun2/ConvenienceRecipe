package com.conveniencerecipe.MypageFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conveniencerecipe.FollowRecyclerViewAdapter;
import com.conveniencerecipe.MypageActivity;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.UserPageActivity;

/**
 * Created by ccei on 2016-08-10.
 */
public class MypageFollowFragment extends Fragment {
    RecyclerView rv;
    FollowRecyclerViewAdapter activityAdapter;
    int i;
    String s;
    final int image[] = {R.drawable.bt_unfollow, R.drawable.bt_follow};

    public MypageFollowFragment() {
    }

    public static MypageFollowFragment newInstance(int i, String s){
        MypageFollowFragment mypageFollowFragment = new MypageFollowFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("i", i);
        bundle.putString("s", s);
        mypageFollowFragment.setArguments(bundle);
        return mypageFollowFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage_follow,container,false);
        if (getArguments() != null) {
            i = getArguments().getInt("i");
            s = getArguments().getString("s");
        }

        rv = (RecyclerView)view.findViewById(R.id.ma_recyclerview);
        if(i == 0) {
            if(s.equals("M")) {
                activityAdapter = new FollowRecyclerViewAdapter(NewestFragment.owner,MypageActivity.userpagedata.follower);
            } else {
                activityAdapter = new FollowRecyclerViewAdapter(NewestFragment.owner,UserPageActivity.userpagedata.follower);
            }
        } else {
            if(s.equals("M")) {
                activityAdapter = new FollowRecyclerViewAdapter(NewestFragment.owner,MypageActivity.userpagedata.following);
            } else {
                activityAdapter = new FollowRecyclerViewAdapter(NewestFragment.owner,UserPageActivity.userpagedata.following);
            }
        }
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(activityAdapter);

        return view;
    }
}
