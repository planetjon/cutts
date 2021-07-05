package cutts.ui;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cutts.io.TimeTableIO;
import cutts.util.SymbolMap;
/**
 * This class sets up and displays the menubar UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class HeaderPanel extends JPanel {
	private final String IMG_BASE = "/images/";

	private final String BACKGROUND = IMG_BASE + "header_background.jpg";
	private final String CUTTS_LOGO = IMG_BASE + "CUTTS_logo.jpg";
	private final String CU_LOGO = IMG_BASE + "CU_logo.jpg";
	private final String LEFT_BUTTON_BUFFER = IMG_BASE + "button_buffer_left.jpg";
	private final String RIGHT_BUTTON_BUFER = IMG_BASE + "button_buffer_right.jpg";

	private Image backgroundimage;
	private JLabel cuttslogo;
	private JLabel culogo;
	private  JLabel leftbuffer, rightbuffer;
	private ImageButton savebutton, loadbutton, exportbutton, reportabugbutton, aboutbutton;

	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;

	public HeaderPanel() {
		super();

		backgroundimage = new ImageIcon( getResource(BACKGROUND) ).getImage();

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.fill = GridBagConstraints.NONE;

		//CUTTS LOGO
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 7;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 1;
		cuttslogo = new JLabel( new ImageIcon( getResource(CUTTS_LOGO) ) );
		layoutmanager.addLayoutComponent(cuttslogo, layoutconstraints);
		add(cuttslogo);

		//CU LOGO
		layoutconstraints.anchor = GridBagConstraints.NORTHEAST;
		layoutconstraints.gridx = 7;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 2;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 1;
		culogo = new JLabel( new ImageIcon( getResource(CU_LOGO) ) );
		layoutmanager.addLayoutComponent(culogo, layoutconstraints);
		add(culogo);

		//BUTTON BUFFER LEFT
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		leftbuffer = new JLabel( new ImageIcon( getResource(LEFT_BUTTON_BUFFER) ) );
		layoutmanager.addLayoutComponent(leftbuffer, layoutconstraints);
		add(leftbuffer);

		//LOAD BUTTON
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		loadbutton = new ImageButton(IMG_BASE + "load", false);
		layoutmanager.addLayoutComponent(loadbutton, layoutconstraints);
		add(loadbutton);

		//SAVE BUTTON
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 2;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		savebutton = new ImageButton(IMG_BASE + "save", false);
		layoutmanager.addLayoutComponent(savebutton, layoutconstraints);
		add(savebutton);

		//EXPORT BUTTON
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 3;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		exportbutton = new ImageButton(IMG_BASE + "export", false);
		layoutmanager.addLayoutComponent(exportbutton, layoutconstraints);
		add(exportbutton);

		//BUG REPORT BUTTON
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 4;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		reportabugbutton = new ImageButton(IMG_BASE + "bug_report", false);
		layoutmanager.addLayoutComponent(reportabugbutton, layoutconstraints);
		add(reportabugbutton);

		//ABOUT BUTTON
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 5;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		aboutbutton = new ImageButton(IMG_BASE + "about", false);
		layoutmanager.addLayoutComponent(aboutbutton, layoutconstraints);
		add(aboutbutton);

		//BUTTON BUFFER RIGHT
		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.gridx = 6;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		rightbuffer = new JLabel( new ImageIcon( getResource(RIGHT_BUTTON_BUFER) ) );
		layoutmanager.addLayoutComponent(rightbuffer, layoutconstraints);
		add(rightbuffer);

		//ADD LISTENER TO LOAD BUTTON
		loadbutton.addActionListener( new ActionListener() {
			//Sets up the loading timetable UI functionality
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter( TimeTableIO.createFilter(new String[]{".cutts"}, "CUTTS Schedule (.cutts)") );
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int returnVal = chooser.showOpenDialog( CUTTSui.getWindow() );
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					if (! TimeTableIO.importTimeTable( chooser.getSelectedFile() ) )
						CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_TIMETABLE_LOAD_FAILURE") );
				}
			}
		});

		//ADD LISTENER TO SAVE BUTTON
		savebutton.addActionListener( new ActionListener() {
			//Sets up the saving timetable UI functionality
			public void actionPerformed(ActionEvent e) {
				File tmp;
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter( TimeTableIO.createFilter(new String[]{".cutt"}, "CUTTS Schedule (.cutts)") );
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int returnVal = chooser.showSaveDialog( CUTTSui.getWindow() );
				if (returnVal == JFileChooser.APPROVE_OPTION){
					tmp = chooser.getSelectedFile();
					if (tmp.getPath().lastIndexOf(".cutts") < tmp.getPath().length() - 6)
						tmp = new File( tmp.getPath() + ".cutts" );

					if (! TimeTableIO.exportTimeTable(tmp) )
						CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_TIMETABLE_SAVE_FAILURE") );
				}
			}
		});

		//ADD LISTENER TO EXPORT BUTTON
		exportbutton.addActionListener( new ActionListener() {
			//sets up the exporting timetable UI functionality
			public void actionPerformed(ActionEvent e) {
				File tmp;
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter( TimeTableIO.createFilter(new String[]{".jpg"}, "JPEG (.jpg)") );
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int returnVal = chooser.showSaveDialog( HeaderPanel.this.getParent() );
				if (returnVal == JFileChooser.APPROVE_OPTION){
					tmp = chooser.getSelectedFile();
					if (tmp.getPath().lastIndexOf(".jpg") < tmp.getPath().length() - 4)
						tmp = new File( tmp.getPath() + ".jpg" );

					if (! CUTTSui.getWindow().exportSemester(tmp) )
						CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_TIMETABLE_EXPORT_FAILURE") );
				}
			}
		});

		//ADD LISTENER TO BUG REPORT BUTTON
		reportabugbutton.addActionListener( new ActionListener() {
			//sets up the bug report UI functionality
			public void actionPerformed(ActionEvent e) {
				CUTTSui.bugReport();
			}
		});

		//ADD LISTENER TO ABOUT BUTTON
		aboutbutton.addActionListener(new ActionListener() {
			//sets up the bug report UI functionality
			public void actionPerformed(ActionEvent e) {
				CUTTSui.about();
			}
		});
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for( int i = 0; i < getWidth(); i += backgroundimage.getWidth(null) ) {
			g.drawImage(backgroundimage, i, 0, null);
		}
	}

	/**
	 * Loads resources.
	 * 
	 * @param path
	 * @return
	 */
	private URL getResource(String path) {
		return getClass().getResource(path);
	}
}
