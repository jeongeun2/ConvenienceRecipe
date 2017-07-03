package com.conveniencerecipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.SearchFragment.PriceFragment;

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
 * Created by ccei 34 on 2016-08-10.
 */
public class SearchResultActivity extends FontActivity {
    RecyclerView rv;
    RecipeRecyclerViewAdapter recyclerViewAdapter;
    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    TextView ingredientoption;
    ImageView no;
    ImageView moreBtn;
    int page;

    Intent intent;
    String id;
    String option;
    String userId;
    String sort;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        intent = getIntent();
        id = intent.getStringExtra("id");
        option = intent.getStringExtra("option");

        page = 1;
        sort = intent.getStringExtra("sort");
        no = (ImageView)findViewById(R.id.nosearch);
        no.setVisibility(View.GONE);
        moreBtn = (ImageView)findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncMoreJSONList().execute();
            }
        });

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
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

        ingredientoption = (TextView)findViewById(R.id.serch_option);
        ingredientoption.setText(option);

        PropertyManager propertyManager = PropertyManager.getInstance();
        userId = propertyManager.getId();

        rv = (RecyclerView)findViewById(R.id.recyclerview);
        rv.setLayoutManager(new GridLayoutManager(SearchResultActivity.this, 2, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        new AsyncPopularJSONList().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PriceFragment.price0.setChecked(false);
        PriceFragment.price1.setChecked(false);
        PriceFragment.price2.setChecked(false);
        PriceFragment.price3.setChecked(false);
        PriceFragment.price4.setChecked(false);
    }

    public void search_backClick(View view) {
        PriceFragment.price0.setChecked(false);
        PriceFragment.price1.setChecked(false);
        PriceFragment.price2.setChecked(false);
        PriceFragment.price3.setChecked(false);
        PriceFragment.price4.setChecked(false);
        finish();
    }

    public class AsyncPopularJSONList extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(SearchResultActivity.this);
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
                        .url(String.format(NetworkDefineConstant.SERVER_URL_SEARCH,sort,id,userId,page++))
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
                no.setVisibility(View.GONE);
                recyclerViewAdapter = new RecipeRecyclerViewAdapter(NewestFragment.owner,result);
                rv.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
            if(result == null) {
                no.setVisibility(View.VISIBLE);
                moreBtn.setVisibility(View.GONE);
            } else if(result.size() < 16){
                moreBtn.setVisibility(View.GONE);
            }
            refreshLayout.setRefreshing(false);
        }
    }

    public class AsyncMoreJSONList extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(SearchResultActivity.this);
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
                        .url(String.format(NetworkDefineConstant.SERVER_URL_SEARCH,sort,id,userId,page++))
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
                no.setVisibility(View.GONE);
                for(int i = 0; i < result.size(); i++) {
                    recyclerViewAdapter.items.add(result.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }if(result == null){
                moreBtn.setVisibility(View.GONE);
                no.setVisibility(View.VISIBLE);
            } else if(result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
        }
    }
}