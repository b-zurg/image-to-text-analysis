package document.structure;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

import document.analysis.LineComponentAnalyzer;

public class Line {
	private List<Word> words;
	private LineComponentAnalyzer analyzer;
	
	public Line(BufferedImage lineImage){
		words = Lists.newArrayList();
		analyzer = new LineComponentAnalyzer();
		
		analyzer.setLineImage(lineImage);
		initWordObjects();
	}
	
	private void initWordObjects(){
		List<BufferedImage> wordImages = analyzer.getWordSubImages();
		for(BufferedImage wordImage : wordImages) {
			words.add(new Word(wordImage));
		}
	}
	
	private List<String> getWordsAsStringList(){
		List<String> totalWords = Lists.newArrayList();
		for(Word wrd : words) {
			totalWords.add(wrd.getWordAsString() + " ");
		}
		return totalWords;
	}
	
	public String getConvertedLine() {
		List<String> wordStrings = getWordsAsStringList();
		String totalString = "";
		for(String wordString: wordStrings) {
			totalString += wordString + " ";
		}
		return totalString;
	}
}
