package com.conveniencerecipe;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.MypageFragment.MypageFollowFragment;
import com.conveniencerecipe.MypageFragment.MypageRecipeFragment;
import com.conveniencerecipe.WriteRecipe.WriteRecipeMainActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-07-27.
 */
public class MypageActivity extends FontActivity {
    private DrawerLayout mDrawerLayout;
    private static final String TAG = "MainActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private UpLoadValueObject upLoadfile;

    NavProfile navProfile;
    CircleImageView navprofileImg;
    public static TextView navprofilenickname;

    public static class UpLoadValueObject {
        File file; //업로드할 파일
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, boolean tempFiles) {
            this.file = file;
            this.tempFiles = tempFiles;
        }
    }

    boolean flag;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static UserPageActivity.UserData userpagedata = new UserPageActivity.UserData();
    public  ImageView profileimage;
    ImageView profileImageBtn;
    public TextView nickname;
    ImageView notice_btn;
    String preNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        new AsyncProfile().execute();
        navprofileImg = (CircleImageView)findViewById(R.id.navi_header_image);
        navprofilenickname = (TextView)findViewById(R.id.nav_nickname);

        userId = PropertyManager.getInstance().getId();
        Log.e("userId", String.valueOf(userId));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileimage = (ImageView)findViewById(R.id.user_profile_image);
        profileImageBtn = (ImageView)findViewById(R.id.profile_change_btn);
        profileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfilePhotoDialog profilePhotoDialog = new ProfilePhotoDialog(MypageActivity.this);
                profilePhotoDialog.show();
            }
        });
        nickname = (TextView)findViewById(R.id.user_nickname);

        notice_btn =(ImageView)findViewById(R.id.notice_btn);

        preNotice = PropertyManager.getInstance().getPUSH();

        notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preNotice.equals("Y")){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(MypageActivity.this, "PUSH알림 꺼짐", Toast.LENGTH_SHORT).show();
                            notice_btn.setImageResource(R.drawable.image_mypage_push2);
                            PropertyManager.getInstance().setPUSH("N");
                            Log.e("푸시","성공");
                            preNotice = "N";
                        }
                    });

                }else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(MypageActivity.this, "PUSH알림 켜짐", Toast.LENGTH_SHORT).show();
                            notice_btn.setImageResource(R.drawable.image_mypage_push);
                            PropertyManager.getInstance().setPUSH("Y");
                            Log.e("푸시해제","성공");
                            preNotice = "Y";
                        }
                    });

                }
            }
        });

        if(preNotice.equals("Y")){
            notice_btn.setImageResource(R.drawable.image_mypage_push);
        }else {
            notice_btn.setImageResource(R.drawable.image_mypage_push2);
        }

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        LinearLayout.OnClickListener navClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nav_1:
                        Intent intent = new Intent(MypageActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.nav_2:
                        startActivity(new Intent(MypageActivity.this, NoticeActivity.class));
                        finish();
                        break;
                    case R.id.nav_3:
                        startActivity(new Intent(MypageActivity.this, ThemeActivity.class));
                        finish();
                        break;
                    case R.id.nav_4:
                        startActivity(new Intent(MypageActivity.this, WriteRecipeMainActivity.class));
                        finish();
                        break;
                    case R.id.nav_5:
                        startActivity(new Intent(MypageActivity.this, MypageActivity.class));
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

        viewPager = (ViewPager) findViewById(R.id.viewpager3);

        tabLayout = (TabLayout) findViewById(R.id.mypage_tabs);
        findViewById(R.id.nickname_btn).setOnClickListener(mClickListener);
    }

    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MypageActivity.this, Mypage_nickname.class);
            startActivity(i);
        }
    };

    public class ProfilePhotoDialog extends Dialog {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.6f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.profile_photo_dialog);

            setLayout();
        }

        public ProfilePhotoDialog(Context context) {
            // Dialog 배경을 투명 처리 해준다.
            super(context , android.R.style.Theme_Translucent_NoTitleBar);
        }

        private ImageView galleryBtn;
        private ImageView cameraBtn;
        private ImageView defaultBtn;

        private void setLayout() {
            galleryBtn = (ImageView) findViewById(R.id.gallery_btn);
            galleryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    doTakeAlbumAction();
                }
            });

            cameraBtn = (ImageView) findViewById(R.id.camera_btn);
            cameraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    doTakePhotoAction();
                }
            });

            defaultBtn = (ImageView)findViewById(R.id.default_btn);
            defaultBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
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
                                        .add("msg", "success")
                                        .build();
                                //요청 세팅
                                Request request = new Request.Builder()
                                        .url(String.format(NetworkDefineConstant.SERVER_URL_PROFILE_DELETE, userId))
                                        .delete(fileUploadBody) //반드시 post로
                                        .build();
                                //동기 방식
                                response = toServer.newCall(request).execute();

                                flag = response.isSuccessful();
                                //응답 코드 200등등
                                int responseCode = response.code();
                                if (flag) {
                                    e("레시피삭제response결과", response.message()); //읃답에 대한 메세지(OK)
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
                    profileimage.setImageResource(R.drawable.image_profile);
                    MainActivity.navprofileImg.setImageResource(R.drawable.image_profile);
                    navprofileImg.setImageResource(R.drawable.image_profile);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncMyPageJSONList().execute();

        if (!isSDCardAvailable()) {
            Toast.makeText(this, "SD 카드가 없어 종료 합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String currentAppPackage = getPackageName();

        myImageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), currentAppPackage);

        checkPermission();

        if (!myImageDir.exists()) {
            if (myImageDir.mkdirs()) {
                Toast.makeText(getApplication(), " 저장할 디렉토리가 생성 됨", Toast.LENGTH_SHORT).show();
            }
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MypageRecipeFragment.newInstance(0,"M"), "활동\n"+userpagedata.activity.size());
        adapter.addFragment(MypageRecipeFragment.newInstance(1,"M"), "스크랩\n"+userpagedata.scrap.size());
        adapter.addFragment(MypageFollowFragment.newInstance(0,"M"), "팔로우\n"+userpagedata.followerTotal);
        adapter.addFragment(MypageFollowFragment.newInstance(1,"M"), "팔로잉\n"+userpagedata.followingTotal);
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

    String userId;

    public class AsyncMyPageJSONList extends AsyncTask<String, Integer, UserPageActivity.UserData> {
        CustomProgressDialog dialog = new CustomProgressDialog(MypageActivity.this);
        @Override
        protected UserPageActivity.UserData doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_USERPAGE, userId, userId))
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
        protected void onPostExecute(UserPageActivity.UserData result) {
            dialog.dismiss();

            if(result != null){
                userpagedata = result;
                if(!result.profileImg.equals("null")) {
                    Glide.with(MyApplication.RecipeContext()).load(Uri.parse(result.profileImg)).into(profileimage);
                } else {
                    profileimage.setImageResource(R.drawable.image_profile);
                }
                nickname.setText(result.nickname);
            }
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);

            } else {
                //사용자가 언제나 허락
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //사용자가 퍼미션을 OK했을 경우

                } else {

                    Log.d(TAG, "Permission always deny");

                    //사용자가 퍼미션을 거절했을 경우
                }
                break;
        }
    }

    public boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 카메라에서 이미지 가져오기
     */
    Uri currentSelectedUri; //업로드할 현재 이미지에 대한 Uri
    File myImageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름

    private void doTakePhotoAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //업로드할 파일의 이름
        currentFileName = "upload_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
        currentSelectedUri = Uri.fromFile(new File(myImageDir, currentFileName));
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, currentSelectedUri);
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                currentSelectedUri = data.getData();
                if (currentSelectedUri != null) {
                    //실제 Image의 full path name을 얻어온다.
                    if (findImageFileNameFromUri(currentSelectedUri)) {
                        //ArrayList에 업로드할  객체를 추가한다.
                        upLoadfile = new UpLoadValueObject(new File(currentFileName), false);
                    }
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap returedBitmap = (Bitmap) extras.get("data");
                    if (tempSavedBitmapFile(returedBitmap)) {
                        Log.e("임시이미지파일저장", "저장됨");
                    } else {
                        Log.e("임시이미지파일저장", "실패");
                    }
                }
                new FileUpLoadAsyncTask().execute(upLoadfile);
                break;
            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지
                upLoadfile = new UpLoadValueObject(new File(myImageDir, currentFileName), false);
                new FileUpLoadAsyncTask().execute(upLoadfile);
                break;
            }
        }
    }

    private boolean tempSavedBitmapFile(Bitmap tempBitmap) {
        boolean flag = false;
        try {
            currentFileName = "upload_" + (System.currentTimeMillis() / 1000);
            String fileSuffix = ".jpg";
            //임시파일을 실행한다.
            File tempFile = File.createTempFile(
                    currentFileName,            // prefix
                    fileSuffix,                   // suffix
                    myImageDir                   // directory
            );
            final FileOutputStream bitmapStream = new FileOutputStream(tempFile);
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 0, bitmapStream);
            upLoadfile = new UpLoadValueObject(tempFile, true);
            if (bitmapStream != null) {
                bitmapStream.close();
            }
            currentSelectedUri = Uri.fromFile(tempFile);
            flag = true;
        } catch (IOException i) {
            Log.e("저장중 문제발생", i.toString(), i);
        }
        return flag;
    }

    private boolean findImageFileNameFromUri(Uri tempUri) {
        boolean flag = false;

        //실제 Image Uri의 절대이름
        String[] IMAGE_DB_COLUMN = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = null;
        try {
            //Primary Key값을 추출
            String imagePK = String.valueOf(ContentUris.parseId(tempUri));
            //Image DB에 쿼리를 날린다.
            cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_DB_COLUMN,
                    MediaStore.Images.Media._ID + "=?",
                    new String[]{imagePK}, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                currentFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                flag = true;
            }
        } catch (SQLiteException sqle) {
            Log.e("findImage....", sqle.toString(), sqle);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return flag;
    }

    private class FileUpLoadAsyncTask extends AsyncTask<UpLoadValueObject, Void, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public FileUpLoadAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(UpLoadValueObject ... upLoadValueObject) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("userId", userId);

                File file = upLoadValueObject[0].file;
                Log.e("Upload", String.valueOf(file.getAbsoluteFile()));
                builder.addFormDataPart("profile_img", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));

                MultipartBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_PROFILE)
                        .post(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();

                boolean flag = response.isSuccessful();

                if (flag) {
                    JSONObject result = new JSONObject(response.body().string());
                    return result.getString("msg");
                }

            } catch (UnknownHostException une) {
                e("aa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("bb", uee.toString());
            } catch (Exception e) {
                e("cc", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && s.equalsIgnoreCase("success")){
                Toast.makeText(MypageActivity.this, "프로필사진 변경 성공", Toast.LENGTH_LONG).show();
                if(upLoadfile.tempFiles){
                    upLoadfile.file.deleteOnExit(); //임시파일을 삭제한다
                }
                profileimage.setImageURI(Uri.fromFile(upLoadfile.file));
                MainActivity.navprofileImg.setImageURI(Uri.fromFile(upLoadfile.file));
                navprofileImg.setImageURI(Uri.fromFile(upLoadfile.file));
            }else{
                Toast.makeText(MypageActivity.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        }
    }
    public class AsyncProfile extends AsyncTask<String, Integer, NavProfile> {
        CustomProgressDialog dialog = new CustomProgressDialog(MypageActivity.this);
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