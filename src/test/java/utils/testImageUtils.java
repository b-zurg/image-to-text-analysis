package utils;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import utils.ImageUtils;
import utils.MyImageIO;

public class testImageUtils {
	ImageUtils imageUtils = new ImageUtils();
	MyImageIO imageio = new MyImageIO();
	
	@Test
	public void test() throws IOException {
		BufferedImage image = imageio.openBufferedImage("D:\\Code\\workspace\\ImageToText-Analysis\\src\\test\\resources\\documents\\paragraph3.png");
		
		final long startTime = System.currentTimeMillis();
		imageUtils.blurImageOld(image, 30);
//		imageUtils.blurImageFast(image, 3, 10);
//		imageUtils.blurImageHorizontal(image, 5, 10);
//		imageUtils.blurImageVertical(image, 5, 10);
//		imageUtils.threshold(image, 0.9);
		
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
		
		imageio.saveImage(image, "D:\\Code\\workspace\\ImageToText-Analysis\\src\\test\\resources\\processed\\", "blurred");
	}

}
