package com.conveniencerecipe.NoticeFragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.CustomProgressDialog;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.MypageActivity;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.PropertyManager;
import com.conveniencerecipe.QnaDetailActivity;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;
import com.conveniencerecipe.RecipeCommentActivity;
import com.conveniencerecipe.UserPageActivity;

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
 * Created by ccei 34 on 2016-07-29.
 */
public class MynoticeFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String sortnum;
    String userId;
    ImageView nonotice;
    RelativeLayout setback;


    public MynoticeFragment() {
    }
    public static MynoticeFragment newInstance(int initValue){
        MynoticeFragment mynoticeFragment = new MynoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value",initValue);
        mynoticeFragment.setArguments(bundle);
        return mynoticeFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notice_mynotice,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.mn_recyclerview);
        setback = (RelativeLayout)view.findViewById(R.id.setback);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        PropertyManager propertyManager = PropertyManager.getInstance();
        userId = propertyManager.getId();
        nonotice = (ImageView)view.findViewById(R.id.nonotice);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncNoticeJSONList().execute("data");
    }

    public class FMViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<NoticeListData> items = new ArrayList<NoticeListData>();

        public FMViewAdapter(ArrayList<NoticeListData> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_notice_my, parent, false);
            return new FMViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((FMViewHolder)holder).setMyData(items.get(position));
            ((FMViewHolder)holder).ProfileImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    Intent intent;
                    if(RecipeActivity.recipeDetailData.userId.equals(userId)) {
                        intent = new Intent(getActivity(), MypageActivity.class);
                    } else {
                        intent = new Intent(getActivity(), UserPageActivity.class);
                    }
                    intent.putExtra("userId", RecipeActivity.recipeDetailData.userId);
                    startActivity(intent);
                }
            });

            if(sortnum == "1"){
                ((FMViewHolder)holder).contentLayout.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RecipeCommentActivity.class);
                        intent.putExtra("recipeId", items.get(position).sort_id);
                        startActivity(intent);
                    }
                });
            }else if(sortnum == "2"){
                ((FMViewHolder)holder).contentLayout.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RecipeActivity.class);
                        intent.putExtra("recipeID", items.get(position).sort_id);
                        startActivity(intent);
                    }
                });
            }else if(sortnum == "3"){
                ((FMViewHolder)holder).contentLayout.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), UserPageActivity.class);
                        intent.putExtra("userId", items.get(position).userId);
                        startActivity(intent);
                    }
                });
            }
            else if(sortnum == "4"){
                ((FMViewHolder)holder).contentLayout.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), QnaDetailActivity.class);
                        intent.putExtra("postingId", items.get(position).sort_id);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class FMViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ProfileImage;
        public final LinearLayout contentLayout;
        public final TextView content;
        public final TextView created_at;
        public String message;
        public String sort;

        public FMViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ProfileImage= (ImageView)itemView.findViewById(R.id.fn_image);
            contentLayout = (LinearLayout)itemView.findViewById(R.id.content_layout);
            content = (TextView)itemView.findViewById(R.id.fn_content);
            created_at = (TextView)itemView.findViewById(R.id.fn_time_text);
        }

        public void setMyData(final NoticeListData data) {
            if(data.sort.equals("1")){
                message = "회원님의 레시피에 댓글을 남겼습니다.";
                sortnum = "1";
            }else if(data.sort.equals("2")){
                message = "회원님의 레시피를 좋아합니다";
                sortnum = "2";
            }else if(data.sort.equals("3")){
                message = "회원님의 게시물에 댓글을 남겼습니다";
                sortnum = "4";
            }else if(data.sort.equals("4")){
                message = "회원님을 팔로우했습니다";
                sortnum = "3";
            }else if(data.sort.equals("5")) {
                message = "회원님의 레시피를 스크랩했습니다.";
                sortnum = "2";
            }

            final SpannableStringBuilder span = new SpannableStringBuilder(data.nickname+"님이 "+message);
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, data.nickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(!data.profile_img.equals("null")) {
                Glide.with(MyApplication.RecipeContext()).load(Uri.parse(data.profile_img)).into(ProfileImage);
            } else {
                ProfileImage.setImageResource(R.drawable.image_profile);
            }

            content.setText(span);
            created_at.setText(data.created_at);
        }

    }

    public class AsyncNoticeJSONList extends AsyncTask<String, Integer, ArrayList<NoticeListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<NoticeListData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_NOTICE_MY,userId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONNoticeList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(ArrayList<NoticeListData> result) {
            dialog.dismiss();

            if(result != null && result.size() > 0){
                FMViewAdapter fmViewAdapter = new FMViewAdapter(result);
                recyclerView.setAdapter(fmViewAdapter);
                fmViewAdapter.notifyDataSetChanged();
                nonotice.setVisibility(View.GONE);
                setback.setBackgroundColor(Color.WHITE);
            }else{
                nonotice.setVisibility(View.VISIBLE);
                setback.setBackgroundColor(Color.parseColor( "#f3f3f3" ));
            }
        }
    }
    public static class NoticeListData {
        public String userId, nickname, profile_img,sort,sort_id,created_at;

        public NoticeListData(String userId, String nickname, String profile_img, String sort, String sort_id,String created_at) {
            this.userId = userId;
            this.nickname = nickname;
            this.profile_img = profile_img;
            this.sort = sort;
            this.sort_id = sort_id;
            this.created_at = created_at;
        }

        public NoticeListData(){}
    }
}