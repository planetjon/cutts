package cutts.io;

/**
 * Abstraction for exporting a timetable to a resource
 * 
 * @author Jonathan Weatherhead
 *
 */
public interface TimeTableExporter {

	public boolean exportTimeTable(String resource);

}
