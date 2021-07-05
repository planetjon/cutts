package cutts.model.courses;

import java.io.*;

import cutts.util.CalendarConstants.*;


/**
 * This class is a data container for relevant course meeting information.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class MeetingInformation implements Serializable {
	public final String location, buildingname, roomnumber;
	public final DayNames day;
	public final int starttime, endtime;

	public MeetingInformation(String _location, DayNames _day, int _starttime, int _endtime) {
		location = _location;
		int pos = location.lastIndexOf(' ');
		if(pos >= 0) {
			buildingname = location.substring(0, pos);
			roomnumber = location.substring(pos + 1);
		}
		else{
			buildingname = "TBA";
			roomnumber = "";
		}
		day = _day;
		starttime = _starttime;
		endtime = _endtime;

	}

}
