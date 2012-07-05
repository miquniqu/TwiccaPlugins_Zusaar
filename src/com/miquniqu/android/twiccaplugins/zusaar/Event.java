package com.miquniqu.android.twiccaplugins.zusaar;

public class Event {
	public static final int PAY_TYPE_MURYOU = 0;
	public static final int PAY_TYPE_YURYOU_KAI = 1;
	public static final int PAY_TYPE_YURYOU_MAE = 2;

	// 項目
	public String event_id; //イベントID 	agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public String title;//タイトル 	GoogleAppEngine勉強会
	public String catchcopy; //キャッチ 	BigTableを使いこなそう！
	public String description; //概要 	Google App Engine （Python/Java）の勉強会です。利用者同士で実践的ノウハウを共有しましょう！
	public String event_url;//Zusaar上のURL 	http://www.zusaar.com/event/agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public String started_at; //イベント開催日時 	2011-04-10T19:00:00+09:00
	public String ended_at;//イベント終了日時 	2011-04-10T21:00:00+09:00
	public String pay_type; //無料／有料イベント 	0:無料イベント
	//1:有料イベント(会場払い)
	//2:有料イベント(前払い)
	public String sankourl; //参考URL 	http://groups.google.com/group/google-app-engine-japan
	public String limit;//定員 	80
	public String address; //開催場所 	東京都港区六本木6-10-1
	public String place; //開催会場 	六本木ヒルズ会議室
	public String lat;//開催会場の緯度 	35.660262
	public String lon;//開催会場の経度 	139.729548
	public String owner_id; //主催者のID 	agZ6dXNhYXJyFQsSBFVzZXIiCzE0NTY2NTI3X3R3DA
	public String owner_profile_url; //主催者のプロフィールURL 	http://twitter.com/knj77
	public String owner_nickname; //主催者のニックネーム 	knj77
	public String accepted; //参加者 	80
	public String waiting; //補欠者 	12
	public String updated_at; //更新日時 	2011-03-04T10:56:17Z

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCatch() {
		return catchcopy;
	}

	public void setCatch(String catchcopy) {
		this.catchcopy = catchcopy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEvent_url() {
		return event_url;
	}

	public void setEvent_url(String event_url) {
		this.event_url = event_url;
	}

	public String getStarted_at() {
		return started_at;
	}

	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}

	public String getEnded_at() {
		return ended_at;
	}

	public void setEnded_at(String ended_at) {
		this.ended_at = ended_at;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getUrl() {
		return sankourl;
	}

	public void setUrl(String sankourl) {
		this.sankourl = sankourl;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getOwner_profile_url() {
		return owner_profile_url;
	}

	public void setOwner_profile_url(String owner_profile_url) {
		this.owner_profile_url = owner_profile_url;
	}

	public String getOwner_nickname() {
		return owner_nickname;
	}

	public void setOwner_nickname(String owner_nickname) {
		this.owner_nickname = owner_nickname;
	}

	public String getAccepted() {
		return accepted;
	}

	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}

	public String getWaiting() {
		return waiting;
	}

	public void setWaiting(String waiting) {
		this.waiting = waiting;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

}
