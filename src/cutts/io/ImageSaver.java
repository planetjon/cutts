package cutts.io;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * This class offers exporting of an Image class to a file in JPEG format
 * 
 * @author Jonathan Weatherhead
 *
 */
public class ImageSaver {

	static public boolean saveToJPEG(Image image, File targetfile, int aquality) throws Exception {
		if(image != null && targetfile !=null) {
			BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = bi.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);

			return ImageIO.write(bi, "jpg", targetfile);
		}
		return false;
	}
}