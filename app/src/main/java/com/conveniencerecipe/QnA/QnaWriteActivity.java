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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.RecipeListFragment.QnAFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-07-29.
 */
public class QnaWriteActivity extends FontActivity implements View.OnClickListener {
    EditText titleEdit, contentEdit;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;

    private static final String TAG = "MainActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    ArrayList<UpLoadValueObject> upLoadfiles = new ArrayList<>();
    public static QnaWriteActivity qnaWriteActivity;
    QnaWriteCancel qnaWriteCancel;

    public class UpLoadValueObject {
        File file; //업로드할 파일
        int num; //단계 번호
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, int num, boolean tempFiles) {
            this.file = file;
            this.num = num;
            this.tempFiles = tempFiles;
        }
    }

    String title, contentText;
    int click;
    TextView completeBtn;
    ImageView image1, image2, image3;
    String postingId;
    QnaDetailActivity.QnaDetailEntityObject detailEntityObject;
    ArrayList<Object> images = new ArrayList<>();
    boolean modify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_write);

        completeBtn = (TextView)findViewById(R.id.complete_btn);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeBtn.setEnabled(false);
                if(titleEdit.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(contentEdit.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if(modify) {
                        title = titleEdit.getText().toString();
                        contentText = contentEdit.getText().toString();
                        new FileModifyAsyncTask().execute(upLoadfiles);
                    } else {
                        title = titleEdit.getText().toString();
                        contentText = contentEdit.getText().toString();
                        new FileUpLoadAsyncTask().execute(upLoadfiles);
                    }
                    QnAFragment.page = 1;
                }
            }
        });

        qnaWriteActivity = QnaWriteActivity.this;
        qnaWriteCancel = new QnaWriteCancel(QnaWriteActivity.this);
        titleEdit = (EditText)findViewById(R.id.title_edit);
        contentEdit = (EditText)findViewById(R.id.content_edit);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);

        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

        Intent intent = getIntent();
        modify = intent.getBooleanExtra("modify", false);
        if(modify) {
            postingId = QnaDetailActivity.postingId;
            detailEntityObject = QnaDetailActivity.qnaDetailEntityObject;
            QnaDetailActivity.qnaDetailActivity.finish();

            titleEdit.setText(detailEntityObject.title);
            contentEdit.setText(detailEntityObject.content);
            for(int i = 0; i < detailEntityObject.image.size(); i++) {
                images.add(detailEntityObject.image.get(i));
                switch (i) {
                    case 0: Glide.with(MyApplication.RecipeContext()).load(Uri.parse(detailEntityObject.image.get(i))).override(300,300).into(image1); break;
                    case 1: Glide.with(MyApplication.RecipeContext()).load(Uri.parse(detailEntityObject.image.get(i))).override(300,300).into(image2); break;
                    case 2: Glide.with(MyApplication.RecipeContext()).load(Uri.parse(detailEntityObject.image.get(i))).override(300,300).into(image3); break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == image1) click = 1;
        else if(view == image2) click = 2;
        else if(view == image3) click = 3;
        if(images.size()+1 < click) {
            Toast.makeText(QnaWriteActivity.this, "사진을 순서대로 등록해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            QnaPhotoDialog photoDialog = new QnaPhotoDialog(QnaWriteActivity.this);
            photoDialog.show();
        }
    }

    public class QnaPhotoDialog extends Dialog {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.6f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.recipe_photo_dialog);

            setLayout();
        }

        public QnaPhotoDialog(Context context) {
            // Dialog 배경을 투명 처리 해준다.
            super(context , android.R.style.Theme_Translucent_NoTitleBar);
        }

        private ImageView galleryBtn;
        private ImageView cameraBtn;

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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        images.clear();
        upLoadfiles.clear();
    }

    private class FileUpLoadAsyncTask extends AsyncTask<ArrayList<UpLoadValueObject>, Void, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");
        CustomProgressDialog customProgressDialog = new CustomProgressDialog(QnaWriteActivity.this);

        public FileUpLoadAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<UpLoadValueObject>... arrayLists) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                String userId = PropertyManager.getInstance().getId();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userId) //기본 쿼리
                        .addFormDataPart("title", title)
                        .addFormDataPart("content", contentText)
                        .build();
                int fileSize = arrayLists[0].size();
                for (int i = 0; i < fileSize; i++) {
                    File file = upLoadfiles.get(i).file;
                    builder.addFormDataPart("image", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }
                RequestBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_QNA_WRITE)
                        .post(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
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
            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customProgressDialog.dismiss();
            if(s.equalsIgnoreCase("success")){
                Toast.makeText(QnaWriteActivity.this, "게시물 업로드 성공", Toast.LENGTH_LONG).show();
                finish();
                int fileSize = upLoadfiles.size();
                for(int i = 0 ; i < fileSize ; i++){
                    UpLoadValueObject fileValue = upLoadfiles.get(i);
                    if(fileValue.tempFiles){
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
            }else{
                Toast.makeText(QnaWriteActivity.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class FileModifyAsyncTask extends AsyncTask<ArrayList<UpLoadValueObject>, Void, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");
        CustomProgressDialog customProgressDialog = new CustomProgressDialog(QnaWriteActivity.this);

        public FileModifyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressDialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<UpLoadValueObject>... arrayLists) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                String userId = PropertyManager.getInstance().getId();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("id", postingId)
                        .addFormDataPart("title", title)
                        .addFormDataPart("content", contentText)
                        .addFormDataPart("img_total", String.valueOf(images.size()))
                        .build();
                boolean change;
                int fileSize = arrayLists[0].size();

                for(int i=0; i < images.size(); i++) {
                    change = false;
                    for(int j=0; j < fileSize; j++) {
                        if(upLoadfiles.get(j).num == i) {
                            change = true;
                            break;
                        }
                    }
                    if(!change) {
                        builder.addFormDataPart("imgcheck_"+i, "N");
                    }
                }
                for (int i = 0; i < fileSize; i++) {
                    File file = upLoadfiles.get(i).file;
                    int num = upLoadfiles.get(i).num;
                    builder.addFormDataPart("imgcheck_"+num, "Y");
                    builder.addFormDataPart("image_"+num, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }
                RequestBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_QNA_WRITE)
                        .put(fileUploadBody) //반드시 post로
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
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
                    finish();
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customProgressDialog.dismiss();
            if(s.equalsIgnoreCase("success")){
                Toast.makeText(QnaWriteActivity.this, "게시물 업로드 성공", Toast.LENGTH_LONG).show();
                int fileSize = upLoadfiles.size();
                for(int i = 0 ; i < fileSize ; i++){
                    UpLoadValueObject fileValue = upLoadfiles.get(i);
                    if(fileValue.tempFiles){
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
            }else{
                Toast.makeText(QnaWriteActivity.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void backClick(View view) {
        qnaWriteCancel.show();
    }

    @Override
    public void onBackPressed() {
        qnaWriteCancel.show();
    }

    @Override
    public void onResume() {
        super.onResume();

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
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap image = BitmapFactory.decodeFile(currentFileName, options);
                        try {
                            ExifInterface exif = new ExifInterface(new File(myImageDir, currentFileName).getPath());
                            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            int exifDegree = exifOrientationToDegrees(exifOrientation);
                            image = rotate(image, exifDegree);
                        } catch (IOException e) {
                            e.getLocalizedMessage();
                        }

                        boolean set = false;
                        switch(click) {
                            case 1:
                                if(images.size() > 0) {
                                    images.remove(0);
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 0) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 0, false));
                                        images.add(0,image);
                                    }
                                } else {
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 0) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 0, false));
                                        images.add(image);
                                    }
                                }
                                image1.setImageDrawable(new BitmapDrawable(image));
                                break;
                            case 2:
                                if(images.size() > 1) {
                                    images.remove(1);
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 1) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 1, false));
                                        images.add(1,image);
                                    }
                                } else {
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 1) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 1, false));
                                        images.add(image);
                                    }
                                }
                                image2.setImageDrawable(new BitmapDrawable(image));
                                break;
                            case 3:
                                if(images.size() > 2) {
                                    images.remove(2);
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 2) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 2, false));
                                        images.add(2,image);
                                    }
                                } else {
                                    for(int i=0; i<upLoadfiles.size(); i++) {
                                        if(upLoadfiles.get(i).num == 2) {
                                            upLoadfiles.get(i).file = new File(currentFileName);
                                            set = true;
                                            break;
                                        }
                                    }
                                    if(!set) {
                                        upLoadfiles.add(new UpLoadValueObject(new File(currentFileName), 2, false));
                                        images.add(image);
                                    }
                                }
                                image3.setImageDrawable(new BitmapDrawable(image));
                                break;
                        }
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
                break;
            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap image = BitmapFactory.decodeFile(new File(myImageDir, currentFileName).getPath(), options);
                try {
                    ExifInterface exif = new ExifInterface(new File(myImageDir, currentFileName).getPath());
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);
                } catch (IOException e) {
                    e.getLocalizedMessage();
                }

                boolean set = false;
                switch(click) {
                    case 1:
                        if(images.size() > 0) {
                            images.remove(0);
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 0) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 0, false));
                                images.add(0,image);
                            }
                        } else {
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 0) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 0, false));
                                images.add(image);
                            }
                        }
                        image1.setImageDrawable(new BitmapDrawable(image));
                        break;
                    case 2:
                        if(images.size() > 1) {
                            images.remove(1);
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 1) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 1, false));
                                images.add(1,image);
                            }
                        } else {
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 1) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 1, false));
                                images.add(image);
                            }
                        }
                        image2.setImageDrawable(new BitmapDrawable(image));
                        break;
                    case 3:
                        if(images.size() > 2) {
                            images.remove(2);
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 2) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 2, false));
                                images.add(2,image);
                            }
                        } else {
                            for(int i=0; i<upLoadfiles.size(); i++) {
                                if(upLoadfiles.get(i).num == 2) {
                                    upLoadfiles.get(i).file = new File(currentFileName);
                                    set = true;
                                    break;
                                }
                            }
                            if(!set) {
                                upLoadfiles.add(new UpLoadValueObject(new File(myImageDir, currentFileName), 2, false));
                                images.add(image);
                            }
                        }
                        image3.setImageDrawable(new BitmapDrawable(image));
                        break;
                }
                break;
            }
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
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
            switch(click) {
                case 1:
                    upLoadfiles.add(new UpLoadValueObject(tempFile, 0, true));
                    images.add(0, tempBitmap);
                    image1.setImageDrawable(new BitmapDrawable(tempBitmap));
                    break;
                case 2:
                    upLoadfiles.add(new UpLoadValueObject(tempFile, 1, true));
                    images.add(1, tempBitmap);
                    image2.setImageDrawable(new BitmapDrawable(tempBitmap));
                    break;
                case 3:
                    upLoadfiles.add(new UpLoadValueObject(tempFile, 2, true));
                    images.add(2, tempBitmap);
                    image3.setImageDrawable(new BitmapDrawable(tempBitmap));
                    break;
            }
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
}