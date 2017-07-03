package com.conveniencerecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.MypageFragment.MypageFollowFragment;
import com.conveniencerecipe.MypageFragment.MypageRecipeFragment;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-08-09.
 */
public class UserPageActivity extends FontActivity {
    private DrawerLayout mDrawerLayout;

    boolean flag;
    private TabLayout tabLayout;
    String userId;
    private ViewPager viewPager;
    public static UserData userpagedata = new UserData();
    ImageView profileimage;
    TextView nickname, topNickname;
    final int image[] = {R.drawable.bt_unfollow, R.drawable.bt_follow};

    String Id;

    ToggleButton followbtn;
    //ImageView userfollowbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        PropertyManager propertyManager = PropertyManager.getInstance();
        Id = propertyManager.getId();

        profileimage = (ImageView)findViewById(R.id.user_profile_image);
        topNickname = (TextView)findViewById(R.id.user_nickname);
        nickname = (TextView)findViewById(R.id.user_nickname1);

        new AsyncUserPageJSONList().execute();

        viewPager = (ViewPager) findViewById(R.id.viewpager3);

        tabLayout = (TabLayout) findViewById(R.id.mypage_tabs);

        followbtn= (ToggleButton)findViewById(R.id.follow_button);
        //userfollowbtn = (ImageView)findViewById(R.id.userpage_follow);

        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userpagedata.followCheck.equals("N")) {

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
                                        .add("userId",userId)
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
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            followbtn.setBackgroundResource(image[1]);
                                            userpagedata.followCheck = "Y";;
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
                                        .add("userId",userId)
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
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            followbtn.setBackgroundResource(image[0]);
                                            userpagedata.followCheck = "N";;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MypageRecipeFragment.newInstance(0,"Y"), "활동\n"+userpagedata.activity.size());
        adapter.addFragment(MypageRecipeFragment.newInstance(1,"Y"), "스크랩\n"+userpagedata.scrap.size());
        adapter.addFragment(MypageFollowFragment.newInstance(0,"Y"), "팔로우\n"+userpagedata.followerTotal);
        adapter.addFragment(MypageFollowFragment.newInstance(1,"Y"), "팔로잉\n"+userpagedata.followingTotal);
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static class UserData {
        public String userId, nickname, profileImg, pageCheck, followCheck;
        public int followerTotal, followingTotal;
        public ArrayList<RecipeListData> activity = new ArrayList<>();
        public ArrayList<RecipeListData> scrap = new ArrayList<>();
        public ArrayList<FollowData> follower = new ArrayList<>();
        public ArrayList<FollowData> following = new ArrayList<>();

        public UserData(){}
    }

    public static class FollowData {
        public String userId, nickname, profileImg, followBack;

        public FollowData() {}
    }

    public class AsyncUserPageJSONList extends AsyncTask<String, Integer, UserData> {
        CustomProgressDialog dialog = new CustomProgressDialog(UserPageActivity.this);
        @Override
        protected UserData doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Intent intent = getIntent();
                userId = intent.getStringExtra("userId");
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_USERPAGE,Id,userId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONUserpageAllList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(UserData result) {
            dialog.dismiss();

            if(result != null){
                userpagedata = result;
                if(!result.profileImg.equals("null")) {
                    Glide.with(MyApplication.RecipeContext()).load(Uri.parse(result.profileImg)).into(profileimage);
                } else {
                    profileimage.setImageResource(R.drawable.image_profile);
                }
                topNickname.setText(result.nickname);
                nickname.setText(result.nickname);
                if(result.followCheck.equals("N")){
                    followbtn.setBackgroundResource(image[0]);
                    //userfollowbtn.setBackgroundResource(followimage[0]);
                }else {
                    followbtn.setBackgroundResource(image[1]);
                    //userfollowbtn.setBackgroundResource(followimage[1]);
                }
            }
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    public void backClick(View view) {
        finish();
    }
}