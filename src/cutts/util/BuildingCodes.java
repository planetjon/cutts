package cutts.util;

import java.util.*;

import static planetjon.espresso4j.Constructs.*;

/**
 * This class is a data container for the Carleton building codes.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class BuildingCodes {
	public static final Map<String, String> codemap;

	static{
		codemap = map();

		codemap.put("architecture building", "AA");
		codemap.put("alumni hall and sports centre", "AH");
		codemap.put("azrieli pavilion", "AP");
		codemap.put("azrieli theatre", "AT");
		codemap.put("colonel by child care centre", "CC");
		codemap.put("residence commons", "CO");
		codemap.put("university commons", "CO");
		codemap.put("dundas house", "DH");
		codemap.put("dunton tower", "DT");
		codemap.put("fieldhouse", "FH");
		codemap.put("glengarry house", "GH");
		codemap.put("grenville house", "GR");
		codemap.put("gymnasium", "GY");
		codemap.put("human computer interaction building", "HC");
		codemap.put("herzberg laboratories", "HP");
		codemap.put("herzberg labs for physics", "HP");
		codemap.put("ice house", "IH");
		codemap.put("loeb building", "LA");
		codemap.put("leeds house", "LE");
		codemap.put("life sciences research building", "LH");
		codemap.put("maintenance building", "MB");
		codemap.put("minto centre", "MC");
		codemap.put("mackenzie building", "ME");
		codemap.put("macodrum library", "ML");
		codemap.put("nesbitt biology building", "NB");
		codemap.put("national wildlife research centre", "NW");
		codemap.put("paterson hall", "PA");
		codemap.put("parking garage", "PG");
		codemap.put("physical recreational centre", "AC");
		codemap.put("prescott house", "PH");
		codemap.put("renfrew house", "RH");
		codemap.put("robertson hall", "RO");
		codemap.put("russell house", "RU");
		codemap.put("russell house", "RU");
		codemap.put("southam hall", "SA");
		codemap.put("steacie building", "SC");
		codemap.put("stormont house", "SH");
		codemap.put("st. patrick's building", "SP");
		codemap.put("social sciences research building", "SR");
		codemap.put("tory building", "TB");
		codemap.put("tennis centre", "TC");
		codemap.put("carleton technology and training centre", "TT");
		codemap.put("university centre", "UC");
		codemap.put("university gymnasium", "GY");
		codemap.put("visualization and simulation centre", "VS");
		codemap.put("carleton university television cutv", "");
	}

	/**
	 * Abbreviates the building name
	 * 
	 * @param buildingname
	 * @return
	 */
	public static String abbreviateLocation(String buildingname) {
		buildingname = buildingname.toLowerCase().trim();
		String abbreviation = codemap.get(buildingname);
		if (abbreviation == null)
			abbreviation = "???";
		
		return abbreviation;
	}
}
