package com.miquniqu.android.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 */
public class LocaleUtil {

	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyyMMddHH = "yyyy-MM-dd HH:";
	public static final String yyyyMMdd = "yyyy-MM-dd";
	public static final String yyyyMMddHHmmssSlash = "yyyy/MM/dd HH:mm:ss";
	public static final String MMddSlash = "MM/dd";
	public static final String MMddHHSlash = "MM/dd HH:";
	public static final String MMddHHmmSlash = "MM/dd HH:mm";
	public static final String MMddHHmmssSlash = "MM/dd HH:mm:ss";
	public static final String yyyyMMddHHSlash = "yyyy/MM/dd HH:";
	public static final String yyyyMMddSlash = "yyyy/MM/dd";
	public static final String HHmmssSlash = "HH:mm:ss";

	public static final String H = "H";
	public static final String HHmm = "HH:mm";

	public static final DateFormat yyyyMMddHHmmssLocale = createDateFormatLocale();
	public static final DateFormat yyyyMMddHHmmssUTC = createDateFormatUTC();

	public static String convertDefault(String _template, Date _date) {
		// ロケールはLocale.getDefault()から。日本だとLocaleはJAPAN。
		Locale loc = Locale.getDefault();
		DateFormat df = new SimpleDateFormat(_template, loc);
		// タイムゾーンはTimeZone.getDefault()。日本だとJST？
		TimeZone zone = TimeZone.getDefault();
		df.setTimeZone(zone);
		return df.format(_date);

	}

	public static String convertUTC(String _template, Date _date) {

		DateFormat df = new SimpleDateFormat(_template);
		// タイムゾーンはUTC
		TimeZone zone = TimeZone.getTimeZone("UTC");
		df.setTimeZone(zone);
		return df.format(_date);
	}

	public static Date convertUTC(Calendar _cal) {
		// タイムゾーンはUTC
		TimeZone zone = TimeZone.getTimeZone("UTC");
		_cal.setTimeZone(zone);
		// 一度再計算してから
		_cal.getTimeInMillis();
		return _cal.getTime();
	}

	/*
	 * デフォルトロケールの場合
	 */
	private static DateFormat createDateFormatLocale() {
		// ロケールはLocale.getDefault()から。日本だとLocaleはJAPAN。
		Locale loc = Locale.getDefault();
		DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss, loc);

		// タイムゾーンはTimeZone.getDefault()。日本だとJST？
		TimeZone zone = TimeZone.getDefault();

		df.setTimeZone(zone);
		return df;
	}

	/*
	 * UTCの場合
	 */
	private static DateFormat createDateFormatUTC() {

		DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss);

		TimeZone zone = TimeZone.getTimeZone("UTC");
		df.setTimeZone(zone);

		return df;
	}

	/**
	 * 出力するツイートの日時情報を文字列化します。
	 * 
	 * @param d
	 *            日時情報
	 * @return 文字列化された日時情報
	 */
	public static String convertDefault(Date d) {
		return yyyyMMddHHmmssLocale.format(d);
	}

	/**
	 * 出力するツイートの日時情報を文字列化します。
	 * 
	 * @param d
	 *            日時情報
	 * @return 文字列化された日時情報
	 */
	public static String convertUTC(Date d) {
		return yyyyMMddHHmmssUTC.format(d);
	}

	/**
	 * デフォルトロケールのDATE型を生成
	 */
	public static Date createStringToDateDefault(String _time) throws ParseException {
		DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss);

		TimeZone zone = TimeZone.getDefault();
		df.setTimeZone(zone);
		Date date = df.parse(_time);
		return date;
	}

	/**
	 * UTCのDATE型を生成
	 */
	public static Date createStringToDateUTC(String _time) throws ParseException {
		DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss);

		TimeZone zone = TimeZone.getTimeZone("UTC");
		df.setTimeZone(zone);
		Date date = df.parse(_time);
		return date;
	}
}