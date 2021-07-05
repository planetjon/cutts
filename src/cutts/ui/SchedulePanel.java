package cutts.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import cutts.controller.CUTTSController;
import cutts.model.TimetableBlock;
import cutts.model.courses.Course;
import cutts.model.courses.MeetingInformation;
import cutts.model.courses.Semester;
import cutts.util.BuildingCodes;
import cutts.util.CalendarConstants;
import cutts.util.TimeManipulation;

/**
 * This class sets up and displays a timetable UI.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class SchedulePanel extends UIPanel implements MouseListener, MouseMotionListener {
	private final int CELL_WIDTH = 111;
	private final int CELL_HEIGHT = 30;
	private final int HEADER_OFFSET = 20;
	private final int TICK_OFFSET = 60;

	private final int CLASS_END_OFFSET = 10;
	private final int CLASS_TIME_UNIT = 30;

	private int WIDTH;
	private int HEIGHT;

	private int SCHEDULE_START_TIME;
	private int SCHEDULE_END_TIME;

	final private Color[] COLOURS;

	private Map<Rectangle, TimetableBlock> rectmap;
	private TimetableBlock mousehoverblock;

	private int coursecount;
	private final Semester SEMESTER;

	public SchedulePanel(CUTTSController _manager, Semester _semester){
		super(_manager);

		coursecount = 0;

		SEMESTER = _semester;
		rectmap = new HashMap<Rectangle, TimetableBlock>();
		mousehoverblock = null;

		WIDTH = CELL_WIDTH * 7 + TICK_OFFSET;
		HEIGHT = HEADER_OFFSET;

		SCHEDULE_START_TIME = 0;
		SCHEDULE_END_TIME = 24 * 60;

		COLOURS = new Color[]{
				new Color(203, 235, 74),
				new Color(69, 219, 95),
				new Color(198, 73, 65),
				new Color(81, 244, 219),
				new Color(252, 101, 97),
				new Color(255, 82, 76),
				new Color(114, 77, 229),
				new Color(199, 248, 222),
				new Color(88, 192, 243),
				new Color(82, 83, 67),
				new Color(221, 87, 105),
				new Color(203, 233, 91),
				new Color(238, 106, 211),
				new Color(213, 225, 72),
				new Color(203, 215, 212),
				new Color(248, 241, 85),
				new Color(81, 251, 65),
				new Color(119, 77, 240),
				new Color(220, 127, 115),
				new Color(233, 97, 222),
				new Color(210, 121, 192),
				new Color(241, 83, 103),
				new Color(243, 194, 204),
				new Color(211, 121, 193),
				new Color(116, 96, 232),
				new Color(237, 97, 109),
				new Color(240, 214, 111),
				new Color(250, 244, 216),
				new Color(118, 209, 254),
				new Color(254, 85, 113),
				new Color(71, 99, 73),
				new Color(86, 99, 192)
		};

		addMouseListener(this);
		addMouseMotionListener(this);

		update();
	}

	public void update() {
		//if no courses registered, nothing to show
		if (getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER) == null)
			return;

		//if there was a change in number of registered courses
		if (getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER).size() != coursecount) {
			coursecount = getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER).size();
			resizeAsNecessary();
			prepRectMap();
			repaint();
		}
	}

	/**
	 * Computes and sets the size of the component necessary to fit all the data
	 */
	private void resizeAsNecessary() {
		int earliest = 60 * 24;
		int latest = 0;
		int starttime, endtime;

		//find the earliest start time and latest end time
		for ( TimetableBlock block : getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER) ) {
			for ( MeetingInformation meeting : block.getMeetings() ) {
				//normalize the limit times to the half-hour
				starttime = meeting.starttime;
				endtime = meeting.endtime + CLASS_END_OFFSET;
				starttime -= starttime % CLASS_TIME_UNIT;
				endtime -= endtime % CLASS_TIME_UNIT;

				if(starttime < earliest)
					earliest = starttime;

				if(endtime > latest )
					latest = endtime;
			}
		}

		SCHEDULE_START_TIME = earliest;
		SCHEDULE_END_TIME = latest;

		HEIGHT = (SCHEDULE_END_TIME - SCHEDULE_START_TIME) / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET + CELL_HEIGHT;
		if(HEIGHT <= 0)
			HEIGHT = HEADER_OFFSET + CELL_HEIGHT;

		setMinimumSize( new Dimension(WIDTH, HEIGHT) );
		setMaximumSize( new Dimension(WIDTH, HEIGHT) );
		setPreferredSize( new Dimension(WIDTH, HEIGHT) );
		revalidate();
	}

	/**
	 * Builds a map of rectangles in real space to courses.
	 * Rectangles will be used for visual rendering and the map
	 * will be used to back the mouse events.
	 */
	private void prepRectMap() {
		int starttime, endtime;

		rectmap.clear();

		for ( TimetableBlock block : getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER) ) {
			for ( MeetingInformation meeting :  block.getMeetings() ) {
				int x, y, width, height;

				//normalize the limit times to the half-hour
				starttime = meeting.starttime;
				endtime = meeting.endtime + CLASS_END_OFFSET;
				starttime -= starttime % CLASS_TIME_UNIT;
				endtime -= endtime % CLASS_TIME_UNIT;
			
				x = CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET;
				y = starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME;
				width = CELL_WIDTH;
				height = endtime - starttime;

				rectmap.put(new Rectangle(x, y, width, height), block);
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int starttime, endtime;

		//clear area
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		//draw days of the week header
		g.setColor(Color.BLACK);
		g.setFont( new Font("sansserif", Font.BOLD, 12) );
		for(int j = 0; j < CalendarConstants.DAYS_OF_THE_WEEK.length; j++) {
			g.drawString(CalendarConstants.DAYS_OF_THE_WEEK[j], j * CELL_WIDTH + TICK_OFFSET + 5, HEADER_OFFSET - 5);
		}

		//draw vertical lines
		g.setColor(Color.DARK_GRAY);
		for (int j = 0; j <= 7; j++)
			g.drawLine(j * CELL_WIDTH + TICK_OFFSET, 0, j * CELL_WIDTH + TICK_OFFSET, HEIGHT);

		if(SCHEDULE_END_TIME < SCHEDULE_START_TIME)
			return;

		//draw time ticks
		for(int j = 0; j <= (SCHEDULE_END_TIME - SCHEDULE_START_TIME) / CLASS_TIME_UNIT; j++)
			g.drawString(TimeManipulation.timeToString(j * CLASS_TIME_UNIT + SCHEDULE_START_TIME), 2, j * CELL_HEIGHT + HEADER_OFFSET + 12);

		//draw horizontal lines
		for(int j = 0; j <= (SCHEDULE_END_TIME - SCHEDULE_START_TIME) / CLASS_TIME_UNIT; j++) {
			if(j % 2 == 1)
				g.setColor(Color.GRAY);
			else
				g.setColor(Color.DARK_GRAY);

			g.drawLine(0, j * CELL_HEIGHT + HEADER_OFFSET, WIDTH, j * CELL_HEIGHT + HEADER_OFFSET);
		}

		//draw courses
		int colourindex = 0;
		for ( TimetableBlock block : getController().getTimetableModelData().getRegisteredTimetableBlocksForSemester(SEMESTER) ) {
			for ( MeetingInformation meeting : block.getMeetings() ) {
				Course c;
				if(block instanceof Course)
					c = (Course)block;
				else
					continue;
				//normalize the limit times to the half-hour
				starttime = meeting.starttime;
				endtime = meeting.endtime + CLASS_END_OFFSET;
				starttime -= starttime % CLASS_TIME_UNIT;
				endtime -= endtime % CLASS_TIME_UNIT;

				if (c.equals(mousehoverblock) )
					g.setColor( COLOURS[colourindex].brighter().brighter().brighter() );
				else
					g.setColor( COLOURS[colourindex] );

				g.fillRect(CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET, starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME, CELL_WIDTH, endtime - starttime);
				g.setColor(Color.black);
				g.drawRect(CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET, starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME, CELL_WIDTH, endtime - starttime);
				g.drawString(c.getId() + " " + c.getSection(), CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET + 3, starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME + 15);
				g.drawString(TimeManipulation.timeToString(meeting.starttime) + "-" + TimeManipulation.timeToString(meeting.endtime), CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET + 3, starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME + 30);
				g.drawString(BuildingCodes.abbreviateLocation(meeting.buildingname) + " " + meeting.roomnumber, CELL_WIDTH * TimeManipulation.dayAsInt(meeting.day) + TICK_OFFSET + 3, starttime / CLASS_TIME_UNIT * CELL_HEIGHT + HEADER_OFFSET - SCHEDULE_START_TIME + 45);
			}
			colourindex++;
		}
	}

	public void mouseClicked(MouseEvent e) {
		for ( Rectangle rect: rectmap.keySet() ) {
			if ( rect.contains( e.getX(),  e.getY() ) ) {
				getController().selectTimetableBLock( rectmap.get(rect) );
				break;
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {
		mousehoverblock = null;

		for ( Rectangle rect : rectmap.keySet() ) {
			if ( rect.contains( e.getX(),  e.getY() ) ) {
				mousehoverblock = rectmap.get(rect);
				break;
			}
		}
		repaint();
	}
}
