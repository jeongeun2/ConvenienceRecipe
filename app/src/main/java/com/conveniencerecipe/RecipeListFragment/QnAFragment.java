package com.conveniencerecipe.RecipeListFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.CustomProgressDialog;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.QnaDetailActivity;
import com.conveniencerecipe.R;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;
/**
 * Created by ccei 34 on 2016-07-29.
 */
public class QnAFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    QnaViewAdapter qnaViewAdapter;
    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    public static AsyncQnAJSONList asyncQnAJSONList;

    ImageView moreBtn;
    public static int page;

    public QnAFragment() {
    }
    public static QnAFragment newInstance(int initValue){
        QnAFragment qnaFragment = new QnAFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value",initValue);
        qnaFragment.setArguments(bundle);
        return qnaFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view =  inflater.inflate(R.layout.fragment_recipelist_qna,null);
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
                        new AsyncQnAJSONList().execute();
                    }
                }, 2000);
            }
        });
        recyclerView = (RecyclerView)view.findViewById(R.id.qna_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        asyncQnAJSONList = new AsyncQnAJSONList();

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

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        new AsyncQnAJSONList().execute();
    }

    public class QnaViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<QnaData> items;

        public QnaViewAdapter(List<QnaData> items) {
            this.items = items;
        }

        @Override
        public QnaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_qna, parent, false);
            return new QnaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((QnaViewHolder)holder).setMyData(items.get(position));

            ((QnaViewHolder)holder).mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), QnaDetailActivity.class);
                    intent.putExtra("postingId", items.get(position).postingId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class QnaViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        ImageView imageView;
        TextView title, nickname, time, commentNum;

        public QnaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView)itemView.findViewById(R.id.qnalist_image);
            title = (TextView)itemView.findViewById(R.id.title_text);
            nickname = (TextView)itemView.findViewById(R.id.nickname_text);
            time = (TextView)itemView.findViewById(R.id.time_text);
            commentNum = (TextView)itemView.findViewById(R.id.comment_num);
        }

        public void setMyData(QnaData data) {
            if(!data.image.equals("null")) {
                Glide.with(MyApplication.RecipeContext()).load(Uri.parse(data.image)).override(276,276).into(imageView);
            }else {
                imageView.setVisibility(View.GONE);
            }
            title.setText(data.title);
            nickname.setText(data.nickname+" | ");
            time.setText(data.time);
            commentNum.setText(data.commentNum);
        }
    }

    //뭐먹지?목록 가져오기
    public class AsyncQnAJSONList extends AsyncTask<String, Integer, ArrayList<QnaData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<QnaData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_QNA_ALL_SELECT, page++))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONQnaList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(ArrayList<QnaData> result) {
            dialog.dismiss();
            if(result == null || result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
            if(result != null && result.size() > 0){
                qnaViewAdapter = new QnaViewAdapter(result);
                recyclerView.setAdapter(qnaViewAdapter);
                qnaViewAdapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }
    }

    public class AsyncMoreJSONList extends AsyncTask<String, Integer, ArrayList<QnaData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<QnaData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_QNA_ALL_SELECT, page++))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONQnaList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(ArrayList<QnaData> result) {
            dialog.dismiss();
            if(result == null || result.size() < 16) {
                moreBtn.setVisibility(View.GONE);
            }
            if(result != null && result.size() > 0){
                for(int i = 0; i < result.size(); i++) {
                    qnaViewAdapter.items.add(result.get(i));
                }
                qnaViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public static class QnaData {
        public String title, nickname, time, postingId, image, commentNum;

        public QnaData(){}

        public QnaData(String title, String nickname, String time, String postingId, String image, String commentNum) {
            this.title = title;
            this.nickname = nickname;
            this.time = time;
            this.postingId = postingId;
            this.image = image;
            this.commentNum = commentNum;
        }
    }
}