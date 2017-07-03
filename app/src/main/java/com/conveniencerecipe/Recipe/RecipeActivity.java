package com.conveniencerecipe;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conveniencerecipe.Recipe.FinalFragment;
import com.conveniencerecipe.Recipe.FirstFragment;

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

public class RecipeActivity extends FontActivity {
    public static ViewPager pager;
    boolean flag;
    String userId;
    ViewPagerAdapter mAdapter;
    RelativeLayout titlebar;
    LinearLayout finalTitlebar;
    ImageView recipe_like;

    public static RecipeDetailData recipeDetailData = new RecipeDetailData();
    TextView recipeTitle;
    TextView createdAt;
    ImageView prevBtn, nextBtn;

    public static String recipeId;
    public static Activity recipeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipeActivity = this;
        recipeTitle = (TextView)findViewById(R.id.recipe_title);
        createdAt = (TextView)findViewById(R.id.date_text);
        pager = (ViewPager)findViewById(R.id.recipe_pager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);

        titlebar = (RelativeLayout)findViewById(R.id.title_bar);
        finalTitlebar = (LinearLayout)findViewById(R.id.final_title_bar);
        finalTitlebar.setVisibility(View.GONE);

        recipe_like =(ImageView)findViewById(R.id.recipe_like);

        prevBtn = (ImageView)findViewById(R.id.prev_btn);
        nextBtn = (ImageView)findViewById(R.id.next_btn);
        prevBtn.setVisibility(View.GONE);

        userId = PropertyManager.getInstance().getId();
        recipe_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RecipeActivity.recipeDetailData.liked.equals("N")) {
                    Toast toast = Toast.makeText(RecipeActivity.this, "성공", Toast.LENGTH_SHORT);
                    ImageView imageView = new ImageView(RecipeActivity.this);
                    imageView.setImageResource(R.drawable.bigheart);
                    toast.setView(imageView);
                    toast.setGravity(Gravity.CENTER,30,50);
                    toast.show();
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
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            recipe_like.setBackgroundResource(R.drawable.image_recipe_likefull);
                                            RecipeActivity.recipeDetailData.liked = "Y";
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RecipeActivity.this,"삭제된 레시피입니다.",Toast.LENGTH_SHORT).show();
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
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            recipe_like.setBackgroundResource(R.drawable.image_recipe_like_red);
                                            RecipeActivity.recipeDetailData.liked = "N";

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RecipeActivity.this,"삭제된 레시피입니다.",Toast.LENGTH_SHORT).show();
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
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == mAdapter.getCount() - 1) {
                    finalTitlebar.setVisibility(View.VISIBLE);
                    titlebar.setVisibility(View.GONE);
                } else {
                    finalTitlebar.setVisibility(View.GONE);
                    titlebar.setVisibility(View.VISIBLE);
                }

                if(position == 0) {
                    prevBtn.setVisibility(View.GONE);
                } else if(position == mAdapter.getCount()-1) {
                    prevBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.GONE);
                } else {
                    prevBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        new AsyncRecipeDetailJSONList().execute();
    }

    public static class RecipeDetailData {
        public String title="", recipeId="", userId="", nickname="", profileImg="", createdAt="",liked="",bookmarked="",theme="";
        public int step = 0, price = 0, time = 0;
        public ArrayList<String> recipeImg = new ArrayList<>();
        public ArrayList<String> recipeMethod = new ArrayList<>();
        public ArrayList<IngredientData> ingredient = new ArrayList<>();

        public RecipeDetailData() {}
    }

    public static class IngredientData {
        public String name, id, price, event;

        public IngredientData(String name, String event, String id, String price) {
            this.name = name;
            this.event = event;
            this.id = id;
            this.price = price;
        }
    }

    public class AsyncRecipeDetailJSONList extends AsyncTask<String, Integer, RecipeDetailData> {
        CustomProgressDialog dialog = new CustomProgressDialog(RecipeActivity.this);
        @Override
        protected RecipeDetailData doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Intent intent = getIntent();
                recipeId = intent.getStringExtra("recipeID");
                String userId = PropertyManager.getInstance().getId();
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_RCIPE_DETAIL, recipeId, userId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONRecipeDetailAllList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(RecipeDetailData result) {
            dialog.dismiss();
            if(result != null){
                recipeDetailData = result;
                recipeTitle.setText(result.title);
                createdAt.setText(result.createdAt);
                mAdapter.add(FirstFragment.newInstance());
                for (int i = 0; i < result.step; i++) {
                    mAdapter.add(RecipeFragment.newInstance(result.recipeImg.get(i), Integer.toString(i+1), result.recipeMethod.get(i)));
                }
                mAdapter.add(FinalFragment.newInstance());
                if(RecipeActivity.recipeDetailData.liked.equals("Y")) {
                    recipe_like.setBackgroundResource(R.drawable.image_recipe_likefull);
                } else {
                    recipe_like.setBackgroundResource(R.drawable.image_recipe_like_red);
                }
            } else {
                Toast.makeText(RecipeActivity.this,"삭제된 레시피입니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void backClick(View view) {
        finish();
    }

    public void prevClick(View view) {
        pager.setCurrentItem(pager.getCurrentItem()-1, true);
    }

    public void nextClick(View view) {
        pager.setCurrentItem(pager.getCurrentItem()+1, true);
    }
}