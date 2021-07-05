package cutts.model.timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cutts.model.Model;
import cutts.model.TimetableBlock;
import cutts.model.courses.MeetingInformation;
import cutts.model.courses.Semester;
import cutts.patterns.Observable;

import static planetjon.espresso4j.Constructs.*;

/**
 * A model representing the state of the application timetable
 * and offers various operations for manipulating it.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimetableModel extends Model implements Observable {
	private Map<Semester, List<TimetableBlock>> timetable;

	public TimetableModel() {
		super();

		timetable = map();
	}

	/**
	 * Attempts to register a timetable block in the timetable.
	 * 
	 * @param timetableblock The timetable block being registered
	 * @return
	 */
	public boolean register(TimetableBlock timetableblock) {
		Semester semester = timetableblock.getSemester();
		List<TimetableBlock> blocklist = timetable.get(semester);

		if (blocklist == null)
			blocklist = addSemester(semester);

		for(TimetableBlock block : blocklist)
			if ( blocksIntersect(block, timetableblock) )
				return false;

		blocklist.add(timetableblock);
		notifyObservers();
		return true;
	}

	/**
	 * Attempts to unregister a timetable block from the timetable.
	 * 
	 * @param timetableblock The timetable block being unregistered
	 * @return
	 */
	public boolean unregister(TimetableBlock timetableblock) {
		Semester semester = timetableblock.getSemester();
		List<TimetableBlock> blocklist = timetable.get(semester);

		if (blocklist == null)
			return false;

		boolean result = blocklist.remove(timetableblock);

		if ( blocklist.isEmpty() )
			removeSemester(semester);

		if(result)
			notifyObservers();

		return result;
	}

	/**
	 * Clears the timetable.
	 */
	public void clearTimeTable() {
		timetable.clear();
	}

	/**
	 * Returns a list of semesters to which timetable blocks are registered.
	 * 
	 * @return
	 */
	public List<Semester> getRegisteredSemesters() {
		return new ArrayList<Semester>( timetable.keySet() );
	}

	/**
	 * Returns a list of timetable blocks that are registered in the semester.
	 * 
	 * @param semester The semester being listed
	 * @return
	 */
	public List<TimetableBlock> getTimetableBlocksForSemester(Semester semester) {
		List<TimetableBlock> courses = timetable.get(semester);
		if(courses == null)
			courses = new ArrayList<TimetableBlock>();

		return courses;
	}

	/**
	 * Checks a timetable block against the timetable to determine if it has been registered.
	 * 
	 * @param timetableblock The timetable block being tested
	 * @return
	 */
	public boolean isRegistered(TimetableBlock timetableblock) {
		Semester semester = timetableblock.getSemester();
		List<TimetableBlock> blocklist = timetable.get(semester);

		if (blocklist != null)
			return blocklist.contains(timetableblock);

		return false;
	}

	/**
	 * Checks two timetable blocks to see if any of their meetings collide.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean blocksIntersect(TimetableBlock a, TimetableBlock b) {
		int lbound, hbound;

		for ( MeetingInformation ma : a.getMeetings() ) {
			for (MeetingInformation mb : b.getMeetings() ) {
				if(! ma.day.equals(mb.day) )
					continue;

				lbound = Math.min(ma.starttime, mb.starttime);
				hbound = Math.max(ma.endtime, mb.endtime);

				if (hbound - lbound < ma.endtime + mb.endtime - ma.starttime - mb.starttime)
					return true;
			}
		}
		return false;
	}

	/**
	 * Adds a semester to the timetable.
	 * 
	 * @param semester
	 * @return
	 */
	private List<TimetableBlock> addSemester(Semester semester) {
		List<TimetableBlock> blocklist = new ArrayList<TimetableBlock>();
		timetable.put(semester, blocklist);
		return blocklist;
	}

	/**
	 * Removes a timetable from the semester.
	 * 
	 * @param semester
	 * @return
	 */
	private List<TimetableBlock> removeSemester(Semester semester) {
		return timetable.remove(semester);
	}
}
