package cutts.controller;

import cutts.model.TimetableBlock;
import cutts.model.applogic.AppLogicModel;
import cutts.model.applogic.AppLogicModelData;
import cutts.model.courses.Course;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;
import cutts.model.timetable.TimetableModel;
import cutts.model.timetable.TimetableModelData;
import cutts.patterns.Observable;
import cutts.patterns.Observer;
import cutts.query.CarletonCentralQuerier;
import cutts.util.TimetableBlockDescriptionCache;

/**
 * Controller for the GUI. It provides all of the available UI operations
 * and updates the UI through the Observer pattern.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class CUTTSController implements Observable {
	private AppLogicModel navigator;
	private TimetableModel timetable;

	private AppLogicModelData navigatordata;
	private TimetableModelData timetabledata;
	private TimetableBlockDescriptionCache infocache;
	
	public CUTTSController() {
		navigator = new AppLogicModel();
		timetable = new TimetableModel();

		navigatordata = new AppLogicModelData(navigator);
		timetabledata = new TimetableModelData(timetable);
		infocache = new TimetableBlockDescriptionCache();
	}

	/**
	 * Call this to "start" the application.
	 */
	public void init() {
		//populate available semesters
		CarletonCentralQuerier.queryAvailableSemesters(navigator);
	}

	/**
	 * Returns the DAO for the navigation model.
	 * @return
	 */
	public AppLogicModelData getNavigationModelData() {
		return navigatordata;
	}

	/**
	 * Returns the DAO for the timetable model.
	 * @return
	 */
	public TimetableModelData getTimetableModelData() {
		return timetabledata;
	}

	/**
	 * Selects a semester in the list of displayed semesters.
	 * 
	 * @param semester The semester to be selected
	 */
	public void navigateSemester(Semester semester) {
		navigator.setSelectedSemester(semester);
	}

	/**
	 * 
	 * Selects a subject in the list of displayed subjects.
	 * 
	 * @param subject The subject to be selected
	 */
	public void navigateSubject(Subject subject) {
		navigator.setSelectedSubject(subject);
	}

	/**
	 * Selects a course in the list of displayed courses.
	 * 
	 * @param course
	 */
	public void navigateCourse(Course course) {
		navigator.setSelectedCourse(course);
	}

	/**
	 * Sets the active semester
	 * 
	 * @param semester Semester to be made active
	 */
	public void selectSemester(Semester semester) {
		navigator.setActiveSemester(semester);
	}

	/**
	 * Sets the active subject
	 * 
	 * @param subject Subject to be made active
	 */
	public void selectSubject(Subject subject) {
		navigator.setActiveSubject(subject);
	}

	/**
	 * Sets the active course.
	 * 
	 * @param course Course to be made active
	 */
	public void selectTimetableBLock(TimetableBlock block) {
		navigator.setActiveTimetableBlock(block);
	}

	/**
	 * Attempts to register the course.
	 * 
	 * @param course The course to be registered
	 * @return
	 */
	public boolean registerTimetableBlock(TimetableBlock block) {
		return timetable.register(block);
	}

	/**
	 * Attempts to unregister the course.
	 * 
	 * @param course The course to be unregistered
	 * @return
	 */
	public boolean unregisterTimetableBlock(TimetableBlock block) {
		return timetable.unregister(block);
	}

	/**
	 * Clears the timetable.
	 */
	public void clearTimeTable() {
		timetable.clearTimeTable();
	}

	/**
	 * Returns extended information for the course.
	 * 
	 * @param _course The course to be queried
	 * @return
	 */
	public String getExtendedTimetableBlockInfo(TimetableBlock block) {
		return infocache.getExtendedInfo(block);
	}

	/**
	 * Registers the observer.
	 * 
	 * @param observer The observer to be registered
	 */
	public void registerObserver(Observer observer) {
		navigator.registerObserver(observer);
		timetable.registerObserver(observer);
	}

	/**
	 * Unregisters the observer.
	 * 
	 * @param observer The observer to be registered
	 */
	public void unregisterObserver(Observer observer) {
		navigator.unregisterObserver(observer);
		timetable.unregisterObserver(observer);
	}
}
