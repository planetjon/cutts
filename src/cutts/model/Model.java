package cutts.model;

import java.util.ArrayList;
import java.util.List;

import cutts.patterns.Observable;
import cutts.patterns.Observer;

/**
 * Base class for models, implementing basic observability.
 * 
 * @author Jonathan Weatherhead
 * 
 */
public abstract class Model implements Observable {

	private List<Observer> observers;

	public Model() {
		observers = new ArrayList<Observer>();
	}

	/**
	 * Registers the observer with the model.
	 * @param observer the observer wishing to be notified of model changes.
	 */
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	/**
	 * Unregisters the observer from the model.
	 * @param observer the observer wishing to no longer be notified of model changes.
	 */
	public void unregisterObserver(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies all observers of a change in the model.
	 */
	protected void notifyObservers() {
		List<Observer> updated = new ArrayList<Observer>();

		while (! observers.isEmpty() ) {
			updated.add(observers.remove(observers.size() - 1) );
			updated.get(updated.size() - 1).update();
		}

		observers = updated;
	}
}
