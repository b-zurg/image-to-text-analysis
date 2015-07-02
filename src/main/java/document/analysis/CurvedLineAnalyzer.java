package document.analysis;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import document.structure.Letter;
import document.structure.SlopeFunctions;
import utils.CoordinateUtils;
import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class CurvedLineAnalyzer {
	BufferedImage untouchedImage, mutableImage;
	MyImageIO imageio = new MyImageIO();

	List<Point> controlPoints = Lists.newArrayList();
	List<Point> totalBlackPixels;


	
	public void setUntouchedImage(BufferedImage image) {
		this.untouchedImage = ImageUtils.copyImage(image);
		this.mutableImage = ImageUtils.copyImage(image);
	}
	public void setBlurredImage(BufferedImage image) {
		this.mutableImage = ImageUtils.copyImage(image);
	}

	public void setHorizontalBlur(int neighborhood, int iterations) {
		ImageUtils.blurImageHorizontal(mutableImage, neighborhood, iterations);
	}
	public void setStandardBlur(int neighborhood, int iterations) {
		ImageUtils.blurImageFast(mutableImage, neighborhood, iterations);
	}
	public void setThreshold(double threshold) {
		ImageUtils.threshold(mutableImage, threshold);
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
	
	private List<Point> getSingleLetterPointSet() {
		Point firstPixel = getTotalBlackPixels().get(0);
		getTotalBlackPixels().remove(firstPixel);
		return getAllPixelsNextTo(firstPixel);
	}
	
	private List<Point> getAllPixelsNextTo(Point firstPixel) {
		List<Point> actualNeighborPixels= Lists.newArrayList(firstPixel);
		List<Point> possibleNeighborPositions = CoordinateUtils.getNeighborCoordinates(firstPixel, 1, false);
		for(Point position : possibleNeighborPositions) {
			if(getTotalBlackPixels().contains(position)) {
				actualNeighborPixels.add(position);
				getTotalBlackPixels().remove(position);
				
				actualNeighborPixels.addAll(getAllPixelsNextTo(position));
			}
		}
		return actualNeighborPixels;
	}
	
	private List<Point> getTotalBlackPixels() {
		if(totalBlackPixels == null) {
			totalBlackPixels = ImageUtils.getBlackPixelPositions(mutableImage);
		}
		return totalBlackPixels;
	}

	
	private boolean lettersStillInPool(){
		if (getTotalBlackPixels().size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public List<Point> getControlPointsForLetter() {
		List<Letter> letters = getLetters();
		List<Point> controlPoints = Lists.newArrayList();
		
		for(Letter l : letters) {
//			List<Point> intervalSet1 = getPixelsBetweenXPercentageVals(l.getLetterPixelPoints(), 0.01, 0.1);
//			List<Point> intervalSet2 = getPixelsBetweenXPercentageVals(l.getLetterPixelPoints(), 0.9, 0.99);
//			
//			controlPoints.add(getCentralPointOf(intervalSet1));
//			controlPoints.add(getCentralPointOf(intervalSet2));
			controlPoints.add(getCentralPointOf(l.getLetterPixelPoints()));
		}
		return controlPoints;
	}
	
	public List<List<Point>> getGroupedControlPoints(List<Point> totalControlPoints) {
		int yRange = 101;
		
		List<List<Point>> pointGroups = Lists.newArrayList();
		List<Point> firstPoints = getTopLeftPoints(totalControlPoints);
		List<Point> workingControlPoints = totalControlPoints.stream().filter(p -> p.X() > 0).collect(Collectors.toList());
		
		
		for(Point firstPoint : firstPoints) {
			Point currentPoint = new Point(firstPoint);
			List<Point> linePoints = Lists.newArrayList(currentPoint);
			
			
			while(true) {
				Point cp = currentPoint;
				Optional<Point> optimalPoint = workingControlPoints.stream()
						.filter(p -> p.Y() + yRange > p.Y() && p.Y() > p.Y() - yRange)
						.filter(p -> p.X() != cp.X() && p.Y() != cp.Y())
						.filter(p -> Math.abs(CoordinateUtils.getRawAngleBetween(cp, p)) < 15)						
						.min(Comparator.comparing(p -> p.X() + 9*Math.abs(CoordinateUtils.getRawAngleBetween(cp, p))));
				if(optimalPoint.isPresent()) {
					linePoints.add(optimalPoint.get());
					currentPoint = optimalPoint.get();
				} else {	
					break;
				}
			}
			pointGroups.add(linePoints);
			
		}
		return pointGroups;
	}
	
	public List<Point> getTopLeftPoints(List<Point> controlPoints) {
		List<Point> workingControlPointsList = Lists.newArrayList(controlPoints);
		List<Point> topLeftPoints = Lists.newArrayList();
		
		Point topLeft = workingControlPointsList.stream()
				.filter(p -> p.X() > 0)
				.min(Comparator.comparing(p -> p.X() + p.Y())).get();
		topLeftPoints.add(new Point(topLeft));
		System.out.println("getting top left points");
		
		while(topLeft != null) {
			Point tl = new Point(topLeft);
			
			List<Point> newWorkList = workingControlPointsList.stream()
					.filter(p -> p.Y() > tl.Y())
					.filter(p -> p.X() > 0)
					.collect(Collectors.toList());
			System.out.println(newWorkList.size());
			System.out.println("finished filtering working control points");
			
			Optional<Point> optTopLeft = newWorkList.stream().min(Comparator.comparing(p -> 0.5*p.X() + p.Y()));
			System.out.println("got top left point");
			if(optTopLeft.isPresent()) {
				topLeft = optTopLeft.get();
				topLeftPoints.add(new Point(topLeft));
			}
			if(!optTopLeft.isPresent()) break;
		}
		return topLeftPoints;
		
	}
	
	
	
	private static Point getCentralPointOf(List<Point> points) 
	{		
		List<Integer> allXs = points.stream().map(p -> p.X()).collect(Collectors.toList());
		List<Integer> allYs = points.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allXs);
		Collections.sort(allYs);
		
		return new Point(allXs.get(allXs.size()/2), allYs.get(allYs.size()/2));
	}
	
	private static List<Point> getPixelsBetweenXPercentageVals(
			List<Point> pixelSet, double leftPercentage, double rightPercentage) 
	{			
		int[] xCutoffs = findYCutoffsBetweenPercentagesFromLeft(pixelSet, leftPercentage, rightPercentage);
		int minx = xCutoffs[0];
		int maxx = xCutoffs[1];
		List<Point> set = pixelSet.stream().filter(p -> minx <= p.X() && p.X() <= maxx).collect(Collectors.toList());
		return set;
	}

	private static int[] findYCutoffsBetweenPercentagesFromLeft(
			List<Point> pixelSet, double leftPercentage, double rightPercentage) 
	{
		List<Integer> allXs = pixelSet.stream().map(p -> p.X()).collect(Collectors.toList());
		Collections.sort(allXs);
		
		int leftXCutoff = (int) (allXs.size() * leftPercentage);	
		int rightXCutoff = (int) (allXs.size() * rightPercentage);
		
		return new int[] {allXs.get(leftXCutoff), allXs.get(rightXCutoff)};
	}


}