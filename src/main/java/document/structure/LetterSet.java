package document.structure;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import utils.ImageUtils;
import utils.Point;

@Deprecated
public class LetterSet {
	private List<Point> on;
	private List<List<Point>> letterPointSets;

	private int width;
	private int height;
	private BufferedImage img;

	public LetterSet(int width, int height, BufferedImage image){
		this.width = width;
		this.height = height;
		this.img = ImageUtils.copyImage(image);

		ImageUtils.threshold(img, 0.8);
		this.letterPointSets = createLetterPointSets();
	}

	public List<Point> getOnPoints(){
		int black = Color.BLACK.getRGB();
		List<Point> onPoints = Lists.newArrayList();
		
		for(int x = 0; x < img.getWidth(); x++){
			for(int y = 0; y < img.getHeight(); y++){
				if(img.getRGB(x, y) == black){
					onPoints.add(new Point(x, y)); 
				}
			}
		}
		return onPoints;
	}

	public List<Point> centerImagePoints(List<Point> onPoints){
		List<Point> originalImagePoints = Lists.newArrayList(onPoints);
		List<Point> offsetImagePoints = Lists.newArrayList();

		Point firstPoint = originalImagePoints.get(0);
		int bottom = firstPoint.Y();
		int top = firstPoint.Y();
		int right = firstPoint.X();
		int left = firstPoint.X();

		for(Point t : originalImagePoints){

			int x = t.X();
			int y = t.Y();
			if(x < left)	{ 	left = x; 	}
			if(x > right)	{	right = x;	}
			if(y < top)		{	top = y;	}
			if(y > bottom)	{	bottom = y;	}
		}

		int imageWidth = Math.abs(right - left);
		int imageHeight = Math.abs(bottom - top);
		int xOffset = (this.width - imageWidth)/2;
		int yOffset = (this.height - imageHeight)/2;

		for(Point t : originalImagePoints){
			int x = t.X();
			int y  = t.Y();
			int xAdd = x - left;
			int yAdd = y - top;
			int xSubtract = t.X() - xOffset;
			int ySubtract = t.Y() - yOffset;
			Point z = new Point(x - xSubtract + xAdd, y - ySubtract + yAdd);
			offsetImagePoints.add(z);
		}

		return offsetImagePoints;
	}

	public BufferedImage getImage(int index){
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		if(letterPointSets.get(index) == null){
			System.err.println("Error: index out of bounds");
			return null;
		}

		for(Point t : letterPointSets.get(index)){
			image.setRGB(t.X(), t.Y(), Color.BLACK.getRGB());
			
		}
		return image;
	}

	
	public List<BufferedImage> getImageSet(){
		List<BufferedImage> imageSet = new ArrayList<BufferedImage>();
		for(int i = 0; i < this.letterPointSets.size(); i++){
			imageSet.add(getImage(i));
		}
		return imageSet;
	}

	public List<List<Point>> createLetterPointSets(){
		List<List<Point>> letterPointSets = Lists.newArrayList();
		List<Point> onPoints = getOnPoints();
		while(!onPoints.isEmpty()){
			List<Point> curImagePoints = this.getAllNextTo(onPoints);
			if(curImagePoints.size() == 0) 
			{ 
				break; 
			}
			List<Point> letterPoints = this.centerImagePoints(curImagePoints);
			letterPointSets.add(letterPoints);
		}                                        
		return letterPointSets;
	}

	private List<Point> getAllNextTo(List<Point> onPoints){
		Point current = onPoints.get(0);
		List<Point> next = Lists.newArrayList();                             
		List<Point> possible = ImageUtils.getNeighborCoordinates(current, 1, true);
		for(Point other : possible){
			if(onPoints.contains(other)){
				next.add(other);
				onPoints.remove(other);
				next.addAll(this
						.getAllNextTo(onPoints));
			}
		}
		return next;
	}
}
