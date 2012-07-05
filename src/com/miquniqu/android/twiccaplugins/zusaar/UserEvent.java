package com.miquniqu.android.twiccaplugins.zusaar;

import java.util.List;

public class UserEvent {
	public static final int PAY_TYPE_MURYOU = 0;
	public static final int PAY_TYPE_YURYOU_KAI = 1;
	public static final int PAY_TYPE_YURYOU_MAE = 2;

	// 項目
	public String event_id; //イベントID 	agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public String title;//タイトル 	GoogleAppEngine勉強会
	public String event_url;//Zusaar上のURL 	http://www.zusaar.com/event/agZ6dXNhYXJyDAsSBUV2ZW50GKEfDA
	public String pay_type; //無料／有料イベント 	0:無料イベント
	//1:有料イベント(会場払い)
	//2:有料イベント(前払い)
	public String limit;//定員 	80
	public String accepted; //参加者 	80
	public String waiting; //補欠者 	12
	public String updated_at; //更新日時 	2011-03-04T10:56:17Z

	private List<User> users;

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

	public String getEvent_url() {
		return event_url;
	}

	public void setEvent_url(String event_url) {
		this.event_url = event_url;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
