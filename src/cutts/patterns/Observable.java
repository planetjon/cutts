package cutts.patterns;

/**
 * Interface for Subject in the Subject/Observer pattern
 * 
 * @author Jonathan Weatherhead
 *
 */
public interface Observable {

	public void registerObserver(Observer observer);

	public void unregisterObserver(Observer observer);

}
