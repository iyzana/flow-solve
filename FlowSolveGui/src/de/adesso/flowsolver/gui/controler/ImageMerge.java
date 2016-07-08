package de.adesso.flowsolver.gui.controler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Created by slinde on 07.07.2016.
 */
public class ImageMerge {

	public static BufferedImage merge(BufferedImage image1, BufferedImage image2){

		int w = Math.max(image1.getWidth(), image2.getWidth());
		int h = Math.max(image1.getHeight(), image2.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, 0, 0, null);

		return combined;
	}
}
