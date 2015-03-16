package document.analysis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;




import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class ItalicsDotAnalyzer {
	
	public void drawOrientationLineOnLetter(BufferedImage image, double[] percentageInterval1, double[] percentageInterval2){
		List<Point> pixelSet = ImageUtils.getBlackPixelPositions(image);
		
		List<Point> intervalSet1 = this.getPixelsBetweenYPercentageVals(pixelSet, percentageInterval1[0], percentageInterval1[1]);
		List<Point> intervalSet2 = this.getPixelsBetweenYPercentageVals(pixelSet, percentageInterval2[0], percentageInterval2[1]);
		Point p1 = getCentralPointOf(intervalSet1);
		Point p2 = getCentralPointOf(intervalSet2);
		
		Function<Integer, Integer> slopeFunct = getSlopeFunctionBetweenPixelSets(intervalSet1, intervalSet2);
		
		IntSummaryStatistics xstats = pixelSet.stream().mapToInt(p -> p.X()).summaryStatistics();
		int minx = xstats.getMin();
		int maxx = xstats.getMax();
		
		int miny = slopeFunct.apply(minx);
		int maxy = slopeFunct.apply(maxx);		
		
		ImageUtils.drawPointOnLetter(image, p1);
		ImageUtils.drawPointOnLetter(image, p2);
		ImageUtils.drawLineBetweenPoints(new Point(minx, miny), new Point(maxx, maxy), image);
	}
	

	
	
	public Function<Integer, Integer> getSlopeFunctionBetweenPixelSets(
			List<Point> set1, List<Point> set2) { 
		Point p1 = getCentralPointOf(set1);
		Point p2 = getCentralPointOf(set2);
		
		return getSlopeFunctionBetweenPoints(p1, p2);
	}
	
	public Function<Integer, Integer> getSlopeFunctionBetweenPoints(Point p1, Point p2) {
		int x1 = p1.X(); int x2 = p2.X();
		int y1 = p1.Y(); int y2 = p2.Y();
		double slope = (y2-y1)/(x2-x1);		
		double yIntercept = -(int) slope*x1 + y1;
		
		Function<Integer, Integer> slopeFunction = x -> (int) (yIntercept + slope*x);
		return slopeFunction;
	}
	
	
	private Point getCentralPointOf(List<Point> points) {
		List<Integer> allXs = points.stream().map(p -> p.X()).collect(Collectors.toList());
		List<Integer> allYs = points.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allXs);
		Collections.sort(allYs);
		
		return new Point(allXs.get(allXs.size()/2), allYs.get(allYs.size()/2));
	}
	
	
	
	private List<Point> getPixelsBetweenYPercentageVals(
			List<Point> pixelSet, double topPercentage, double bottomPercentage) {
		
		int[] yCutoffs = findYCutoffsBetweenPercentagesFromTop(pixelSet, topPercentage, bottomPercentage);
		int miny = yCutoffs[0];
		int maxy = yCutoffs[1];
		List<Point> set = pixelSet.stream().filter(p -> miny <= p.Y() && p.Y() <= maxy).collect(Collectors.toList());
		return set;
	}
	
	
	
	private int[] findYCutoffsBetweenPercentagesFromTop(
			List<Point> pixelSet, double topPercentage, double bottomPercentage) {
		List<Integer> allYs = pixelSet.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allYs);
		
		int topYCutoff = (int) (allYs.size() * topPercentage);	
		int bottomYCutoff = (int) (allYs.size() * bottomPercentage);
		return new int[] {allYs.get(topYCutoff), allYs.get(bottomYCutoff)};
	}
	
	
	public static void main(String[] args) throws IOException {
		MyImageIO imageio = new MyImageIO();
		BufferedImage iImage = imageio.openBufferedImage("D:\\Pictures\\Screenshots\\i.png");
		
		ItalicsDotAnalyzer ianalyzer = new ItalicsDotAnalyzer();
		
		ianalyzer.drawOrientationLineOnLetter(iImage, new double[] {0.3, 0.4}, new double[] {0.5, 0.6});
		imageio.saveImage(iImage, "D:\\Code\\workspace", "orientationIDrawn");
	}
}
