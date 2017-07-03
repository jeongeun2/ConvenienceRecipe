package com.conveniencerecipe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
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
import com.matthewtamlin.sliding_intro_screen_library.DotIndicator;

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
 * Created by ccei 34 on 2016-08-09.
 */
public class QnaDetailActivity extends FontActivity {
    ViewPager pager;
    ViewPagerAdapter adapter;
    int pagersize;

    SwipeRefreshLayout refreshLayout;
    Handler mHandler = new Handler(Looper.getMainLooper());
    static RecyclerView recyclerView;
    RecyclerAdapter commentRecyclerViewAdapter;
    static EditText commentEdit;
    static TextView commentInputBtn;
    static String modifyCommentId = "";
    public static AsyncCommentJSONList commentJSONList;

    static InputMethodManager imm;

    ImageView profileimage;
    TextView nickname;
    TextView qnatitle;
    TextView qnatime;
    TextView qnacontent;
    public static QnaDetailEntityObject qnaDetailEntityObject;
    ImageView menuBtn;
    DotIndicator indicator;
    public static QnaCommentDialog qnaCommentDialog;

    Intent intent;
    public static String postingId;
    public static Activity qnaDetailActivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qnadetail_comment);

        new AsyncCommentJSONList().execute("data");
        pager = (ViewPager)findViewById(R.id.qna_detail_viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator = (DotIndicator)findViewById( R.id.qna_indicator_ad );
        indicator.setSelectedDotColor( Color.parseColor( "#f44236" ) );
        indicator.setUnselectedDotColor( Color.parseColor( "#CFCFCF" ) );

        //final int pageCount = Models.size();
        int pageCount = pagersize;
        //pager.setAdapter( Models );

        final Runnable setImageRunnable = new Runnable()
        {
            public void run()
            {
                int currentPage = pager.getCurrentItem();
                if( currentPage >= pagersize - 1 ) pager.setCurrentItem( 0, true );
                else pager.setCurrentItem( currentPage + 1, true );
                indicator.setSelectedItem( ( currentPage + 1 == pagersize ) ? 0 : currentPage + 1, true );
            }
        };

        profileimage = (ImageView)findViewById(R.id.qna_detail_image);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(qnaDetailEntityObject.userId.equals(PropertyManager.getInstance().getId())) {
                    intent = new Intent(QnaDetailActivity.this, MypageActivity.class);
                } else {
                    intent = new Intent(QnaDetailActivity.this, UserPageActivity.class);
                }
                intent.putExtra("userId", qnaDetailEntityObject.userId);
                startActivity(intent);
            }
        });
        nickname = (TextView)findViewById(R.id.nick_name);
        qnatitle = (TextView)findViewById(R.id.qna_title);
        qnatime = (TextView)findViewById(R.id.qna_time);
        qnacontent = (TextView)findViewById(R.id.qna_detail_text);

        intent = getIntent();
        postingId = intent.getStringExtra("postingId");



        menuBtn = (ImageView)findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QnaDetailDialog qnaDetailDialog = new QnaDetailDialog(QnaDetailActivity.this);
                qnaDetailDialog.show();
            }
        });
        qnaDetailActivity = QnaDetailActivity.this;

       /* refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
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
        });*/
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView=(RecyclerView)findViewById(R.id.qna_comment_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

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
                new AsyncCommentJSONList().execute("data");
            }
        });
        commentJSONList = new AsyncCommentJSONList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncQnaDetailJSONList().execute();
    }

    public static class QnaDetailEntityObject {
        public String nickname, profile_img, title, created_at, content, userId;
        public ArrayList<String> image = new ArrayList<String>();

        public QnaDetailEntityObject(){}
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        List<RecipeCommentActivity.CommentListData> items;

        public RecyclerAdapter(List<RecipeCommentActivity.CommentListData> items) {
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
                    qnaCommentDialog = new QnaCommentDialog(QnaDetailActivity.this, items.get(position).content, items.get(position).commentId);
                    qnaCommentDialog.show();
                    qnaCommentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            new AsyncQnaDetailJSONList().execute();
                        }
                    });
                }
            });
            holder.profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    if(items.get(position).userId.equals(PropertyManager.getInstance().getId())) {
                        intent = new Intent(QnaDetailActivity.this, MypageActivity.class);
                    } else {
                        intent = new Intent(QnaDetailActivity.this, UserPageActivity.class);
                    }
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
                menuBtn = (ImageView)itemView.findViewById(R.id.menu_btn);
            }

            public void setMyData(RecipeCommentActivity.CommentListData data) {
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

    public class AsyncCommentJSONList extends AsyncTask<String, Integer, ArrayList<RecipeCommentActivity.CommentListData>> {
        CustomProgressDialog dialog = new CustomProgressDialog(QnaDetailActivity.this);
        @Override
        protected ArrayList<RecipeCommentActivity.CommentListData> doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_QNA_COMMENT, postingId))
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
        protected void onPostExecute(ArrayList<RecipeCommentActivity.CommentListData> result) {
            dialog.dismiss();

            if(result != null && result.size() >= 0){
                commentRecyclerViewAdapter = new RecyclerAdapter(result);
                recyclerView.setAdapter(commentRecyclerViewAdapter);
                commentRecyclerViewAdapter.notifyDataSetChanged();
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


                    String id = PropertyManager.getInstance().getId();
                    //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                    RequestBody commentUploadBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //파일 업로드시 반드시 설정
                            .addFormDataPart("userId", id) //기본 쿼리
                            .addFormDataPart("id", postingId)
                            .addFormDataPart("comment", commentEdit.getText().toString())
                            .build();
                    //요청 세팅
                    Request request = new Request.Builder()
                            .url(NetworkDefineConstant.SERVER_URL_QNA_COMMENT_INSERT)
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
                            .url(NetworkDefineConstant.SERVER_URL_QNA_COMMENT_INSERT)
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
        return flag;
    }

    public class AsyncQnaDetailJSONList extends AsyncTask<String, Integer, QnaDetailEntityObject> {
        CustomProgressDialog dialog = new CustomProgressDialog(QnaDetailActivity.this);
        @Override
        protected QnaDetailEntityObject doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_QNADETAIL, postingId))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONQnaDetailAllList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(QnaDetailEntityObject result) {
            dialog.dismiss();

            if(result != null){
                qnaDetailEntityObject = result;
                if(!result.profile_img.equals("null")) {
                    Glide.with(MyApplication.RecipeContext()).load(Uri.parse(result.profile_img)).into(profileimage);
                } else {
                    profileimage.setImageResource(R.drawable.image_profile);
                }
                nickname.setText(result.nickname+" | ");
                qnatitle.setText(result.title);
                qnatime.setText(result.created_at);
                qnacontent.setText(result.content);

                pagersize = result.image.size();
                if(result.image.size() == 0) {
                    pager.setVisibility(View.GONE);
                } else {
                    for(int i = 0; i < result.image.size(); i++) {
                        adapter.add(QnaDetailFragment.newInstance(result.image.get(i)));
                    }
                }
                if(!qnaDetailEntityObject.userId.equals(PropertyManager.getInstance().getId())) {
                    menuBtn.setVisibility(View.GONE);
                }
                indicator.setNumberOfItems(pagersize);
                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }
                    @Override
                    public void onPageSelected(int position) {
                        indicator.setSelectedItem( pager.getCurrentItem(), true );
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                new AsyncCommentJSONList().execute();
            } else{
                Toast.makeText(QnaDetailActivity.this, "뭐먹지 정보를 얻어오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void backClick(View view) {
        finish();
    }
}