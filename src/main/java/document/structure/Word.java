package document.structure;

import java.awt.image.BufferedImage;
import java.util.List;

import document.analysis.WordComponentAnalyzer;
import recognition.LetterOCR;

public class Word {
	WordComponentAnalyzer analyzer;
	BufferedImage wordImage;
	List<BufferedImage> letterImages;
	
	public Word(BufferedImage wordImage) {
		analyzer = new WordComponentAnalyzer(wordImage);
		letterImages = analyzer.getLetterImages();
		this.wordImage = wordImage;
	}
	
	public String getWordAsString(){
		String converted = "";
		for(BufferedImage letterImage : letterImages) 
		{	
			LetterOCR.setPageSegmentationMode(LetterOCR.SINGLECHAR);
			LetterOCR.getInstance();
			converted += LetterOCR.recognize(letterImage).trim();
		}
//		LetterOCR.setPageSegmentationMode(LetterOCR.WORD);
//		LetterOCR.getInstance();
//		return LetterOCR.recognize(wordImage).trim();
		return converted;
	}
	
	public List<BufferedImage> getLetterImages() {
		return letterImages;
	}
}
