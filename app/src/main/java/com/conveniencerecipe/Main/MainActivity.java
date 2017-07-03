package com.conveniencerecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.RecipeListFragment.PopularFragment;
import com.conveniencerecipe.RecipeListFragment.QnAFragment;
import com.conveniencerecipe.RecipeListFragment.R_FollowingFragment;
import com.conveniencerecipe.WriteRecipe.WriteRecipeMainActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MainActivity extends FontActivity {
    private DrawerLayout mDrawerLayout;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    NavProfile navProfile;
    public static CircleImageView navprofileImg;
    public static TextView navprofilenickname;

    int tabPosition = 0;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncProfile().execute();
        navprofileImg = (CircleImageView)findViewById(R.id.navi_header_image);
        navprofilenickname = (TextView)findViewById(R.id.nav_nickname);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nav_1:
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.nav_2:
                        startActivity(new Intent(MainActivity.this, NoticeActivity.class));
                        break;
                    case R.id.nav_3:
                        startActivity(new Intent(MainActivity.this, ThemeActivity.class));
                        break;
                    case R.id.nav_4:
                        startActivity(new Intent(MainActivity.this, WriteRecipeMainActivity.class));
                        break;
                    case R.id.nav_5:
                        startActivity(new Intent(MainActivity.this, MypageActivity.class));
                        break;
                }
            }
        };

        findViewById(R.id.nav_1).setOnClickListener(navClickListener);
        findViewById(R.id.nav_2).setOnClickListener(navClickListener);
        findViewById(R.id.nav_3).setOnClickListener(navClickListener);
        findViewById(R.id.nav_4).setOnClickListener(navClickListener);
        findViewById(R.id.nav_5).setOnClickListener(navClickListener);

        viewPager = (ViewPager)findViewById(R.id.viewpager1);
        if(viewPager !=null){
            setupRecipeViewPager(viewPager);
        }

        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.write_fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WriteRecipeMainActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.hold);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //NewestFragment.pager.setCurrentItem(0);
                    case 1:case 2:
                        fab.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, WriteRecipeMainActivity.class));
                            }
                        });
                        break;
                    case 3:
                        fab.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, QnaWriteActivity.class));
                            }
                        });
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentPosition = tab.getPosition();
                if(currentPosition == 0 || currentPosition == 3){
                    tabPosition = currentPosition;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window  = getWindow();
            Transition exitTrans = new Explode();

            Transition reenterTrans = new Explode();

            window.setExitTransition(exitTrans);
            window.setReenterTransition(reenterTrans);
            window.setAllowEnterTransitionOverlap(true);
            window.setAllowReturnTransitionOverlap(true);
        }

        findViewById(R.id.searchbtn).setOnClickListener(mClickListener);

        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        FirebaseInstanceId.getInstance().getToken();

    }

    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //tabLayout.setupWithViewPager(viewPager);
        if(tabPosition == 0 || tabPosition == 3){
            tabLayout.getTabAt(tabPosition).select();
        }
    }

    private  void setupRecipeViewPager(ViewPager viewPager){
        RecipeListPagerAdapter recipelistAdapter = new RecipeListPagerAdapter(getSupportFragmentManager());

        recipelistAdapter.appendFragment(NewestFragment.newInstance(1),"최신");
        recipelistAdapter.appendFragment(PopularFragment.newInstance(1),"인기");
        recipelistAdapter.appendFragment(R_FollowingFragment.newInstance(1),"팔로잉");
        recipelistAdapter.appendFragment(QnAFragment.newInstance(1),"뭐먹지?");

        viewPager.setAdapter(recipelistAdapter);
    }

    private static class RecipeListPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> recipeFragment = new ArrayList<>();
        private final ArrayList<String> tabTitles = new ArrayList<>();

        public RecipeListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void appendFragment(Fragment fragment, String title ){
            recipeFragment.add(fragment);
            tabTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return recipeFragment.get(position);
        }

        @Override
        public int getCount() {
            return recipeFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
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

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;
        if(0<=intervalTime && FINISH_INTERVAL_TIME>=intervalTime){
            super.onBackPressed();
        }else {
            backPressedTime=currentTime;
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public class AsyncProfile extends AsyncTask<String, Integer, NavProfile> {
        CustomProgressDialog dialog = new CustomProgressDialog(MainActivity.this);
        String userId = PropertyManager.getInstance().getId();
        @Override
        protected NavProfile doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_NAV_PROFILE,userId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONProfile(new StringBuilder(retruedJson));
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
        protected void onPostExecute(NavProfile result) {
            dialog.dismiss();

            if(result != null){
                navProfile = result;
                navprofilenickname.setText(result.navnickname);
                if(!result.navprofileImg.equals("null")) {
                    Glide.with(MyApplication.RecipeContext()).load(Uri.parse(result.navprofileImg)).into(navprofileImg);
                } else {
                    navprofileImg.setImageResource(R.drawable.image_profile);
                }
            }
        }
    }

}
