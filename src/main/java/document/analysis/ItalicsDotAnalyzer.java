package document.analysis;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class ItalicsDotAnalyzer {
	
	public void drawOrientationLineOnLetter(BufferedImage image, double[] percentageInterval1, double[] percentageInterval2){
		List<Point> pixelSet = ImageUtils.getBlackPixelPositions(image);
		
		List<Point> intervalSet1 = this.getPixelsBetweenYPercentageVals(pixelSet, percentageInterval1[0], percentageInterval1[1]);
		List<Point> intervalSet2 = this.getPixelsBetweenYPercentageVals(pixelSet, percentageInterval2[0], percentageInterval2[1]);
 		System.out.println("interval 1 set size: " + intervalSet1.size());
 		System.out.println("interval 2 set size: " + intervalSet2.size());
		
		
		Function<Integer, Integer> slopeFunct = getSlopeFunctionBetweenPixelSets(intervalSet1, intervalSet2);
		
		IntSummaryStatistics xstats = pixelSet.stream().mapToInt(p -> p.X()).summaryStatistics();
		int minx = xstats.getMin();
		int maxx = xstats.getMax();
		
		int miny = slopeFunct.apply(0);
		int maxy = slopeFunct.apply(100);
		
		System.out.println("x1: " + minx + " y1: " + miny);
		System.out.println("x2: " + maxx + " y2: " + maxy);		
		ImageUtils.drawLineBetweenPoints(new Point(minx, miny), new Point(maxx, maxy), image);
	}
	
	public Function<Integer, Integer> getSlopeFunctionBetweenPixelSets(
			List<Point> set1, List<Point> set2) { 
		Point p1 = getCentralPointOf(set1);
		Point p2 = getCentralPointOf(set2);
		
		return getSlopeFunctionBetweenPoints(p1, p2);
	}
	
	public Function<Integer, Integer> getSlopeFunctionBetweenPoints(Point p1, Point p2) {
		int x1 = p1.X();
		int x2 = p2.X();
		
		int y1 = p1.Y();
		int y2 = p2.Y();
		System.out.println("x1: " + x1 + " y1: " + y1);
		System.out.println("x2: " + x2 + " y2: " + y2);		

		double slope = (y2-y1)/(x2-x1);
		
		Function<Integer, Integer> slopeFunction = x -> y1 + (int) slope*x;
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
//		List<Point> set = pixelSet.stream().filter(p -> miny >= p.Y()).collect(Collectors.toList());
		List<Point> toRemove = Lists.newArrayList();
		for(int i = 0; i < pixelSet.size(); i++) {
			if((pixelSet.get(i).Y() < miny) && (pixelSet.get(i).Y() > maxy)) {
				toRemove.add(pixelSet.get(i));
			}
		}
		pixelSet.removeAll(toRemove);
		System.out.println("minmax: " + miny + " : " + maxy);
		System.out.println("setsize: " + pixelSet.size());
		return pixelSet;
	}
	
	
	
	private int[] findYCutoffsBetweenPercentagesFromTop(
			List<Point> pixelSet, double topPercentage, double bottomPercentage) {
		List<Integer> allYs = pixelSet.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allYs);
		
		int topYCutoff = (int) (allYs.size() * topPercentage);	
		int bottomYCutoff = (int) (allYs.size() * bottomPercentage);
		return new int[] {topYCutoff, bottomYCutoff};
	}
	
	
	public static void main(String[] args) throws IOException {
		MyImageIO imageio = new MyImageIO();
		BufferedImage iImage = imageio.openBufferedImage("D:\\Pictures\\Screenshots\\i.png");
		
		ItalicsDotAnalyzer ianalyzer = new ItalicsDotAnalyzer();
		
		ianalyzer.drawOrientationLineOnLetter(iImage, new double[] {0.3, 0.4}, new double[] {0.6, 0.7});
		imageio.saveImage(iImage, "D:\\Code\\workspace", "orientationIDrawn");
	}
}
