package com.conveniencerecipe;

import android.util.Log;

import com.conveniencerecipe.NoticeFragment.FollowingnoticeFragment;
import com.conveniencerecipe.NoticeFragment.MynoticeFragment;
import com.conveniencerecipe.RecipeListFragment.NewestFragment;
import com.conveniencerecipe.RecipeListFragment.QnAFragment;
import com.conveniencerecipe.WriteRecipe.WriteRecipeMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseDataParseHandler {

    public static ArrayList<QnAFragment.QnaData> getJSONQnaList(StringBuilder buf) {

        ArrayList<QnAFragment.QnaData> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("data");
                int dataSize = datas.length();
                if (dataSize > 0) {
                    jsonAllList = new ArrayList<QnAFragment.QnaData>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        QnAFragment.QnaData qnaData = new QnAFragment.QnaData();
                        qnaData.title = data.optString("title");
                        qnaData.time = data.optString("created_at");
                        qnaData.nickname = data.optString("nickname");
                        qnaData.image = data.optString("image");
                        qnaData.postingId = data.optString("posting_id");
                        qnaData.commentNum = data.optString("comments");
                        jsonAllList.add(qnaData);
                    }

                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }


    public static ArrayList<RecipeListData> getJSONRecipeList(StringBuilder buf){
        ArrayList<RecipeListData> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            NewestFragment.banner1 = jsonObject.optString("banner_1");
            NewestFragment.banner2 = jsonObject.optString("banner_2");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("data");
                int dataSize = datas.length();
                if (dataSize > 0) {
                    jsonAllList = new ArrayList<RecipeListData>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        RecipeListData recipeData = new RecipeListData();
                        recipeData.image = data.optString("image");
                        recipeData.title = data.optString("title");
                        recipeData.likenum = data.optString("likes");
                        recipeData.commentnum = data.optString("comments");
                        recipeData.recipeID = data.optString("recipe_id");
                        recipeData.liked = data.optString("liked");

                        JSONArray event = data.optJSONArray("event");
                        int eventdataSize = event.length();
                        if (eventdataSize > 0) {
                            for (int j = 0; j < eventdataSize; j++) {
                                recipeData.event.add(event.get(j).toString());
                            }
                        }
                        jsonAllList.add(recipeData);
                    }
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

    public static ArrayList<RecipeCommentActivity.CommentListData> getJSONCommentList(StringBuilder buf) {

        ArrayList<RecipeCommentActivity.CommentListData> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("comment");
                int dataSize = datas.length();
                    jsonAllList = new ArrayList<RecipeCommentActivity.CommentListData>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        RecipeCommentActivity.CommentListData commentData = new RecipeCommentActivity.CommentListData();
                        commentData.userId = data.optString("userId");
                        commentData.commentId = data.optString("comment_id");
                        commentData.nickname = data.optString("nickname");
                        commentData.profile_img = data.optString("profile_img");
                        commentData.content = data.optString("content");
                        commentData.created_at = data.optString("created_at");
                        jsonAllList.add(commentData);
                    }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

    public static QnaDetailActivity.QnaDetailEntityObject getJSONQnaDetailAllList(StringBuilder buf) {
        QnaDetailActivity.QnaDetailEntityObject qnadetail = new QnaDetailActivity.QnaDetailEntityObject();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            qnadetail.nickname = jsonObject.optString("nickname");
            qnadetail.profile_img = jsonObject.optString("profile_img");
            qnadetail.title = jsonObject.optString("title");
            qnadetail.created_at = jsonObject.optString("created_at");
            qnadetail.content = jsonObject.optString("content");
            qnadetail.userId = jsonObject.optString("userId");

            JSONArray images = jsonObject.optJSONArray("image");

            int dataSize = images.length();
            if (dataSize > 0) {
                for (int i = 0; i < dataSize; i++) {
                    qnadetail.image.add(images.get(i).toString());
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return qnadetail;
    }



    public static NavProfile getJSONProfile(StringBuilder buf) {
        NavProfile navprofile = new NavProfile();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            navprofile.navprofileImg = jsonObject.optString("profile_img");
            navprofile.navnickname = jsonObject.optString("nickname");

        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return navprofile;
    }



    public static UserPageActivity.UserData getJSONUserpageAllList(StringBuilder buf) {
        UserPageActivity.UserData userPageData = new UserPageActivity.UserData();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            userPageData.userId = jsonObject.optString("userId");
            userPageData.nickname = jsonObject.optString("nickname");
            userPageData.profileImg = jsonObject.optString("profile_img");
            userPageData.pageCheck = jsonObject.optString("pageCheck");
            userPageData.followCheck = jsonObject.optString("followCheck");
            userPageData.followerTotal = jsonObject.optInt("follower_total");
            userPageData.followingTotal = jsonObject.optInt("following_total");

            JSONArray follower = jsonObject.optJSONArray("follower");
            JSONArray following = jsonObject.optJSONArray("following");
            JSONArray activity = jsonObject.optJSONArray("activity");
            JSONArray bookmark = jsonObject.optJSONArray("bookmark");

            int followerdataSize = follower.length();
            int followingdataSize = following.length();
            int activitydataSize = activity.length();
            int bookmarkdataSize = bookmark.length();

            if (followerdataSize > 0) {
                for (int i = 0; i < followerdataSize; i++) {
                    UserPageActivity.FollowData followerData = new UserPageActivity.FollowData();
                    JSONObject followerJSONObject = follower.getJSONObject(i);
                    followerData.userId = followerJSONObject.optString("userId");
                    followerData.nickname = followerJSONObject.optString("nickname");
                    followerData.profileImg = followerJSONObject.optString("profile_img");
                    followerData.followBack = followerJSONObject.optString("follow_back");

                    userPageData.follower.add(followerData);
                }
            }
            if (followingdataSize > 0) {
                for (int i = 0; i < followingdataSize; i++) {
                    UserPageActivity.FollowData followerData = new UserPageActivity.FollowData();
                    JSONObject followingJSONObject = following.getJSONObject(i);
                    followerData.userId = followingJSONObject.optString("userId");
                    followerData.nickname = followingJSONObject.optString("nickname");
                    followerData.profileImg = followingJSONObject.optString("profile_img");
                    followerData.followBack = followingJSONObject.optString("follow_back");

                    userPageData.following.add(followerData);
                }
            }
            if (activitydataSize > 0) {
                for (int i = 0; i < activitydataSize; i++) {
                    RecipeListData recipeListData = new RecipeListData();
                    JSONObject activityData = activity.getJSONObject(i);
                    recipeListData.recipeID = activityData.optString("recipe_id");
                    recipeListData.title = activityData.optString("title");
                    recipeListData.image = activityData.optString("image");
                    recipeListData.likenum = activityData.optString("likes");
                    recipeListData.commentnum = activityData.optString("comments");
                    recipeListData.liked = activityData.optString("liked");

                    JSONArray event = activityData.optJSONArray("event");
                    int eventdataSize = event.length();
                    if (eventdataSize > 0) {
                        for (int j = 0; j < eventdataSize; j++) {
                            recipeListData.event.add(event.get(j).toString());
                        }
                    }
                    userPageData.activity.add(recipeListData);
                }
            }
            if (bookmarkdataSize > 0) {
                for (int i = 0; i < bookmarkdataSize; i++) {
                    RecipeListData recipeListData = new RecipeListData();
                    JSONObject bookmarkData = bookmark.getJSONObject(i);
                    recipeListData.recipeID = bookmarkData.optString("recipe_id");
                    recipeListData.title = bookmarkData.optString("title");
                    recipeListData.image = bookmarkData.optString("image");
                    recipeListData.likenum = bookmarkData.optString("likes");
                    recipeListData.commentnum = bookmarkData.optString("comments");
                    recipeListData.liked = bookmarkData.optString("liked");

                    JSONArray event = bookmarkData.optJSONArray("event");
                    int eventdataSize = event.length();
                    if (eventdataSize > 0) {
                        for (int j = 0; j < eventdataSize; j++) {
                            recipeListData.event.add(event.get(j).toString());
                        }
                    }
                    userPageData.scrap.add(recipeListData);
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return userPageData;
    }

    public static RecipeActivity.RecipeDetailData getJSONRecipeDetailAllList(StringBuilder buf) {
        RecipeActivity.RecipeDetailData recipeDetail = new RecipeActivity.RecipeDetailData();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            recipeDetail.liked = jsonObject.optString("liked");
            recipeDetail.bookmarked = jsonObject.optString("bookmarked");
            recipeDetail.recipeId = jsonObject.optString("_id");
            recipeDetail.userId = jsonObject.optString("userId");
            recipeDetail.title = jsonObject.optString("title");
            recipeDetail.step = jsonObject.optInt("step");
            recipeDetail.price = jsonObject.optInt("price");
            recipeDetail.createdAt = jsonObject.optString("created_at");
            recipeDetail.nickname = jsonObject.optString("nickname");
            recipeDetail.profileImg = jsonObject.optString("profile_img");
            recipeDetail.time = jsonObject.optInt("time");
            JSONArray themeData = jsonObject.optJSONArray("theme");
            recipeDetail.theme = themeData.get(0).toString();

            JSONArray recipeDatas = jsonObject.optJSONArray("data");
            for (int i = 0; i < recipeDatas.length(); i++) {
                JSONObject recipeData = recipeDatas.getJSONObject(i);
                recipeDetail.recipeMethod.add(recipeData.optString("method"));
                recipeDetail.recipeImg.add(recipeData.optString("image"));
            }

            JSONArray ingrediDatas = jsonObject.optJSONArray("ingredient");
            if(!ingrediDatas.getJSONObject(0).optString("id").equals("57bd717e4555becf4e7c1046")) {
                for(int i = 0; i < ingrediDatas.length(); i++) {
                    JSONObject ingrediData = ingrediDatas.getJSONObject(i);
                    recipeDetail.ingredient.add(new RecipeActivity.IngredientData
                            (ingrediData.optString("title"), ingrediData.optString("event"), ingrediData.optString("id"), ingrediData.optString("price")));
                }
            }

            JSONArray newIngredients = jsonObject.optJSONArray("new_ingredient");
            for(int i = 0; i < newIngredients.length(); i++) {
                recipeDetail.ingredient.add(new RecipeActivity.IngredientData(newIngredients.getString(i), null, null, null));
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return recipeDetail;
    }

    /*public static ArrayList<FinalFragment.RecommendData> getJSONRecommendList(StringBuilder buf) {
        ArrayList<FinalFragment.RecommendData> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("data");
                int dataSize = datas.length();
                if (dataSize > 0) {
                    jsonAllList = new ArrayList<FinalFragment.RecommendData>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        FinalFragment.RecommendData recommendData = new FinalFragment.RecommendData();
                        recommendData.image = data.optString("image");
                        recommendData.title = data.optString("title");
                        recommendData.likenum = data.optString("likes");
                        recommendData.commentnum = data.optString("comments");
                        recommendData.recipeID = data.optString("recipe_id");
                        recommendData.liked = data.optString("liked");
                        jsonAllList.add(recommendData);
                    }

                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }*/

    public static ArrayList<MynoticeFragment.NoticeListData> getJSONNoticeList(StringBuilder buf) {

        ArrayList<MynoticeFragment.NoticeListData> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("data");
                int dataSize = datas.length();
                if (dataSize > 0) {
                    jsonAllList = new ArrayList<MynoticeFragment.NoticeListData>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        MynoticeFragment.NoticeListData noticeData = new MynoticeFragment.NoticeListData();
                        noticeData.userId = data.optString("userId");
                        noticeData.nickname = data.optString("nickname");
                        noticeData.profile_img = data.optString("profile_img");
                        noticeData.sort = data.optString("sort");
                        noticeData.sort_id = data.optString("sort_id");
                        noticeData.created_at = data.optString("created_at");
                        jsonAllList.add(noticeData);
                    }

                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

    //팔로우소식알림
    public static ArrayList<FollowingnoticeFragment.NoticeFollowLlist> getJSONFollowNoticeList(StringBuilder buf) {

        ArrayList<FollowingnoticeFragment.NoticeFollowLlist> jsonAllList = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            String msg = jsonObject.optString("msg");
            if (msg != null && msg.equalsIgnoreCase("success")) {
                JSONArray datas = jsonObject.optJSONArray("data");
                int dataSize = datas.length();
                if (dataSize > 0) {
                    jsonAllList = new ArrayList<FollowingnoticeFragment.NoticeFollowLlist>();
                    for (int i = 0; i < dataSize; i++) {
                        JSONObject data = datas.optJSONObject(i);
                        FollowingnoticeFragment.NoticeFollowLlist follownoticeData = new FollowingnoticeFragment.NoticeFollowLlist();
                        follownoticeData.userId = data.optString("userId");
                        follownoticeData.nickname = data.optString("nickname");
                        follownoticeData.profile_img = data.optString("profile_img");
                        follownoticeData.recipe_id = data.optString("recipe_id");
                        follownoticeData.title = data.optString("title");
                        follownoticeData.created_at = data.optString("created_at");
                        jsonAllList.add(follownoticeData);
                    }
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonAllList;
    }

    public static SearchActivity.IngredientListData getJSONIngredientList(StringBuilder buf) {
        SearchActivity.IngredientListData ingredientData = new SearchActivity.IngredientListData();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());

            JSONArray ingredientlist = jsonObject.optJSONArray("data");

            int dataSize = ingredientlist.length();
            if (dataSize > 0) {
                for (int i = 0; i < dataSize; i++) {
                    ingredientData.ingredient.add(ingredientlist.get(i).toString());
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return ingredientData;
    }

    public static String getJSONNickname(StringBuilder buf) {
        String msg = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            msg = jsonObject.optString("msg");
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return msg;
    }

    public static WriteRecipeMainActivity.IngredientData[] getAutoIngredientList(StringBuilder buf) {
        WriteRecipeMainActivity.IngredientData[] ingredientDatas = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            JSONArray ingredientlist = jsonObject.optJSONArray("data");

            int dataSize = ingredientlist.length();
            if (dataSize > 0) {
                ingredientDatas = new WriteRecipeMainActivity.IngredientData[dataSize];
                for (int i = 0; i < dataSize; i++) {
                    JSONObject ingredientData = ingredientlist.getJSONObject(i);
                    String id = ingredientData.optString("_id");
                    String title = ingredientData.optString("title");
                    int price = ingredientData.optInt("price");
                    ingredientDatas[i] = new WriteRecipeMainActivity.IngredientData(id, title, price);
                }
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return ingredientDatas;
    }
}