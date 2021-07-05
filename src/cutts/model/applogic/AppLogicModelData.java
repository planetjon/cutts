package cutts.model.applogic;

import java.util.List;

import cutts.model.TimetableBlock;
import cutts.model.courses.Course;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;
import cutts.patterns.DataAccessObject;

/**
 * A Data Access Object for the Navigation Model.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class AppLogicModelData extends DataAccessObject {

	private AppLogicModel navigationmodel;

	public AppLogicModelData(AppLogicModel _navigationmodel) {
		navigationmodel = _navigationmodel;
	}

	/**
	 * Returns a list of currently displayed semesters.
	 * 
	 * @return
	 */
	public List<Semester> getDisplayedSemesters() {
		return navigationmodel.displayedsemesters;
	}

	/**
	 * Return a list of currently displayed subjects for the currently
	 * selected semester.
	 * 
	 * @return
	 */
	public List<Subject> getDisplayedSubjects() {
		return navigationmodel.displayedsubjects;
	}

	/**
	 * Returns a list of currently displayed courses for the currently
	 * selected semester and subject.
	 * 
	 * @return
	 */
	public List<Course> getDisplayedCourses() {
		return navigationmodel.displayedcourses;
	}

	/**
	 * Returns the currently selected semester.
	 * 
	 * @return
	 */
	public Semester getSelectedSemester() {
		return navigationmodel.displayedsemesterselection;
	}

	/**
	 * Returns the currently selected subject.
	 * 
	 * @return
	 */
	public Subject getSelectedSubject() {
		return navigationmodel.displayedsubjectselection;
	}

	/**
	 * Returns the currently selected course.
	 * 
	 * @return
	 */
	public Course getSelectedCourse() {
		return navigationmodel.displayedcourseselection;
	}

	/**
	 * Returns the active semester.
	 * 
	 * @return
	 */
	public Semester getActiveSemester() {
		return navigationmodel.activesemester;
	}

	/**
	 * Returns the active subject.
	 * 
	 * @return
	 */
	public Subject getActiveSubject() {
		return navigationmodel.activesubject;
	}

	/**
	 * Returns the active course.
	 * 
	 * @return
	 */
	public TimetableBlock getActiveTimetableBlock() {
		return navigationmodel.activetimetableblock;
	}
}
