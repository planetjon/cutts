package cutts.io;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import cutts.controller.CUTTSController;
import cutts.model.TimetableBlock;
import cutts.model.courses.Semester;

import static planetjon.espresso4j.Constructs.*;

public class LocalTimeTableExporter implements TimeTableExporter {
	private CUTTSController controller;

	public LocalTimeTableExporter(CUTTSController _controller) {
		controller = _controller;
	}

	/**
	 * Offers timetable saving functionality. Saves current timetable to local file.
	 * 
	 * @param path
	 * @return
	 */
	public boolean exportTimeTable(String path) {
		ObjectOutputStream fbuffer = null;
		List<TimetableBlock> blocklist = list();
		boolean successflag = true;

		//collect current timetable data
		for ( Semester semester : controller.getTimetableModelData().getRegisteredSemesters() )
			for ( TimetableBlock block : controller.getTimetableModelData().getRegisteredTimetableBlocksForSemester(semester) )
				blocklist.add(block);

		try {
			//write the data to file
			fbuffer = new ObjectOutputStream( new FileOutputStream(path) );
			fbuffer.writeObject(blocklist);
			fbuffer.close();
		}
		catch (Exception e) {
			successflag = false;
		}

		return successflag;
	}

}
