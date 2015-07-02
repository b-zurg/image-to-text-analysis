package document.analysis;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import document.structure.Letter;
import utils.ImageUtils;
import utils.Point;

public class ContiguousSegmentAnalyzer {

	
	public List<Letter> getContiguousSegments(BufferedImage image) {
		List<Point> ungroupedPoints = ImageUtils.getBlackPixelPositions(image);
		List<Letter> groupedSegments = getContiguousSegments(ungroupedPoints);
	}
	
	public List<Letter> getContiguousSegments(List<Point> totalBlackPixels) {
		
		return null;
	}
	
	public List<Integer> getAllXsForY(List<Point> totalBlackPixels, int y) {
		return totalBlackPixels.stream().filter(p -> p.Y() == y).map(p -> p.X()).collect(Collectors.toList()); 
	}
	
}
