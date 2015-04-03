package document.structure;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

import document.analysis.LineComponentAnalyzer;

public class Line {
	private List<Word> words;
	private LineComponentAnalyzer analyzer;
	private BufferedImage image;
	
	public Line(BufferedImage img){
		this.image = img;
		analyzer = new LineComponentAnalyzer();
		analyzer.setUntouchedImage(img);
	}
	public void setStandardBlur(int neighborhood, int iterations) {
		analyzer.setStandardBlur(neighborhood, iterations);
	}
	public void setVerticalBlur(int neighborhood, int iterations) {
		analyzer.setVerticalBlur(neighborhood, iterations);
	}
	public void setThreshold(double thresh) {
		analyzer.setThreshold(thresh);
	}
	
	
	
	private List<String> getWordsAsStringList(){
		List<String> totalWords = Lists.newArrayList();
		List<Word> words = analyzer.getWordObjects();
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
		totalString += "\n";
		return totalString;
	}
}
