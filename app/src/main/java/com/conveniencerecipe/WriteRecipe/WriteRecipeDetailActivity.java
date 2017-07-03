package com.conveniencerecipe.WriteRecipe;

import android.Manifest;
import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conveniencerecipe.FontActivity;
import com.conveniencerecipe.MyApplication;
import com.conveniencerecipe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WriteRecipeDetailActivity extends FontActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    ImageView cameraBtn;

    ArrayAdapter<String> adapter;
    ListView ingrediListView;
    EditText editText;
    Bitmap image;

    boolean modify;
    int modifyPosition;

    ArrayList<String> ingrediList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_recipe_detail);

        cameraBtn = (ImageView)findViewById(R.id.photo_btn);
        cameraBtn.setOnClickListener(this);

        editText = (EditText)findViewById(R.id.recipe_edit);
        ingrediListView = (ListView)findViewById(R.id.list_ingredi);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, ingrediList);
        ingrediListView.setAdapter(adapter);

        ingrediListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editText.append(ingrediList.get(i));
            }
        });

        for(int i = 0; i < WriteRecipeMainActivity.ingrediItems.size(); i++) {
            ingrediList.add(WriteRecipeMainActivity.ingrediItems.get(i)+" ");
        }
        adapter.notifyDataSetChanged();
        calculate();

        final ArrayList<CookItemData> cookItems = new ArrayList<CookItemData>();
        cookItems.add(new CookItemData(null,null,"조리기구"));
        cookItems.add(new CookItemData(R.drawable.microwave, R.drawable.image_upload_microwave2, "전자레인지"));
        cookItems.add(new CookItemData(R.drawable.hotwater, R.drawable.image_upload_hotwater, "끓는물"));
        cookItems.add(new CookItemData(R.drawable.frypan, R.drawable.image_upload_frypan, "프라이팬"));
        cookItems.add(new CookItemData(R.drawable.pot, R.drawable.image_upload_pot, "냄비"));
        Spinner cookSp = (Spinner)findViewById(R.id.cook_list);
        CookAdapter cookAdapter = new CookAdapter(this,R.layout.cook_spinner,R.id.cook_image,cookItems);
        cookSp.setAdapter(cookAdapter);

        cookSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    editText.append(cookItems.get(i).cookText+" ");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        final ArrayList<CookTimeItemData> cookTimeItems = new ArrayList<CookTimeItemData>();
        cookTimeItems.add(new CookTimeItemData("조리시간"));
        cookTimeItems.add(new CookTimeItemData("30초"));
        cookTimeItems.add(new CookTimeItemData("1분"));
        cookTimeItems.add(new CookTimeItemData("1분30초"));
        cookTimeItems.add(new CookTimeItemData("2분"));
        cookTimeItems.add(new CookTimeItemData("2분30초"));
        cookTimeItems.add(new CookTimeItemData("3분"));
        Spinner cookTimeSp = (Spinner)findViewById(R.id.cooktime_list);
        CookTimeAdapter cookTimeAdapter = new CookTimeAdapter(this,R.layout.cooktime_spinner,R.id.cooktime_text,cookTimeItems);
        cookTimeSp.setAdapter(cookTimeAdapter);

        cookTimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    editText.append(cookTimeItems.get(i).cookTimeText + " ");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        Intent intent = getIntent();
        modify = intent.getBooleanExtra("modify", false);
        modifyPosition = intent.getIntExtra("position", 0);

        if(modify) {
            RecipeStepData recipeData = WriteRecipeMainActivity.recyclerAdapter.items.get(modifyPosition);
            if(recipeData.image instanceof String) {
                Glide.with(MyApplication.RecipeContext()).load(Uri.parse((String)recipeData.image)).override(669,669).into(cameraBtn);
            } else if(recipeData.image instanceof Bitmap) {
                cameraBtn.setImageBitmap((Bitmap)recipeData.image);
            }
            editText.setText(recipeData.text);
        }
    }

    public class CookAdapter extends ArrayAdapter<CookItemData> {
        int groupid;
        ArrayList<CookItemData> list;
        LayoutInflater inflater;
        public CookAdapter(Activity context, int groupid, int id, ArrayList<CookItemData> list){
            super(context,id,list);
            this.list=list;
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groupid=groupid;
        }

        public View getView(int position, View convertView, ViewGroup parent ){
            View itemView=inflater.inflate(groupid,parent,false);
            ImageView imageView=(ImageView)itemView.findViewById(R.id.cook_image);
            TextView textView = (TextView)itemView.findViewById(R.id.cook_text);
            if(position != 0) {
                imageView.setImageResource(list.get(position).getCookImage());
            }
            if(position == 0) {
                textView.setText(list.get(position).getCookText());
            }
            return itemView;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent){
            View itemView=inflater.inflate(R.layout.cook_spinner_dropdown,parent,false);
            ImageView imageView=(ImageView)itemView.findViewById(R.id.cook_image);
            if(position != 0) {
                imageView.setImageResource(list.get(position).getDropdownCookImage());
            }
            TextView textView=(TextView)itemView.findViewById(R.id.cook_text);
            textView.setText(list.get(position).getCookText());
            return itemView;
        }
    }

    public class CookTimeAdapter extends ArrayAdapter<CookTimeItemData> {
        int groupid;
        ArrayList<CookTimeItemData> list;
        LayoutInflater inflater;
        public CookTimeAdapter(Activity context, int groupid, int id, ArrayList<CookTimeItemData> list){
            super(context,id,list);
            this.list=list;
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groupid=groupid;
        }

        public View getView(int position, View convertView, ViewGroup parent ){
            View itemView=inflater.inflate(groupid,parent,false);
            TextView textView=(TextView)itemView.findViewById(R.id.cooktime_text);
            textView.setText(list.get(position).getCookTimeText());
            return itemView;
        }

        public View getDropDownView(int position, View convertView, ViewGroup  parent){
            View itemView=inflater.inflate(R.layout.cooktime_spinner_dropdown,parent,false);
            TextView textView=(TextView)itemView.findViewById(R.id.cooktime_text);
            textView.setText(list.get(position).getCookTimeText());
            return itemView;
        }
    }

    public void completeClick(View v) {
        if(editText.getText().toString().equals("")) {
            Toast.makeText(this, "레시피 설명을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if(image == null && !modify) {
            Toast.makeText(this, "사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if(modify) {
                if(image != null) {
                    WriteRecipeMainActivity.images.add(modifyPosition, image);
                    WriteRecipeMainActivity.images.remove(modifyPosition+1);
                    WriteRecipeMainActivity.recyclerAdapter.items
                            .add(modifyPosition,new RecipeStepData(image, String.valueOf(modifyPosition+1), editText.getText().toString()));
                } else {
                    Object object = WriteRecipeMainActivity.recyclerAdapter.items.get(modifyPosition).image;
                    WriteRecipeMainActivity.recyclerAdapter.items
                            .add(modifyPosition,new RecipeStepData(object, String.valueOf(modifyPosition+1), editText.getText().toString()));
                }
                WriteRecipeMainActivity.recyclerAdapter.items.remove(modifyPosition+1);
                WriteRecipeMainActivity.recyclerAdapter.notifyDataSetChanged();
                WriteRecipeMainActivity.methods.add(modifyPosition, editText.getText().toString());
                WriteRecipeMainActivity.methods.remove(modifyPosition+1);
                finish();
            } else {
                WriteRecipeMainActivity.images.add(image);
                WriteRecipeMainActivity.methods.add(editText.getText().toString());
                WriteRecipeMainActivity.stepNum++;
                WriteRecipeMainActivity.refresh();
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(WriteRecipeDetailActivity.this, "정방형으로 촬영시 더 예쁘게 나와요~", Toast.LENGTH_SHORT).show();
        RecipePhotoDialog recipePhotoDialog = new RecipePhotoDialog(WriteRecipeDetailActivity.this);
        recipePhotoDialog.show();
    }

    public class RecipePhotoDialog extends Dialog {
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

        public RecipePhotoDialog(Context context) {
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

    public void backClick(View view) {
        finish();
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
            case CROP_FROM_CAMERA: {
                // 크롭된 이미지를 세팅

                final Bundle extras = data.getExtras();

                if (extras != null) {
                    image = extras.getParcelable("data");
                    cameraBtn.setImageDrawable(new BitmapDrawable(image));
                }
                break;
            }
            case PICK_FROM_ALBUM: {
                currentSelectedUri = data.getData();
                if (currentSelectedUri != null) {
                    //실제 Image의 full path name을 얻어온다.
                    if (findImageFileNameFromUri(currentSelectedUri)) {
                        //ArrayList에 업로드할  객체를 추가한다.
                        if(modify) {
                            boolean add = false;
                            for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                                if(WriteRecipeMainActivity.upLoadfiles.get(i).num == modifyPosition) {
                                    WriteRecipeMainActivity.upLoadfiles.get(i).file = new File(currentFileName);
                                    WriteRecipeMainActivity.upLoadfiles.get(i).tempFiles = false;
                                    add = true;
                                    break;
                                }
                            }
                            if(!add) {
                                WriteRecipeMainActivity.upLoadfiles
                                        .add(new WriteRecipeMainActivity.UpLoadValueObject(new File(currentFileName),modifyPosition, false));
                            }
                        } else {
                            boolean add = false;
                            for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                                if(WriteRecipeMainActivity.upLoadfiles.get(i).num == WriteRecipeMainActivity.stepNum) {
                                    WriteRecipeMainActivity.upLoadfiles.get(i).file = new File(currentFileName);
                                    WriteRecipeMainActivity.upLoadfiles.get(i).tempFiles = false;
                                    add = true;
                                    break;
                                }
                            }
                            if(!add) {
                                WriteRecipeMainActivity.upLoadfiles
                                        .add(new WriteRecipeMainActivity.UpLoadValueObject(new File(currentFileName),WriteRecipeMainActivity.stepNum, false));
                            }
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                image = BitmapFactory.decodeFile(currentFileName, options);
                try {
                    ExifInterface exif = new ExifInterface(new File(myImageDir, currentFileName).getPath());
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);
                } catch (IOException e) {
                    e.getLocalizedMessage();
                }
                cameraBtn.setImageDrawable(new BitmapDrawable(image));
                //cropIntent(currentSelectedUri);

                break;
            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지
                if(WriteRecipeMainActivity.stepNum == WriteRecipeMainActivity.upLoadfiles.size()) {
                    for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                        if(WriteRecipeMainActivity.upLoadfiles.get(i).num == WriteRecipeMainActivity.stepNum) {
                            WriteRecipeMainActivity.upLoadfiles.get(i).file = new File(myImageDir, currentFileName);
                            WriteRecipeMainActivity.upLoadfiles.get(i).tempFiles = false;
                            break;
                        }
                    }
                } else {
                    WriteRecipeMainActivity.upLoadfiles
                            .add(new WriteRecipeMainActivity.UpLoadValueObject(new File(myImageDir, currentFileName),WriteRecipeMainActivity.stepNum, false));
                }
                // cropIntent(currentSelectedUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                image = BitmapFactory.decodeFile(new File(myImageDir, currentFileName).getPath(), options);
                try {
                    ExifInterface exif = new ExifInterface(new File(myImageDir, currentFileName).getPath());
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);
                } catch (IOException e) {
                    e.getLocalizedMessage();
                }
                cameraBtn.setImageDrawable(new BitmapDrawable(image));
                break;
            }
        }
    }


//   private  void  cropIntent(Uri cropUri){
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(cropUri, "image");
//
//        intent.putExtra("outputX", 1080);
//        intent.putExtra("outputY", 1080);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
//
//        startActivityForResult(intent, CROP_FROM_CAMERA);
//    }

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
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);
            if(WriteRecipeMainActivity.stepNum == WriteRecipeMainActivity.upLoadfiles.size()) {
                for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                    if(WriteRecipeMainActivity.upLoadfiles.get(i).num == WriteRecipeMainActivity.stepNum) {
                        WriteRecipeMainActivity.upLoadfiles.get(i).file = tempFile;
                        WriteRecipeMainActivity.upLoadfiles.get(i).tempFiles = false;
                        break;
                    }
                }
            } else {
                WriteRecipeMainActivity.upLoadfiles
                        .add(new WriteRecipeMainActivity.UpLoadValueObject(tempFile, WriteRecipeMainActivity.stepNum, false));
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

    public void calculate() {
        ViewGroup.LayoutParams params = ingrediListView.getLayoutParams();
        params.height = adapter.getCount()*185 + (ingrediListView.getDividerHeight() * (adapter.getCount() - 1));
        ingrediListView.setLayoutParams(params);
        ingrediListView.requestLayout();
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
}