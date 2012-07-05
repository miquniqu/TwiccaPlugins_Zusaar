package com.miquniqu.android.twiccaplugins.zusaar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miquniqu.android.twiccaplugins.zusaar.db.UserData;
import com.miquniqu.android.twiccaplugins.zusaar.db.ZusaarDBHelper;

public class EventActivity extends Activity {

	//private LinearLayout mLayoutFooter = null;
	private LinearLayout mLayoutHistory = null;
	private LinearLayout mLayoutOwner = null;
	private LinearLayout mLayoutJoin = null;

	public EditText mEdtInput;
	public List<UserData> mUserHistoryList;
	public EventList mEventList;

	private ListView mListViewEvent = null;
	private TextView mTextViewNone = null;
	private TextView mTextViewInfoName = null;
	private TextView mTextViewInfoOwner = null;
	private TextView mTextViewInfoJoin = null;

	private ListView mListViewSelect = null;
	private AlertDialog mSelectDialog = null;

	private boolean mInitialize = false;

	private HistoryTask mHistoryTask = null;
	private EventTask mEventTask = null;
	private Event mTargetEventItem = null;

	//private Adapter mSelectListAdapter = null;

	private Drawable mDrowStarOn = null;
	private Drawable mDrowStarOff = null;

	private int mTextColorDefault = 0;
	private int mTextColorGlay = 0;

	// 検索指定(初期値=参加者)
	private int mSearchTarget = ApConst.SEARCH_EVENT_OWNER;
	private String mScreenName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//　インジケーター
		requestWindowFeature(Window.FEATURE_PROGRESS);
		// レイアウト設定
		setContentView(R.layout.event_screen);

		// onResumedでの初回動作用
		mInitialize = true;

		// メニュー操作
		mLayoutHistory = (LinearLayout) findViewById(R.id.layout_history);
		mLayoutOwner = (LinearLayout) findViewById(R.id.layout_owner);
		mLayoutJoin = (LinearLayout) findViewById(R.id.layout_join);
		// --イベントハンドラ
		// リスナークラスを作って登録する
		ClickListener clicklistener = new ClickListener();

		mLayoutHistory.setOnClickListener(clicklistener);
		mLayoutOwner.setOnClickListener(clicklistener);
		mLayoutJoin.setOnClickListener(clicklistener);

		mListViewEvent = (ListView) findViewById(R.id.list_event);
		mTextViewNone = (TextView) findViewById(R.id.text_none);
		mTextViewInfoName = (TextView) findViewById(R.id.text_info_name);
		mTextViewInfoOwner = (TextView) findViewById(R.id.text_info_owner);
		mTextViewInfoJoin = (TextView) findViewById(R.id.text_info_join);

		mTextViewInfoName.setVisibility(View.GONE);

		mDrowStarOn = getResources().getDrawable(android.R.drawable.btn_star_big_on);
		mDrowStarOff = getResources().getDrawable(android.R.drawable.btn_star_big_off);

		mTextColorDefault = getResources().getColor(R.color.text_default);
		mTextColorGlay = getResources().getColor(R.color.text_glay);
		mTextViewInfoOwner.setTextColor(mTextColorGlay);
		mTextViewInfoJoin.setTextColor(mTextColorGlay);

		//リスト操作
		mListViewEvent.setVisibility(View.GONE);
		mTextViewNone.setVisibility(View.GONE);

		//mLayoutInfo.setVisibility(View.GONE);

		//LayoutInflater inflater = LayoutInflater.from(this);
		//mLayoutFooter = (LinearLayout) inflater.inflate(R.layout.event_footer, null);
		//mListViewEvent.addFooterView(mLayoutFooter);

		// リスト選択時のイベント
		mListViewEvent.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				EventAdapter.EventHolder targetHolder = (EventAdapter.EventHolder) view.getTag();
				mTargetEventItem = targetHolder.eventItem;

