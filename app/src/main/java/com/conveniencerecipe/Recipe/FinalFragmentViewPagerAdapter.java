package com.conveniencerecipe.Recipe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by pyoinsoo on 2016-07-29.
 */
public class FinalFragmentViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> items = new ArrayList<Fragment>();

    public void add(Fragment item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public FinalFragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.45f;
    }
}
