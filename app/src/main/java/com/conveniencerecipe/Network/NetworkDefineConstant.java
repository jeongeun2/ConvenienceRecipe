/*
 *   Http 요청 상수값
 */
package com.conveniencerecipe;

public class NetworkDefineConstant {
	public static final String HOST_URL = "52.78.68.2";
	//웹서버 포트번호
	public static final int PORT_NUMBER = 3000;
	//저장관련 URL 주소
	public static final String SERVER_URL_RECIPE_INSERT = "http://" + HOST_URL + ":3000/recipe";
	public static final String SERVER_URL_NEWEST_LIST_SELECT = "http://" + HOST_URL + ":3000/recipe?list=%s&id=%s&page=%d";
	public static final String SERVER_URL_RCIPE_DETAIL = "http://" + HOST_URL + ":3000/recipe/%s?id=%s";
	public static final String SERVER_URL_RCIPE_DELETE = "http://" + HOST_URL + ":3000/recipe/%s";

	public static final String SERVER_URL_QNA_ALL_SELECT = "http://" + HOST_URL + ":3000/board?page=%s";
	public static final String SERVER_URL_QNA_WRITE = "http://" + HOST_URL + ":3000/board";

	public static final String SERVER_URL_LIKE_CLICK = "http://" + HOST_URL + ":3000/recipe/like";
	public static final String SERVER_URL_UNLIKE_CLICK = "http://" + HOST_URL + ":3000/recipe/del/like";

	public static final String SERVER_URL_COMMENT = "http://" + HOST_URL + ":3000/recipe/%s/comment";
	public static final String SERVER_URL_COMMENT_INSERT = "http://" + HOST_URL + ":3000/recipe/comment";

	public static final String SERVER_URL_QNA_COMMENT = "http://" + HOST_URL + ":3000/board/%s/comment";
	public static final String SERVER_URL_QNA_COMMENT_INSERT = "http://" + HOST_URL + ":3000/board/comment";

	public static final String SERVER_URL_QNADETAIL = "http://" + HOST_URL + ":3000/board/%s";

	public static final String SERVER_URL_NOTICE_MY = "http://" + HOST_URL + ":3000/profile/news?list=mynews&id=%s";
	public static final String SERVER_URL_NOTICE_FOLLOW = "http://" + HOST_URL + ":3000/profile/news?list=follow&id=%s";

	public static final String SERVER_URL_SEARCH = "http://" + HOST_URL + ":3000/search?sort=%s&sort_id=%s&id=%s&page=%s";

	public static final String SERVER_URL_SEARCH_INGREDIENT = "http://" + HOST_URL + ":3000/search/word";

	public static final String SERVER_URL_USERPAGE = "http://" + HOST_URL + ":3000/userpage?id=%s&userId=%s";

	public static final String SERVER_URL_FOLLOW_CLICK = "http://" + HOST_URL + ":3000/profile/follow";
	public static final String SERVER_URL_UNFOLLOW_CLICK = "http://" + HOST_URL + ":3000/profile/del/follow";

	public static final String SERVER_URL_NICKNAME_DUPLICATED = "http://" + HOST_URL + ":3000/profile?nickname=%s";
	public static final String SERVER_URL_NICKNAME = "http://" + HOST_URL + ":3000/profile/nickname";
	public static final String SERVER_URL_PROFILE = "http://" + HOST_URL + ":3000/profile/image";
	public static final String SERVER_URL_PROFILE_DELETE = "http://" + HOST_URL + ":3000/profile/%s/image";

	public static final String SERVER_URL_SIGNUP = "http://" + HOST_URL + ":3000/deviceSignup";
	public static final String SERVER_URL_LOGIN = "http://" + HOST_URL + ":3000/deviceLogin";

	public static final String SERVER_NAV_PROFILE = "http://" + HOST_URL + ":3000/profile/image?id=%s";
	public static final String SERVER_BOOKMARK = "http://" + HOST_URL + ":3000/recipe/bookmark";
	public static final String SERVER_UNBOOKMARK = "http://" + HOST_URL + ":3000/recipe/del/bookmark";
	public static final String SERVER_RECOMMEND = "http://" + HOST_URL + ":3000/recipe/recommend?id=%s&recipe_id=%s";

	public static final String SERVER_URL_AUTOCOMPLETE_INGREDIENT = "http://" + HOST_URL + ":3000/recipe/ingredient?word=%s";
}