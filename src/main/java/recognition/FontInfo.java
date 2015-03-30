package recognition;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FontInfo {
	
	private StringProperty processedFontName, unprocessedFontName;
	private IntegerProperty score;
	private StringProperty ocrResults;
	
	
	public StringProperty getProcessedFontNameProperty() {
		return processedFontName;
	}
	public String getProcessedFontName() {
		return processedFontName.get();
	}
	public void setProcessedFontName(String processedFontName) {
		this.processedFontName = new SimpleStringProperty(processedFontName);
	}
	
	
	
	public StringProperty getUnprocessedFontNameProperty() {
		return unprocessedFontName;
	}
	public String getUnprocessedFontName() {
		return unprocessedFontName.get();
	}
	public void setUnprocessedFontName(String unprocessedFontName) {
		this.unprocessedFontName = new SimpleStringProperty(unprocessedFontName);
	}
	
	
	
	public IntegerProperty getScoreProperty() {
		return score;
	}
	public int getScore() {
		return score.get();
	}
	public void setScore(Integer score) {
		this.score = new SimpleIntegerProperty(score);
	}
	
	
	
	public StringProperty getOcrResultsProperty() {
		return ocrResults;
	}
	public String getOcrResults() {
		return ocrResults.get();
	}
	public void setOcrResults(String ocrResults) {
		this.ocrResults = new SimpleStringProperty(ocrResults);
	}

	
	
}
