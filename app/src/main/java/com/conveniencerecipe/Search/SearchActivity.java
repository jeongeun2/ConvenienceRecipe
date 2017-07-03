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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.SearchFragment.IngrediFragment;
import com.conveniencerecipe.SearchFragment.PriceFragment;
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
 * Created by ccei 34 on 2016-07-28.
 */
public class SearchActivity extends FontActivity {
    private DrawerLayout mDrawerLayout;
    IngredientListData ingredientListData;
    NavProfile navProfile;
    CircleImageView navprofileImg;
    TextView navprofilenickname;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView test;
    String ingredientText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        new AsyncProfile().execute();
        navprofileImg = (CircleImageView)findViewById(R.id.navi_header_image);
        navprofilenickname = (TextView)findViewById(R.id.nav_nickname);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        test = (TextView)findViewById(R.id.test);

        ImageView search_button = (ImageView)findViewById(R.id.search_button);

        final EditText autoEdit=(EditText) findViewById(R.id.auto_complete_search_view);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientText = autoEdit.getText().toString();
                if(ingredientText.length()>0){
                    Intent i = new Intent(SearchActivity.this,SearchResultActivity.class);
                    i.putExtra("id",ingredientText);
                    i.putExtra("sort","ingredient");
                    i.putExtra("option",ingredientText);
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.search_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nav_1:
                        startActivity(new Intent(SearchActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.nav_2:
                        startActivity(new Intent(SearchActivity.this, NoticeActivity.class));
                        finish();
                        break;
                    case R.id.nav_3:
                        startActivity(new Intent(SearchActivity.this, ThemeActivity.class));
                        finish();
                        break;
                    case R.id.nav_4:
                        startActivity(new Intent(SearchActivity.this, WriteRecipeMainActivity.class));
                        finish();
                        break;
                    case R.id.nav_5:
                        startActivity(new Intent(SearchActivity.this, MypageActivity.class));
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


    public static class IngredientListData {
        public ArrayList<String> ingredient= new ArrayList<String>();

        public IngredientListData(){}
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
        adapter.addFragment(new IngrediFragment(), "재료");
        adapter.addFragment(new PriceFragment(), "가격");
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
        CustomProgressDialog dialog = new CustomProgressDialog(SearchActivity.this);
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
