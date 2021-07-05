package cutts;

import java.util.List;

import cutts.controller.CUTTSController;
import cutts.io.HttpRequest;
import cutts.io.TimeTableIO;
import cutts.ui.CUTTSui;
import cutts.util.SymbolMap;

import planetjon.espresso4j.*;
import static planetjon.espresso4j.Constructs.*;

public class Launcher {
	private static final String UPDATE_SERVER = "https://planetjon.ca/CUTTS.php";
	private static final String SYMBOLMAP_PATH = "/cutts/cutts-symbols.xml";

	public static void main(String[] args) {
		SymbolMap.loadSymbols(SYMBOLMAP_PATH);
		CUTTSController mediator = new CUTTSController();
		TimeTableIO.createUtil(mediator);
		CUTTSui.createWindow(mediator);
		mediator.init();

		//check for updates

		new Thread() {
			public void run() {
				List<Pair<String, String>> vars = list();
				String content = "";
				vars.add( pair("version", CUTTSui.CUTTS_VERSION) );
				try {
					content = HttpRequest.post(UPDATE_SERVER, null, vars);
				}
				catch (Exception e) {
				}

				content = content.trim();
				if (! content.equals("") )
					CUTTSui.infoMessage(content);
			}
		}.start();
	}

}
