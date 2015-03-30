package recognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

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
    	String tessPath = jnaPath;
    	
    	
    	
    	tesseract = Tesseract.getInstance();
    	tesseract.setLanguage("ArialItalic");
    	tesseract.setDatapath(getDataPath());
//    	tesseract.setPageSegMode(1/0);
//    	tesseract.setOcrEngineMode(10);
	}
	
	private String getJnaPath(){
    	String homeDir = System.getProperty("user.dir");
    	String jnaPath = homeDir + sep + "src" + sep + "main" + sep + "resources" + sep + "tesseract";
        return jnaPath;
	}
	
	private String getDataPath() {
		String homeDir = System.getProperty("user.dir");
		String dataPath = homeDir + sep + "src" + sep + "main" + sep + "resources" + sep + "fonts";
		return dataPath;
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
		File folder = new File(getDataPath()+sep+"tessdata");
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
	
	public void guessFonts(BufferedImage line, String enteredText) {
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
		
		for(String font : bestFonts) {
			System.out.println(getProcessedFontName(font));
			System.out.println(fontToScore.get(font));
			System.out.println(fontResults.get(font));
		}
		
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
	
	
	public static void main(String[] args) throws IOException {
		BufferedImage img = imageio.openBufferedImage("D:\\Code\\workspace\\ImageToText-Analysis\\src\\test\\resources\\documents\\text calibri.png");
		LetterOCR locr = new LetterOCR();
		locr.guessFonts(img, "Hello my name is surlysmiles jar jar jar ja r jdfjdjkjd ls;aljfkd");
		
	}
}