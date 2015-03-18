package document.analysis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import utils.CoordinateUtils;
import utils.ImageUtils;
import utils.Point;

import com.google.common.collect.Lists;

import document.structure.Letter;

public class WordComponentAnalyzer {
	BufferedImage image;
	List<Point> totalBlackPixels;
	List<Letter> letters;
	
	public WordComponentAnalyzer(BufferedImage image){
		this.image = image;
		ImageUtils.threshold(image, 0.9);
		
		this.totalBlackPixels = ImageUtils.getBlackPixelPositions(image);
		this.letters = getLetters();
		combineDotsBackToIJ();
		removeSuperSmallLetters();
	}

	
	public List<BufferedImage> getLetterImages() {
//		BufferedImage wordImage = ImageUtils.makeImageFromPoints(getBlackPixelPositions(), image.getWidth(), image.getHeight(), 10);
		List<BufferedImage> images = Lists.newArrayList();
		for(Letter letter : letters) {
			images.add(letter.getCenteredLetterImage(10));
		}
		return images;
	}
	
	
	//put letter dots back on the letters
	public void combineDotsBackToIJ() {
		List<Letter> lettersToRemove = Lists.newArrayList();
		List<Integer> lettersToIgnore = Lists.newArrayList();
		for(int i = 0; i < letters.size(); i++) {
			for (int j = 0; j < letters.size(); j++) {
				if(i != j && !lettersToIgnore.contains(i) && !lettersToIgnore.contains(j)) {
					Letter letter1 = letters.get(i);
					Letter letter2 = letters.get(j);
					int pixwidth = 2;
//					if(letter1.getWidth() > pixwidth  && letter1.getHeight() > pixwidth  && letter2.getWidth() > pixwidth  && letter2.getHeight()> pixwidth ) {
					if(letter1.isOtherLetterDot(letter2)){
						letter1.gobbleLetter(letter2);
						lettersToRemove.add(letter2);
						lettersToIgnore.add(j);
//					}
					}
				}
			}
		}
		letters.removeAll(lettersToRemove);
	}
	
	public void removeSuperSmallLetters() {
		List<Letter> lettersToRemove = Lists.newArrayList();
		for(Letter letter : letters) {
			if(letter.getLetterPixelPoints().size() < 5) {
				lettersToRemove.add(letter);
			}
		}
		letters.removeAll(lettersToRemove);
	}
	
	
	public List<Letter> getLetters() {
		List<Letter> newLetters = Lists.newArrayList();
		while (lettersStillInPool()) 
		{
			List<Point> singleLetterPointSet = getSingleLetterPointSet();
			newLetters.add(new Letter(singleLetterPointSet));
		}
		return newLetters;
	}
	
	public List<Point> getSingleLetterPointSet() {
		Point firstPixel = totalBlackPixels.get(0);
		totalBlackPixels.remove(firstPixel);
		return getAllPixelsNextTo(firstPixel);
	}
	
	public List<Point> getAllPixelsNextTo(Point firstPixel) {
		List<Point> actualNeighborPixels= Lists.newArrayList(firstPixel);
		List<Point> possibleNeighborPositions = CoordinateUtils.getNeighborCoordinates(firstPixel, 1, false);
		for(Point position : possibleNeighborPositions) {
			if(totalBlackPixels.contains(position)) {
				actualNeighborPixels.add(position);
				totalBlackPixels.remove(position);
				
				actualNeighborPixels.addAll(getAllPixelsNextTo(position));
			}
		}
		return actualNeighborPixels;
	}
	
	public boolean lettersStillInPool(){
		if (totalBlackPixels.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
