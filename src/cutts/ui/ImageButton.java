package cutts.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Implements a customized button with images for the states.
 * 
 * @author Jonathan Weatherhead
 *
 */
public class ImageButton extends JComponent implements MouseListener {
	public static final int	IS_UP = 0;
	public static final int	IS_HOVERING = 1;
	public static final int	IS_DOWN	= 2;
	public static final int	IS_DISABLED	= 3;

	public static final String	UP_EXT = "_up.jpg";
	public static final String	HOVERING_EXT = "_hovering.jpg";
	public static final String	DOWN_EXT = "_down.jpg";
	public static final String	DISABLED_EXT = "_disabled.jpg";

	private Image upimage, hoverimage, downimage, disabledimage;
	private int state;

	private ArrayList<ActionListener> actionlisteners;
	private boolean	ispressed;

	public ImageButton(String _imgname, boolean isdisabled) {
		upimage = new ImageIcon( getResource(_imgname + UP_EXT) ).getImage();
		hoverimage = new ImageIcon( getResource(_imgname + HOVERING_EXT) ).getImage();
		downimage = new ImageIcon( getResource(_imgname + DOWN_EXT) ).getImage();
		disabledimage = new ImageIcon( getResource(_imgname + DISABLED_EXT) ).getImage();
		actionlisteners = new ArrayList<ActionListener>();

		ispressed = false;

		int width = upimage.getWidth(null);
		int height = upimage.getHeight(null);
		Dimension dim = new Dimension(width, height);

		if(isdisabled)
			state = IS_DISABLED;
		else
			state = IS_UP;

		setSize(width, height);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setPreferredSize(dim);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		//do nothing
	}

	public void mousePressed(MouseEvent e) {
		if (state != IS_DISABLED) {
			state = IS_DOWN;
			ispressed = true;
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (state != IS_DISABLED) {
			ispressed = false;
			if(state == IS_DOWN) {
				state = IS_HOVERING;
				announceAction();
			}
			else {
				state = IS_UP;
			}
			repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (state != IS_DISABLED) {
			state = ispressed ? IS_DOWN : IS_HOVERING;
			repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (state != IS_DISABLED) {
			state = IS_UP;
			repaint();
		}
	}

	private void announceAction() {
		ActionEvent e = new ActionEvent(this, 0, null);
		for (ActionListener listener : actionlisteners) {
			listener.actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener listener) {
		actionlisteners.add(listener);
	}

	public boolean isEnabled() {
		return state != IS_DISABLED;
	}

	public void setEnabled(boolean flag) {
		if(flag){
			state = IS_UP;
		}
		else {
			state = IS_DISABLED;
			ispressed = false;
		}
		repaint();
	}	

	public void paintComponent(Graphics g) {
		switch (state) {
			case IS_UP:
				g.drawImage(upimage, 0, 0, null);
				break;
			case IS_DOWN:
				g.drawImage(downimage, 0, 0, null);
				break;
			case IS_HOVERING:
				g.drawImage(hoverimage, 0, 0, null);
				break;
			case IS_DISABLED:
				g.drawImage(disabledimage, 0, 0, null);
				break;
			default:
		}
	}

	/**
	 * Loads resources.
	 *  
	 * @param path
	 * @return
	 */
	private URL getResource(String path) {
		return getClass().getResource(path);
	}
}

