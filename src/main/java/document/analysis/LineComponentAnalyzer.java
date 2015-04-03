package document.analysis;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import document.structure.Word;
import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class LineComponentAnalyzer {
	BufferedImage untouchedImage, mutableImage;
	MyImageIO imageio = new MyImageIO();

	public void setUntouchedImage(BufferedImage image) {
		this.untouchedImage = ImageUtils.copyImage(image);
		this.mutableImage = ImageUtils.copyImage(image);
	}
	public void setBlurredImage(BufferedImage image) {
		this.mutableImage = ImageUtils.copyImage(image);
	}

	public void setVerticalBlur(int neighborhood, int iterations) {
		ImageUtils.blurImageVertical(mutableImage, neighborhood, iterations);
	}
	public void setStandardBlur(int neighborhood, int iterations) {
		ImageUtils.blurImageFast(mutableImage, neighborhood, iterations);
	}
	public void setThreshold(double threshold) {
		ImageUtils.threshold(mutableImage, threshold);
	}
	
	public List<BufferedImage> getWordSubImages() {
		List<BufferedImage> subImages = Lists.newArrayList();
		List<Integer> wordSplits = getWordSplits();
		
		int topY = 0;
		int bottomY = mutableImage.getHeight();
		for(int i = 1; i < wordSplits.size(); i++) {
			int leftX = wordSplits.get(i-1);
			int rightX = wordSplits.get(i);
			
			BufferedImage subImage = ImageUtils.getSubImageFrom(untouchedImage, new Point(leftX, topY), new Point(rightX, bottomY));
			subImages.add(subImage);
		}
		return subImages;
	}
	
	public List<Word> getWordObjects() {
		List<Word> words = getWordSubImages().stream().map(img -> new Word(img)).collect(Collectors.toList());
		return words;
	}
	
	//returns arrayList of x values where one should split on a line
	public List<Integer> getWordSplits(){
		int middleY = mutableImage.getHeight()/2;
		List<Integer> xSplits = traceSideWords(middleY);
		List<Integer> average = getXsBetweenSplits(xSplits);
		return average;
	}
	
	private List<Integer> traceSideWords(int middleY){
		List<Integer> xSplits = Lists.newArrayList();
		int prevColor = mutableImage.getRGB(0, 0);
		
//		xSplits.add(0);
		for(int x = 0; x < mutableImage.getWidth(); x++)
		{
			int currentColor = mutableImage.getRGB(x, middleY);
			if(currentColor != prevColor)
			{
				xSplits.add(x);
				prevColor = currentColor;
			}
		}
//		xSplits.add(mutableImage.getWidth());
		return xSplits;	
	}
	
	private List<Integer> getXsBetweenSplits(List<Integer> xSplits){
		List<Integer> average = Lists.newArrayList();
//		average.add(0);
		for(int x = 1;  x < xSplits.size()-1; x += 2)
		{
			int newX = (xSplits.get(x) + xSplits.get(x+1))/2;
			average.add(newX);
		}
//		average.add(mutableImage.getWidth());
		return average;
	}
}
