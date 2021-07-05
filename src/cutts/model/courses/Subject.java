package cutts.model.courses;

import java.io.*;

/**
 * This class is a data container for relevant subject information
 * 
 * @author Jonathan Weatherhead
 *
 */
public class Subject implements Serializable {
	private String name;
	private String id;

	public Subject(String _name, String _id) {
		name = _name;
		id = _id;
	}

	/**
	 * Returns the descriptive name of the subject.
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
		if(o instanceof Subject)
			return id.equals( ( (Subject) o).getId() );
			
		return false;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return getName();
	}
}
