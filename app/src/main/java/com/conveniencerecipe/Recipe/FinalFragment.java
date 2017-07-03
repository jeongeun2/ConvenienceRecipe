package com.conveniencerecipe.Recipe;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.CustomProgressDialog;
import com.conveniencerecipe.MainActivity;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.MypageActivity;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.PropertyManager;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;
import com.conveniencerecipe.RecipeCommentActivity;
import com.conveniencerecipe.RecipeEditDialog;
import com.conveniencerecipe.RecipeListData;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.RecommendRecyclerview;
import com.conveniencerecipe.UserPageActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-28.
 */
public class FinalFragment extends Fragment{
    RecyclerView rv;
    RecommendRecyclerview recyclerViewAdapter;
    ViewPager rec_pager;
    FinalFragmentViewPagerAdapter rec_adapter;
    FloatingActionButton bookmark;
    FloatingActionButton comment;
    FloatingActionMenu materialDesignFAM;

    ImageView profileImg;
    TextView nickname;
    String userId;
    MainActivity mainOwner = NewestFragment.owner;

    public FinalFragment() {}

    public static FinalFragment newInstance(){
        FinalFragment fragment = new FinalFragment();

        return fragment;
    }

    boolean flag;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_final, container, false);

        rv = (RecyclerView)view.findViewById(R.id.final_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(1, 300);
        rv.setLayoutManager(layoutManager);

        profileImg = (ImageView)view.findViewById(R.id.profile_image);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
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
        if(RecipeActivity.recipeDetailData.profileImg != null && !RecipeActivity.recipeDetailData.profileImg.equals("null")) {
            Glide.with(MyApplication.RecipeContext()).load(Uri.parse(RecipeActivity.recipeDetailData.profileImg)).into(profileImg);
        } else {
            profileImg.setImageResource(R.drawable.image_profile);
        }
        nickname = (TextView)view.findViewById(R.id.nickname_text);
        nickname.setText(RecipeActivity.recipeDetailData.nickname);

        materialDesignFAM = (FloatingActionMenu)view.findViewById(R.id.floating_action_menu);
        bookmark = (FloatingActionButton)view.findViewById(R.id.action_menu_item1);
        comment = (FloatingActionButton)view.findViewById(R.id.action_menu_item2);

        comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecipeCommentActivity.class);
                intent.putExtra("recipeId", RecipeActivity.recipeId);
                startActivity(intent);
            }
        });


        userId = PropertyManager.getInstance().getId();
        if(!RecipeActivity.recipeDetailData.userId.equals(userId)) {
            view.findViewById(R.id.edit_btn).setVisibility(View.GONE);
        }
        view.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeEditDialog recipeEditDialog = new RecipeEditDialog(getContext());
                recipeEditDialog.show();
            }
        });
        if(RecipeActivity.recipeDetailData.userId.equals(userId)) {
            bookmark.setVisibility(View.GONE);
        }else if(RecipeActivity.recipeDetailData.bookmarked.equals("Y")) {
            bookmark.setImageResource(R.drawable.recipe_scrap_1);
        } else if(RecipeActivity.recipeDetailData.bookmarked.equals("N")){
            bookmark.setImageResource(R.drawable.recipe_scrap);
        }

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RecipeActivity.recipeDetailData.bookmarked.equals("N")) {
                    bookmark.setImageResource(R.drawable.recipe_scrap_1);

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
                                        .add("recipe_id", RecipeActivity.recipeDetailData.recipeId)
                                        .build();
                                //요청 세팅
                                Request request = new Request.Builder()
                                        .url(NetworkDefineConstant.SERVER_BOOKMARK)
                                        .post(likeReqBody) //반드시 post로
                                        .build();
                                //동기 방식
                                response = toServer.newCall(request).execute();

                                flag = response.isSuccessful();
                                //응답 코드 200등등
                                int responseCode = response.code();
                                if (flag) {
                                    e("북마크결과", response.message()); //읃답에 대한 메세지(OK)
                                    e("response응답바디", response.body().string()); //json으로 변신
                                    mainOwner.runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            bookmark.setImageResource(R.drawable.recipe_scrap_1);
                                            RecipeActivity.recipeDetailData.bookmarked = "Y";
                                        }
                                    });
                                } else {
                                    mainOwner.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mainOwner,"삭제된 레시피입니다.",Toast.LENGTH_SHORT).show();
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
                } else if(RecipeActivity.recipeDetailData.bookmarked.equals("Y")){
                    bookmark.setImageResource(R.drawable.recipe_scrap);

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
                                        .add("recipe_id", RecipeActivity.recipeDetailData.recipeId)
                                        .build();
                                //요청 세팅
                                Request request = new Request.Builder()
                                        .url(NetworkDefineConstant.SERVER_UNBOOKMARK)
                                        .post(fileUploadBody) //반드시 post로
                                        .build();
                                //동기 방식
                                response = toServer.newCall(request).execute();

                                flag = response.isSuccessful();
                                //응답 코드 200등등
                                int responseCode = response.code();
                                if (flag) {
                                    e("북마크취소결과", response.message()); //읃답에 대한 메세지(OK)
                                    e("response응답바디", response.body().string()); //json으로 변신
                                    mainOwner.runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            bookmark.setImageResource(R.drawable.recipe_scrap);
                                            RecipeActivity.recipeDetailData.bookmarked = "N";
                                        }
                                    });
                                } else {
                                    mainOwner.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mainOwner,"삭제된 레시피입니다.",Toast.LENGTH_SHORT).show();
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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncRecommendData().execute();
    }

    public class AsyncRecommendData extends AsyncTask<String, Integer, ArrayList<RecipeListData>> {
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
                        .url(String.format(NetworkDefineConstant.SERVER_RECOMMEND, userId,RecipeActivity.recipeDetailData.recipeId))
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
                recyclerViewAdapter = new RecommendRecyclerview(result);
                rv.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void prevClick(View view) {
        RecipeActivity.pager.setCurrentItem(RecipeActivity.pager.getCurrentItem()-1, true);
    }
}