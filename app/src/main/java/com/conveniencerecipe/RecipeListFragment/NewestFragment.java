package com.conveniencerecipe.RecipeListFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conveniencerecipe.CustomProgressDialog;
import com.conveniencerecipe.MainActivity;
import com.conveniencerecipe.MyPagerAdapter;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.PropertyManager;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeListData;
import com.conveniencerecipe.RecipeRecyclerViewAdapter;
import com.conveniencerecipe.ViewPagerAdapter;
import com.matthewtamlin.sliding_intro_screen_library.DotIndicator;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-19.
 */
public class NewestFragment extends Fragment {
    private ArrayList<ImageView> dots;
    private static Integer NUM_PAGES;

    public static MainActivity owner;
    ViewPager pager;
    ViewPagerAdapter mAdapter;
    private MyPagerAdapter adapter;
    RecyclerView rv;
    RecipeRecyclerViewAdapter newestRecyclerViewAdapter;
    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView moreBtn;

    public static AsyncNewestJSONList newestJSONList;
    public static String banner1, banner2;
    int page;

    public NewestFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        new AsyncNewestJSONList().execute();
    }

    public static NewestFragment newInstance(int initValue){
        NewestFragment newestFragment = new NewestFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value",initValue);
        newestFragment.setArguments(bundle);
        return newestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipelist_newest,container,false);
        pager = (ViewPager)view.findViewById(R.id.top_pager);
        final DotIndicator indicator = (DotIndicator)view.findViewById( R.id.main_indicator_ad );

        owner = (MainActivity)getActivity();
        MyPagerAdapter adapter= new MyPagerAdapter(getLayoutInflater(getArguments()));
        pager.setAdapter(adapter);
        indicator.setSelectedDotColor( Color.parseColor( "#f44236" ) );
        indicator.setUnselectedDotColor( Color.parseColor( "#CFCFCF" ) );

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.pink);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        moreBtn.setVisibility(View.VISIBLE);
                        new AsyncNewestJSONList().execute();
                    }
                }, 2000);
            }
        });

        final int pageCount = 2;
        //pager.setAdapter( Models );
        indicator.setNumberOfItems( pageCount);

        final Runnable setImageRunnable = new Runnable()
        {
            public void run()
            {
                int currentPage = pager.getCurrentItem();
                if( currentPage >= pageCount - 1 ) pager.setCurrentItem( 0, true );
                else pager.setCurrentItem( currentPage + 1, true );
                indicator.setSelectedItem( ( currentPage + 1 == pageCount ) ? 0 : currentPage + 1, true );
            }
        };
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                indicator.setSelectedItem( pager.getCurrentItem(), true );
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        newestJSONList = new AsyncNewestJSONList();

        page = 1;
        moreBtn = (ImageView)view.findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncMoreJSONList().execute();
            }
        });

        return view;
    }

    public class AsyncNewestJSONList extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<RecipeListData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                String userId = PropertyManager.getInstance().getId();
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_NEWEST_LIST_SELECT, "uptodate", userId, page++))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONRecipeList(new StringBuilder(retruedJson));
                }
            }catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
                e.printStackTrace();
            } finally {
                if(response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeListData> result) {
            dialog.dismiss();
            if(result == null || result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
            if(result != null && result.size() > 0){
                newestRecyclerViewAdapter = new RecipeRecyclerViewAdapter(owner, result);
                rv.setAdapter(newestRecyclerViewAdapter);
                newestRecyclerViewAdapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }
    }

    public class AsyncMoreJSONList extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<RecipeListData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                String userId = PropertyManager.getInstance().getId();
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_NEWEST_LIST_SELECT, "uptodate", userId, page++))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONRecipeList(new StringBuilder(retruedJson));
                }
            }catch (UnknownHostException une) {
                e("aaa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
            } catch (Exception e) {
                e("ccc", e.toString());
                e.printStackTrace();
            } finally {
                if(response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeListData> result) {
            dialog.dismiss();
            if(result == null || result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
            if(result != null && result.size() > 0){
                for(int i = 0; i < result.size(); i++) {
                    newestRecyclerViewAdapter.items.add(result.get(i));
                }
                newestRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}