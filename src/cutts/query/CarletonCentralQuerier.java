package cutts.query;

import java.util.List;

import planetjon.espresso4j.*;
import static planetjon.espresso4j.Constructs.*;

import cutts.io.HttpRequest;
import cutts.model.courses.Course;
import cutts.model.courses.MeetingInformation;
import cutts.model.courses.Semester;
import cutts.model.courses.Subject;
import cutts.ui.CUTTSui;
import cutts.util.CalendarConstants.DayNames;
import cutts.util.SymbolMap;
import cutts.util.TagParser;

/**
 * Utility class that queries and extracts timetable information from Carleton Central
 * 
 * @author Jonathan Weatherhead
 *
 */
public class CarletonCentralQuerier {
	private static final String HOST = "http://central.carleton.ca/prod/";
	private static final String SEMESTER_SEARCH = "bwckschd.p_disp_dyn_sched";
	private static final String SUBJECT_SEARCH = "bwckgens.p_proc_term_date";
	private static final String COURSE_SEARCH = "bwckschd.p_get_crse_unsec";
	private static final String INFO_SEARCH = "bwckschd.p_disp_detail_sched";

	private static List<Pair<String, String>> parameters = list();

	private static final int MAX_CLASSES_PER_WEEK	= 99;

	/**
	 * Retrieves available semesters and updates the query handler.
	 * 
	 * @param qh
	 */
	public static void queryAvailableSemesters(NavigationQueryHandler qh) {
		List<Semester> semesters = list();
		String content;
		String id, name;
		int tracker;
		
		tracker = 0;
		parameters.clear();

		//make request
		content = getContentString(HOST + SEMESTER_SEARCH, parameters);

		//if content is null, return
		if(content == null)
			return;

		try{
			//extract available semesters
			TagParser tp = new TagParser(content);

			//move to selection box that holds semester data
			tp.nextTagOfType("form");
			tp.nextTagOfType("select");
			content = tp.getTagContent();

			//skip first 2 lines
			tracker = content.indexOf("\n", tracker) + 1;
			tracker = content.indexOf("\n", tracker) + 1;
	
			//read in each line and parse
			while (content.indexOf("\n", tracker) != -1) {
				id = content.substring(tracker + 15, tracker + 21);
				name = content.substring( tracker + 23, content.indexOf('(', tracker) - 1 );
				semesters.add( new Semester(name, id) );
	
				tracker = content.indexOf("\n", tracker) + 1;
			}
	
			//Update queryhandler's semester map
			qh.updateDisplayedSemesters(semesters);
		}
		catch (Exception e) {
			CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_QUERY_FAILURE") );
			return;
		}
	}

	/**
	 * Retrieves available semester information and updates the query handler
	 * 
	 * @param qh
	 * @param semester
	 */
	public static void queryAvailableSubjects(NavigationQueryHandler qh, Semester semester) {
		List<Subject> subjects= list();
		String content;
		String id, name;
		int tracker;
		
		tracker = 0;
		parameters.clear();

		//set up needed information to request available subjects
		parameters.add( pair( "p_term", semester.getId() ) );
		parameters.add( pair("p_calling_proc", "bwckschd.p_disp_dyn_sched") );

		//make request
		content = getContentString(HOST + SUBJECT_SEARCH, parameters);

		//if content is null, return
		if(content == null) return;

		try{
			//extract available subjects
			TagParser tp = new TagParser(content);

			//move to selection box that holds subject data
			tp.nextTagOfType("form");
			tp.nextTagOfType("select");
			content = tp.getTagContent();

			//skip first line
			tracker = content.indexOf("\n", tracker) + 1;
	
			//read in each line and parse
			while (content.indexOf("\n", tracker) != -1) {
				id = content.substring(tracker + 15, content.indexOf('"', tracker + 15) );
				name = content.substring( content.indexOf('"', tracker + 15) + 2, content.indexOf("\n", tracker) );
				subjects.add( new Subject(name, id) );
	
				tracker = content.indexOf("\n", tracker) + 1;
			}
	
			//Update queryhandler's subject map
			qh.updateDisplayedSubjects(subjects);
		}
		catch (Exception e) {
			CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_QUERY_FAILURE") );
			return;
		}
	}

