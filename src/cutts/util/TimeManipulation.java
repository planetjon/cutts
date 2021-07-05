package cutts.util;

import cutts.util.CalendarConstants.*;

/**
 * This class is a utility class for time related operations.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimeManipulation {

	/**
	 * Converts a time in seconds to 12H format.
	 * 
	 * @param time
	 * @return
	 */
	public static String timeToString(int time) {
		if(time == -1) return "???";

		String stime = "", ap;
		int h, m;

		ap = time >= 12*60 ? "PM" : "AM";
		m = time % 60;

		h = ( (time - m) / 60 ) % 12;
		h += h == 0 ? 12 : 0;

		stime = h + ":" + (m < 10 ? "0" : "") + m + " " + ap;

		return stime;
	}

	/**
	 * Returns the ordinal value of the day.
	 * 
	 * @param day
	 * @return
	 */
	public static int dayAsInt(DayNames day) {
		return day.ordinal();
	}
}
