package runner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import recognition.LetterOCR;

import com.google.common.collect.Lists;

import utils.MyImageIO;
import document.analysis.ParagraphComponentAnalyzer;
import document.structure.Line;



public class Main {
	
    public static void main(String[] args) throws IOException {
    	MyImageIO imageio = new MyImageIO();
    	ParagraphComponentAnalyzer analyzer = new ParagraphComponentAnalyzer();
//    	LetterOCR locr = new LetterOCR();
//    	LetterOCR.initTesseract();
    	analyzer.setParagraphImage(imageio.openBufferedImage("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\documents\\paragraph3.png"));
    	
    	List<BufferedImage> subImages = analyzer.getLineSubImages();
//    	System.out.println(subImages.size());
    	List<Line> lines = Lists.newArrayList();
    	for(BufferedImage lineImage : subImages) {
    		lines.add(new Line(lineImage));
    	}
    	for(Line l : lines) {
    		System.out.println(l.getConvertedLine());
    	}
    	
    }
}