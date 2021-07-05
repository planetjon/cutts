package cutts.util;

import java.util.*;

import cutts.model.*;
import cutts.model.courses.Course;
import cutts.query.*;

/**
 * This class provides caching for the extended course information functionality
 * with the intent of improving the user experience by reducing loading time.
 * Cache is implemented with the clock algorithm.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class TimetableBlockDescriptionCache {
	private final int CACHE_SIZE = 12;

	private List<Course> courses;
	private List<String> extendedinfo;
	private List<Boolean> chances;
	private int cycler;

	public TimetableBlockDescriptionCache() {
		courses = new ArrayList<Course>(CACHE_SIZE);
		extendedinfo = new ArrayList<String>(CACHE_SIZE);
		chances = new ArrayList<Boolean>(CACHE_SIZE);

		//prep cache
		for (int i = 0; i < CACHE_SIZE; i++) {
			courses.add(null);
			extendedinfo.add(null);
			chances.add(false);
		}
	}

	/**
	 * Returns extended information for the course.
	 * 
	 * @param block The course to be queried
	 * @return
	 */
	public String getExtendedInfo(TimetableBlock block) {
		if (! (block instanceof Course) )
			return "";

		int index = 0;
		String info = null;

		//if cached, use the cache
		if( ( index = courses.indexOf(block) ) != -1 ) {
			chances.set(index, true);
			return extendedinfo.get(index);
		}
		//otherwise, query for the information and cache it
		else {
			info = CarletonCentralQuerier.queryExtendedCourseInfo( (Course)block );
			updateExtendedInfoCache( (Course)block, info);

			return info;
		}
	}

	/**
	 * Adds information to the cache.
	 * 
	 * @param course
	 * @param info
	 */
	private void updateExtendedInfoCache(Course course, String info){
		//don't cache nothing
		if(info == null)
			return;

		//find out whom to boot
		while( chances.get(cycler % CACHE_SIZE) != false) {
			chances.set(cycler % CACHE_SIZE, false);
			cycler++;
		}
		cycler %= CACHE_SIZE;

		//boot victim and add new element
		courses.set(cycler, course);
		extendedinfo.set(cycler, info);
		chances.set(cycler, true);

		cycler++;
	}
}
