package cutts.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cutts.controller.CUTTSController;
import cutts.model.TimetableBlock;
import cutts.model.courses.Course;
import cutts.model.courses.Semester;

public class TimetableBlockListPanel extends UIPanel {
	private JList<TimetableBlock> courses;
	private JScrollPane scrollpane; 

	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;

	private boolean updateflag, listenersdisabled;

	public TimetableBlockListPanel(CUTTSController mediator) {
		super(mediator);

		courses = new JList<TimetableBlock>();
		scrollpane = new JScrollPane(courses);

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.anchor = GridBagConstraints.NORTHEAST;
		layoutconstraints.fill = GridBagConstraints.BOTH;

		//CUTTS BANNER
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 100;
		layoutconstraints.weighty = 100;
		layoutmanager.addLayoutComponent(scrollpane, layoutconstraints);
		add(scrollpane);

		updateflag = true;
		listenersdisabled = false;

		//ADD LISTSELECTIONLISTENER TO COURSE LIST
		courses.addListSelectionListener( new ListSelectionListener() {
			/*
			 * When course value changes, update the mediator's selected course
			 */
			public void valueChanged(ListSelectionEvent e){
				if(listenersdisabled)
					return;

				if ( e.getValueIsAdjusting() )
					return;

				updateflag = false;
				TimetableBlock block = (TimetableBlock)( (JList<?>)e.getSource() ).getSelectedValue();
				getController().selectTimetableBLock(block);
			}
		});
	}

	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	public void update() {
		if(updateflag) {
			listenersdisabled = true;

			DefaultListModel<TimetableBlock> m = new DefaultListModel<TimetableBlock>();
			Semester semester = getController().getNavigationModelData().getActiveSemester();
		
			if(semester != null)
				for( TimetableBlock block : getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(semester) )
					m.addElement(block);
		
			courses.setModel(m);
		
			scrollpane.setMinimumSize( new Dimension( getWidth(), getHeight() ) );
			scrollpane.setMaximumSize( new Dimension( getWidth(), getHeight() ) );
			scrollpane.setPreferredSize( new Dimension( getWidth(), getHeight() ) );
			scrollpane.revalidate();

			listenersdisabled = false;
		}
		updateflag = true;
	}

}
