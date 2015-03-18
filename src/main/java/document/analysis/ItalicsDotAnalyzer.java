package document.analysis;

import java.io.IOException;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import document.analysis.containers.SlopeFunctions;
import document.structure.Letter;
import utils.CoordinateUtils;
import utils.Point;

public class ItalicsDotAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(ItalicsDotAnalyzer.class);

	
	
	public static boolean isLetterDotOfLetter(
			Letter letter, Letter dot, 
			double[] percentageInterval1, double[] percentageInterval2) 
	{	
		log.info("Starting dot analysis of two Letter objects");
		
		List<Point> dotPoints = dot.getLetterPixelPoints();
		List<Point> letterPoints = letter.getLetterPixelPoints();
		
		int bufferWidth = getWidthOfLetterShaft(letterPoints);
		
		SlopeFunctions functions  =  getSlopeFunctionsForLetter(letterPoints, percentageInterval1, percentageInterval2);

		int starty = getTopYOfLetter(letter.getLetterPixelPoints());
		int endy = 0;
		
        for(int y = dot.getTop(); y < dot.getBottom(); y++) 
        {	
        	int expectedx = functions.getXInputY(y);
        	int expectedmin = expectedx - bufferWidth*2;
        	int expectedmax = expectedx + bufferWidth*2;
        	
        	if(!(expectedmin < dot.getLeft() && dot.getRight() < expectedmax)) {
        		return false;
        	}
        }
        log.info("Finished dot analysis of letters");
        return true;
	}
	
	private static int getTopYOfLetter(List<Point> letter) 
	{
		IntSummaryStatistics yStats = letter.stream().mapToInt(p -> p.Y()).summaryStatistics();
		return yStats.getMin();
	}
	
	private static int getWidthOfLetterShaft(List<Point> letter) 
	{
		Point centerPoint = getCentralPointOf(getPixelsBetweenYPercentageVals(letter, 0.5, 0.51));
		IntSummaryStatistics xstats = letter.stream().filter(p -> p.Y() == centerPoint.Y()).mapToInt(p -> p.X()).summaryStatistics();
		int width = xstats.getMax() - xstats.getMin();
		return width;
	}
	
	private static SlopeFunctions getSlopeFunctionsForLetter(
			List<Point> letter,
			double[] percentageInterval1, 
			double[] percentageInterval2) 
	{
		log.info("Creating letter slope functions");
		
		List<Point> intervalSet1 = getPixelsBetweenYPercentageVals(letter, percentageInterval1[0], percentageInterval1[1]);
		List<Point> intervalSet2 = getPixelsBetweenYPercentageVals(letter, percentageInterval2[0], percentageInterval2[1]);
		
		return getSlopeFunctionBetweenPixelSets(intervalSet1, intervalSet2);
	}
	
	private static SlopeFunctions getSlopeFunctionBetweenPixelSets(
			List<Point> set1, List<Point> set2) 
	{ 
		log.info("Finding slope between two point sets");
		
		Point p1 = getCentralPointOf(set1);
		Point p2 = getCentralPointOf(set2);
		
		return CoordinateUtils.getSlopeFunctionBetweenPoints(p1, p2);
	}
	
	private static Point getCentralPointOf(List<Point> points) 
	{
		log.info("Finding central point of Point set");
		
		List<Integer> allXs = points.stream().map(p -> p.X()).collect(Collectors.toList());
		List<Integer> allYs = points.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allXs);
		Collections.sort(allYs);
		
		return new Point(allXs.get(allXs.size()/2), allYs.get(allYs.size()/2));
	}
	
	private static List<Point> getPixelsBetweenYPercentageVals(
			List<Point> pixelSet, double topPercentage, double bottomPercentage) 
	{	
		log.info("Finding Point set between percentages of Point set");
		
		int[] yCutoffs = findYCutoffsBetweenPercentagesFromTop(pixelSet, topPercentage, bottomPercentage);
		int miny = yCutoffs[0];
		int maxy = yCutoffs[1];
		List<Point> set = pixelSet.stream().filter(p -> miny <= p.Y() && p.Y() <= maxy).collect(Collectors.toList());
		return set;
	}

	private static int[] findYCutoffsBetweenPercentagesFromTop(
			List<Point> pixelSet, double topPercentage, double bottomPercentage) 
	{
		log.info("Finding y value corresponding to percentage traversal from top of Point set");
		List<Integer> allYs = pixelSet.stream().map(p -> p.Y()).collect(Collectors.toList());
		Collections.sort(allYs);
		
		int topYCutoff = (int) (allYs.size() * topPercentage);	
		int bottomYCutoff = (int) (allYs.size() * bottomPercentage);
		return new int[] {allYs.get(topYCutoff), allYs.get(bottomYCutoff)};
	}
	
	
	public static void main(String[] args) throws IOException {

	}
}
