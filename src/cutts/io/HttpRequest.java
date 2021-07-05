package cutts.io;

import java.net.*;
import java.io.*;
import java.util.*;

import planetjon.espresso4j.*;

/**
 * This class offers HTTP request functionality.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class HttpRequest {

	/**
	 * HTTP GET operation
	 * 
	 * @param url
	 * @param cookies
	 * @param vars
	 * @return
	 * @throws IOException.
	 */
	static public String get(String url, List<Pair<String, String>> cookies, List<Pair<String, String>> vars) throws IOException {
		StringBuffer urlstring = new StringBuffer(url);

		//if vars, append them to the url string
		if ( vars != null && ! vars.isEmpty() ) {
			urlstring.append("?");
			for (Pair<String, String> p : vars) {
				urlstring.append(p.head + "=" + p.tail);
				urlstring.append("&");
			}
			urlstring.setLength(urlstring.length() - 1);
		}

		//setup connection
		URLConnection connection = new URL( urlstring.toString() ).openConnection();

		//if cookies, add them to the request headers
		if( cookies != null && ! cookies.isEmpty() ) {
			StringBuffer cookielist = new StringBuffer();
			for (Pair<String, String> p : cookies) {
				cookielist.append(p.head + "=" + p.tail);
				cookielist.append("; ");
			}
			cookielist.setLength(cookielist.length() - 2);
			connection.setRequestProperty( "Cookie", cookielist.toString() );
		}

	    return request(connection);
	}

	/**
	 * HTTP POST operation.
	 * 
	 * @param url
	 * @param cookies
	 * @param vars
	 * @return
	 * @throws IOException
	 */
	static public String post(String url, List<Pair<String, String>> cookies, List<Pair<String, String>> vars) throws IOException {
		StringBuffer urlstring = new StringBuffer(url);
	
		//setup connection
		URLConnection connection = new URL( urlstring.toString() ).openConnection();
		connection.setDoOutput(true);
	
		//if cookies, add them to the request headers
		if ( cookies != null && ! cookies.isEmpty() ) {
			StringBuffer cookielist = new StringBuffer();
			for (Pair<String, String> p : cookies) {
				cookielist.append(p.head + "=" + p.tail);
				cookielist.append("; ");
			}
			cookielist.setLength(cookielist.length() - 2);
			connection.setRequestProperty( "Cookie", cookielist.toString() );
		}
	
		//if vars, add appropriate request headers and write vars
		if( vars != null && ! vars.isEmpty() ) {
			StringBuffer varlist = new StringBuffer();
			for (Pair<String, String> p : vars) {
				varlist.append(p.head + "=" + p.tail);
				varlist.append("&");
			}

			varlist.setLength(varlist.length() - 1);
			connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty( "Content-Length", Integer.toString(varlist.length()) );
	
			OutputStream os = connection.getOutputStream();
			os.write( varlist.toString().getBytes() );
		}
	
		return request(connection);
	}

	static private String request(URLConnection connection) throws IOException {
		StringBuffer content = new StringBuffer();

		//make http request
		connection.connect();
	
		//read in http response
		InputStream in = connection.getInputStream();
	    byte[] buf = new byte[65536];
	    int nread;
	    synchronized (in) {
	      while((nread = in.read(buf, 0, buf.length)) >= 0) {
	        content.append( new String(buf, 0, nread) );
	      }
	    }
	    buf = null;
	
		return content.toString();
	}
	
}
