package cutts.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import cutts.controller.CUTTSController;

/**
 * A utility class with the intent of offering import/export functionality
 * on the timetable.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimeTableIO {
	private static TimeTableIO ioutil = null;

	private CUTTSController controller;

	/**
	 * Sets the singleton IO utility.
	 */
	public static void createUtil(CUTTSController _mediator) {
		if (TimeTableIO.ioutil == null)
			TimeTableIO.ioutil = new TimeTableIO(_mediator);
	}

	/**
	 * Gets the singleton Utility.
	 * 
	 * @return
	 */
	public static TimeTableIO getUtility() {
		return TimeTableIO.ioutil;
	}

	private TimeTableIO (CUTTSController _controller) {
		controller = _controller;
	}

	public static boolean importTimeTable(File file) {
		return new LocalTimeTableImporter(TimeTableIO.getUtility().controller).importTimeTable( file.getPath() );
	}

	public static boolean exportTimeTable(File file) {
		return new LocalTimeTableExporter(TimeTableIO.getUtility().controller).exportTimeTable( file.getPath() );
	}

	public static FileFilter createFilter(final String listoftypes[], final String description) {
		return new FileFilter() {
			private String types[] = listoftypes;
			private String name = description;
			public boolean accept(File f) {
				for (int i=0; i<this.types.length; i++) {
					if (f.getName().indexOf(this.types[i]) > -1)
						return true;
				}
				if ( f.isDirectory() )
						return true;

				return false;
			}

			public String getDescription() {
					return name;				
			}	
		};
	}
}
