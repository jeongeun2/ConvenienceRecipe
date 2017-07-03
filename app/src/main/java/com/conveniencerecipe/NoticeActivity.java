package com.conveniencerecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.NoticeFragment.FollowingnoticeFragment;
import com.conveniencerecipe.NoticeFragment.MynoticeFragment;
import com.conveniencerecipe.WriteRecipe.WriteRecipeMainActivity;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-07-27.
 */
public class NoticeActivity extends FontActivity {
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    NavProfile navProfile;
    CircleImageView navprofileImg;
    TextView navprofilenickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        new AsyncProfile().execute();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navprofileImg = (CircleImageView)findViewById(R.id.navi_header_image);
        navprofilenickname = (TextView)findViewById(R.id.nav_nickname);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.notice_tabs);
        tabLayout.setupWithViewPager(viewPager);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nav_1:
                        Intent intent = new Intent(NoticeActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.nav_2:
                        startActivity(new Intent(NoticeActivity.this, NoticeActivity.class));
                        finish();
                        break;
                    case R.id.nav_3:
                        startActivity(new Intent(NoticeActivity.this, ThemeActivity.class));
                        finish();
                        break;
                    case R.id.nav_4:
                        startActivity(new Intent(NoticeActivity.this, WriteRecipeMainActivity.class));
                        finish();
                        break;
                    case R.id.nav_5:
                        startActivity(new Intent(NoticeActivity.this, MypageActivity.class));
                        finish();
                        break;
                }
            }
        };

        findViewById(R.id.nav_1).setOnClickListener(navClickListener);
        findViewById(R.id.nav_2).setOnClickListener(navClickListener);
        findViewById(R.id.nav_3).setOnClickListener(navClickListener);
        findViewById(R.id.nav_4).setOnClickListener(navClickListener);
        findViewById(R.id.nav_5).setOnClickListener(navClickListener);

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
        adapter.addFragment(new MynoticeFragment().newInstance(1), "나");
        adapter.addFragment(new FollowingnoticeFragment().newInstance(1), "팔로잉");
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
    public class AsyncProfile extends AsyncTask<String, Integer, NavProfile> {
        CustomProgressDialog dialog = new CustomProgressDialog(NoticeActivity.this);
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
