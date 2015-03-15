package document.analysis;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.sun.prism.paint.Color;

import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class ParagraphComponentAnalyzer {
	BufferedImage untouchedImage, mutableImage;
	MyImageIO imageio = new MyImageIO();

	
	private void preprocessImageForLineAnalysis() {
		ImageUtils.blurImageFast(mutableImage, 5,  5);
		ImageUtils.blurImageHorizontal(mutableImage, 5, 5);
		ImageUtils.threshold(mutableImage, 0.9);
	}
	
	public void setParagraphImage(BufferedImage image) {
		this.untouchedImage = ImageUtils.copyImage(image);
		this.mutableImage = ImageUtils.copyImage(image);

		preprocessImageForLineAnalysis();
	}
	
	
	public List<BufferedImage> getLineSubImages() {
		List<Integer> lineSplits = getLineSplits();
		List<BufferedImage> subImages = Lists.newArrayList();
		for(int i = 1; i < lineSplits.size(); i++) {
			int topY = lineSplits.get(i-1);
			int bottomY = lineSplits.get(i);
			int middleY = (topY+bottomY)/2;
			int leftX = getBeginningX(middleY, 10);
			int rightX = getEndX(middleY, 10);
			
			BufferedImage subImage = ImageUtils.getSubImageFrom(untouchedImage, new Point(leftX, topY), new Point(rightX, bottomY));
			subImages.add(subImage);
		}
		return subImages;
	}
	
	private int getBeginningX(int y, int buffer) {
		int prevColor = mutableImage.getRGB(0, y);
		int beginX = 0;
		for(int x = 0; x < mutableImage.getWidth(); x++) 
		{
			int currentColor = mutableImage.getRGB(x, y);
			if(currentColor != prevColor) {
				beginX = x;
				break;
			}
		}
		return Math.max(beginX - buffer, 0);
	}
	private int getEndX(int y, int buffer) {
		int endX = mutableImage.getWidth()-1;
		int prevColor = mutableImage.getRGB(endX, y);
		for(int x = endX; x >= 0; x--)
		{
			int currentColor = mutableImage.getRGB(x, y);
			if(currentColor != prevColor) {
				endX = x;
				break;
			}
		}
		return Math.min(endX + buffer, mutableImage.getWidth()-1);
	}
	
	private List<Integer> getLineSplits(){
		
		ArrayListMultimap<Integer, Integer> yTracerSamples = getVerticalTracerSampleSet(80);
		List<Integer> best = getVerticalTracerWithMostSplits(yTracerSamples);
		List<Integer> average = this.getYsBetweenSplits(best);

//		ImageUtils.createHorizontalRedLinesAt(untouchedImage, average);
//		imageio.saveImage(untouchedImage, "D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\processed\\", "lines");
		
		return average;
	}
	
	private ArrayListMultimap<Integer, Integer> getVerticalTracerSampleSet(int tracers){
		int numTracers = mutableImage.getWidth()/tracers;
		ArrayListMultimap<Integer, Integer> lineSplits = ArrayListMultimap.create(numTracers + 5, 100);
		
		int prevColor = mutableImage.getRGB(0, 0);
		for(int x = numTracers; x < mutableImage.getWidth(); x += numTracers)
		{
//			lineSplits.put(x, 0);
			for(int y = 1; y < mutableImage.getHeight(); y++)
			{
				int currentColor = mutableImage.getRGB(x, y);	
				if(currentColor != prevColor) 
				{
					lineSplits.put(x, y);
				}
				prevColor = currentColor;
			}
//			lineSplits.put(x, mutableImage.getHeight());
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
}