				String[] str_items = { getResources().getString(R.string.daialog_mesage_event_0), getResources().getString(R.string.daialog_mesage_event_1), "@" + mTargetEventItem.owner_nickname + getResources().getString(R.string.daialog_mesage_event_2), "@" + mTargetEventItem.owner_nickname + getResources().getString(R.string.daialog_mesage_event_3), getResources().getString(R.string.daialog_mesage_event_4) };
				new AlertDialog.Builder(EventActivity.this).setTitle(mTargetEventItem.title).setItems(str_items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: {
							// イベントURLを見る
							if (mTargetEventItem.event_url != null && mTargetEventItem.event_url.length() != 0) {
								String toastString = getResources().getString(R.string.event_toast);
								Toast.makeText(EventActivity.this, toastString, Toast.LENGTH_SHORT).show();
								Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTargetEventItem.event_url));
								startActivity(sendIntent);
							}
						}
							break;
						case 1: {
							// 参加者一覧を検索する
							// 日付選択へ遷移
							Intent intent = new Intent(getApplicationContext(), UserActivity.class);
							intent.putExtra(ApConst.INTENT_KEY_EVENTTITLE, mTargetEventItem.title);
							intent.putExtra(ApConst.INTENT_KEY_EVENTID, mTargetEventItem.event_id);

							startActivity(intent);
						}

							break;
						case 2: {
							String mTweet = "";
							try {
								mTweet = "https://twitter.com/intent/user?screen_name=" + URLEncoder.encode(mTargetEventItem.owner_nickname, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTweet));
							startActivity(intent);
						}

							break;
						case 3:
						// 主催者にイベント情報をつぶやく
						{
							String mTweet = "";
							try {
								mTweet = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode("@" + mTargetEventItem.owner_nickname + " / " + mTargetEventItem.title, "UTF-8") + "&url=" + URLEncoder.encode(mTargetEventItem.event_url, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTweet));
							startActivity(intent);
						}
							break;
						case 4:
						// イベント情報をつぶやく
						{
							String mTweet = "";
							try {
								mTweet = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode(mTargetEventItem.title + " #zusaar", "UTF-8") + "&url=" + URLEncoder.encode(mTargetEventItem.event_url, "UTF-8");
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
		super.onResume();

		if (mInitialize == true) {
			// 初回要求の場合のみ
			mInitialize = false;

			// 設定値読み込み
			final SharedPreferences preferences = getSharedPreferences(ApConst.PREFERENCES_NAME, MODE_PRIVATE);

			// 最後に検索した対象を取得(初期値は主催者)
			mSearchTarget = preferences.getInt(ApConst.PRE_LAST_SEARCH_TARGET, ApConst.SEARCH_EVENT_OWNER);

			// インテントから名前取得(インテント呼び出し判定)
			Intent receivedIntent = getIntent();
			String screenName = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

			if (screenName != null && screenName.length() != 0) {
				// 指定有りの場合、インテント呼び出し
				// 最後の指定情報を保存
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(ApConst.PRE_LAST_SEARCH_NICKNAME, screenName);
				editor.putInt(ApConst.PRE_LAST_SEARCH_TARGET, mSearchTarget);
				editor.commit();

				mScreenName = screenName;

				mEventTask = new EventTask(EventActivity.this, mScreenName);
				mEventTask.execute();
			} else {
				//指定無しの場合、直接呼出し
				//入力ダイアログ表示
				String nickname = preferences.getString(ApConst.PRE_LAST_SEARCH_NICKNAME, "");

				// Create EditText
				mEdtInput = new EditText(this);
				mEdtInput.setText(nickname);

				// Show Dialog
				new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(R.string.daialog_label).setView(mEdtInput).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* OKボタンをクリックした時の処理 */
						String inputNickname = mEdtInput.getText().toString();
						if (inputNickname != null && inputNickname.length() != 0) {
							// 一旦保存
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(ApConst.PRE_LAST_SEARCH_NICKNAME, inputNickname);
							editor.putInt(ApConst.PRE_LAST_SEARCH_TARGET, mSearchTarget);
							editor.commit();

							mScreenName = inputNickname;

							mEventTask = new EventTask(EventActivity.this, inputNickname);
							mEventTask.execute();

						} else {
							// 未入力なら死ぬ
							finish();
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* Cancel ボタンをクリックした時の処理 */
						finish();
					}
				}).show();
			}
		} else {
			// ２回目要求だったら(画面非表示→タイマー停止→復帰後など)
			// 何もしない
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// タスクを終了
		if (mEventTask != null) {
			mEventTask.cancel(true);
			mEventTask = null;
		}
		if (mHistoryTask != null) {
			mHistoryTask.cancel(true);
			mHistoryTask = null;
		}
	}

	/**
	 * 指定nicknameからEventデータを取得する
	 * 
	 * @param sUrl
	 * @return
	 */
	public EventList callEventApi(int target, String nickname) {
		// ニックネーム_検索
		String targetUrl = "";

		try {
			if (target == ApConst.SEARCH_EVENT_OWNER) {
				targetUrl = ApConst.ZUSAAR_API_EVENT + "owner_nickname=" + URLEncoder.encode(nickname, "UTF-8");
			} else {
				targetUrl = ApConst.ZUSAAR_API_EVENT + "nickname=" + URLEncoder.encode(nickname, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		String jsondata = getJson(targetUrl);
		if (jsondata == null) {
			return null;
		}

		EventList eventList = JSON.decode(jsondata, EventList.class);

		/*StringBuilder sb = new StringBuilder();
		sb.append("eventList.Results_returned:" + eventList.getResults_returned());
		sb.append("eventList.Results_start:" + eventList.getResults_start());
		*/
		return eventList;
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
	public class HistoryTask extends AsyncTask<Void, Integer, List<UserData>> {
		private Context mContext;

		public HistoryTask(Context context) {
			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<UserData> doInBackground(Void... params) {
			// このメソッドから、UIは操作できない
			List<UserData> userHistoryList = null;

			if (isCancelled()) { // 終了チェック
				return null;
			}
			try {

				// 入力されたら
				userHistoryList = ZusaarDBHelper.selectLimit(getApplicationContext(), ApConst.USER_HISTORY_COUNT);

			} catch (Exception e) {
				// TODO: handle exception
				throw new RuntimeException(e);
			}
			return userHistoryList;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 進捗
		}

		@Override
		protected void onPostExecute(List<UserData> userHistoryList) {

			if (isCancelled()) { // 終了チェック
				return;
			}
			if (userHistoryList == null || userHistoryList.size() == 0) {
				// 取得エラーの場合
				String toastString = getResources().getString(R.string.history_toast);
				Toast.makeText(EventActivity.this, toastString, Toast.LENGTH_SHORT).show();
			} else {
				// 該当有り
				mUserHistoryList = userHistoryList;

				// ContextからLayoutInflaterを取得
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View dialogView = (View) inflater.inflate(R.layout.dialog_listselect, null);
				mListViewSelect = (ListView) dialogView.findViewById(R.id.list_select);

				ArrayList<String> screennameList = new ArrayList<String>();
				for (Object object : userHistoryList) {
					UserData data = (UserData) object;
					screennameList.add(data.screenname);
				}

				if (screennameList.size() != 0) {
					// アダプタ生成
					ListAdapter adapter = new SelectItemListAdapter(EventActivity.this, R.layout.dialog_itemrow, userHistoryList);
					// アダプタ設定
					mListViewSelect.setAdapter(adapter);

					// リスト選択時のイベント
					mListViewSelect.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							ViewHolder holder = (ViewHolder) view.getTag();
							UserData userdata = holder.userdata;

							// 設定値読み込み
							final SharedPreferences preferences = getSharedPreferences(ApConst.PREFERENCES_NAME, MODE_PRIVATE);

							// 最後に検索した対象を取得(初期値は主催者)
							mSearchTarget = preferences.getInt(ApConst.PRE_LAST_SEARCH_TARGET, ApConst.SEARCH_EVENT_OWNER);

							// 指定有りの場合、インテント呼び出し
							// 最後の指定情報を保存
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(ApConst.PRE_LAST_SEARCH_NICKNAME, userdata.screenname);
							editor.putInt(ApConst.PRE_LAST_SEARCH_TARGET, mSearchTarget);
							editor.commit();

							mScreenName = userdata.screenname;
							mEventTask = new EventTask(EventActivity.this, mScreenName);
							mEventTask.execute();

							//ダイアログを閉じる
							mSelectDialog.dismiss();

						}
					});

					// リスト長押し選択時のイベント
					mListViewSelect.setOnItemLongClickListener(new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
							ViewHolder holder = (ViewHolder) view.getTag();
							String screenName = holder.userdata.screenname;

							// お気に入りを反転
							//ユーザー履歴登録
							if (holder.userdata.favoriteflag) {
								// 解除
								ZusaarDBHelper.updateFavorite(getApplicationContext(), screenName, false);
							} else {
								// 設定
								ZusaarDBHelper.updateFavorite(getApplicationContext(), screenName, true);
							}
							// メモリ上反映する
							holder.userdata.favoriteflag = !holder.userdata.favoriteflag;

							//--リストに再反映
							SelectItemListAdapter adapter = (SelectItemListAdapter) mListViewSelect.getAdapter();
							// データ反映
							adapter.notifyDataSetChanged();
							// リスト表示反映
							mListViewSelect.invalidateViews();

							// バイブレート
							runVibrator(ApConst.VIBRATOR_FAVORITE);

							return false;
						}

					});

					mSelectDialog = new AlertDialog.Builder(EventActivity.this).setTitle(getString(R.string.dlgtitle_screenname)).setView(dialogView).setCancelable(true).create();
					mSelectDialog.show();

				}
			}

		}
	}

	/*
	 * 非同期で時間のかかる処理を
	 */
	public class EventTask extends AsyncTask<Void, Integer, EventList> {
		private Context mContext;
		private String mNickname;

		public EventTask(Context context, String nickname) {
			this.mContext = context;
			this.mNickname = nickname;

		}

		@Override
		protected void onPreExecute() {
			// プログレスバーを表示します
			setProgressBarVisibility(true);
			// インジケーター：4%
			setProgress(4 * 100);

			mTextViewInfoOwner.setTextColor(mTextColorGlay);
			mTextViewInfoJoin.setTextColor(mTextColorGlay);
			mListViewEvent.setVisibility(View.GONE);
			mTextViewNone.setVisibility(View.GONE);
			mTextViewInfoName.setVisibility(View.VISIBLE);
			String info = "@" + mNickname + getResources().getString(R.string.event_info);
			//String info = "@" + mNickname;
			mTextViewInfoName.setText(info);
			if (mSearchTarget == ApConst.SEARCH_EVENT_OWNER) {
				mTextViewInfoOwner.setTextColor(mTextColorDefault);
			} else {
				mTextViewInfoJoin.setTextColor(mTextColorDefault);
			}

		}

		@Override
		protected EventList doInBackground(Void... params) {
			// このメソッドから、UIは操作できない
			int progressCnt = 0;
			EventList eventList = null;

			if (isCancelled()) { // 終了チェック
				return null;
			}
			// インジケーター：5%
			publishProgress(5 * 100);

			try {

				//ユーザー履歴登録
				ZusaarDBHelper.insertHistory(getApplicationContext(), mNickname);

				// インジケーター
				publishProgress(10 * 100);

				// 入力されたら
				eventList = callEventApi(mSearchTarget, mNickname);

				// インジケーター
				publishProgress(80 * 100);

			} catch (Exception e) {
				// TODO: handle exception
				throw new RuntimeException(e);
			}
			//
			// 何らかの繰り返し処理
			//
			//publishProgress(Integer.valueOf(75));
			// publishProgress(Integer.valueOf(progressCnt++));
			//}
			return eventList;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// プログレスバーに進捗状況を設定します
			// [0 - 10000]の範囲 50% = 5000
			setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(EventList eventList) {

			try {
				if (isCancelled()) { // 終了チェック
					return;
				}
				// インジケーター：90%
				setProgress(90 * 100);

				if (eventList == null) {
					// 通信エラーの場合
					String toastString = getResources().getString(R.string.connect_toast);
					Toast.makeText(EventActivity.this, toastString, Toast.LENGTH_SHORT).show();
				} else if (eventList.getResults_returned() != 0 && eventList.getEvent().size() != 0) {
					// 該当有り
					mEventList = eventList;
					//--アダプタ生成
					ListAdapter adapter = new EventAdapter(EventActivity.this, R.layout.event_itemrow, mEventList.getEvent());
					// フッター
					//mListViewEvent.addFooterView(mLayoutFooter);

					mListViewEvent.setAdapter(adapter);
					mListViewEvent.setVisibility(View.VISIBLE);
				} else {
					// 検索エラー、該当件数0件の場合
					mTextViewNone.setVisibility(View.VISIBLE);
				}

			} finally {

				mEventTask = null;
				// インジケーター：100%(消す)
				setProgress(100 * 100);
			}
		}

	}

	/***
	 * 画面内のボタンクリックリスナー
	 */
	public class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (v == mLayoutHistory) {
				// 履歴検索
				// 消してから開始
				if (mHistoryTask != null) {
					mHistoryTask.cancel(true);
					mHistoryTask = null;
				}
				mHistoryTask = new HistoryTask(EventActivity.this);
				mHistoryTask.execute();
			} else if (v == mLayoutOwner) {
				// 主催者検索
				mSearchTarget = ApConst.SEARCH_EVENT_OWNER;
				// 設定値の保存
				SharedPreferences preferences = getSharedPreferences(ApConst.PREFERENCES_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(ApConst.PRE_LAST_SEARCH_TARGET, mSearchTarget);
				editor.commit();

				// 消してから開始
				if (mEventTask != null) {
					mEventTask.cancel(true);
					mEventTask = null;
				}
				mEventTask = new EventTask(EventActivity.this, mScreenName);
				mEventTask.execute();

			} else if (v == mLayoutJoin) {
				// 参加者検索
				mSearchTarget = ApConst.SEARCH_EVENT_USER;
				// 設定値の保存
				SharedPreferences preferences = getSharedPreferences(ApConst.PREFERENCES_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(ApConst.PRE_LAST_SEARCH_TARGET, mSearchTarget);
				editor.commit();

				// 消してから開始
				if (mEventTask != null) {
					mEventTask.cancel(true);
					mEventTask = null;
				}
				mEventTask = new EventTask(EventActivity.this, mScreenName);
				mEventTask.execute();
			}
		}
	}

	class ViewHolder {
		public TextView textViewScreenname;
		public ImageView imageViewStar;
		public UserData userdata;
	}

	public class SelectItemListAdapter extends ArrayAdapter<UserData> {
		private LayoutInflater inflater;
		private int layoutId;
		private List<UserData> items;

		public SelectItemListAdapter(Context _context, int _layoutId, List<UserData> _items) {
			super(_context, _layoutId, _items);

			this.layoutId = _layoutId;
			this.items = _items;
			inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int _position, View _convertView, ViewGroup _parent) {
			ViewHolder holder;
			// 対象のアイテムを取得
			UserData userdata = items.get(_position);

			// convertViewがnullなら新規作成
			if (_convertView == null) {
				_convertView = inflater.inflate(layoutId, _parent, false);

				holder = new ViewHolder();
				holder.textViewScreenname = (TextView) _convertView.findViewWithTag("text_screenname");
				holder.imageViewStar = (ImageView) _convertView.findViewWithTag("image_star");
				_convertView.setTag(holder);
			}
			// convertViewが渡されたらそれを利用、なかったら新規作成
			else {
				// view = convertView;
				holder = (ViewHolder) _convertView.getTag();
			}

			// アイテムを保存する
			holder.userdata = userdata;

			holder.textViewScreenname.setText("@" + userdata.screenname);

			if (userdata.favoriteflag) {
				holder.imageViewStar.setImageDrawable(mDrowStarOn);
			} else {
				holder.imageViewStar.setImageDrawable(mDrowStarOff);
			}

			return _convertView;
		}
	}

	/**
	 * 振動処理
	 * 
	 * @param milliseconds
	 */
	public void runVibrator(int milliseconds) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(milliseconds);
	}

	/**
	 * 縦横切り替え時に再生成しないようにする Manifestも設定が必要
	 * */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * JSONに対応するクラス 各フィールド名をJSONのKEY名と同じにする JSONのKEY名が 「sample-name」のような場合は「sampleName」とする
	 */
	class Hoge {

		private String name;

		private int age;

		public void setName(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setAge(final int age) {
			this.age = age;
		}

		public int getAge() {
			return age;
		}
	}

	/**
	 * HogeクラスのListを保持するクラス
	 */
	class HogeList {

		private String id;

		private List<Hoge> hoges;

		public void setId(final String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setHoges(final List<Hoge> hoges) {
			this.hoges = hoges;
		}

		public List<Hoge> getHoges() {
			return hoges;
		}
	}
}