package recognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import utils.MyImageIO;
import net.sourceforge.tess4j.TessAPI.TessBaseAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class LetterOCR {
	private static Tesseract tesseract;
	private static MyImageIO imageio = new MyImageIO();
	private static String sep = File.separator;
	private static String font;
	
	private static LetterOCR instance;
	public static int WORD = 8, LINE = 7, STANDARD = 3, SINGLECHAR = 6;
	
	public static LetterOCR getInstance() {
		if(instance == null) {
			instance = new LetterOCR();
		}
		return instance;
	}
	
	protected LetterOCR(){
		initTesseract();
	}
	
	public void initTesseract() {
		String jnaPath = getJnaPath();
    	System.setProperty("jna.library.path", jnaPath);
    	
   
    	tesseract = Tesseract.getInstance();
    	tesseract.setLanguage("Calibri");
    	tesseract.setDatapath(getDataPath());
    	tesseract.setPageSegMode(LetterOCR.LINE);
    	
    	tesseract.setTessVariable("load_system_dawg", "F");
    	tesseract.setTessVariable("load_freq_dawg", "F");
    	tesseract.setTessVariable("load_punc_dawg", "F");
    	tesseract.setTessVariable("load_number_dawg", "F");
    	tesseract.setTessVariable("load_unambig_dawg", "F");
    	tesseract.setTessVariable("load_bigram_dawg", "F");
    	tesseract.setTessVariable("load_fixed_length_dawgs", "F");
//    	tesseract.setTessVariable("tessedit_char_blacklist", ".%&X");
    	tesseract.setTessVariable("language_model_penalty_non_dict_word", "0.00");
    	tesseract.setTessVariable("language_model_penalty_non_freq_dict_word", "0.00");
    	tesseract.setTessVariable("classify_enable_learning", "0");
    	tesseract.setTessVariable("classify_enable_adaptive_matcher", "0");
    	
//    	tesseract.setHocr(false);
//    	tesseract.setOcrEngineMode(10);
	}
	
	public static void setPageSegmentationMode(int val) {
		tesseract.setPageSegMode(val);
	}
	
	
	private static String getJnaPath(){
    	String jnaPath = LetterOCR.class.getResource("/tesseract").getPath();
        return jnaPath;
	}
	
	private static String getDataPath() {
		String dataPath = LetterOCR.class.getResource("/fonts").getPath();
		return dataPath.substring(1);
	}
	
	public static void setFont(String font) {
		LetterOCR.font = font;
		tesseract.setLanguage(font);
	}
	
	public static String recognize(BufferedImage image) {
		try {
			System.out.println(font);
			return tesseract.doOCR(image);
		}
		catch (TesseractException e) {
			System.err.println("Could not recognize letter because of: " + e.getMessage());
		}
		return null;
	}
	
	
	public static List<String> getFonts() {
		File folder = new File(getDataPath()+"/tessdata");
		File[] listOfFiles = folder.listFiles();
		List<String> fonts = Arrays.stream(listOfFiles)
				.filter(f->f.isFile())
				.map(f->f.getName())
				.map(f->f.replaceAll("\\..*", ""))
				.collect(Collectors.toList());
		return fonts;
	}
	
	public static String getProcessedFontName(String fontName) {
		String regex = "\\..*";
		String name = fontName.replaceAll(regex, "");
		name = name.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
		return name;
	}
	
	public static List<FontInfo> guessFonts(BufferedImage line, String enteredText) {
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
	private static List<String> getTopTenFonts(Multimap<Integer, String> scores) {
		Set<Integer> sortedScores = scores.keys().stream().sorted().collect(Collectors.toSet());
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