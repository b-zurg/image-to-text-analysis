package document.structure;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.CoordinateUtils;
import utils.ImageUtils;
import utils.Point;

public class Letter {
	private List<Point> originalPixels;
	private Set<Integer> allXs;
	private int originalLeft, originalRight, originalTop, originalBottom;
	private int width, height;
	
	public Letter(List<Point> originalBlackPixels) {
		originalPixels = originalBlackPixels;
		setImageRange();
	}

	private void setImageRange() {
		allXs = new HashSet<Integer>();
		
		Point firstPoint = originalPixels.get(0);
		originalBottom = firstPoint.Y();
		originalTop = firstPoint.Y();
		originalRight = firstPoint.X();
		originalLeft = firstPoint.X();
		
		for(Point t : originalPixels)
		{
			int x = t.X();
			int y = t.Y();
			allXs.add(x);
			
			if(x < originalLeft)	{ 	originalLeft = x; 	}
			if(x > originalRight)	{	originalRight = x;	}
			if(y < originalTop)		{	originalTop = y;	}
			if(y > originalBottom)	{	originalBottom = y;	}
		}
		width = originalRight - originalLeft;
		height = originalBottom - originalTop;
		

	}
	
	public List<Point> getLetterPixelPoints() {
		return originalPixels;
	}
	public BufferedImage getCenteredLetterImage(int spacing) {
//		System.out.println("original left: " + originalLeft + " original top: " + originalTop);
		List<Point> centeredImagePoints = CoordinateUtils.centerImagePoints(originalPixels, this.originalLeft, this.originalTop);
		return ImageUtils.makeImageFromPoints(centeredImagePoints, width, height, spacing);
	}
	
	
	public Set<Integer> getLetterXs() {
		return allXs;
	}
	public int getLeft() { return originalLeft; }
	public int getRight() { return originalRight; }
	public int getTop() { return originalTop; }
	public int getBottom() { return originalBottom; }
	public int getWidth()  { return originalRight - originalLeft; } 
	public int getHeight() { return originalBottom - originalTop; }
	
	public void gobbleLetter(Letter other) {
		this.originalPixels.addAll(other.getLetterPixelPoints());
		setImageRange();
	}
	
	private boolean letterIsEligibleDot(Letter other){
		if (other.getLeft() > this.getRight()){
			return false;
		} 
		else if (this.getLeft() > other.getRight() ) {
			return false;
		}
		else { 
			return true;
		}
	}
	
	public boolean isOtherLetterDot(Letter other, double percentageThreshold) {
		if(letterIsEligibleDot(other)){
		
			Set<Integer> otherLetterXs = other.getLetterXs();
			
			int otherSize = otherLetterXs.size();
			int hits = 0;
			
			for(int x : otherLetterXs){
				if (allXs.contains(x)){
					hits++;
				}
			}
			
			float percentageLikely = hits/otherSize;
			if(percentageLikely > percentageThreshold) {
				return true;
			} else {
				return false;
			}
		}
		else { return false; }
	}
}
