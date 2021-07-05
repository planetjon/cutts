package cutts.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cutts.controller.CUTTSController;
import cutts.model.TimetableBlock;
import cutts.util.SymbolMap;

/**
 * This class sets and displays a course information UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimetableBlockInfoPanel extends UIPanel {
	private JTextArea info;
	private JScrollPane infopane;
	private JButton adminbutton, infobutton;
	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;

	public TimetableBlockInfoPanel(CUTTSController _manager){
		super(_manager);

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.fill = GridBagConstraints.BOTH;
		layoutconstraints.insets = new Insets(2,2,2,2);

		//ADMIN BUTTON
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 1;
		adminbutton = new JButton("Add");
		adminbutton.setToolTipText("Register this course");
		layoutmanager.addLayoutComponent(adminbutton, layoutconstraints);
		add(adminbutton);

		//INFO BUTTON
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 1;
		infobutton = new JButton("Info");
		infobutton.setToolTipText("Get more course information");
		layoutmanager.addLayoutComponent(infobutton, layoutconstraints);
		add(infobutton);

		//INFO PANE
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 2;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 1;
		info = new JTextArea();
		info.setFont( new Font("sansserif", Font.BOLD, 12) );
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		info.setEditable(false);
		infopane = new JScrollPane(info);
		layoutmanager.addLayoutComponent(infopane, layoutconstraints);
		add(infopane);

		infobutton.setEnabled(false);
		adminbutton.setEnabled(false);

		//ADD ACTIONLISENER TO REGISTRATION BUTTON
		adminbutton.addActionListener( new ActionListener() {
			/**
			 * On event, attempt to register or unregister the active course
			 * depending on its registration status.
			 */
			public void actionPerformed(ActionEvent e) {
				TimetableBlock activeblock = getController().getNavigationModelData().getActiveTimetableBlock();

				if( getController().getTimetableModelData().isRegistered(activeblock) ) {
					getController().unregisterTimetableBlock(activeblock);
				}
				else {
					if(! getController().registerTimetableBlock(activeblock) )
						CUTTSui.warningMessage( SymbolMap.lookupSymbol( "CUTTS_REGISTRATION_CONFLICT", activeblock.getName() ) );
				}
			}
		});

		//ADD ACTIONLISTENER TO INFO BUTTON
		infobutton.addActionListener( new ActionListener() {
			/**
			 * On event, obtain detailed course information for the active course
			 */
			public void actionPerformed(ActionEvent e) {
				int tracker = 0, linecount = 0, poscount = 0, offset = info.getText().length() - 1;
				String extendedinfo = "\n--- Extended Info ---\n\n" + getController().getExtendedTimetableBlockInfo( getController().getNavigationModelData().getActiveTimetableBlock() );

				while( linecount++ < 5 && (tracker = extendedinfo.indexOf('\n', tracker) + 1) != 0){
					poscount = tracker;
				}

				info.append(extendedinfo);
				info.setCaretPosition(offset + poscount);
				infobutton.setEnabled(false);
			}
		});
	}

	public void update() {
		TimetableBlock activeblock = getController().getNavigationModelData().getActiveTimetableBlock();

		//if there is an active course show its information
		if(activeblock != null) {
			String courseinfo = activeblock.getInformation();

			//update the gui
			info.setText(courseinfo);
			info.setCaretPosition(0);
			infobutton.setEnabled(true);
			adminbutton.setEnabled(true);

			//set the admin button's text
			if( getController().getTimetableModelData().isRegistered(activeblock) ) {
				adminbutton.setText("Rem");
				adminbutton.setToolTipText("Unregister this course");
			}
			else {
				adminbutton.setText("Add");
				adminbutton.setToolTipText("Register this course");
			}
		}
		//clear the gui and disable buttons
		else {
			info.setText("");
			infobutton.setEnabled(false);
			adminbutton.setEnabled(false);
		}
	}
}
