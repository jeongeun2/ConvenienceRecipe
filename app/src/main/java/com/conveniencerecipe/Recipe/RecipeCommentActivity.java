package com.conveniencerecipe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei 34 on 2016-08-03.
 */
public class RecipeCommentActivity extends FontActivity {
    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    static RecyclerView recyclerView;
    static EditText commentEdit;
    static TextView commentInputBtn;
    static String modifyCommentId = "";
    public static AsyncCommentJSONList commentJSONList;

    static InputMethodManager imm;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.pink);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncCommentJSONList().execute("data");
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView=(RecyclerView)findViewById(R.id.comment_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        commentEdit = (EditText)findViewById(R.id.comment_edit);
        commentInputBtn = (TextView)findViewById(R.id.comment_input_btn);
        commentInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentInputBtn.getText().toString().equals("등록")) {
                    if(commentEdit.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        commentUpload();
                    }
                } else {
                    commentModify();
                    commentInputBtn.setText("등록");
                }
            }
        });

        commentJSONList = new AsyncCommentJSONList();
    }

    public void closeClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncCommentJSONList().execute();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        List<CommentListData> items;

        public RecyclerAdapter(List<CommentListData> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,null);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ((ViewHolder)holder).setMyData(items.get(position));

            if(!items.get(position).userId.equals(PropertyManager.getInstance().getId())) {
                holder.menuBtn.setVisibility(View.GONE);
            }
            holder.menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecipeCommentDialog recipeCommentDialog = new RecipeCommentDialog(RecipeCommentActivity.this,
                            items.get(position).content, items.get(position).commentId);
                    recipeCommentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            new AsyncCommentJSONList().execute();
                        }
                    });
                    recipeCommentDialog.show();
                }
            });
            holder.profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecipeCommentActivity.this, UserPageActivity.class);
                    intent.putExtra("userId", items.get(position).userId);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout commentLayout;
            CircleImageView profile_img;
            TextView nickname;
            TextView content;
            TextView created_at;
            ImageView menuBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                commentLayout = (LinearLayout)itemView.findViewById(R.id.comment_layout);
                profile_img=(CircleImageView)itemView.findViewById(R.id.Comment_list_image);
                nickname=(TextView)itemView.findViewById(R.id.Comment_nickname);
                created_at=(TextView)itemView.findViewById(R.id.Comment_time);
                content=(TextView)itemView.findViewById(R.id.Comment);
                menuBtn=(ImageView)itemView.findViewById(R.id.menu_btn);
            }

            public void setMyData(CommentListData data) {
                if(!data.profile_img.equals("null")) {
                    Glide.with(MyApplication.RecipeContext()).load(Uri.parse(data.profile_img)).into(profile_img);
                }else {
                    profile_img.setImageResource(R.drawable.image_profile);
                }
                nickname.setText(data.nickname);
                created_at.setText(data.created_at);
                content.setText(data.content);
            }
        }
    }

    public static class CommentListData {
        public String userId, commentId, nickname, profile_img,content,created_at;

        public CommentListData(){}
    }

    public class AsyncCommentJSONList extends AsyncTask<String, Integer, ArrayList<CommentListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(RecipeCommentActivity.this);
        @Override
        protected ArrayList<CommentListData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Intent intent = getIntent();
                String recipeId = intent.getStringExtra("recipeId");
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_COMMENT, recipeId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONCommentList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(ArrayList<CommentListData> result) {
            dialog.dismiss();

            if(result != null && result.size() >= 0){
                RecyclerAdapter commentRecyclerViewAdapter = new RecyclerAdapter(result);
                recyclerView.setAdapter(commentRecyclerViewAdapter);
                commentRecyclerViewAdapter.notifyDataSetChanged();
            } else{
                Toast.makeText(RecipeCommentActivity.this, "레시피댓글 정보를 얻어오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            commentEdit.setText("");
        }
    }

    boolean flag;
    private boolean commentUpload() {
        //이 부분은  AsyncTask의 백그라운드로 변신시키기 바람
        new Thread(new Runnable() {
            public void run() {
                Response response = null;
                //업로드할 Mime Type 설정
                final MediaType pngType = MediaType.parse("image/png");

                try {
                    //업로드는 타임 및 리드타임을 넉넉히 준다.
                    OkHttpClient toServer = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();
                    Intent intent = getIntent();
                    String recipeId = intent.getStringExtra("recipeId");
                    String id = PropertyManager.getInstance().getId();
                    //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                    RequestBody commentUploadBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //파일 업로드시 반드시 설정
                            .addFormDataPart("userId", id) //기본 쿼리
                            .addFormDataPart("id", recipeId)
                            .addFormDataPart("comment", commentEdit.getText().toString())
                            .build();
                    //요청 세팅
                    Request request = new Request.Builder()
                            .url(NetworkDefineConstant.SERVER_URL_COMMENT_INSERT)
                            .post(commentUploadBody) //반드시 post로
                            .build();
                    //동기 방식
                    response = toServer.newCall(request).execute();

                    flag = response.isSuccessful();
                    //응답 코드 200등등
                    int responseCode = response.code();
                    if (flag) {
                        e("response결과", response.message()); //읃답에 대한 메세지(OK)
                        e("response응답바디", response.body().string()); //json으로 변신
                    }
                } catch (UnknownHostException une) {
                    e("fileUpLoad", une.toString());
                } catch (UnsupportedEncodingException uee) {
                    e("fileUpLoad", uee.toString());
                } catch (Exception e) {
                    e("fileUpLoad", e.toString());
                } finally {
                    if(response != null) {
                        response.close();
                        imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
                    }
                }
            }
        }).start();
        new AsyncCommentJSONList().execute();
        return flag;
    }

    private boolean commentModify() {
        //이 부분은  AsyncTask의 백그라운드로 변신시키기 바람
        new Thread(new Runnable() {
            final  InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            public void run() {
                Response response = null;
                //업로드할 Mime Type 설정
                final MediaType pngType = MediaType.parse("image/png");

                try {
                    //업로드는 타임 및 리드타임을 넉넉히 준다.
                    OkHttpClient toServer = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();
                    Intent intent = getIntent();
                    String recipeId = intent.getStringExtra("recipeId");
                    //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                    RequestBody commentUploadBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //파일 업로드시 반드시 설정
                            .addFormDataPart("id", modifyCommentId)
                            .addFormDataPart("comment", commentEdit.getText().toString())
                            .build();
                    //요청 세팅
                    Request request = new Request.Builder()
                            .url(NetworkDefineConstant.SERVER_URL_COMMENT_INSERT)
                            .put(commentUploadBody) //반드시 post로
                            .build();
                    //동기 방식
                    response = toServer.newCall(request).execute();

                    flag = response.isSuccessful();
                    //응답 코드 200등등
                    int responseCode = response.code();
                    if (flag) {
                        e("response결과", response.message()); //읃답에 대한 메세지(OK)
                        e("response응답바디", response.body().string()); //json으로 변신
                    }
                } catch (UnknownHostException une) {
                    e("fileUpLoad", une.toString());
                } catch (UnsupportedEncodingException uee) {
                    e("fileUpLoad", uee.toString());
                } catch (Exception e) {
                    e("fileUpLoad", e.toString());
                } finally {
                    if(response != null) {
                        response.close();
                        imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
                    }
                }
            }
        }).start();
        new AsyncCommentJSONList().execute();
        return flag;
    }
}