package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import document.structure.SlopeFunctions;
import document.structure.SlopeFunctions.SlopeFunctionsBuilder;

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
	
	public static SlopeFunctions getSlopeFunctionBetweenPoints(Point p1, Point p2) {
		int x1 = p1.X(); int x2 = p2.X();
		int y1 = p1.Y(); int y2 = p2.Y();
		int rise = (y2 - y1); int run = (x2 - x1);
		
		double slope = run != 0 ? rise/run : 0;
		
		double yIntercept1 = (int) (y1 - (slope*x1));
		double yIntercept2 = (int) (y2 - (slope*x2));
		double yInterceptFinal = (int) ((yIntercept1 + yIntercept2) /2);		
		
		double slope2 = slope;
		Function<Integer, Integer> xInFunction = 
				slope != 0 
					? x -> (int) (yInterceptFinal + slope2*x)
					: x -> y1;
		Function<Integer, Integer> yInFunction = 
				slope != 0 
					? y -> (int) ((y-yInterceptFinal)/slope2)
					: y -> x1;
		
		return new SlopeFunctions.SlopeFunctionsBuilder().
				setXInputYFunction(xInFunction).
				setYInputXFunction(yInFunction).
				build();
	}
	
	
}
