package com.conveniencerecipe.WriteRecipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.conveniencerecipe.FontActivity;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.PropertyManager;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;
import com.conveniencerecipe.ViewPagerAdapter;

import java.io.File;
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

public class WriteRecipePreviewActivity extends FontActivity {
    ViewPager pager;
    ViewPagerAdapter mAdapter;

    ImageView writeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_recipe_preview);

        writeBtn = (ImageView)findViewById(R.id.write_btn);
        pager = (ViewPager)findViewById(R.id.recipe_pager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);

        for(int i=0; i < WriteRecipeMainActivity.images.size(); i++) {
            mAdapter.add(RecipePreviewFragment.newInstance(WriteRecipeMainActivity.images.get(i), Integer.toString(i+1),
                    WriteRecipeMainActivity.methods.get(i)));
        }

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeBtn.setEnabled(false);
                Toast toast = Toast.makeText(WriteRecipePreviewActivity.this, "성공", Toast.LENGTH_SHORT);
                ImageView imageView = new ImageView(WriteRecipePreviewActivity.this);
                imageView.setImageResource(R.drawable.image_recipecomplete_pop);
                toast.setView(imageView);
                toast.setGravity(Gravity.CENTER,50,50);
                toast.show();
                if(WriteRecipeMainActivity.modify) {
                    writeBtn.setEnabled(false);
                    new RecipeModifyAsyncTask().execute(WriteRecipeMainActivity.upLoadfiles);
                } else {
                    if (WriteRecipeMainActivity.upLoadfiles != null && WriteRecipeMainActivity.upLoadfiles.size() > 0) {
                        new FileUpLoadAsyncTask().execute(WriteRecipeMainActivity.upLoadfiles);
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                // 실행할 동작 코딩
                                WriteRecipeMainActivity.recipeMainActivity.finish();
                                finish();
                            }
                        }, 1500);
                    } else {
                        Toast.makeText(WriteRecipePreviewActivity.this, "업로드할 파일이 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void backClick(View view) {
        finish();
    }

    private class FileUpLoadAsyncTask extends AsyncTask<ArrayList<WriteRecipeMainActivity.UpLoadValueObject>, Void, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public FileUpLoadAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<WriteRecipeMainActivity.UpLoadValueObject>... arrayLists) {
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
                        .addFormDataPart("title", WriteRecipeMainActivity.entityObject.title)
                        .addFormDataPart("step", String.valueOf(WriteRecipeMainActivity.stepNum-1))
                        .addFormDataPart("theme", String.valueOf(WriteRecipeMainActivity.entityObject.theme))
                        .addFormDataPart("time", WriteRecipeMainActivity.entityObject.time)
                        .addFormDataPart("price", WriteRecipeMainActivity.entityObject.price);
                int ingrediSize = WriteRecipeMainActivity.entityObject.ingredientArray.size();
                String ingredient = "";
                String newIngredient = "";
                for(int i = 0; i < ingrediSize; i++) {
                    String id = WriteRecipeMainActivity.entityObject.ingredientArray.get(i).id;
                    if(id == null) {
                        if(newIngredient.equals("")) newIngredient = WriteRecipeMainActivity.entityObject.ingredientArray.get(i).title;
                        else newIngredient += "|" + WriteRecipeMainActivity.entityObject.ingredientArray.get(i).title;
                    } else {
                        if(ingredient.equals("")) ingredient = id;
                        else ingredient += "|" + id;
                    }
                }
                if(ingredient.equals("")) ingredient = "57bd717e4555becf4e7c1046";
                builder.addFormDataPart("new_ingredient", newIngredient);
                builder.addFormDataPart("ingredient", ingredient);

                int fileSize = arrayLists[0].size();
                for (int i = 0; i < fileSize; i++) {
                    File file = WriteRecipeMainActivity.upLoadfiles.get(i).file;
                    builder.addFormDataPart("image_" + i, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                    builder.addFormDataPart("method_" + i, WriteRecipeMainActivity.methods.get(i));
                }
                RequestBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_RECIPE_INSERT)
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
            if(s.equalsIgnoreCase("success")){
                int fileSize = WriteRecipeMainActivity.upLoadfiles.size();

                for(int i = 0 ; i < fileSize ; i++){
                    WriteRecipeMainActivity.UpLoadValueObject fileValue = WriteRecipeMainActivity.upLoadfiles.get(i);
                    if(fileValue.tempFiles){
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
            }else{
                Toast.makeText(WriteRecipePreviewActivity.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
            writeBtn.setEnabled(true);
        }
    }

    private class RecipeModifyAsyncTask extends AsyncTask<ArrayList<WriteRecipeMainActivity.UpLoadValueObject>, Void, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public RecipeModifyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<WriteRecipeMainActivity.UpLoadValueObject>... arrayLists) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("id", RecipeActivity.recipeId)
                        .addFormDataPart("title", WriteRecipeMainActivity.entityObject.title)
                        .addFormDataPart("step", String.valueOf(WriteRecipeMainActivity.stepNum-1))
                        .addFormDataPart("theme", String.valueOf(WriteRecipeMainActivity.entityObject.theme))
                        .addFormDataPart("time", WriteRecipeMainActivity.entityObject.time)
                        .addFormDataPart("price", WriteRecipeMainActivity.entityObject.price);
                int ingrediSize = WriteRecipeMainActivity.entityObject.ingredientArray.size();
                String ingredient = "";
                String newIngredient = "";
                for(int i = 0; i < ingrediSize; i++) {
                    String id = WriteRecipeMainActivity.entityObject.ingredientArray.get(i).id;
                    if(id == null) {
                        if(newIngredient.equals("")) newIngredient = WriteRecipeMainActivity.entityObject.ingredientArray.get(i).title;
                        else newIngredient += "|" + WriteRecipeMainActivity.entityObject.ingredientArray.get(i).title;
                    } else {
                        if(ingredient.equals("")) ingredient = id;
                        else ingredient += "|" + id;
                    }
                }
                if(ingredient.equals("")) ingredient = "57bd717e4555becf4e7c1046";
                builder.addFormDataPart("new_ingredient", newIngredient);
                builder.addFormDataPart("ingredient", ingredient);

                int step = WriteRecipeMainActivity.images.size();
                boolean change;
                for (int i = 0; i < step; i++) {
                    change = false;
                    builder.addFormDataPart("method_" + i, WriteRecipeMainActivity.methods.get(i));
                    for(int j = 0; j < WriteRecipeMainActivity.upLoadfiles.size(); j++) {
                        if(WriteRecipeMainActivity.upLoadfiles.get(j).num-1 == i) {
                            change = true;
                            break;
                        }
                    }
                    if(!change) {
                        builder.addFormDataPart("imgcheck_"+i, "N");
                    }
                }
                for(int i = 0; i < WriteRecipeMainActivity.upLoadfiles.size(); i++) {
                    File file = WriteRecipeMainActivity.upLoadfiles.get(i).file;
                    int num = WriteRecipeMainActivity.upLoadfiles.get(i).num-1;
                    builder.addFormDataPart("image_" + num, file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                    builder.addFormDataPart("imgcheck_" + num, "Y");
                }
                RequestBody fileUploadBody = builder.build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_URL_RECIPE_INSERT)
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
                }
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("success")){
                int fileSize = WriteRecipeMainActivity.upLoadfiles.size();

                for(int i = 0 ; i < fileSize ; i++){
                    WriteRecipeMainActivity.UpLoadValueObject fileValue = WriteRecipeMainActivity.upLoadfiles.get(i);
                    if(fileValue.tempFiles){
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                }
                WriteRecipeMainActivity.recipeMainActivity.finish();
                finish();
            }else{
                Toast.makeText(WriteRecipePreviewActivity.this, "파일업로드에 실패했습니다", Toast.LENGTH_LONG).show();
            }
            writeBtn.setEnabled(true);
        }
    }
}