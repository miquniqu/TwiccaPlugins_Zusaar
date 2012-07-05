package com.miquniqu.android.twiccaplugins.zusaar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity {

	public UserList mUserList;
	public UserEvent mUserEvent;

	private ListView mListViewUser = null;
	private TextView mTextViewNone = null;
	private LinearLayout mLayoutInfo = null;
	private TextView mTextViewInfo = null;

	private boolean mInitialize = false;
	private String mEventTitle = null;
	private String mEventId = null;

	private UserTask mUserTask = null;
	private User mTargetUserItem = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//　インジケーター
		requestWindowFeature(Window.FEATURE_PROGRESS);

		// レイアウト設定
		setContentView(R.layout.user_screen);

		// onResumedでの初回動作用
		mInitialize = true;

		mLayoutInfo = (LinearLayout) findViewById(R.id.layout_info);

		mListViewUser = (ListView) findViewById(R.id.list_user);
		mTextViewNone = (TextView) findViewById(R.id.text_none);
		mTextViewInfo = (TextView) findViewById(R.id.text_info);

		mListViewUser.setVisibility(View.GONE);
		mTextViewNone.setVisibility(View.GONE);

		mLayoutInfo.setVisibility(View.GONE);

		// リスト選択時のイベント
		mListViewUser.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				UserAdapter.UserHolder targetHolder = (UserAdapter.UserHolder) view.getTag();
				mTargetUserItem = targetHolder.userItem;

				String profileUser = "@" + mTargetUserItem.nickname + getResources().getString(R.string.daialog_mesage_user_0);
				String replyUser = "@" + mTargetUserItem.nickname + getResources().getString(R.string.daialog_mesage_user_1);

				String[] str_items = { profileUser, replyUser };
				new AlertDialog.Builder(UserActivity.this).setTitle(mTargetUserItem.nickname).setItems(str_items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case 0: {
							String mTweet = "";
							try {
								mTweet = "https://twitter.com/intent/user?screen_name=" + URLEncoder.encode(mTargetUserItem.nickname, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTweet));
							startActivity(intent);
						}
							break;
						case 1:
						// イベント情報をつぶやく
						{
							String mTweet = "";
							try {
								mTweet = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode("@" + mTargetUserItem.nickname + " / " + mEventTitle, "UTF-8") + "&url=" + URLEncoder.encode(mUserEvent.event_url, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTweet));
							startActivity(intent);
						}
							break;
						default:
							break;
						}
					}
				}).show();

			}
		});

	}

	// 起動時、再表示時に呼ばれる
	@Override
	public void onResume() {
		String methodName = "onResume";
		super.onResume();

		if (mInitialize == true) {
			// 初回要求の場合のみ
			mInitialize = false;

			// インテントから名前取得(インテント呼び出し判定)
			Intent receivedIntent = getIntent();
			mEventTitle = receivedIntent.getStringExtra(ApConst.INTENT_KEY_EVENTTITLE);
			mEventId = receivedIntent.getStringExtra(ApConst.INTENT_KEY_EVENTID);

			if (mEventId != null && mEventId.length() != 0) {
				// 指定有りの場合、インテント呼び出し

				mUserTask = new UserTask(UserActivity.this, mEventId);
				mUserTask.execute();
			}
		} else {
			// ２回目要求だったら(画面非表示→タイマー停止→復帰後など)
			// 何もしない
		}

	}

	@Override
	public void onPause() {
		String methodName = "onPause";
		super.onPause();
		// タスクを終了
		if (mUserTask != null) {
			mUserTask.cancel(true);
			mUserTask = null;
		}
	}

	/**
	 * 指定nicknameからEventデータを取得する
	 * 
	 * @param sUrl
	 * @return
	 */
	public UserList callUserApi(String eventid) {
		// ニックネーム_検索
		String targetUrl = ApConst.ZUSAAR_API_USER + eventid;

		String jsondata = getJson(targetUrl);
		if (jsondata == null) {
			return null;
		}

		UserList userList = JSON.decode(jsondata, UserList.class);

		StringBuilder sb = new StringBuilder();
		sb.append("eventList.Results_returned:" + userList.getResults_returned());
		sb.append("eventList.Results_start:" + userList.getResults_start());

		return userList;
	}

	/**
	 * 指定URLからgetしたjson文字列を取得する
	 * 
	 * @param sUrl
	 * @return
	 */
	public String getJson(String targeturl) {
		HttpClient objHttp = new DefaultHttpClient();
		HttpParams params = objHttp.getParams();
		HttpConnectionParams.setConnectionTimeout(params, ApConst.CONNECT_TIMEOUT); //接続のタイムアウト
		HttpConnectionParams.setSoTimeout(params, ApConst.CONNECT_TIMEOUT); //データ取得のタイムアウト
		String sReturn = "";
		try {
			HttpGet objGet = new HttpGet(targeturl);
			HttpResponse objResponse = objHttp.execute(objGet);
			if (objResponse.getStatusLine().getStatusCode() < 400) {
				InputStream objStream = objResponse.getEntity().getContent();
				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;
				while ((sLine = objBuf.readLine()) != null) {
					objJson.append(sLine);
				}
				sReturn = objJson.toString();
				objStream.close();
			}
		} catch (IOException e) {
			return null;
		}
		return sReturn;
	}

	/*
	 * 非同期で時間のかかる処理を
	 */
	public class UserTask extends AsyncTask<Void, Integer, UserList> {
		private Context mContext;
		private String mEventId;

		public UserTask(Context context, String eventid) {
			this.mContext = context;
			this.mEventId = eventid;

		}

		@Override
		protected void onPreExecute() {

			// プログレスバーを表示します
			setProgressBarVisibility(true);
			// インジケーター：4%
			setProgress(4 * 100);

			mListViewUser.setVisibility(View.GONE);
			mTextViewNone.setVisibility(View.GONE);
			mLayoutInfo.setVisibility(View.VISIBLE);
			String info = getResources().getString(R.string.user_info).replace("?", mEventTitle);
			mTextViewInfo.setText(info);
		}

		@Override
		protected UserList doInBackground(Void... params) {
			// このメソッドから、UIは操作できない
			int progressCnt = 0;
			UserList userList = null;

			if (isCancelled()) { // 終了チェック
				return null;
			}
			// インジケーター：5%
			publishProgress(5 * 100);

			try {

				// インジケーター
				publishProgress(10 * 100);

				// 入力されたら
				userList = callUserApi(mEventId);

			} catch (Exception e) {
				// TODO: handle exception
				throw new RuntimeException(e);
			}

			// インジケーター
			publishProgress(80 * 100);

			return userList;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// プログレスバーに進捗状況を設定します
			// [0 - 10000]の範囲 50% = 5000
			setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(UserList userList) {

			try {
				if (isCancelled()) { // 終了チェック
					return;
				}
				// インジケーター：90%
				setProgress(90 * 100);
				if (userList == null) {
					// 通信エラーの場合
					String toastString = getResources().getString(R.string.connect_toast);
					Toast.makeText(UserActivity.this, toastString, Toast.LENGTH_SHORT).show();
				} else if (userList.getResults_returned() != 0 && userList.getEvent().size() != 0) {

					mUserList = userList;
					// 該当イベント有り、一件目を確認
					List<UserEvent> userEvents = userList.getEvent();
					mUserEvent = userEvents.get(0);
					if (mUserEvent.getUsers() != null && mUserEvent.getUsers().size() != 0) {
						// 該当イベントの該当ユーザ有り
						//--アダプタ生成
						ListAdapter adapter = new UserAdapter(UserActivity.this, R.layout.user_itemrow, mUserEvent.getUsers());
						mListViewUser.setAdapter(adapter);
						mListViewUser.setVisibility(View.VISIBLE);
					}
				} else {
					// 検索エラー、該当件数0件の場合
					mTextViewNone.setVisibility(View.VISIBLE);
				}

			} finally {

				mUserTask = null;
				// インジケーター：100%(消す)
				setProgress(100 * 100);
			}
		}
	}

	/**
	 * 縦横切り替え時に再生成しないようにする Manifestも設定が必要 http://d.hatena.ne.jp/hyoromo/20090712/1247385249 http://y-anz-m.blogspot.com/2010/10/androidconfiguration-change.html
	 * */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}