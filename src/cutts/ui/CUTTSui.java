package cutts.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import cutts.controller.CUTTSController;
import cutts.io.ImageSaver;
import cutts.util.SymbolMap;

/**
 * This is the main GUI for the application
 * 
 * @author Jonathan Weatherhead
 *
 */
public class CUTTSui extends JFrame {
	private static CUTTSui cuttswindow = null;
	public static final String CUTTS_VERSION = "2.00";

	private final int WIDTH = 1024;
	private final int HEIGHT = 700;

	private CUTTSController mediator;

	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;

	private JSplitPane timetableandcourselistpanel;

	private HeaderPanel cuttsbanner;
	private UIPanel timetablepanel, querypanel, courseinfopanel, courselistpanel;

	/**
	 * Sets the singleton GUI.
	 */
	public static void createWindow(CUTTSController mediator) {
		if (CUTTSui.cuttswindow == null) {
			CUTTSui.cuttswindow = new CUTTSui("CUTTS " + CUTTSui.CUTTS_VERSION, mediator);
			CUTTSui.cuttswindow.init();
		}
	}

	/**
	 * Gets the singleton GUI.
	 * 
	 * @return
	 */
	public static CUTTSui getWindow() {
		return CUTTSui.cuttswindow;
	}

	private CUTTSui(String _name, CUTTSController _mediator) {
		super(_name);

		mediator = _mediator;

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.anchor = GridBagConstraints.NORTHEAST;
		layoutconstraints.fill = GridBagConstraints.BOTH;
		layoutconstraints.insets = new Insets(2,2,2,2);

		//CUTTS BANNER
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 2;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 100;
		layoutconstraints.weighty = 0;
		cuttsbanner = new HeaderPanel();
		layoutmanager.addLayoutComponent(cuttsbanner, layoutconstraints);
		add(cuttsbanner);

		//QUERY PANEL
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 2;
		layoutconstraints.weightx = 25;
		layoutconstraints.weighty = 100;
		querypanel = new QueryPanel(mediator);
		querypanel.setBackground( new Color(242, 242, 242) );
		layoutmanager.addLayoutComponent(querypanel, layoutconstraints);
		add(querypanel);

		//TIMETABLE AND COURSELIST SPLIT
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 75;
		layoutconstraints.weighty = 80;
		timetablepanel = new SemesterTabsPanel(mediator);
		courselistpanel = new TimetableBlockListPanel(mediator);
		timetableandcourselistpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                timetablepanel, courselistpanel);
		timetableandcourselistpanel.setOneTouchExpandable(true);
		timetableandcourselistpanel.setDividerLocation(540);
		timetableandcourselistpanel.setResizeWeight(1.0);
		timetableandcourselistpanel.setBackground( new Color(242, 242, 242) );
		layoutmanager.addLayoutComponent(timetableandcourselistpanel, layoutconstraints);
		add(timetableandcourselistpanel);

		//INFO PANEL
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 2;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 75;
		layoutconstraints.weighty = 20;
		courseinfopanel = new TimetableBlockInfoPanel(mediator);
		courseinfopanel.setBackground( new Color(242, 242, 242) );
		layoutmanager.addLayoutComponent(courseinfopanel, layoutconstraints);
		add(courseinfopanel);
	}

	/**
	 * Perform final setup operations and start up the application.
	 */
	public void init() {
		setIconImage( new ImageIcon( getResource("/images/CUTTS_Icon.jpg") ).getImage() );

		setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);
		setVisible(true);
	}

	/**
	 * Offers timetable exporting functionality. Exports visible timetable semester
	 * to file in jpeg format.
	 * 
	 * @param _file
	 * @return
	 */
	public boolean exportSemester(File _file) {
		Image image;
		Component schedulecomponent;
		Graphics g;
		boolean successflag = true;

		//get the ui component containing the visible timetable graphics
		schedulecomponent = ( (SemesterTabsPanel)timetablepanel ).getDisplayedScheduleComponent();
		if(schedulecomponent == null)
			return false;

		//create an image buffer and paint to it
		image = createImage( schedulecomponent.getWidth(), schedulecomponent.getHeight() );
		g = image.getGraphics();
		schedulecomponent.paint(g);
		
		try {
			//save the image to file
			if(! ImageSaver.saveToJPEG(image, _file, 85) )
				successflag = false;
		}
		catch (Exception e) {
			successflag = false;
		}

		return successflag;
	}

	/**
	 * Spawns a bug report dialog.
	 */
	public static void bugReport() {
		JDialog bugreportdialog = new BugReportDialog(CUTTSui.cuttswindow);
        bugreportdialog.setVisible(true);
	}

	/**
	 * Spawns the about dialog.
	 */
	public static void about() {
		CUTTSui.infoMessage(SymbolMap.lookupSymbol("CUTTS_ABOUT"));
	}

	/**
	 * Offers information message dialog functionality.
	 * 
	 * @param title
	 * @param message
	 */
	public static void infoMessage(String message) {
		JOptionPane.showMessageDialog(CUTTSui.cuttswindow, message, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Offers warning message dialog functionality.
	 * 
	 * @param title
	 * @param message
	 */
	public static void warningMessage(String message) {
		JOptionPane.showMessageDialog(CUTTSui.cuttswindow, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Offers error message dialog functionality.
	 * 
	 * @param title
	 * @param message
	 */
	public static void errorMessage(String message) {
		JOptionPane.showMessageDialog(CUTTSui.cuttswindow, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Loads resources.
	 * 
	 * @param _path
	 * @return
	 */
	private URL getResource(String _path) {
		return getClass().getResource(_path);
	}
}
