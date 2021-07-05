package cutts.util;

import java.util.*;
import java.io.*;

/**
 * A simple markup tag parser. It is not thread-safe, and behaviour when parsing content of
 * embedded language tags is undefined (skipping is recommended)
 * 
 * @author Jonathan Weatherhead
 *@version 1.2
 */
final public class TagParser{
	private final String tagstring;
	private HashMap<String, String> tagproperties;
	private int curtagstart, nextseekpos;
	private boolean reachedeof;
	String curelementtype;

	/**
	 * Constructor that takes a string representation of the document that you wish to parse
	 * 
	 * @param _tagstring The string representation of the document to be parsed
	 */
	public TagParser(String _tagstring){
		tagstring = _tagstring;
		tagproperties = new HashMap<String, String>();
		curtagstart = nextseekpos = 0;
		reachedeof = false;
		curelementtype = null;
	}

	/**
	 * Moves the seeker to the next tag in the document and returns the element type, or null if
	 * end of document is reached.
	 * Throws EOFException if the tracker has already reached end of document
	 * 
	 * @return The element type of the current tag
	 * @throws EOFException
	 */
	public String nextTag() throws EOFException{
		if( reachedeof || nextseekpos >= tagstring.length() ) throw new EOFException("No more tags in document");

		int tracker = 0;
		int indexholder = 0;

		try{
			//find start of next non-closing tag
			curtagstart = nextOccurenceOf(tagstring, nextseekpos, '<');
			while(tagstring.charAt( nextNonWhitespaceChar(tagstring, curtagstart + 1) ) == '/'){
					curtagstart = nextOccurenceOf(tagstring, curtagstart + 1, '<');
			}

			//extract element type
			tracker = curtagstart;
			indexholder = tracker = nextNonWhitespaceChar(tagstring, tracker + 1);
			for( ; tagstring.substring(tracker, tracker + 1).matches("[^\\s>]"); tracker++);
			curelementtype = tagstring.substring(indexholder, tracker);

			//iterate through the characters until the closing > is found
			while(tagstring.charAt(tracker) != '>'){
				if(tagstring.charAt(tracker) == '\'' || tagstring.charAt(tracker) == '"')
					tracker = nextOccurenceOf( tagstring, tracker + 1, tagstring.charAt(tracker) );
				tracker++;
			}
	
			//update seeker
			nextseekpos = tracker + 1;

			//return element type
			return curelementtype.toLowerCase();
		}
		catch(StringIndexOutOfBoundsException e){
			reachedeof = true;
			return null;
		}
	}

	/**
	 * Peeks at the next tag in the document and returns the element type, or null if
	 * end of document is reached.
	 * Throws EOFException if the tracker has already reached end of document
	 * 
	 * @return The element type of the current tag
	 */
	public String peekAtNextTag() throws EOFException {
		int _curtagstart = curtagstart;
		int _nextseekpos = nextseekpos;
		String _curelementtype = curelementtype;

		String tag = nextTag();

		curtagstart = _curtagstart;
		nextseekpos = _nextseekpos;
		curelementtype = _curelementtype;
		
		return tag;
	}

