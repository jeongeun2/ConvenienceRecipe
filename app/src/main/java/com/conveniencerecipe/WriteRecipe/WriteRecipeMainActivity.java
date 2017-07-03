package com.conveniencerecipe.WriteRecipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.conveniencerecipe.FontActivity;
import com.conveniencerecipe.NetworkDefineConstant;
import com.conveniencerecipe.ParseDataParseHandler;
import com.conveniencerecipe.R;
import com.conveniencerecipe.RecipeActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class WriteRecipeMainActivity extends FontActivity {
    EditText mTitle;
    EditText mIngredient;
    EditText mPrice;
    EditText mTime;
    Spinner spTheme;
    ArrayAdapter<CharSequence> adapterTheme;
    static RecipeEntityObject entityObject;

    public static Activity recipeMainActivity;
    Toolbar toolbar;

    ListView listView;
    IngredientListAdapter listAdapter;
    static ArrayList<String> ingrediItems = new ArrayList<>();

    RecyclerView recyclerView;
    static RecipeStepAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    CancelWriteRecipe cancelWriteRecipe;

    TextView preivewBtn;
    static ArrayList<Object> images = new ArrayList<>();
    static  ArrayList<String> methods = new ArrayList<>();
    static ArrayList<UpLoadValueObject> upLoadfiles = new ArrayList<>();
    public static int stepNum = 1;
    public static boolean modify;
    public static RecipeActivity.RecipeDetailData recipeDetailData;
    public static IngredientData[] ingredientDatas;
    ListView autoListView;
    ArrayAdapter<String> autoAdapter;
    ArrayList<String> autoIngrediList = new ArrayList<>();
    NestedScrollView nestedScrollView;

    public static class UpLoadValueObject {
        File file; //업로드할 파일
        int num; //단계 번호
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, int num, boolean tempFiles) {
            this.file = file;
            this.num = num;
            this.tempFiles = tempFiles;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_recipe_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cancelWriteRecipe = new CancelWriteRecipe(WriteRecipeMainActivity.this);

        Intent intent = getIntent();
        modify = intent.getBooleanExtra("modify", false);

        recipeMainActivity = WriteRecipeMainActivity.this;
        if(entityObject == null) {
            entityObject = new RecipeEntityObject();
        }

        mTitle = (EditText)findViewById(R.id.title_edit);
        mIngredient = (EditText)findViewById(R.id.ingredient_edit);
        mPrice = (EditText)findViewById(R.id.price_edit);
        mTime = (EditText)findViewById(R.id.time_edit);

        spTheme = (Spinner)findViewById(R.id.theme_list);
        adapterTheme = ArrayAdapter.createFromResource(this, R.array.theme_array_item, R.layout.spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(adapterTheme);

        spTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                entityObject.theme = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                entityObject.theme = 0;
            }
        });

        mIngredient.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mIngredient.setInputType(InputType.TYPE_CLASS_TEXT);

        ImageView button=(ImageView)findViewById(R.id.ingredi_add);
        listView=(ListView)findViewById(R.id.list_ingredi);
        listAdapter = new IngredientListAdapter(this, ingrediItems);
        listView.setAdapter(listAdapter);
        calculate();
        final InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        autoListView = (ListView)findViewById(R.id.autocomplete_list);
        autoAdapter = new ArrayAdapter<String>(this, R.layout.item_auto_list, autoIngrediList);
        autoListView.setAdapter(autoAdapter);
        nestedScrollView = (NestedScrollView)findViewById(R.id.nested_scrollview);
        autoListView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                //웹뷰 스크롤 끝나면 스크롤 간섭 허가
                    nestedScrollView.requestDisallowInterceptTouchEvent(false);
                }else{
                    //웹뷰 스크롤 중에는 스크롤뷰 간섭 x
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        mIngredient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new IngredientJSONList().execute(mIngredient.getText().toString());
            }
        });

        autoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ingrediItems.add(autoIngrediList.get(i));
                entityObject.ingredientArray.add(ingredientDatas[i]);
                mPrice.setText(String.valueOf(Integer.parseInt(mPrice.getText().toString())+ingredientDatas[i].price));
                calculate();
                mIngredient.setText("");
                imm.hideSoftInputFromWindow(mIngredient.getWindowToken(), 0);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIngredient.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "재료를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    ingrediItems.add(mIngredient.getText().toString());
                    calculate();
                    for(int i=0; i<autoAdapter.getCount(); i++) {
                        if(autoAdapter.getItem(i).equals(mIngredient.getText().toString())) {
                            entityObject.ingredientArray.add(ingredientDatas[i]);
                            mPrice.setText(String.valueOf(Integer.parseInt(mPrice.getText().toString())+ingredientDatas[i].price));
                            mIngredient.setText("");
                            imm.hideSoftInputFromWindow(mIngredient.getWindowToken(), 0);
                            return;
                        }
                    }
                    entityObject.ingredientArray.add(new IngredientData(null,mIngredient.getText().toString(),0));
                    mIngredient.setText("");
                    imm.hideSoftInputFromWindow(mIngredient.getWindowToken(), 0);
                }
            }
        });

        mIngredient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    if(mIngredient.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "재료를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ingrediItems.add(mIngredient.getText().toString());
                        calculate();
                        for(int i=0; i<autoAdapter.getCount(); i++) {
                            if(autoAdapter.getItem(i).equals(mIngredient.getText().toString())) {
                                entityObject.ingredientArray.add(ingredientDatas[i]);
                                mPrice.setText(String.valueOf(Integer.parseInt(mPrice.getText().toString())+ingredientDatas[i].price));
                                mIngredient.setText("");
                                imm.hideSoftInputFromWindow(mIngredient.getWindowToken(), 0);
                                return true;
                            }
                        }
                        entityObject.ingredientArray.add(new IngredientData(null,mIngredient.getText().toString(),0));
                        mIngredient.setText("");
                        imm.hideSoftInputFromWindow(mIngredient.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.write_recycler);
        recyclerAdapter = new RecipeStepAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        refresh();

        preivewBtn = (TextView)findViewById(R.id.preview_btn);
        preivewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entityObject.title = mTitle.getText().toString();
                entityObject.price = mPrice.getText().toString();
                entityObject.time = mTime.getText().toString();

                if(entityObject.title == null || entityObject.title.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                    mTitle.requestFocus();
                    return;
                } else if(entityObject.price == null || entityObject.price.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "가격을 입력해주세요", Toast.LENGTH_SHORT).show();
                    mPrice.requestFocus();
                    return;
                } else if(images.size() == 0){
                    Toast.makeText(getApplicationContext(), "레시피를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if(ingrediItems.size() == 0){
                    Toast.makeText(getApplicationContext(), "재료를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }  else if(entityObject.time == null|| entityObject.time.length() <= 0){
                    Toast.makeText(getApplicationContext(), "시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(WriteRecipeMainActivity.this, WriteRecipePreviewActivity.class);
                    startActivity(intent);
                }
            }
        });

        if(modify) {
            recipeDetailData = RecipeActivity.recipeDetailData;
            if(RecipeActivity.recipeActivity != null) {
                RecipeActivity.recipeActivity.finish();
            }
            mTitle.setText(recipeDetailData.title);
            mPrice.setText(String.valueOf(recipeDetailData.price));
            mTime.setText(String.valueOf(recipeDetailData.time));
            for(int i=0; i < recipeDetailData.ingredient.size(); i++) {
               ingrediItems.add(recipeDetailData.ingredient.get(i).name);
                if(recipeDetailData.ingredient.get(i).id == null) {
                    entityObject.ingredientArray.add(new IngredientData(null, recipeDetailData.ingredient.get(i).name, 0));
                } else {
                    entityObject.ingredientArray.add
                            (new IngredientData(recipeDetailData.ingredient.get(i).id, recipeDetailData.ingredient.get(i).name, Integer.parseInt(recipeDetailData.ingredient.get(i).price)));
                }
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = listAdapter.getCount()*170 + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
            for(int i=0; i < RecipeActivity.recipeDetailData.step; i++) {
                WriteRecipeMainActivity.images.add(recipeDetailData.recipeImg.get(i));
                WriteRecipeMainActivity.methods.add(recipeDetailData.recipeMethod.get(i));
                WriteRecipeMainActivity.stepNum++;
                WriteRecipeMainActivity.refresh();
            }
        }
    }

    public static class IngredientData {
        public String id, title;
        public int price;

        public IngredientData() {}

        public IngredientData(String id, String title, int price) {
            this.id = id;
            this.title = title;
            this.price = price;
        }
    }

    public class IngredientListAdapter extends ArrayAdapter {
        Context context;
        ArrayList<String> list;

        IngredientListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            ImageView button = null;

            final String item = (String) getItem(position);
            convertView = LayoutInflater.from(context).inflate(R.layout.ingredient_list_item, null);
            textView = (TextView) convertView.findViewById(R.id.ingredi_text);
            button = (ImageView) convertView.findViewById(R.id.delete_btn);
            textView.setText(item);

            button.setOnClickListener(new ImageView.OnClickListener() {
                public void onClick(View v) {
                    list.remove(position);
                    mPrice.setText(String.valueOf(Integer.parseInt(mPrice.getText().toString())-entityObject.ingredientArray.get(position).price));
                    entityObject.ingredientArray.remove(position);
                    notifyDataSetChanged();
                    calculate();
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        images.clear();
        methods.clear();
        upLoadfiles.clear();
        ingrediItems.clear();
        recyclerAdapter.items.clear();
        entityObject.ingredientArray.clear();
        stepNum = 1;
    }

    public void backClick(View view) {
        cancelWriteRecipe.show();
    }

    public void plusClick(View v) {
        Intent intent = new Intent(this, WriteRecipeDetailActivity.class);
        startActivity(intent);
    }

    public void calculate() {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            final View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void refresh() {
        if(images.size()-recyclerAdapter.getItemCount() != 0) {
            for(int i=recyclerAdapter.getItemCount(); i<images.size(); i++) {
                recyclerAdapter.add(new RecipeStepData(images.get(i), Integer.toString(i+1), methods.get(i)));
            }
        }
    }

    public class IngredientJSONList extends AsyncTask<String, Integer, IngredientData[]> {
        @Override
        protected IngredientData[] doInBackground(String... params) {
            Response response = null;
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                String word = params[0];
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.SERVER_URL_AUTOCOMPLETE_INGREDIENT, word))
                        .build();
                //동기 방식
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();

                ResponseBody responseBody = response.body();
                String retruedJson = responseBody.string();

                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getAutoIngredientList(new StringBuilder(retruedJson));
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
        protected void onPostExecute(IngredientData[] result) {
            if(result != null){
                autoIngrediList.clear();
                ingredientDatas = result;
                for(int i = 0; i < ingredientDatas.length; i++) {
                    autoIngrediList.add(ingredientDatas[i].title);
                }
                autoAdapter.notifyDataSetChanged();
                if(autoAdapter.getCount() > 5) {
                    ViewGroup.LayoutParams params = autoListView.getLayoutParams();
                    params.height = 560;
                    autoListView.setLayoutParams(params);
                    autoListView.requestLayout();
                } else {
                    autoListCalculate();
                }
            } else {
                autoIngrediList.clear();
                autoAdapter.notifyDataSetChanged();
                autoListCalculate();
            }
        }
    }

    public void autoListCalculate() {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(autoListView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < autoAdapter.getCount(); i++) {
            final View listItem = autoAdapter.getView(i, null, autoListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = autoListView.getLayoutParams();
        params.height = totalHeight + (autoListView.getDividerHeight() * (autoListView.getCount() - 1));
        autoListView.setLayoutParams(params);
        autoListView.requestLayout();
    }

    @Override
    public void onBackPressed() {
        cancelWriteRecipe.show();
    }
}