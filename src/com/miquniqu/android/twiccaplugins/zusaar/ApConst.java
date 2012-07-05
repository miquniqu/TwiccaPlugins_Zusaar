package com.miquniqu.android.twiccaplugins.zusaar;

public class ApConst {
	public static final String PREFERENCES_NAME = "com.miquniqu.android.twiccaplugins.zusaar";
	public static final String PRE_LAST_SEARCH_TARGET = "last_search_target";
	public static final String PRE_LAST_SEARCH_NICKNAME = "last_search_nickname";
	public static final String ZUSAAR_API_EVENT = "http://www.zusaar.com/api/event/?";
	public static final String ZUSAAR_API_USER = "http://www.zusaar.com/api/event/user/?event_id=";

	// イベント一覧検索パターン:参加者検索
	public static final int SEARCH_EVENT_OWNER = 1;
	// イベント一覧検索パターン:主催者検索
	public static final int SEARCH_EVENT_USER = 2;

	// インテントキー：ニックネーム
	public static final String INTENT_KEY_NICKNAME = "SELECT_NICKNAME";
	// インテントキー：イベントタイトル
	public static final String INTENT_KEY_EVENTTITLE = "SELECT_EVENTTITLE";
	// インテントキー：イベントID
	public static final String INTENT_KEY_EVENTID = "SELECT_EVENTID";

	// 通信タイムアウト
	public static final int CONNECT_TIMEOUT = 15000;

	// 履歴表示件数
	public static final int USER_HISTORY_COUNT = 20;

	// お気に入り選択時バイブレーション時間（ミリ）
	public static final int VIBRATOR_FAVORITE = 50;
}