	/**
	 * Moves the seeker to the next peer-level tag and returns the element type,
	 * or null if end of document is reached.
	 * If there is no next peer-level tag, behaviour depends on the overflow flag.
	 * If set to true, nextTag() is called internally and the result is returned.
	 * if set to false, null is returned and seeker is moved to the first character after
	 * the closing tag of the current tag. if this is the case, nextPeerTag(), getTagproperties() and
	 * gettagContent() will return null and not move the tracker until nextTag() is called.
	 * 
	 * Don't call this method if the seeker is currently on an unpaired tag unless document follows XHTML standard.
	 * Throws EOFException if there are no more tags in document or document ends unexpectedly
	 * 
	 * @param overflow behaviour flag 
	 * @return The element type of the current tag
	 * @throws EOFException if the tracker has already reached end of document
	 */
	public String nextPeerTag(boolean overflow) throws EOFException{
		if(reachedeof || nextseekpos >= tagstring.length() ) throw new EOFException("No more tags in document");
		if(curelementtype == null) return null;

		int tracker = 0;
		int indexholder = 0;
		Stack<String> elementstack = new Stack<String>();
		boolean closetag = false;
		
		//force reparse of current tag, so that XHTML '/' can be checked for, and it works if this is called for first
		//tag in the document(stupid, but possible?)
		nextseekpos = curtagstart;
		try{
			do{
				closetag = false;

				//find start of next tag
				curtagstart = nextOccurenceOf(tagstring, nextseekpos, '<');
				tracker = nextNonWhitespaceChar(tagstring, curtagstart + 1);

				//if first character is '/' set closetag flag and advance tracker
				if(closetag = tagstring.charAt(tracker) == '/'){
					tracker = nextNonWhitespaceChar(tagstring, tracker + 1);
				}
				
				//extract element type
				indexholder = tracker;
				for( ; tagstring.substring(tracker, tracker + 1).matches("[^\\s>]"); tracker++);
				curelementtype = tagstring.substring(indexholder, tracker);
	
				//if closetag is false and top of elementstack matches, push element onto elementstack
				if(! closetag && (elementstack.empty() || elementstack.peek().toLowerCase().equals( curelementtype.toLowerCase() )) ){
					elementstack.push(curelementtype);
				}

				//else if closetag is true and the top of elementstack matches, pop
				else if( closetag && elementstack.peek().toLowerCase().equals( curelementtype.toLowerCase() ) ){
					elementstack.pop();
				}

				//else, ignore tag

				//iterate through the characters until the closing > is found
				while(tagstring.charAt(tracker) != '>'){
					if(tagstring.charAt(tracker) == '\'' || tagstring.charAt(tracker) == '"')
						tracker = nextOccurenceOf( tagstring, tracker + 1, tagstring.charAt(tracker) );

					//if XHTML '/' is found and top of elementstack matches, pop stack
					if(tagstring.charAt(tracker) == '/' && elementstack.peek() == curelementtype){
						elementstack.pop();
					}
					tracker++;
				}

				//update seeker
				nextseekpos = tracker + 1;
			}while(! elementstack.empty() );

			//check to see if there is a next tag
			if(! hasMoreTags() ){
				reachedeof = true;
				return null;
			}

			//if there is no other tag in this nesting, consult overflow flag
			if( ! doesNextTagClose() || overflow){
				return nextTag();
			}
			else{
				curelementtype = null;
				return null;
			}
		}
		catch(StringIndexOutOfBoundsException e){
			reachedeof = true;
			throw new EOFException("No more tags in document");
		}
	}

	/**
	 * Moves the seeker to the next tag of type "elementtype" in the document and returns element type.
	 * Returns null if there are no more tags in document, no more tags of type "elementtype"
	 * in document, or document ends unexpectedly
	 * 
	 * @param elementtype The type of element to be searched for
	 * @return The element type of the tag
	 * @throws EOFException
	 */
	public String nextTagOfType(String elementtype) throws EOFException{
		String ref = elementtype.toLowerCase();
		while( hasMoreTags() ){
			if( nextTag().equals(ref) ) return ref;
		}
		return null;
	}

