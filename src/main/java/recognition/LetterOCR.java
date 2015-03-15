package recognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import utils.MyImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class LetterOCR {
	private  Tesseract tesseract;
	private static MyImageIO imageio = new MyImageIO();
	private static String sep = File.separator;
	
	public LetterOCR(){
		initTesseract();
	}
	
	public void initTesseract() {
		String jnaPath = getJnaPath();
    	System.setProperty("jna.library.path", jnaPath);
    	String tessPath = jnaPath;
    	
    	
    	
    	tesseract = Tesseract.getInstance();
    	tesseract.setLanguage("eng");
    	tesseract.setDatapath(tessPath);
    	tesseract.setPageSegMode(10);
//    	tesseract.setOcrEngineMode(10);
	}
	
	private String getJnaPath(){
    	String homeDir = System.getProperty("user.dir");
    	String jnaPath = homeDir + sep + "src" + sep + "main" + sep + "resources" + sep + "tesseract";
        return jnaPath;
	}
	
	public String recognize(BufferedImage image) {
		try {
			return tesseract.doOCR(image);
		} 
		catch (TesseractException e) {
			System.err.println("Could not recognize letter because of: " + e.getMessage());
		}
		return null;
	}
	
	
	public static void main(String[] args) throws IOException {
		BufferedImage img = imageio.openBufferedImage("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\words\\line2\\word0\\letter7.png");
		LetterOCR locr = new LetterOCR();
		System.out.println(locr.recognize(img));
		
	}
}