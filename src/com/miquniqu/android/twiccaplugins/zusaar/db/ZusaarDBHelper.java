package com.miquniqu.android.twiccaplugins.zusaar.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.miquniqu.android.util.LocaleUtil;

public class ZusaarDBHelper extends SQLiteOpenHelper {
	private static final String CLASSNAME = "ZusaarDBHelper";

	private final static String DB_NAME = "zusaar.db";// DB名
	private final static int DB_VERSION = 1; // バージョン

	//--テーブル名
	// ユーザー表示履歴
	private final static String DB_TABLE_USER_HISTORY = "user_history";
	// ユーザー表示履歴(お気に入り)
	private final static String DB_TABLE_USER_FAVORITE = "user_favorite";

	private Context mContext;

	// データベースヘルパーのコンストラクタ
	public ZusaarDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	// データベースの生成
	@Override
	public void onCreate(SQLiteDatabase db) {

		// --しょぼいRSSから取得した番組データ
		{
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS ");
			sb.append(DB_TABLE_USER_HISTORY);
			sb.append("(");
			// 自動採取番のID
			sb.append("_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
			// twitter screenname
			sb.append("SCREENNAME TEXT NOT NULL,");
			// 表示日時(juliandayで登録。JAVAでの扱いはDouble。文字列に変換するときは考慮が必要)
			sb.append("DATE REAL NOT NULL");
			sb.append(")");
			// 実行
			db.execSQL(sb.toString());
		}
		{
			// ユニークIndex screennameでユニーク
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE UNIQUE INDEX IF NOT EXISTS ");
			sb.append(DB_TABLE_USER_HISTORY);
			sb.append("_uidx1");
			sb.append(" ON ");
			sb.append(DB_TABLE_USER_HISTORY);
			sb.append("(");
			sb.append("SCREENNAME");
			sb.append(")");
			// 実行
			db.execSQL(sb.toString());
		}
		// --ユーザ履歴に対するお気に入り
		{
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS ");
			sb.append(DB_TABLE_USER_FAVORITE);
			sb.append("(");
			// 自動採取番のID
			sb.append("_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
			// twitter screenname
			sb.append("SCREENNAME TEXT NOT NULL,");
			// お気に入り状態(0=指定なし、1=お気に入り)
			sb.append("FAVORITEFLAG INTEGER NOT NULL");
			sb.append(")");
			// 実行
			db.execSQL(sb.toString());

			/*
			 * ON CONFLICT
			 * REPLACE
			 * 元のデータを上書きする
			 */
		}

		{
			// ユニークIndex screennameでユニーク
			StringBuilder sb = new StringBuilder();
			//
			sb.append("CREATE UNIQUE INDEX IF NOT EXISTS ");
			sb.append(DB_TABLE_USER_FAVORITE);
			sb.append("_uidx1");
			sb.append(" ON ");
			sb.append(DB_TABLE_USER_FAVORITE);
			sb.append("(");
			sb.append("SCREENNAME");
			sb.append(")");
			// 実行
			db.execSQL(sb.toString());

		}

	}

	// データベースのアップグレード
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + DB_TABLE_USER_HISTORY);
		db.execSQL("drop index if exists " + DB_TABLE_USER_HISTORY + "_uidx1");

		db.execSQL("drop table if exists " + DB_TABLE_USER_FAVORITE);
		db.execSQL("drop index if exists " + DB_TABLE_USER_FAVORITE + "_uidx1");

		onCreate(db);
	}

	// 履歴情報：1件登録
	private long insertHistory(SQLiteDatabase _db, String screenname) {

		// REPLACE指定で元のデータを上書きする→登録時間が更新されて、最上段にくる

		// REPLACE指定で元のデータを上書きする
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OR REPLACE INTO ");
		sb.append(DB_TABLE_USER_HISTORY);
		sb.append("(SCREENNAME,DATE)");
		sb.append("values");
		sb.append("(?,julianday('now'));");

		// IDは自動採番
		SQLiteStatement stmt = _db.compileStatement(sb.toString());
		stmt.bindString(1, screenname);

		long retID = stmt.executeInsert();

		stmt.close();

		return retID;
	}

	// 履歴情報(お気に入り)：1件登録
	private long insertFavorite(SQLiteDatabase db, String screenname) {

		// IGNORE指定で元のデータを残して処理をなかったことにする

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OR IGNORE INTO ");
		sb.append(DB_TABLE_USER_FAVORITE);
		sb.append("(SCREENNAME,FAVORITEFLAG)");
		sb.append("values");
		sb.append("(?,?);");

		// IDは自動採番
		SQLiteStatement stmt = db.compileStatement(sb.toString());
		stmt.bindString(1, screenname);
		// お気に入り状態(0=指定なし、1=お気に入り、デフォルト0)
		stmt.bindLong(2, 0);

		long retID = stmt.executeInsert();

		stmt.close();

		return retID;
	}

