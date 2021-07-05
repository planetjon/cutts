package cutts.query;

import java.util.List;

import cutts.model.courses.Course;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;

/**
 * Interface for handling queries made by the timetable querier.
 * 
 * @author Jonathan Weatherhead
 *
 */
public interface NavigationQueryHandler {

	public void updateDisplayedSemesters(List<Semester> semesters);

	public void updateDisplayedSubjects(List<Subject> subjects);

	public void updateDisplayedCourses(List<Course> courses);

}
