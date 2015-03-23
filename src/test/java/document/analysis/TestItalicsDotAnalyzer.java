package document.analysis;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import document.structure.Word;
import utils.MyImageIO;

public class TestItalicsDotAnalyzer {


	List<BufferedImage> images;
	MyImageIO imageio;
	String testClassesPath;
	String imagesOpenPath, imagesSavePath;
	
	@Before
	public void init() throws IOException {
		testClassesPath = TestItalicsDotAnalyzer.class.getResource("../../").getPath();
		imagesOpenPath = testClassesPath + "/italicized/";
		imagesSavePath = testClassesPath + "/processed/italicized";
		
		File f1 = new File(imagesOpenPath);
		Files.createParentDirs(f1);
		File f2 = new File(imagesSavePath);
		Files.createParentDirs(f2);
		
		
		File [] imageFiles = new File(imagesOpenPath).listFiles();
		
		imageio = new MyImageIO();
		images = Lists.newArrayList();
		
		for(File file : imageFiles) {
			String path = file.getAbsolutePath();
			BufferedImage image = imageio.openBufferedImage(path);
			images.add(image);
		}
	}
	
	
	@Test
	public void test() {
		
		int count = 0; 
		for(BufferedImage image : images) {
			Word word = new Word(image);
			List<BufferedImage> wordImages = word.getLetterImages();
			
			for(int i = 0; i < wordImages.size(); i++) {
				BufferedImage wordImage = wordImages.get(i);
				imageio.saveImage(wordImage, imagesSavePath, "font" + count + "word" + i);
			}
			count++;
		}
	}

}
