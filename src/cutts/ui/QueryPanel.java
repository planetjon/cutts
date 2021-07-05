package cutts.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cutts.controller.CUTTSController;
import cutts.model.courses.Course;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;
import cutts.util.SymbolMap;

/**
 * This class sets up and displays a course query UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class QueryPanel extends UIPanel {
	private final int SEMESTER_LIMIT = 3;

	private JList<Semester> semesters;
	private JList<Subject> subjects;
	private JList<Course> courses;
	private JScrollPane semesterpane, subjectpane, coursepane;
	private JTextField searchbox;
	private JButton submitsearch;
	private GridBagLayout layoutmanager;
	private GridBagConstraints layoutconstraints;
	private boolean updatesemesters, updatesubjects, updatecourses, listenersdisabled;
	
	public QueryPanel(CUTTSController _manager) {
		super(_manager);

		updatesemesters = true;
		updatesubjects = true;
		updatecourses = true;
		listenersdisabled = false;

		layoutmanager = new GridBagLayout();
		layoutconstraints = new GridBagConstraints();
		setLayout(layoutmanager);

		layoutconstraints.anchor = GridBagConstraints.NORTHWEST;
		layoutconstraints.fill = GridBagConstraints.BOTH;
		layoutconstraints.insets = new Insets(2,2,2,2);

		//SEMESTER LIST
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 0;
		layoutconstraints.gridwidth = 2;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 1;
		semesters = new JList<Semester>();
		semesterpane = new JScrollPane(semesters);
		semesters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layoutmanager.addLayoutComponent(semesterpane, layoutconstraints);
		add(semesterpane);

		//SUBJECT LIST
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 1;
		layoutconstraints.gridwidth = 2;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 2;
		subjects = new JList<Subject>();
		subjectpane = new JScrollPane(subjects);
		subjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layoutmanager.addLayoutComponent(subjectpane, layoutconstraints);
		add(subjectpane);

		//COURSE LIST
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 2;
		layoutconstraints.gridwidth = 2;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 3;
		courses = new JList<Course>();
		coursepane = new JScrollPane(courses);
		courses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layoutmanager.addLayoutComponent(coursepane, layoutconstraints);
		add(coursepane);

		//SEARCH BOX
		layoutconstraints.gridx = 0;
		layoutconstraints.gridy = 3;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 1;
		layoutconstraints.weighty = 0;
		searchbox = new JTextField("Search for a course");
		layoutmanager.addLayoutComponent(searchbox, layoutconstraints);
		add(searchbox);

		//SUBMIT SEARCH
		layoutconstraints.gridx = 1;
		layoutconstraints.gridy = 3;
		layoutconstraints.gridwidth = 1;
		layoutconstraints.gridheight = 1;
		layoutconstraints.weightx = 0;
		layoutconstraints.weighty = 0;
		submitsearch = new JButton("Search");
		layoutmanager.addLayoutComponent(submitsearch, layoutconstraints);
		add(submitsearch);

		//ADD LISTSELECTIONLISTENER TO SEMESTER LIST
		semesters.addListSelectionListener( new ListSelectionListener() {
			/*
			 * When semester value changes, flag subject and course lists for update
			 * and update the mediator's selected semester
			*/
			public void valueChanged(ListSelectionEvent e) {
				if (listenersdisabled)
					return;

				if( e.getValueIsAdjusting() )
					return;

				Semester semester = (Semester)((JList<?>)e.getSource()).getSelectedValue();
				updatesubjects = true;
				updatecourses = true;

				getController().navigateSemester(semester);
			}
		});

		//ADD LISTSELECTIONLISTENER TO SUBJECT LIST
		subjects.addListSelectionListener(new ListSelectionListener() {
			/*
			 * When subject value changes, flag course list for update
			 * and update the mediator's selected subject
			 */
			public void valueChanged(ListSelectionEvent e) {
				if (listenersdisabled)
					return;

				if ( e.getValueIsAdjusting() )
					return;

				Subject subject = (Subject)((JList<?>)e.getSource()).getSelectedValue();
				updatecourses = true;

				getController().navigateSubject(subject);
			}
		});

		//ADD LISTSELECTIONLISTENER TO COURSE LIST
		courses.addListSelectionListener( new ListSelectionListener() {
			/*
			 * When course value changes, update the mediator's selected course
			 */
			public void valueChanged(ListSelectionEvent e){
				if (listenersdisabled)
					return;

				if ( e.getValueIsAdjusting() )
					return;

				Course course = (Course)( (JList<?>)e.getSource() ).getSelectedValue();

				getController().navigateCourse(course);
			}
		});

		//ADD MOUSELISTENER TO COURSE LIST
		courses.addMouseListener( new MouseAdapter() {
			/*
			 * When course value is double-clicked, register the course
			 */
			public void mouseClicked(MouseEvent e) {
				if (listenersdisabled || e.getClickCount() != 2)
					return;

				Course course = (Course)( (JList<?>)e.getSource() ).getSelectedValue();

				if(! getController().registerTimetableBlock(course) )
					CUTTSui.warningMessage( SymbolMap.lookupSymbol( "CUTTS_REGISTRATION_CONFLICT", course.getName() ) );
			}
		});

		//ADD ACTIONLISTENER TO SEARCHBOX
		searchbox.addActionListener( new ActionListener() {
			//on event, simulate a click on the submit button
			public void actionPerformed(ActionEvent e) {
				submitsearch.doClick();
			}
		});

		//ADD ACTIONLISTENER TO SUBMITSEARCH
		submitsearch.addActionListener( new ActionListener() {
			/*
			 * on event, process the searchbox's text and simulate selecting
			 * those values in their respective lists
			 */
			public void actionPerformed(ActionEvent e) {
				String[] searchparams;
				String subjectid;
				int courseno;

				//trim surrounding whitespace and make uppercase
				String searchstring = searchbox.getText().toUpperCase().trim();

				//validate the search
				if ( searchstring == null || ! searchstring.matches("^[A-Z]{3,4}( \\d{4})?$") ) {
					CUTTSui.warningMessage( SymbolMap.lookupSymbol("CUTTS_INVALID_COURSE_CODE") );
					return;
				}
				else {
					//process the search
					searchparams = searchstring.split(" ");
					subjectid = searchparams[0];
					if (searchparams.length == 2)
						courseno = Integer.parseInt( searchparams[1] );
					else
						courseno = -1;

					//if no semester is selected, warn and return
					if (getController().getNavigationModelData().getSelectedSemester() == null){
						 CUTTSui.warningMessage( SymbolMap.lookupSymbol("CUTTS_SEMETER_NOT_SELECTED") );
						 return;
					 }

					/*
					 * If no subject is selected or selected subject is not the one specified in search,
					 * attempt to select the specified subject
					 */
					if ( getController().getNavigationModelData().getSelectedSubject() == null || ! getController().getNavigationModelData().getSelectedSubject().getId().equals(subjectid) ) {
						int si;
						//iterate subject list to obtain the respective subject object for search subject
						for (si = 0; si < subjects.getModel().getSize(); si++) {
							if ( ( (Subject)subjects.getModel().getElementAt(si) ).getId().equals(subjectid) )
								break;
						}
						//if found the subject, simulate selecting it
						if ( si < subjects.getModel().getSize() ) {
							subjects.setSelectedIndex(si);
							subjects.ensureIndexIsVisible(si);
						}
						else {
							CUTTSui.warningMessage( SymbolMap.lookupSymbol("CUTTS_SUBJECT_NOT_FOUND", subjectid) );
							return;
						}
					}

					//if no course was specified, done
					if(courseno == -1)
						return;

					/*
					 * If no course is selected or selected course is not the one specified in search,
					 * attempt to select the specified course
					 */
					if(getController().getNavigationModelData().getSelectedCourse() == null || getController().getNavigationModelData().getSelectedCourse().getNumber() != courseno) {
						int ci;
						//iterate course list to obtain the respective course object for search course
						for (ci = 0; ci < courses.getModel().getSize(); ci++) {
							if ( ( (Course)courses.getModel().getElementAt(ci) ).getNumber() == courseno)
								break;
						}
						//if found the course, simulate selecting it
						if ( ci < courses.getModel().getSize() ){
							courses.setSelectedIndex(ci);
							courses.ensureIndexIsVisible(ci);
						}
						else {
							CUTTSui.warningMessage( SymbolMap.lookupSymbol("CUTTS_COURSE_NOT_FOUND", subjectid + " " + courseno) );
							return;
						}
					}
				}
			}
		});
	}

	public void update(){
		listenersdisabled = true;

		//update semester list if needed
		if (updatesemesters) {
			DefaultListModel<Semester> m = new DefaultListModel<Semester>();
			for ( Semester semester : getController().getNavigationModelData().getDisplayedSemesters().subList(0, SEMESTER_LIMIT) )
				m.addElement(semester);

			semesters.setModel(m);
		}
		updatesemesters = false;

		//update subject list if needed
		if (updatesubjects) {
			DefaultListModel<Subject> m = new DefaultListModel<Subject>();
			for( Subject subject : getController().getNavigationModelData().getDisplayedSubjects() )
				m.addElement(subject);

			subjects.setModel(m);
		}
		updatesubjects = false;

		//update course list if needed
		if (updatecourses) {
			DefaultListModel<Course> m = new DefaultListModel<Course>();
			for( Course course : getController().getNavigationModelData().getDisplayedCourses() )
				m.addElement(course);

			courses.setModel(m);
		}
		updatecourses = false;

		listenersdisabled = false;
	}
}