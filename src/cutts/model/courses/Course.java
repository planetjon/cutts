package cutts.model.courses;

import java.util.List;

import cutts.model.TimetableBlock;
import cutts.util.CalendarConstants;
import cutts.util.TimeManipulation;

/**
 * This class is a data container for relevant course information.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class Course extends TimetableBlock {
	private Subject subject;
	private String id, section, crn;
	private String lecturers;

	public Course(Semester _semester, Subject _subject, String _id, String _section, String _crn, String _name, String _lecturers, List<MeetingInformation> _meetings) {
		super(_name, _semester, _meetings, "");

		semester = _semester;
		subject = _subject;
		id = _id;
		crn = _crn;
		section	= _section;
		name = _name;
		lecturers = _lecturers;
		meetings = _meetings;
	}

	/**
	 * Returns the subject that this course is classified under.
	 * 
	 * @return
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Returns the number of this course.
	 * 
	 * @return
	 */
	public int getNumber() {
		return Integer.parseInt( id.split(" ")[1] );
	}

	/**
	 * Returns the code for this course.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the CRN of this course.
	 * @return
	 */
	public String getCRN() {
		return crn;
	}

	/**
	 * Returns the section of this course.
	 * 
	 * @return
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Returns the lecturer(s) of the course.
	 * 
	 * @return
	 */
	public String getLecturers() {
		return lecturers;
	}

	public String getInformation() {
		String description = "";
		description += getId() + " " + getSection() + " - " + getName() + "\n";
		description += "CRN " + getCRN() + "\n";
		description += "\n";
		description += "Lecturer(s) " + getLecturers() + "\n";
		description += "\n";

		for ( MeetingInformation meeting : getMeetings() ) {
			description += meeting.location + " ";
			description += CalendarConstants.DAYS_OF_THE_WEEK[ TimeManipulation.dayAsInt(meeting.day) ] + " ";
			description += TimeManipulation.timeToString(meeting.starttime) + " - " + TimeManipulation.timeToString(meeting.endtime);
			description += "\n";
		}

		return description;
	}

	public boolean equals(Object o) {
		if(o instanceof Course)
			return (id + section).equals( ((Course)o).getId() + ((Course)o).getSection() );

		return false;
	}

	public int hashCode() {
		return (id + section).hashCode();
	}

	public String toString() {
		return getId() + " " + getSection() + " - " + getName();
	}

}
