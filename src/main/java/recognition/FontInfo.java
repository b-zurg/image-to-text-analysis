package recognition;

public class FontInfo {
	
	private String processedFontName, unprocessedFontName;
	private int score;
	private String ocrResults;
	
	
	public String getProcessedFontName() {
		return processedFontName;
	}
	public void setProcessedFontName(String processedFontName) {
		this.processedFontName = processedFontName;
	}
	public String getUnprocessedFontName() {
		return unprocessedFontName;
	}
	public void setUnprocessedFontName(String unprocessedFontName) {
		this.unprocessedFontName = unprocessedFontName;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getOcrResults() {
		return ocrResults;
	}
	public void setOcrResults(String ocrResults) {
		this.ocrResults = ocrResults;
	}
}