	/**
	 * Returns a map containing the properties of the tag. The element type is mapped through
	 * the "elementtype" key
	 * 
	 * @return A map containing the current tag properties
	 */
	//assumption that there is always a space before XHTML tag closer '/'
	public HashMap<String, String> getTagProperties(){
		if(curelementtype == null) return null;

		tagproperties.clear();
		int tracker = 0;
		int indexholder = 0;
		String propertyname;
		
		//try{
			//extract element type
			tracker = curtagstart;
			indexholder = tracker = nextNonWhitespaceChar(tagstring, tracker + 1);
			for( ; tagstring.substring(tracker, tracker + 1).matches("[^\\s>]"); tracker++);
			tagproperties.put( "elementtype", tagstring.substring(indexholder, tracker) );

			//parse rest of tag
			while(true){
				//read first non-whitespace char
				tracker = nextNonWhitespaceChar(tagstring, tracker);
	
				//if it's a slash, find the closing '>'
				if(tagstring.charAt(tracker) == '/'){
					nextseekpos = nextOccurenceOf(tagstring, tracker, '>') + 1;
					break;
				}
				//if it's the closing '>'
				else if(tagstring.charAt(tracker) == '>'){
					nextseekpos = ++tracker;
					break;
				}
				//otherwise, it is a tag property
				else{
					//read property name and move the seeker to the safe char after property name {\s,=,>}
					for(nextseekpos = tracker; tagstring.substring(tracker, tracker + 1).matches("[^\\s=>]"); tracker++);

					//extract property name
					propertyname = tagstring.substring(nextseekpos, tracker);

					//move to '=', char, '/', or '>'
					tracker = nextNonWhitespaceChar(tagstring, tracker);

					//if tracker is at '='
					if(tagstring.charAt(tracker) == '='){
						tracker = nextNonWhitespaceChar(tagstring, tracker + 1);

						//if encased by quotes, extract
						if( tagstring.substring(tracker, tracker + 1).matches("['\"]") ){
							nextseekpos = tracker + 1;
							tracker = nextOccurenceOf( tagstring, nextseekpos, tagstring.charAt(tracker) );
							tagproperties.put( propertyname, tagstring.substring(nextseekpos, tracker) );
							//advance tracker from quote for next iteration
							tracker++;
						}
						//otherwise, value is not encased. extract at first whitespace
						else{
							nextseekpos = tracker;
							tracker = nextWhitespaceChar(tagstring, nextseekpos);
							tagproperties.put( propertyname, tagstring.substring(nextseekpos, tracker) );
						}
					}
					//if tracker is at char (not '/' and '>')
					else if(tagstring.substring(tracker, tracker + 1).matches("[^/>]") ){
					//else if( Character.isLetter(tagstring.charAt(tracker)) ){
						//back tracker up and add property name as its value
						tracker--;
						tagproperties.put( propertyname, propertyname);
					}
					//if tracker is at '/'
					else if(tagstring.charAt(tracker) == '/'){
						//add property name as its value
						tagproperties.put( propertyname, propertyname);
					}
					//if tracker is at '>'
					else if(tagstring.charAt(tracker) == '>'){
						//add property name as its value
						tagproperties.put( propertyname, propertyname);
					}
				}
			}
		//}
		//catch(EOFException e){
			//return null;
		//}
		return tagproperties;
	}

