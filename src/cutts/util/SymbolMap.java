package cutts.util;

import java.net.URI;
import java.util.Properties;

/**
 * A Java Properties XML reader intended to be used for mapping keys to values.
 * See java.util.Properties for details.
 * 
 * @see java.util.Properties
 * @author Jonathan Weatherhead
 *
 */
public class SymbolMap {
	private static SymbolMap symbolmap;
	private	Properties symbolcache;

	private SymbolMap(String path) {
		symbolcache = new Properties();

		try {
			symbolcache.loadFromXML( getClass().getResource(path).openStream() );
		}
		catch (Exception e) {
		}
	}

	/**
	 * Loads an XML symbol map
	 * 
	 * @param symbolpath The path to the XML symbol map
	 */
	public static void loadSymbols(String symbolpath) {
		if (SymbolMap.symbolmap == null)
			SymbolMap.symbolmap = new SymbolMap(symbolpath);
	}

	/**
	 * Looks up a symbol in the map. Returns empty String if the map has not been preppred
	 * or the symbol cannot be found.
	 * 
	 * @param symbol The symbol to be looked up in the map
	 * @param variables Optional parameters to be interpolated if the string has interpolation characters
	 * @return
	 */
	public static String lookupSymbol(String symbol, String... variables) {
		if(SymbolMap.symbolmap == null)
			return "";

		String message = symbolmap.symbolcache.getProperty(symbol);
		if(message == null)
			return "";

		return String.format(message, (Object)variables);
	}
}
