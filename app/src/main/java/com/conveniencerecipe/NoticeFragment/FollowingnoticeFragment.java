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
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;
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
public class FollowingnoticeFragment extends Fragment {
    ImageView nonotice;
    RecyclerView recyclerView;
    String userId;
    RelativeLayout setback;
    RecyclerView.LayoutManager layoutManager;


    public FollowingnoticeFragment() {
    }
    public static FollowingnoticeFragment newInstance(int initValue){
        FollowingnoticeFragment followingnoticeFragment = new FollowingnoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value",initValue);
        followingnoticeFragment.setArguments(bundle);
        return followingnoticeFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_notice_mynotice,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.mn_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        PropertyManager propertyManager = PropertyManager.getInstance();
        userId = propertyManager.getId();

        setback = (RelativeLayout)view.findViewById(R.id.setback);
        nonotice = (ImageView)view.findViewById(R.id.nonotice);
        new AsyncNoticeJSONList().execute("data");


        return view;
    }

    public class FMViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<NoticeFollowLlist> items = new ArrayList<NoticeFollowLlist>();

        public FMViewAdapter(ArrayList<NoticeFollowLlist> items) {
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
            ((FMViewHolder)holder).contentLayout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), RecipeActivity.class);
                    intent.putExtra("recipeID", items.get(position).recipe_id);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class FMViewHolder extends RecyclerView.ViewHolder {
        // ImageView imageView;
        // TextView content, time;
        public final ImageView ProfileImage;
        public final LinearLayout contentLayout;
        public final TextView content;
        public final TextView created_at;
        public String message;

        public FMViewHolder(View itemView) {
            super(itemView);
            ProfileImage= (ImageView)itemView.findViewById(R.id.fn_image);
            contentLayout = (LinearLayout)itemView.findViewById(R.id.content_layout);
            content = (TextView)itemView.findViewById(R.id.fn_content);
            created_at = (TextView)itemView.findViewById(R.id.fn_time_text);
        }

        public void setMyData(NoticeFollowLlist data) {
            message = " 레시피를 올렸습니다.";

            final SpannableStringBuilder span = new SpannableStringBuilder(data.nickname+"님이 "+data.title+message);
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, data.nickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new StyleSpan(Typeface.BOLD), data.nickname.length()+2, data.nickname.length()+3+data.title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if(!data.profile_img.equals("null")) {
                Glide.with(MyApplication.RecipeContext()).load(Uri.parse(data.profile_img)).into(ProfileImage);
            } else {
                ProfileImage.setImageResource(R.drawable.image_profile);
            }
            content.setText(span);
            created_at.setText(data.created_at);
        }
    }

    public class AsyncNoticeJSONList extends AsyncTask<String, Integer, ArrayList<NoticeFollowLlist>> {
        CustomProgressDialog dialog = new CustomProgressDialog(getContext());
        @Override
        protected ArrayList<NoticeFollowLlist> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_NOTICE_FOLLOW,userId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONFollowNoticeList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(ArrayList<NoticeFollowLlist> result) {
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

    public static class NoticeFollowLlist {
        public String userId, nickname, profile_img,recipe_id,title,created_at;

        public NoticeFollowLlist(String userId, String nickname, String profile_img, String recipe_id, String title, String created_at) {
            this.userId = userId;
            this.nickname = nickname;
            this.profile_img = profile_img;
            this.recipe_id = recipe_id;
            this.title = title;
            this.created_at = created_at;
        }

        public NoticeFollowLlist(){}
    }
}