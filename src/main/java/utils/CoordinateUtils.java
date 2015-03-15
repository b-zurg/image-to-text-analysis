package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

public class CoordinateUtils {

	
	public static List<Point> getNeighborCoordinates(int x, int y, int range, boolean includeCurrent){
		List<Point> coords = Lists.newArrayList();


		for(int modx = x-range; modx <= x+range; modx++)
		{
			for(int mody = y-range; mody <= y+range; mody++)
			{
				coords.add(new Point(modx, mody));
			}
		}
		
		if(!includeCurrent) { 
			coords.remove(new Point(x, y)); 
		}
		return coords;
	}
	
	public static List<Point> getNeighborCoordinates(Point p, int range, boolean includeCurrent) {
		return getNeighborCoordinates(p.X(), p.Y(), range, includeCurrent);
	}

	public static List<Point> centerImagePoints(List<Point> onPoints, int left, int top){
		List<Point> originalImagePoints = Lists.newArrayList(onPoints);
		List<Point> offsetImagePoints = Lists.newArrayList();
		for(Point t : originalImagePoints){
			int x = t.X();
			int y  = t.Y();
			int xSubtract = left;
			int ySubtract = top;
			
			int finalX = x - xSubtract;
			int finalY = y - ySubtract;
			
			Point z = new Point(finalX, finalY);
			offsetImagePoints.add(z);
		}

		return offsetImagePoints;
	}
	
	public static int[] getImageRange(List<Point> imagePoints) {
		Point firstPoint = imagePoints.get(0);
		int bottom = firstPoint.Y();
		int top = firstPoint.Y();
		int right = firstPoint.X();
		int left = firstPoint.X();
		
		for(Point t : imagePoints){

			int x = t.X();
			int y = t.Y();
			if(x < left)	{ 	left = x; 	}
			if(x > right)	{	right = x;	}
			if(y < top)		{	top = y;	}
			if(y > bottom)	{	bottom = y;	}
		}
		return new int[] {left, right, top, bottom};
	}
	
}