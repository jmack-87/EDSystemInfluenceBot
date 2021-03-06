package com.jmack.EDSystemInfluenceBot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateCompare {

	private static Date dateTime1;
	private static Date dateTime2;
	private static String dateComp;
	private static String tick = "T16:30:00.000Z";


	/**
	 * Given a date-time string, compare against today's server tick date-time.
	 * @param date1
	 * @return comparison character indicating if date >, =, < today's tick
	 */
	public static String compareDate(String date1) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
		SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
		todayFormat.setCalendar(cal);
		dateFormat.setCalendar(cal);

		String todayDate = todayFormat.format(cal.getTime());
		todayDate = todayDate+tick;

		try {
			dateTime1 = dateFormat.parse(date1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			dateTime2 = dateFormat.parse(todayDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int dateCompare = dateTime1.compareTo(dateTime2);

		switch (dateCompare) {
			case 1:
				dateComp = "+";
				break;
			case 0:
				dateComp = "0";
				break;
			case -1:
				dateComp = "-";
				break;
		}

		return dateComp;
	}


	public static void setTick(String time) {

		DateCompare.tick = time;

	}


	public static String getTick() {

		return DateCompare.tick;

	}

}
