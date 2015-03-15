package document.analysis;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import recognition.LetterOCR;

import com.google.common.io.Files;

import document.structure.Line;
import document.structure.Word;
import utils.ImageUtils;
import utils.MyImageIO;

public class TestParagraphComponentAnalyzer {
	MyImageIO imageio = new MyImageIO();
	ImageUtils imageUtils = new ImageUtils();
	ParagraphComponentAnalyzer Panalyzer = new ParagraphComponentAnalyzer();
	LineComponentAnalyzer Lanalyzer = new LineComponentAnalyzer();
	
	@Test
	public void test() throws IOException {
		BufferedImage img = imageio.openBufferedImage("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\documents\\paragraph3.png");
		Panalyzer.setParagraphImage(img);
		List<BufferedImage> subLines = Panalyzer.getLineSubImages();
		
//		LetterOCR locr = new LetterOCR();
//		
		for(int i = 0; i < subLines.size(); i++) {
//			Line l = new Line(subLines.get(i));
//			System.out.println(l.getConvertedLine());
		
			
			
			File file1 = new File("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\lines\\gg");
			Files.createParentDirs(file1);

			imageio.saveImage(subLines.get(i), "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\lines", "line" + i);
	
			Lanalyzer.setLineImage(subLines.get(i));
			List<BufferedImage> subWords = Lanalyzer.getWordSubImages();
			for(int j = 0; j < subWords.size(); j++) {
				File file2 = new File("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\words\\" + "line"+i+"\\gg");
				Files.createParentDirs(file2);
				imageio.saveImage(subWords.get(j), "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\words\\" + "line"+i+"\\", "word" + j);
			
				Word word = new Word(subWords.get(j));
				List<BufferedImage> subLetters = word.getLetterImages();
				for(int k = 0; k < subLetters.size(); k++) {
					File file3 = new File("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\words\\" + "line"+i+"\\", "word" + j + "\\gg");
					Files.createParentDirs(file3);
					
					imageio.saveImage(subLetters.get(k), "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\words\\" + "line"+i+"\\", "word" + j + "\\letter" + k);
//					locr.recognize(subLetters.get(k));
				}
			}
		}
		
	}

}
