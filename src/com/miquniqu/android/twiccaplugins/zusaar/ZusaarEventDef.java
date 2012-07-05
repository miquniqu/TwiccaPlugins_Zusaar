package com.miquniqu.android.twiccaplugins.zusaar;

public class ZusaarEventDef {
	public static final String RESULTS_RETURNED = "results_returned";// 	含まれる検索結果の件数 	1
	public static final String RESULTS_START = "results_start"; //検索の開始位置 	1
	public static final String EVENT = "event";//[複数要素]

	public static final String EVENT_ID = "event_id"; //イベントID 	agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public static final String TITLE = "title";//タイトル 	GoogleAppEngine勉強会
	public static final String CATCHCOPY = "copy"; //キャッチ 	BigTableを使いこなそう！
	public static final String DESCRIPTION = "description"; //概要 	Google App Engine （Python/Java）の勉強会です。利用者同士で実践的ノウハウを共有しましょう！
	public static final String EVENT_URL = "event_url";//Zusaar上のURL 	http://www.zusaar.com/event/agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public static final String STARTED_AT = "started_at"; //イベント開催日時 	2011-04-10T19:00:00+09:00
	public static final String ENDED_AT = "ended_at";//イベント終了日時 	2011-04-10T21:00:00+09:00
	public static final String PAY_TYPE = "pay_type"; //無料／有料イベント 	0:無料イベント
	//1:有料イベント(会場払い)
	//2:有料イベント(前払い)
	public static final String SANKOU_URL = "url"; //参考URL 	http://groups.google.com/group/google-app-engine-japan
	public static final String LIMIT = "limit";//定員 	80
	public static final String ADDRESS = "address"; //開催場所 	東京都港区六本木6-10-1
	public static final String place = "place"; //開催会場 	六本木ヒルズ会議室
	public static final String lat = "lat";//開催会場の緯度 	35.660262
	public static final String lon = "lon";//開催会場の経度 	139.729548
	public static final String owner_id = "owner_id"; //主催者のID 	agZ6dXNhYXJyFQsSBFVzZXIiCzE0NTY2NTI3X3R3DA
	public static final String owner_nickname = "owner_nickname"; //主催者のニックネーム 	knj77
	public static final String accepted = "accepted"; //参加者 	80
	public static final String waiting = "waiting"; //補欠者 	12
	public static final String updated_at = "updated_at"; //更新日時 	2011-03-04T10:56:17Z
}