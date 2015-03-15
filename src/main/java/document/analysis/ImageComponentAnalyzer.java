package document.analysis;

import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;

import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import document.structure.LetterSet;
import document.structure.Line;
import document.structure.Word;
@Deprecated
public class ImageComponentAnalyzer {
	MyImageIO imageio = new MyImageIO();
	BufferedImage untouchedImage, mutableImage;

	public ImageComponentAnalyzer(String filePath) throws IOException {
		this.initVariables(filePath);
		this.preprocessImage();
		
		
	}
	private void initVariables(String filePath) throws IOException {
		untouchedImage = imageio.openBufferedImage(filePath);
		mutableImage = imageio.openBufferedImage(filePath);
	}
	
	private void preprocessImage(){
		ImageUtils.blurImageFast(mutableImage, 5,  5);
		ImageUtils.blurImageHorizontal(mutableImage, 5, 10);
		ImageUtils.threshold(mutableImage, 0.9);

	}
	
	//returns arrayList of y values where one should split
	public List<Integer> getLineSplits(BufferedImage image){
		
		ArrayListMultimap<Integer, Integer> yTracerSamples = getVerticalTracerSampleSet(image, 40);
		List<Integer> best = getVerticalTracerWithMostSplits(yTracerSamples);
		List<Integer> average = this.getYsBetweenSplits(best);

		ImageUtils.createHorizontalRedLinesAt(image, average);
		imageio.saveImage(image, "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\", "lines");
		
		
		return average;
	}

	public List<Integer> getLineSplits(){
		
		ArrayListMultimap<Integer, Integer> yTracerSamples = getVerticalTracerSampleSet(mutableImage, 50);
		List<Integer> best = getVerticalTracerWithMostSplits(yTracerSamples);
		List<Integer> average = this.getYsBetweenSplits(best);

		ImageUtils.createHorizontalRedLinesAt(untouchedImage, average);
		imageio.saveImage(untouchedImage, "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\", "lines");
		
		
		return average;
	}
	
	private ArrayListMultimap<Integer, Integer> getVerticalTracerSampleSet(BufferedImage image, int tracers){
		int numTracers = image.getWidth()/tracers;
		ArrayListMultimap<Integer, Integer> lineSplits = ArrayListMultimap.create(numTracers + 5, 100);
		
		int prevColor = image.getRGB(0, 0);
		for(int x = numTracers; x < image.getWidth(); x += numTracers)
		{
			lineSplits.put(x, 0);
			for(int y = 0; y < image.getHeight(); y++)
			{
				int currentColor = image.getRGB(x, y);	
				if(currentColor != prevColor) 
				{
					lineSplits.put(x, y);
				}
				prevColor = currentColor;
			}
			lineSplits.put(x, image.getHeight());
		}
		return lineSplits;
	}
	
	private List<Integer> getVerticalTracerWithMostSplits(ArrayListMultimap<Integer, Integer> yTracerSamples){
		List<Integer> best = Lists.newArrayList();
		for(int x : yTracerSamples.keySet())
		{
			if(yTracerSamples.get(x).size() > best.size())
			{	
				best = yTracerSamples.get(x);
			}
		}
		return best;
	}
	
	private List<Integer> getYsBetweenSplits(List<Integer> yTracer){
		List<Integer> average = Lists.newArrayList();
		for(int y = 1;  y < yTracer.size(); y += 2){
			int newY = (yTracer.get(y) + yTracer.get(y-1))/2;
			average.add(newY);
		}
		return average;
	}

	
	//returns arrayList of x values where one should split on a line
	private List<Integer> getWordSplits(BufferedImage image, int middleY){
		List<Integer> xSplits = traceSideWords(image, middleY);
		List<Integer> average = getXsBetweenSplits(xSplits, image.getWidth());
		return average;
	}
	
	private List<Integer> traceSideWords(BufferedImage image, int middleY){
		List<Integer> xSplits = Lists.newArrayList();
		int prevColor = image.getRGB(0, 0);
		
		xSplits.add(0);
		for(int x = 0; x < image.getWidth(); x++)
		{
			int currentColor = image.getRGB(x, middleY);
			if(currentColor != prevColor)
			{
				xSplits.add(x);
				prevColor = currentColor;
			}
		}
		xSplits.add(image.getWidth());
		return xSplits;	
	}
	
	private List<Integer> getXsBetweenSplits(List<Integer> xSplits, int imageWidth){
		List<Integer> average = Lists.newArrayList();
		average.add(0);
		for(int x = 1;  x < xSplits.size()-1; x += 2)
		{
			int newX = (xSplits.get(x) + xSplits.get(x+1))/2;
			average.add(newX);
		}
		average.add(imageWidth);
		return average;
	}

//	public List<Line> getAllLinesFromImage() throws IOException{
//		List<Integer> lineSplits = getLineSplits(mutableImage);
//		List<Line> allLines = Lists.newArrayList();
//
//		for(int y = 0; y < lineSplits.size()-2; y++)
//		{
//			int yTop = lineSplits.get(y);
//			int yBottom = lineSplits.get(y+1);
//			int yMiddle = (yTop + yBottom)/2;
//
//			List<Integer> wordSplits = getWordSplits(mutableImage, yMiddle);
//			List<Word> words = getWordsForLine(wordSplits, yTop, yBottom);
//			Line line = new Line(words);
//			
//			allLines.add(line);
//		}
//		return allLines;
//	}
//	
//	public List<Word> getWordsForLine(List<Integer> wordSplits, int yTop, int yBottom) {
//		List<Word> words = Lists.newArrayList();
//		
//		for(int i = 1; i < wordSplits.size(); i++)
//		{
//			int xStart = wordSplits.get(i-1);
//			int xEnd = wordSplits.get(i);
//		
//			Point topLeft = new Point(xStart, yTop);
//			Point bottomRight = new Point(xEnd, yBottom);
//			
//			BufferedImage wordImage = ImageUtils.getSubImageFrom(untouchedImage, topLeft, bottomRight);
//			LetterSet wordLetterSet = new LetterSet(100, 100, wordImage);
//			Word word = new Word(wordLetterSet);
//			
//			words.add(word);
//		}
//		return words;
//	}
	
}
