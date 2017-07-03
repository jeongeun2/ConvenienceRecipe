package com.conveniencerecipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by ccei on 2016-08-10.
 */
public class FollowRecyclerViewAdapter extends RecyclerView.Adapter<FollowRecyclerViewAdapter.ViewHolder> {
    private ArrayList<UserPageActivity.FollowData> items;
    MainActivity mainOwner;

    boolean flag;
    public FollowRecyclerViewAdapter(Context context, ArrayList<UserPageActivity.FollowData> items) {
        this.items = items;
        mainOwner = (MainActivity)context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView userfollowbtn;
        public final View mView;
        public final ImageView followImage;
        public String userId = null;

        public final TextView followName;

        public ViewHolder(View view) {
            super(view);

            userfollowbtn = (ImageView) view.findViewById(R.id.userpage_follow);
            mView = view;
            followImage = (ImageView) view.findViewById(R.id.mypage_follow_image);
            followName = (TextView) view.findViewById(R.id.mypage_follow_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypage_follow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.followName.setText(items.get(position).nickname);
        final String Id = PropertyManager.getInstance().getId();
        final int image[] = {R.drawable.follow_plus, R.drawable.follow_done};
        final String myId = PropertyManager.getInstance().getId();

        if(!items.get(position).profileImg.equals("null")) {
            Glide.with(MyApplication.RecipeContext()).load(Uri.parse(items.get(position).profileImg)).into(holder.followImage);
        } else {
            holder.followImage.setImageResource(R.drawable.image_profile);
        }

        holder.followImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(items.get(position).userId.equals(myId)) {
                    intent = new Intent(MyApplication.RecipeContext(), MypageActivity.class);
                } else {
                    intent = new Intent(MyApplication.RecipeContext(), UserPageActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", items.get(position).userId);
                MyApplication.RecipeContext().startActivity(intent);
            }
        });

        holder.followName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(items.get(position).userId.equals(myId)) {
                    intent = new Intent(MyApplication.RecipeContext(), MypageActivity.class);
                } else {
                    intent = new Intent(MyApplication.RecipeContext(), UserPageActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", items.get(position).userId);
                MyApplication.RecipeContext().startActivity(intent);
            }
        });

        if(items.get(position).followBack.equals("M")){
            holder.userfollowbtn.setVisibility(View.GONE);
        }else if(items.get(position).followBack.equals("N")){
            holder.userfollowbtn.setImageResource(R.drawable.follow_plus);
        }else {
            holder.userfollowbtn.setImageResource(R.drawable.follow_done);
        }

        holder.userfollowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.get(position).followBack.equals("N")) {
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
                                        .add("id",Id)
                                        .add("userId", items.get(position).userId)
                                        .build();
                                //요청 세팅
                                Request request = new Request.Builder()
                                        .url(NetworkDefineConstant.SERVER_URL_FOLLOW_CLICK)
                                        .post(fileUploadBody) //반드시 post로
                                        .build();
                                //동기 방식
                                response = toServer.newCall(request).execute();

                                flag = response.isSuccessful();
                                //응답 코드 200등등
                                int responseCode = response.code();
                                if (flag) {
                                    e("팔로우response결과", response.message()); //읃답에 대한 메세지(OK)
                                    e("response응답바디", response.body().string()); //json으로 변신
                                    mainOwner.runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            items.get(position).followBack = "Y";
                                            holder.userfollowbtn.setImageResource(R.drawable.follow_done);
                                        }
                                    });
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
                                        .add("id",Id)
                                        .add("userId",items.get(position).userId)
                                        .build();
                                //요청 세팅
                                Request request = new Request.Builder()
                                        .url(NetworkDefineConstant.SERVER_URL_UNFOLLOW_CLICK)
                                        .post(fileUploadBody) //반드시 post로
                                        .build();
                                //동기 방식
                                response = toServer.newCall(request).execute();

                                flag = response.isSuccessful();
                                //응답 코드 200등등
                                int responseCode = response.code();
                                if (flag) {
                                    e("팔로우취소response결과", response.message()); //읃답에 대한 메세지(OK)
                                    e("response응답바디", response.body().string()); //json으로 변신
                                    mainOwner.runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            items.get(position).followBack = "N";
                                            holder.userfollowbtn.setImageResource(R.drawable.follow_plus);
                                        }
                                    });
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

    @Override
    public int getItemCount() {
        return items.size();
    }
}
