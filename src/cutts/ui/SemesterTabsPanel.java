package cutts.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cutts.controller.CUTTSController;
import cutts.model.courses.Semester;

import static planetjon.espresso4j.Constructs.*;

/**
 * This class sets up and displays a semester tab management UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class SemesterTabsPanel extends UIPanel {
	private JTabbedPane tabbedpane;
	private Map<Semester, JScrollPane> semestertabs;

	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;

	private boolean updateflag;

	public SemesterTabsPanel(CUTTSController _manager) {
		super(_manager);

		semestertabs = map();

		updateflag = true;

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.fill = GridBagConstraints.BOTH;
		layoutconstraints.insets = new Insets(2,2,2,2);

		//TABED PANE
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 1;
		tabbedpane = new JTabbedPane();
		layoutmanager.addLayoutComponent(tabbedpane, layoutconstraints);
		add(tabbedpane);

		tabbedpane.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//set the active semester to the one respective to the currently viewed tab
				for ( Semester semester :  semestertabs.keySet() ) {
					if( semestertabs.get(semester).equals( tabbedpane.getSelectedComponent() ) ) {
						updateflag = false;
						getController().selectSemester(semester);
						break;
					}
				}
			}
		});
	}

	/**
	 * Returns the component that is currently visible in the tabbed pane.
	 * 
	 * @return
	 */
	public Component getDisplayedScheduleComponent() {
		return ( (JScrollPane)tabbedpane.getSelectedComponent() ).getViewport().getView();
	}

	public void update() {
		if(updateflag) {
			Semester semester = null;
			JScrollPane scrollpane = null;
	
			//add tab if there is a new semester
			if( semestertabs.size() < getController().getTimetableModelData().getRegisteredSemesters().size() ) {
				//find new semester
				for ( Semester s : getController().getTimetableModelData().getRegisteredSemesters() ) {
					if(! semestertabs.containsKey(s) ) {
						semester = s;
					 	break;
					 }
				}
				//add tab, and update semesterpanellist and tablist
				scrollpane = new JScrollPane( new SchedulePanel(getController(), semester) );
				semestertabs.put(semester, scrollpane);
				tabbedpane.addTab(semester.getName(), scrollpane);
				tabbedpane.setSelectedComponent( tabbedpane.getComponent( tabbedpane.getTabCount() - 1) );
				((JScrollPane)tabbedpane.getComponentAt( tabbedpane.getComponentCount() - 1 )).getVerticalScrollBar().setUnitIncrement(10);
			}
			//remove tab if a semester was removed
			else if ( semestertabs.size() > getController().getTimetableModelData().getRegisteredSemesters().size() ) {
				//find removed semester
				for ( Semester s :  semestertabs.keySet() ) {
					if (! getController().getTimetableModelData().getRegisteredSemesters().contains(s) ) {
						semester = s;
					 	break;
					 }
				}
				//remove tab, and update semesterpanellist and tablist
				scrollpane = semestertabs.get(semester);
				semestertabs.remove(semester);
				tabbedpane.remove(scrollpane);
			}
			//if no change, give focus to semester tab of active course, if applicable
			else {
				Semester semestertoview;
				//if there is an active course, get its semester
				if (getController().getNavigationModelData().getActiveTimetableBlock() != null)
					semestertoview = getController().getNavigationModelData().getActiveTimetableBlock().getSemester();
				//if there is no active course, get the active semester
				else if(getController().getNavigationModelData().getActiveSemester() != null)
					semestertoview = getController().getNavigationModelData().getActiveSemester();
				//if there is no active course or semester, do nothing
				else
					return;
	
				//if the semester to view is in the tabbed pane, focus on its respective tab
				if ( semestertabs.containsKey(semestertoview) )
					tabbedpane.setSelectedComponent( semestertabs.get(semestertoview) );
			}
		}
		updateflag = true;
	}
}