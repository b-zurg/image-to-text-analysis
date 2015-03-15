package document.structure;

import java.awt.image.BufferedImage;
import java.util.List;

import document.analysis.WordComponentAnalyzer;
import recognition.LetterOCR;

public class Word {
	WordComponentAnalyzer analyzer;
	BufferedImage wordImage;
	List<BufferedImage> letterImages;
	LetterOCR locr = new LetterOCR();
	
	public Word(BufferedImage wordImage) {
		analyzer = new WordComponentAnalyzer(wordImage);
		letterImages = analyzer.getLetterImages();
	}
	
	public String getWordAsString(){
		String converted = "";
		for(BufferedImage letterImage : letterImages) 
		{	
			converted += locr.recognize(letterImage).trim();
		}
		return converted;
	}
	
	public List<BufferedImage> getLetterImages() {
		return letterImages;
	}
}
