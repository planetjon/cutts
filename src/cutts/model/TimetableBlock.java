package cutts.model;

import java.io.Serializable;
import java.util.List;

import cutts.model.courses.Course;
import cutts.model.courses.MeetingInformation;
import cutts.model.courses.Semester;
import cutts.util.CalendarConstants;
import cutts.util.TimeManipulation;

public class TimetableBlock implements Serializable {
	protected String name;
	protected Semester semester;
	protected List<MeetingInformation> meetings;
	protected String description;
	
	
	public TimetableBlock(String _name, Semester _semester,  List<MeetingInformation> _meetings, String _description) {
		name = _name;
		semester = _semester;
		meetings = _meetings;
		description = _description;
	}

	/**
	 * Returns the descriptive name of this course.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the semester that this timetable block occurs in.
	 * 
	 * @return
	 */
	public Semester getSemester() {
		return semester;
	}

	/**
	 * Returns a list of meetings for this timetable block.
	 * 
	 * @return
	 */
	public List<MeetingInformation> getMeetings() {
		return meetings;
	}

	public String getInformation() {
		String text = "";
		text += getName() + "\n + description + \n";

		for ( MeetingInformation meeting : getMeetings() ) {
			text += meeting.location + " ";
			text += CalendarConstants.DAYS_OF_THE_WEEK[ TimeManipulation.dayAsInt(meeting.day) ] + " ";
			text += TimeManipulation.timeToString(meeting.starttime) + " - " + TimeManipulation.timeToString(meeting.endtime);
			text += "\n";
		}

		return text;
	}

	public boolean equals(Object o) {
		if(o instanceof TimetableBlock)
			return name.equals( ((TimetableBlock)o).getName() );

		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}
}
