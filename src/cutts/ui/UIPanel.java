package cutts.ui;

import javax.swing.JPanel;

import cutts.controller.CUTTSController;
import cutts.patterns.Observer;

/**
 * Base class for all panels that rely on the Controller.
 * 
 * @author Jonathan Weatherhead
 *
 */
abstract public class UIPanel extends JPanel implements Observer {
	private CUTTSController controller;

	public UIPanel(CUTTSController _controller){
		super();
		controller = _controller;
		controller.registerObserver(this);
	}

	/**
	 * Return the Controller for this class.
	 * 
	 * @return
	 */
	protected CUTTSController getController() {
		return controller;
	}
}
