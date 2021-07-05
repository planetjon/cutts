package cutts.io;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import cutts.controller.CUTTSController;
import cutts.model.TimetableBlock;

public class LocalTimeTableImporter implements TimeTableImporter {
	private CUTTSController mediator;

	public LocalTimeTableImporter(CUTTSController _mediator) {
		mediator = _mediator;
	}

	/**
	 * Offers timetable loading functionality. Clears existing timetable information
	 * and imports data to the timetable from the local file.
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean importTimeTable(String path) {
		ObjectInputStream filebuffer = null;
		List<TimetableBlock> importedblocks = null;
		boolean successflag = true;

		try {
			//read the data in
			filebuffer = new ObjectInputStream( new FileInputStream(path) );
			importedblocks = (List<TimetableBlock>)filebuffer.readObject();
			filebuffer.close();

			//clear existing timetable data
			mediator.clearTimeTable();

			//populate timetable with data from file
			for (TimetableBlock block : importedblocks)
				mediator.registerTimetableBlock(block);
		}
		catch (Exception e) {
			successflag = false;
		}

		return successflag;
	}

}
