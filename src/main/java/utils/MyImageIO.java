package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MyImageIO {
	public BufferedImage openBufferedImage(String filename) throws IOException{
		BufferedImage image = ImageIO.read(new File(filename));
		return image;
	} 
	public void saveImage(Image image, String filePath, String fileName){
		try {
			File outputfile = new File(filePath+ "\\" + fileName + ".png");
			ImageIO.write((RenderedImage) image, "png", outputfile);
		} catch (IOException e) {
			System.err.println("could not save image because of: " + e.getMessage());
		}
	}
}
