package com.miquniqu.android.twiccaplugins.zusaar;

public class User {

	public static final int STATUS_WAITING = 0;
	public static final int STATUS_ACCEPTED = 1;

	public String user_id; //参加者のユーザID 	agZ6dXNhYXJyFQsSBFVzZXIiCzE0NTY2NTI3X3R3DA
	public String nickname;//参加者のニックネーム 	knj77
	public String status; //参加者のステータス 	1:出席、0:キャンセル待ち

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