	/**
	 * Retrieves available course information and updates the query handler.
	 * 
	 * @param qh
	 * @param semester
	 * @param subject
	 */
	public static void queryAvailableCourses(NavigationQueryHandler qh, Semester semester, Subject subject) {
		List<Course> courses = list();

		String coursecode, coursename, coursecrn, coursesection;
		String professors;

		short classesperweek;
		String[] locations;
		DayNames[] days;
		int[] starttimes, endtimes;

		String content;
		String temp;
		String[] temparray;
		int tracker;
		
		tracker = 0;
		parameters.clear();

		//set up needed information to request available courses
		parameters.add( pair("term_in", semester.getId() ) );		//TERM
		parameters.add( pair("sel_subj", "dummy") );		//SUBJECT
		parameters.add( pair("sel_day", "dummy") );		//DAY OF THE WEEK
		parameters.add( pair("sel_schd", "dummy") );		//SCHEDULE TYPE
		parameters.add( pair("sel_insm", "dummy") );		//???
		parameters.add( pair("sel_camp", "dummy") );		//???
		parameters.add( pair("sel_levl", "dummy") );		//COURSE LEVEL
		parameters.add( pair("sel_sess", "dummy") );		//???
		parameters.add( pair("sel_instr", "dummy") );		//INSTRUCTOR
		parameters.add( pair("sel_ptrm", "dummy") );		//???
		parameters.add( pair("sel_attr", "dummy") );		//???

		parameters.add( pair("sel_subj", subject.getId() ) );	//SUBJECT
		parameters.add( pair("sel_crse", "") );			//COURSE NUMBER
		parameters.add( pair("sel_title", "") );			//COURSE TITLE
		parameters.add( pair("sel_schd", "%25") );			//SCHEDULE TYPE
		parameters.add( pair("sel_from_cred", "") );		//CREDIT RANGE MIN
		parameters.add( pair("sel_to_cred", "") );			//CREDIT RANGE MAX
		parameters.add( pair("sel_levl", "%25") );			//COURSE LEVEL
		parameters.add( pair("sel_instr", "%25") );		//INSTRUCTOR
		parameters.add( pair("begin_hh", "0") );			//COURSE HOUR START
		parameters.add( pair("begin_mi", "0") );			//COURSE MIN START
		parameters.add( pair("begin_ap", "a") );			//COURSE AM/PM START
		parameters.add( pair("end_hh", "0") );				//COURSE HOUR END
		parameters.add( pair("end_mi", "0") );				//COURSE MINUTE END
		parameters.add( pair("end_ap", "a") );				//COURSE AM/PM END

		//make request
		content = getContentString(HOST + COURSE_SEARCH, parameters);

		//if content is null, return
		if(content == null)
			return;

		try {
			//extract available courses
			TagParser tp = new TagParser(content);
	
			//move to table that holds course data
			tp.nextTagOfType("div");
			tp.nextPeerTag(false);
			tp.nextPeerTag(false);
			tp.nextTagOfType("table");
			tp.nextTagOfType("table");
			tp.nextTag();

			//while there are fields, read and process
			while ( tp.nextTag().equals("tr") ) {

				classesperweek = 0;
				locations = new String[MAX_CLASSES_PER_WEEK];
				days = new DayNames[MAX_CLASSES_PER_WEEK];
				starttimes = new int[MAX_CLASSES_PER_WEEK];
				endtimes = new int[MAX_CLASSES_PER_WEEK];

				//arraylist to keep store names of profs. lame solution
				List<String> profs = list();

				//to please java
				professors = "";

				//extract course name & number & section
				tp.nextTagOfType("a");
				temp = tp.getTagContent();

				temparray = temp.split(" - ");

				coursename = temparray[0].trim();
				coursecrn = temparray[1];
				coursecode = temparray[2];
				coursesection = temparray[3];

				//extract schedule info
				tp.nextTagOfType("table");

				//skip data headers
				tp.nextTagOfType("tr");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");
				tp.nextTagOfType("th");

				//parse data from each table row
				while ( tp.nextTag().equals("tr") ) {

					//move to class type
					tp.nextTagOfType("td");

					//extract start & end time
					int starttime, endtime;
					tp.nextTagOfType("td");
					temp = tp.getTagContent();

					//if TBA, set to -1
					if (temp.charAt(0) == '<') {
						starttime = endtime = -1;
					}
					//otherwise, process the start and end times
					else{
						temparray = temp.split(" - ");
						starttime = (Integer.parseInt( temparray[0].substring(0, temparray[0].indexOf(':') ) ) % 12) * 60
									+ Integer.parseInt( temparray[0].substring(temparray[0].indexOf(':') + 1,  temparray[0].indexOf(' ') ) )
									+ ( temparray[0].substring( temparray[0].indexOf(' ') + 1).equals("pm") ? 12*60 : 0 );
	
						endtime = (Integer.parseInt( temparray[1].substring(0, temparray[1].indexOf(':') ) ) % 12) * 60
						+ Integer.parseInt( temparray[1].substring(temparray[1].indexOf(':') + 1,  temparray[1].indexOf(' ') ) )
						+ ( temparray[1].substring( temparray[1].indexOf(' ') + 1).equals("pm") ? 12*60 : 0 );
					}

					//extract days
					String daystr;
					tp.nextTagOfType("td");
					daystr = tp.getTagContent();
					//if &nbsp; , set to ""
					if (daystr.charAt(0) == '&')
						daystr = "";

					//extract location
					tp.nextTagOfType("td");
					temp = tp.getTagContent();
					//if TBA, set to TBA
					if (temp.charAt(0) == '<')
						temp = "TBA";


					//set days, start times, end times, and location
					for (tracker = 0; tracker < daystr.length(); tracker++, classesperweek++) {
						starttimes[classesperweek] = starttime;
						endtimes[classesperweek] = endtime;
						locations[classesperweek] = temp;

						//process day of the week
						switch ( daystr.charAt(tracker) ) {
						case 'M':
							days[classesperweek] = DayNames.MONDAY;
							break;
						case 'T':
							days[classesperweek] = DayNames.TUESDAY;
							break;
						case 'W':
							days[classesperweek] = DayNames.WEDNESDAY;
							break;
						case 'R':
							days[classesperweek] = DayNames.THURSDAY;
							break;
						case 'F':
							days[classesperweek] = DayNames.FRIDAY;
							break;
						case 'S':
							days[classesperweek] = DayNames.SATURDAY;
							break;
						case 'U':
							days[classesperweek] = DayNames.SUNDAY;
							break;
						}
					}

					//move to date range
					tp.nextTagOfType("td");

					//move to type
					tp.nextTagOfType("td");

					//extract prof and add to list if not already there
					tp.nextTagOfType("td");
					temparray = tp.getTagContent().split(",");
					for (tracker = 0; tracker < temparray.length; tracker++) {
						temp = temparray[tracker];
						temp = temp.trim();
						temp = temp.indexOf('(') != -1 ? temp.substring(0, temp.indexOf('(') - 1) : temp.substring(0);
						//if TBA
						if (temp.charAt(0) == '<')
							temp = "TBA";
						if (! profs.contains(temp) )
							profs.add(temp);
					}
				}

				//burn tags before next tr
				while( tp.peekAtNextTag().equals("br") ) tp.nextTag();

				//build prof string
				for (String s : profs) {
					professors += s;
					professors += ", ";
				}
				professors = professors.substring(0, professors.length() - 2);

				//add course
				List<MeetingInformation> meetings = list();
				for (int i = 0; i < classesperweek; i++) {
					meetings.add( new MeetingInformation(locations[i], days[i], starttimes[i], endtimes[i]) );
				}
				Course c = new Course(semester, subject, coursecode, coursesection, coursecrn, coursename, professors, meetings);
				courses.add(c);
			}

			//Update queryhandler's course list
			qh.updateDisplayedCourses(courses);
		}
		catch (Exception e) {
			CUTTSui.errorMessage(SymbolMap.lookupSymbol("CUTTS_QUERY_FAILURE"));
			return;
		}
	}

