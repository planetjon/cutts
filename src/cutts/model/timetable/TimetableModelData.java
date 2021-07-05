package cutts.model.timetable;

import java.util.List;

import cutts.model.TimetableBlock;
import cutts.model.courses.Semester;
import cutts.patterns.DataAccessObject;

/**
 * A Data Access Object for the Timetable model.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimetableModelData extends DataAccessObject {

	private TimetableModel timetablemodel;

	public TimetableModelData(TimetableModel _timetablemodel) {
		timetablemodel = _timetablemodel;
	}

	/**
	 * Checks the timetable block to see if it is registered.
	 * 
	 * @param timetableblock The course being tested
	 * @return
	 */
	public boolean isRegistered(TimetableBlock timetableblock) {
		return timetablemodel.isRegistered(timetableblock);
	}

	/**
	 * Returns a list of all semesters to which timetable blocks have been registered.
	 * 
	 * @return
	 */
	public List<Semester> getRegisteredSemesters() {
		return timetablemodel.getRegisteredSemesters();
	}

	/**
	 * Returns a list of all registered timetable blocks for the semester.
	 * 
	 * @param semester The Semester being listed
	 * @return
	 */
	public List<TimetableBlock> getRegisteredTimetableBlocksForSemester(Semester semester) {
		return timetablemodel.getTimetableBlocksForSemester(semester);
	}
}
