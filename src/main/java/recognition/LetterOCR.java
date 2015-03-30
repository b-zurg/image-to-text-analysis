package recognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

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
    	
    	tesseract = Tesseract.getInstance();
    	tesseract.setLanguage("Calibri");
    	tesseract.setDatapath(getDataPath());
//    	tesseract.setPageSegMode(1/0);
//    	tesseract.setOcrEngineMode(10);
	}
	
	private String getJnaPath(){
    	String jnaPath = LetterOCR.class.getResource("/tesseract").getPath();
        return jnaPath;
	}
	
	private String getDataPath() {
		String dataPath = getClass().getResource("/fonts").getPath();
		return dataPath.substring(1);
	}
	
	public void setFont(String font) {
		tesseract.setLanguage(font);
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
	
	
	public List<String> getFonts() {
		File folder = new File(getDataPath()+"/tessdata");
		File[] listOfFiles = folder.listFiles();
		List<String> fonts = Arrays.stream(listOfFiles)
				.filter(f->f.isFile())
				.map(f->f.getName())
				.map(f->f.replaceAll("\\..*", ""))
				.collect(Collectors.toList());
		return fonts;
	}
	
	public String getProcessedFontName(String fontName) {
		String regex = "\\..*";
		String name = fontName.replaceAll(regex, "");
		name = name.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
		return name;
	}
	
	public List<FontInfo> guessFonts(BufferedImage line, String enteredText) {
		List<String> fonts = getFonts();
		Multimap<Integer, String> scoresToFont = ArrayListMultimap.create();
		Map<String, String> fontResults = Maps.newHashMap();
		Map<String, Integer> fontToScore = Maps.newHashMap();
		
		for (String font : fonts) {
			setFont(font);
			String recognized = recognize(line);
			int distance = StringUtils.getLevenshteinDistance(enteredText, recognized);
			
			scoresToFont.put(distance, font);
			fontToScore.put(font, distance);
			fontResults.put(font, recognized);
		}
		List<String> bestFonts = getTopTenFonts(scoresToFont);
		List<FontInfo> bestFontInfo = Lists.newArrayList();
		
		for(String unprocessedFontName : bestFonts) {
			FontInfo info = new FontInfo();
			info.setUnprocessedFontName(unprocessedFontName);
			info.setProcessedFontName(getProcessedFontName(unprocessedFontName));
			info.setOcrResults(fontResults.get(unprocessedFontName));
			info.setScore(fontToScore.get(unprocessedFontName));
			bestFontInfo.add(info);
		}
		return bestFontInfo;
	}
	private List<String> getTopTenFonts(Multimap<Integer, String> scores) {
		List<Integer> sortedScores = scores.keys().stream().sorted().collect(Collectors.toList());
		List<String> bestFonts = Lists.newArrayList();
		
		int collected = 0;
		for(Integer key : sortedScores) {
			List<String> fonts = (List<String>) scores.get(key);

			for(String font : fonts) {
				bestFonts.add(font);
				collected++;
				if(collected >10) break;
			}
			if(collected > 10)  break; 
		}
		return bestFonts;
	}
}