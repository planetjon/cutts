package cutts.io;

/**
 * Abstraction for importing a timetable from a resource
 * 
 * @author Jonathan Weatherhead
 *
 */
public interface TimeTableImporter {

	public boolean importTimeTable(String resource);

}