	// (IF)履歴情報：1件登録
	public synchronized static void insertHistory(Context context, String screenname) {
		ZusaarDBHelper helper = new ZusaarDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			// トランザクション開始
			db.beginTransaction();

			helper.insertHistory(db, screenname);
			helper.insertFavorite(db, screenname);

			// コミット
			db.setTransactionSuccessful();

		} finally {
			// トランザクション終了(エラー時はロールバック)
			db.endTransaction();

			db.close();
			helper.close();
		}
	}

	// 履歴情報(お気に入り)：1件更新
	private void updateFavorite(SQLiteDatabase db, String screenname, boolean favorite) {

		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(DB_TABLE_USER_FAVORITE);
		sb.append(" SET FAVORITEFLAG = ?");
		sb.append(" WHERE");
		sb.append(" SCREENNAME = ?;");

		// IDは自動採番
		SQLiteStatement stmt = db.compileStatement(sb.toString());

		// 受信状態：受信無し=0、保存有り=1
		int index = 1;

		if (favorite) {
			// 設定
			stmt.bindLong(index++, 1);
		} else {
			// 解除
			stmt.bindLong(index++, 0);
		}
		stmt.bindString(index++, screenname);

		stmt.execute();

		stmt.close();

		return;
	}

	// (IF)履歴情報(お気に入り)：1件更新
	public synchronized static void updateFavorite(Context context, String screenname, boolean favorite) {
		ZusaarDBHelper helper = new ZusaarDBHelper(context);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			helper.updateFavorite(db, screenname, favorite);

		} finally {
			db.close();
			helper.close();
		}
	}

	// (IF)履歴情報：全件削除
	public synchronized static int deleteAllUserHistory(Context _context) {
		ZusaarDBHelper helper = new ZusaarDBHelper(_context);
		SQLiteDatabase db = helper.getWritableDatabase();
		int retCount;
		try {
			retCount = db.delete(DB_TABLE_USER_HISTORY, null, null);
		} finally {
			db.close();
			helper.close();
		}
		return retCount;
	}

	// 件数指定検索
	public List<UserData> selectLimit(SQLiteDatabase _db, int _limit) {
		String methodName = "selectLimit";

		List<UserData> list = new ArrayList<UserData>();
		// SQL編集
		StringBuilder sb = new StringBuilder();
		sb.append("select uh.SCREENNAME, datetime(uh.DATE) as DATE, uf.FAVORITEFLAG from ");
		sb.append(DB_TABLE_USER_HISTORY).append(" uh");
		sb.append(" LEFT JOIN ").append(DB_TABLE_USER_FAVORITE).append(" uf ON uh.SCREENNAME=uf.SCREENNAME");

		sb.append(" ORDER BY uf.FAVORITEFLAG DESC, uh.DATE DESC");
		sb.append(" LIMIT ").append(_limit);

		Cursor cur = _db.rawQuery(sb.toString(), null);
		try {
			if (cur.moveToFirst()) {
				// moveToFirst→取得したレコード件数が0件はfalse
				do {
					UserData data = new UserData();
					data.screenname = cur.getString(cur.getColumnIndex("SCREENNAME"));
					try {
						data.date = LocaleUtil.createStringToDateUTC(cur.getString(cur.getColumnIndex("DATE")));
					} catch (ParseException e) {
						data.date = null;
					}
					if (cur.getInt(cur.getColumnIndex("FAVORITEFLAG")) == UserData.FLAG_ON) {
						data.favoriteflag = true;
					} else {
						data.favoriteflag = false;
					}

					list.add(data);
				} while (cur.moveToNext());
				// moveToNext→次のレコードが無ければfalse
			}
		} finally {
			cur.close();
		}

		return list;
	}

	// 件数指定検索
	public synchronized static List<UserData> selectLimit(Context context, int limit) {
		String methodName = "selectLimit";

		List<UserData> list = new ArrayList<UserData>();

		ZusaarDBHelper helper = new ZusaarDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		try {
			list = helper.selectLimit(db, limit);

		} finally {
			db.close();
			helper.close();
		}
		return list;
	}

	private static String isNullValue(String value) {

		if (value == null) {
			value = "";
		}
		return value;
	}
}
