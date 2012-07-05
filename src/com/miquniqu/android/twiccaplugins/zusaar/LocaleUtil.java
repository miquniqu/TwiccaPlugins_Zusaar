package com.miquniqu.android.twiccaplugins.zusaar;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 *
 */
public class LocaleUtil {

	public static String convertYYYYMMDDHHMM(String srcString) {
		String dstString = null;
		FastDateFormat fastDateFormat = org.apache.commons.lang3.time.DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT;
		String patterns[] = { fastDateFormat.getPattern() };
		try {
			Date dstDate = org.apache.commons.lang3.time.DateUtils.parseDate(srcString, patterns);
			SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyy/MM/dd(EEE) HH:mm");
			dstString = dstDateFormat.format(dstDate);
		} catch (Exception e) {
			dstString = "";
		}
		return dstString;

	}

	public static String convertHHMM(String srcString) {
		String dstString = null;
		FastDateFormat fastDateFormat = org.apache.commons.lang3.time.DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT;
		String patterns[] = { fastDateFormat.getPattern() };
		try {
			Date dstDate = org.apache.commons.lang3.time.DateUtils.parseDate(srcString, patterns);
			SimpleDateFormat dstDateFormat = new SimpleDateFormat("HH:mm");
			dstString = dstDateFormat.format(dstDate);
		} catch (Exception e) {
			dstString = "";
		}
		return dstString;

	}

}