	/**
	 * Returns the string content of a paired tag. Do not call this method if the seeker
	 * is currently on an unpaired tag unless the tag follows the XHTML standard
	 * 
	 * @return A string of the content that is contained in the current [paired] tag
	 */
	public String getTagContent() throws EOFException{
		if(reachedeof || nextseekpos >= tagstring.length() ) throw new EOFException("Past end of document");
		if(curelementtype == null) return null;

		int tracker = 0;
		Stack<String> elementstack = new Stack<String>();
		boolean closetag = false, xhtmlclosed = false;
		int indexholder;

		int save_nextseekpos = nextseekpos;
		int save_curtagstart = curtagstart;
		String save_curelementtype = curelementtype;
		String content;

		//force reparse of current tag, so that XHTML '/' can be checked for, and it works if this is called for first
		//tag in the document(stupid, but possible?)
		nextseekpos = curtagstart;
		try{
			do{
				closetag = false;
				xhtmlclosed = false;

				//find start of next tag
				curtagstart = nextOccurenceOf(tagstring, nextseekpos, '<');
				tracker = nextNonWhitespaceChar(tagstring, curtagstart + 1);

				//if first character is '/' set closetag flag and advance tracker
				if(closetag = tagstring.charAt(tracker) == '/'){
					tracker = nextNonWhitespaceChar(tagstring, tracker + 1);
				}

				//extract element type
				indexholder = tracker;
				for( ; tagstring.substring(tracker, tracker + 1).matches("[^\\s>]"); tracker++);
				curelementtype = tagstring.substring(indexholder, tracker);

				//if closetag is false and top of elementstack matches, push element onto elementstack
				if(! closetag && (elementstack.empty() || elementstack.peek().toLowerCase().equals( curelementtype.toLowerCase() )) ){
					elementstack.push(curelementtype);
				}

				//else if closetag is true and the top of elementstack matches, pop
				else if( closetag && elementstack.peek().toLowerCase().equals( curelementtype.toLowerCase() ) ){
					elementstack.pop();
				}

				//else, ignore tag

				//iterate through the characters until the closing > is found
				while(tagstring.charAt(tracker) != '>'){
					if(tagstring.charAt(tracker) == '\'' || tagstring.charAt(tracker) == '"')
						tracker = nextOccurenceOf( tagstring, tracker + 1, tagstring.charAt(tracker) );

					//if XHTML '/' is found and top of elementstack matches, pop stack
					if(tagstring.charAt(tracker) == '/' && elementstack.peek() == curelementtype){
						elementstack.pop();
						xhtmlclosed = true;
					}
					tracker++;
				}

				//update seeker
				nextseekpos = tracker + 1;

				//advance curtagstart to nextseekpos if the tag is a closed unpaired tag, so that "" is returned
				if(xhtmlclosed) curtagstart = nextseekpos;

			}while(! elementstack.empty() );

			//extract content and restore state
			content = tagstring.substring(save_nextseekpos, curtagstart);
			nextseekpos = save_nextseekpos;
			curtagstart = save_curtagstart;
			curelementtype = save_curelementtype;

			return content;
		}
		catch(StringIndexOutOfBoundsException e){
			reachedeof = true;
			throw new EOFException("No more tags in document");
		}
	}

	/**
	 * Determines whether there are remaining [unparsed] tags in the document
	 * 
	 * @return Returns true if EOF has not been reached, and false otherwise
	 */
	public boolean hasMoreTags(){
		int pos = nextseekpos;
		int lim = tagstring.length();

		while( pos + 1 < lim && !(tagstring.charAt(pos) == '<' && tagstring.charAt(pos + 1) != '/' ) ) pos++;

		return pos + 1 < lim;
	}

	/**
	 * Determines whether the next tag in document is a closing tag
	 * 
	 * @return Returns true if next tag closes, and false otherwise
	 */
	public boolean doesNextTagClose(){
		//find start of next tag
		int tmp_curtagstart = nextOccurenceOf(tagstring, nextseekpos, '<');
		int tmp_tracker = nextNonWhitespaceChar(tagstring, tmp_curtagstart + 1);

		//if first character is '/' return true
		return tagstring.charAt(tmp_tracker) == '/';
	}

	/**
	 * Returns the index of the next non-whitespace character. Returns -1 if EOF is reached first
	 * 
	 * @param s The string to be searched
	 * @param offset The offset at which to start
	 * @return The index of the next non-whitespace character
	 */
	private int nextNonWhitespaceChar(String s, int offset){
		int pos = offset;
		int lim = s.length();

		while( pos < lim && s.substring(pos, pos + 1).matches("\\s") ) pos++;

		if(pos >= lim){
			return -1;
		}
		return pos;
	}

	/**
	 * Returns the index of the next whitespace character. returns -1 if EOF is reached first
	 * 
	 * @param s The string to be searched
	 * @param offset The offset at which to start
	 * @return The index of the next whitespace character
	 */
	private int nextWhitespaceChar(String s, int offset){
		int pos = offset;
		int lim = s.length();

		while( pos < lim && s.substring(pos, pos + 1).matches("\\S") ) pos++;

		if(pos >= lim){
			return -1;
		}
		return pos;
	}

	/**
	 * Returns the index of the next specified character. returns -1 i EOF is reached first
	 * 
	 * @param s The string to be searched
	 * @param offset The offset at which to start
	 * @param c The specified character that is to be searched fir
	 * @return The index of the next specified character
	 */
	private int nextOccurenceOf(String s, int offset, char c){
		return s.indexOf(c, offset);
	}
}
