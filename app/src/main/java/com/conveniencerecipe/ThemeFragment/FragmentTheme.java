package com.conveniencerecipe.ThemeFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conveniencerecipe.CustomProgressDialog;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.PropertyManager;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeListData;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.RecipeRecyclerViewAdapter;

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
 * Created by ccei 34 on 2016-08-08.
 */
public class FragmentTheme extends Fragment{
    RecyclerView rv;
    RecipeRecyclerViewAdapter recyclerViewAdapter;
    String themeId;
    String userId;
    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView moreBtn;
    ImageView notheme;
    int page;

    public FragmentTheme() {
    }
    public static FragmentTheme newInstance(String themeId){
        FragmentTheme themeOne = new FragmentTheme();
        Bundle bundle = new Bundle();
        bundle.putString("themeId",themeId);
        themeOne.setArguments(bundle);

        return themeOne;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipelist_popular,container,false);
        page = 1;

        PropertyManager propertyManager = PropertyManager.getInstance();
        userId = propertyManager.getId();
        if (getArguments() != null) {
            themeId = getArguments().getString("themeId");
        }
        moreBtn = (ImageView)view.findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncMoreJSONList().execute();
            }
        });

        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        notheme = (ImageView)view.findViewById(R.id.nofollowing);
        notheme.setImageResource(R.drawable.noresult);

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
                        new AsyncPopularJSONList().execute();
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        new AsyncPopularJSONList().execute();
    }

    public class AsyncPopularJSONList extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
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

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_SEARCH,"theme",themeId,userId,page++))
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

            if(result != null && result.size() > 0){
                notheme.setVisibility(View.GONE);
                recyclerViewAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,result);
                rv.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
            if(result == null){
                moreBtn.setVisibility(View.GONE);
                notheme.setVisibility(View.VISIBLE);
            } else if(result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
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
                        .url(String.format(NetworkDefineConstant.SERVER_URL_SEARCH,"theme",themeId,userId,page++))
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

            if(result != null && result.size() > 0) {
                notheme.setVisibility(View.GONE);
                for(int i = 0; i < result.size(); i++) {
                    recyclerViewAdapter.items.add(result.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }
            if(result == null){
                moreBtn.setVisibility(View.GONE);
                notheme.setVisibility(View.VISIBLE);
            } else if(result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
        }
    }
}