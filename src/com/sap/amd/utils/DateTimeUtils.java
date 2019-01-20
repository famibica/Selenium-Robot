package com.sap.amd.utils;

import java.util.Date;

public class DateTimeUtils
{
	/**
	 * Get minutes between two dates
	 * @param d1 before
	 * @param d2 after
	 * @return minutes
	 */
	public static int getMinutesBetween(Date d1, Date d2)
	{
		return (int)((d2.getTime() / 60000) - (d1.getTime() / 60000));
	}
	
	/**
	 * Changes a given minutes value to a String representing [-]HH:mm. 
	 * @param minutes
	 * @return String representation of time
	 */
	public static String minutesToTime(int minutes)
	{
		boolean negative = false;
		double days = ((double)minutes / 60) / 24;
		
		if (days < 0)
		{
			negative = true;
			days = Math.abs(days);
		}
	
		String time = ((int)days == 0 ? "" : (int)days + "d ");
		double hours = (days - (int)days) * 24;
		
		time += ((int)hours == 0 ? "" : (int)hours + "h ");

		double mins = (hours - (int)hours) * 60;
		
		time += Math.round(mins) + "min";
		
		if (negative)
		{
			time = "- " + time;
		}
		
		return time;
	}
}
