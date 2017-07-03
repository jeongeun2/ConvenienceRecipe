package com.conveniencerecipe;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-08-23.
 */
public class RecommendRecyclerview extends RecyclerView.Adapter<RecommendRecyclerview.ViewHolder>{
    private ArrayList<RecipeListData> items = new ArrayList<RecipeListData>();

    public RecommendRecyclerview(ArrayList<RecipeListData> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final int image[] = {R.drawable.ic_like, R.drawable.image_main_likefull};

        public final View mView;
        public final ImageView RecipeImage;
        public final TextView RecipeTitle;
        public final TextView likenum;
        public final TextView commentnum;
        public final ToggleButton likeBtn;
        public ImageView event1, event2, event3;
        public String recipeId = null;
        public String liked = null;

        boolean flag;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            likeBtn = (ToggleButton)view.findViewById(R.id.p_recipe_like);
            RecipeImage = (ImageView)view.findViewById(R.id.recipemain_image);
            RecipeTitle = (TextView)view.findViewById(R.id.recipe_title);
            likenum = (TextView)view.findViewById(R.id.like_num);
            commentnum = (TextView)view.findViewById(R.id.comment_num);
            event1 = (ImageView)view.findViewById(R.id.event1);
            event2 = (ImageView)view.findViewById(R.id.event2);
            event3 = (ImageView)view.findViewById(R.id.event3);

            final String userId = PropertyManager.getInstance().getId();
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(liked.equals("N")) {
                        likeBtn.setBackgroundResource(image[1]);

                        new Thread(new Runnable() {
                            public void run() {
                                Response response = null;
                                try {
                                    //업로드는 타임 및 리드타임을 넉넉히 준다.
                                    OkHttpClient toServer = new OkHttpClient.Builder()
                                            .connectTimeout(30, TimeUnit.SECONDS)
                                            .readTimeout(30, TimeUnit.SECONDS)
                                            .build();
                                    //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                                    RequestBody likeReqBody = new FormBody.Builder()
                                            .add("id", userId)
                                            .add("recipe_id", recipeId)
                                            .build();
                                    //요청 세팅
                                    Request request = new Request.Builder()
                                            .url(NetworkDefineConstant.SERVER_URL_LIKE_CLICK)
                                            .post(likeReqBody) //반드시 post로
                                            .build();
                                    //동기 방식
                                    response = toServer.newCall(request).execute();

                                    flag = response.isSuccessful();
                                    //응답 코드 200등등
                                    int responseCode = response.code();
                                    if (flag) {
                                        e("좋아요response결과", response.message()); //읃답에 대한 메세지(OK)
                                        e("response응답바디", response.body().string()); //json으로 변신
                                    }
                                } catch (UnknownHostException une) {
                                    e("aaa", une.toString());
                                } catch (UnsupportedEncodingException uee) {
                                    e("bbb", uee.toString());
                                } catch (Exception e) {
                                    e("ccc", e.toString());
                                } finally {
                                    if(response != null) {
                                        response.close();
                                    }
                                }
                            }
                        }).start();
                    } else {
                        likeBtn.setBackgroundResource(image[0]);

                        new Thread(new Runnable() {
                            public void run() {
                                Response response = null;
                                try {
                                    //업로드는 타임 및 리드타임을 넉넉히 준다.
                                    OkHttpClient toServer = new OkHttpClient.Builder()
                                            .connectTimeout(30, TimeUnit.SECONDS)
                                            .readTimeout(30, TimeUnit.SECONDS)
                                            .build();
                                    //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                                    RequestBody fileUploadBody = new FormBody.Builder()
                                            .add("id", userId)
                                            .add("recipe_id", recipeId)
                                            .build();
                                    //요청 세팅
                                    Request request = new Request.Builder()
                                            .url(NetworkDefineConstant.SERVER_URL_UNLIKE_CLICK)
                                            .post(fileUploadBody) //반드시 post로
                                            .build();
                                    //동기 방식
                                    response = toServer.newCall(request).execute();

                                    flag = response.isSuccessful();
                                    //응답 코드 200등등
                                    int responseCode = response.code();
                                    if (flag) {
                                        e("좋아요취소response결과", response.message()); //읃답에 대한 메세지(OK)
                                        e("response응답바디", response.body().string()); //json으로 변신
                                    }
                                } catch (UnknownHostException une) {
                                    e("aaa", une.toString());
                                } catch (UnsupportedEncodingException uee) {
                                    e("bbb", uee.toString());
                                } catch (Exception e) {
                                    e("ccc", e.toString());
                                } finally {
                                    if(response != null) {
                                        response.close();
                                    }
                                }
                            }
                        }).start();
                    }
                }
            });
        }

        public void setMyData(RecipeListData data) {
            Glide.with(MyApplication.RecipeContext()).load(Uri.parse(data.image)).into(RecipeImage);
            RecipeTitle.setText(data.title);
            likenum.setText(data.likenum);
            commentnum.setText(data.commentnum);
            recipeId = data.recipeID;
            if(data.liked.equals("Y")) {
                likeBtn.setBackgroundResource(R.drawable.image_main_likefull);
            } else {
                likeBtn.setBackgroundResource(R.drawable.ic_like);
            }
            liked = data.liked;
            if(data.event.size() > 0) {
                switch (data.event.get(0)) {
                    case "1+1": event1.setImageResource(R.drawable.image_1plus1); break;
                    case "2+1": event1.setImageResource(R.drawable.image_2plus1); break;
                    case "3+1": event1.setImageResource(R.drawable.image_3plus1); break;
                }
            }
            if(data.event.size() > 1) {
                switch (data.event.get(1)) {
                    case "1+1": event2.setImageResource(R.drawable.image_1plus1); break;
                    case "2+1": event2.setImageResource(R.drawable.image_2plus1); break;
                    case "3+1": event2.setImageResource(R.drawable.image_3plus1); break;
                }
            }
            if(data.event.size() > 2) {
                switch (data.event.get(2)) {
                    case "1+1": event3.setImageResource(R.drawable.image_1plus1); break;
                    case "2+1": event3.setImageResource(R.drawable.image_2plus1); break;
                    case "3+1": event3.setImageResource(R.drawable.image_3plus1); break;
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendrecipe,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        ((ViewHolder)holder).setMyData(items.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.RecipeContext(), RecipeActivity.class);
                intent.putExtra("recipeID", items.get(position).recipeID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.RecipeContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}