	/**
	 * Retrieves and returns detailed information regarding the course.
	 * 
	 * @param course
	 * @return
	 */
	public static String queryExtendedCourseInfo(Course course) {
		String content;

		parameters.clear();

		//make request
		String url = HOST + INFO_SEARCH + "?term_in=" + course.getSemester().getId() + "&crn_in=" + course.getCRN();
		content = getContentString(url, parameters);

		//if content is null, return empty string
		if (content == null) {
			return "No additional information available";
		}

		try {
			//extract available semesters
			TagParser tp = new TagParser(content);

			//move to table field that contains data
			tp.nextTagOfType("div");
			tp.nextPeerTag(false);
			tp.nextPeerTag(false);
			tp.nextTagOfType("table");
			tp.nextTagOfType("tr");
			tp.nextTagOfType("tr");
			tp.nextTagOfType("td");

			//extract raw semester data
			content = tp.getTagContent();

			//srip html tags and tidy info
			content = content.replaceAll("<[^>]+>", "");
			content = content.replace("&nbsp;", " ");
			content = content.replace("\n  ", "\n");

			return content;
		}
		catch(Exception e) {
			CUTTSui.errorMessage( SymbolMap.lookupSymbol("CUTTS_QUERY_FAILURE") );
			return null;
		}
	}

	/**
	 * Makes an HTTP request and returns the response.
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 */
	private static String getContentString(String url, List<Pair<String, String>> parameters) {
		String content;
		try {
			content = HttpRequest.post(url, null, parameters);
		}
		catch (Exception e) {
			CUTTSui.errorMessage(SymbolMap.lookupSymbol("CUTTS_HTTP_ERROR"));
			return null;
		}
		return content;
	}
}
