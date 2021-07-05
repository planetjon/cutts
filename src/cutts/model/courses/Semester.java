package cutts.model.courses;

import java.io.*;

/**
 * This class is a data container for relevant semester information
 * 
 * @author Jonathan Weatherhead
 *
 */
public class Semester implements Serializable {
	private String name, id;

	public Semester(String _name, String _id) {
		name = _name;
		id = _id;
	}

	/**
	 * Returns the descriptive name of the semester.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the id of the semester.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public boolean equals(Object o) {
		if(o instanceof Semester)
			return id.equals( ( (Semester) o).getId() );
			
		return false;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return getName();
	}

}
