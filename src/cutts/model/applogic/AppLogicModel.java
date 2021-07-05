package cutts.model.applogic;

import java.util.ArrayList;
import java.util.List;

import cutts.model.Model;
import cutts.model.TimetableBlock;
import cutts.model.courses.Course;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;
import cutts.patterns.Observable;
import cutts.query.NavigationQueryHandler;
import cutts.query.CarletonCentralQuerier;

import static planetjon.espresso4j.Constructs.*;

/**
 * A model representing the state of the application navigation
 * and offers various operations for manipulating it.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class AppLogicModel extends Model implements NavigationQueryHandler, Observable {
	List<Semester> displayedsemesters;
	List<Subject> displayedsubjects;
	List<Course> displayedcourses;

	Semester displayedsemesterselection;
	Subject displayedsubjectselection;
	Course displayedcourseselection;

	Semester activesemester;
	Subject activesubject;
	TimetableBlock activetimetableblock;

	public AppLogicModel() {
		super();

		displayedsemesters = list();
		displayedsubjects = list();
		displayedcourses = list();

		displayedsemesterselection = null;
		displayedsubjectselection = null;
		displayedcourseselection = null;

		activesemester = null;
		activetimetableblock = null;
	}

	/**
	 * Selects a semester in the list of displayed semesters
	 * 
	 * @param semester The semester to be selected
	 */
	public void setSelectedSemester(Semester semester) {
		displayedsemesterselection = semester;
		activesemester = semester;

		displayedsubjects.clear();
		displayedsubjectselection = null;

		displayedcourses.clear();
		displayedcourseselection = null;
		activetimetableblock = null;

		if (displayedsemesterselection != null)
			CarletonCentralQuerier.queryAvailableSubjects(this, displayedsemesterselection);
	}

	/**
	 * 
	 * Selects a subject in the list of displayed subjects.
	 * 
	 * @param subject The subject to be selected
	 */
	public void setSelectedSubject(Subject subject) {
		displayedsubjectselection = subject;

		displayedcourses.clear();
		displayedcourseselection = null;
		activetimetableblock = null;

		if (displayedsemesterselection != null && displayedsubjectselection != null)
			CarletonCentralQuerier.queryAvailableCourses(this, displayedsemesterselection, displayedsubjectselection);
	}

	/**
	 * Selects a course in the list of displayed courses.
	 * 
	 * @param course The course to be selected
	 */
	public void setSelectedCourse(Course course) {
		displayedcourseselection = course;
		activetimetableblock = course;
		notifyObservers();
	}

	/**
	 * Sets the active semester.
	 * 
	 * @param semester The semester to be made active
	 */
	public void setActiveSemester(Semester semester) {
		activesemester = semester;
		notifyObservers();
	}

	/**
	 * Sets the active subject
	 * 
	 * @param subject The subject to be made active
	 */
	public void setActiveSubject(Subject subject) {
		activesubject = subject;
		notifyObservers();
	}

	/**
	 * Sets the active course.
	 * 
	 * @param _course The course to be made active
	 */
	public void setActiveTimetableBlock(TimetableBlock block) {
		activetimetableblock = block;
		notifyObservers();
	}

	/**
	 * Updates the model's list of displayed semesters.
	 */
	public void updateDisplayedSemesters(List<Semester> semesters) {
		displayedsemesters = semesters;
		notifyObservers();
	}

	/**
	 * Updates the model's list of displayed subjects.
	 */
	public void updateDisplayedSubjects(List<Subject> subjects) {
		displayedsubjects = subjects;
		notifyObservers();
	}

	/**
	 * Updates the model's list of displayed courses.
	 */
	public void updateDisplayedCourses(List<Course> courses) {
		displayedcourses = courses;
		notifyObservers();
	}
}
