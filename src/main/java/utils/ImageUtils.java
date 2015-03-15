package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import document.structure.Letter;

public class ImageUtils {

	private static Function<Integer, Integer> getBlue = rgb -> rgb & 0x000000FF;
	private static Function<Integer, Integer> getRed = rgb -> (rgb>>16) & 0x000000FF;
	private static Function<Integer, Integer> getGreen = rgb -> (rgb>>8) & 0x000000FF;

	public static void makeImageGrey(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x) 
		{
			for (int y = 0; y < img.getHeight(); ++y) 
			{
				int rgb = img.getRGB(x, y);
				int r = getRed.apply(rgb);
				int g = getGreen.apply(rgb);
				int b = getBlue.apply(rgb);

				int gray = (r + g + b) / 3;
				Color color = new Color(gray, gray, gray);
				img.setRGB(x, y, color.getRGB());
			}
		}
	}

	public void blurImageOld (BufferedImage image, int blurLevel) {
		for (int t = 1; t < blurLevel -1; t++) 
		{
			for (int i = 1; i < image.getWidth() -1; i++) 
			{
				for (int j = 1; j < image.getHeight() -1; j++)	
				{
					List<Point> neighborCoords = CoordinateUtils.getNeighborCoordinates(i, j, 1, true);

					int blue = totalColorForPixel(image, neighborCoords, getBlue);
					int red  = totalColorForPixel(image, neighborCoords, getRed);
					int green = totalColorForPixel(image, neighborCoords, getGreen);
					int totalPixels = neighborCoords.size();
					
					Color color = new Color(red/totalPixels, green/totalPixels, blue/totalPixels);
					image.setRGB(i, j, color.getRGB());
				}
			}
		}
	}

	public static void blurImageFast(BufferedImage image, int radius, int blurLevel) {
		blurImageHorizontal(image, radius, blurLevel);
		blurImageVertical(image, radius, blurLevel);

	}

	public static void blurImageHorizontal(BufferedImage image, int radius, int blurLevel) {
		BufferedImage newImage = image;
		for(int level = 0; level < blurLevel; level++) 
		{
			for(int y = 0; y < newImage.getHeight(); y++) 
			{
				int leftx = 0; 
				int rightx = Math.min(newImage.getWidth(), radius);

				List<Point> neighborCoords = Lists.newArrayList();
				for(int xs= leftx; xs < rightx; xs++) 
				{
					neighborCoords.add(new Point(xs, y));
				}
				int totalPixels = neighborCoords.size();
				int totalblue = totalColorForPixel(newImage, neighborCoords, getBlue);
				int totalred = totalColorForPixel(newImage, neighborCoords, getRed);
				int totalgreen = totalColorForPixel(newImage, neighborCoords, getGreen);

				Color averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);
				image.setRGB(0, y, averagedColor.getRGB());

				for(int x = 1; x < newImage.getWidth(); x++)
				{	
					int range = Math.min(newImage.getWidth(), x + radius) - Math.max(0, x);
					int prevRGB = newImage.getRGB(x-1, y);
					int nextRGB = newImage.getRGB(Math.min(newImage.getWidth()-1, x+radius), y);

					totalblue = totalblue - getBlue.apply(prevRGB) + getBlue.apply(nextRGB);
					totalred = totalred - getRed.apply(prevRGB) + getRed.apply(nextRGB);
					totalgreen = totalgreen - getGreen.apply(prevRGB) + getGreen.apply(nextRGB);

					averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);
					image.setRGB(x, y, averagedColor.getRGB());
				}
			}
		}
	}

	public static void blurImageVertical(BufferedImage image, int radius, int blurLevel) {
		BufferedImage newImage = image;
		for(int level = 0; level < blurLevel; level++) 
		{
			for(int x = 0; x < newImage.getWidth(); x++)
			{
				int topy = 0;
				int bottomy = Math.min(newImage.getHeight(), radius);

				List<Point> neighborCoords = Lists.newArrayList();
				for(int ys = topy; ys < bottomy; ys++) {
					neighborCoords.add(new Point(x, ys));
				}
				int totalPixels = neighborCoords.size();
				int totalblue = totalColorForPixel(newImage, neighborCoords, getBlue);
				int totalred = totalColorForPixel(newImage, neighborCoords, getRed);
				int totalgreen = totalColorForPixel(newImage, neighborCoords, getGreen);

				Color averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);

				image.setRGB(x, 0, averagedColor.getRGB());

				for(int y = 1; y < newImage.getHeight(); y++)
				{
					int range = Math.min(newImage.getHeight(), y+radius) - Math.max(0, y);
					int prevRGB = newImage.getRGB(x, y-1);
					int nextRGB = newImage.getRGB(x, Math.min(newImage.getHeight()-1, y + radius));

					totalblue = totalblue - getBlue.apply(prevRGB) + getBlue.apply(nextRGB);
					totalred = totalred - getRed.apply(prevRGB) + getRed.apply(nextRGB);
					totalgreen = totalgreen - getGreen.apply(prevRGB) + getGreen.apply(nextRGB);

					averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);
					image.setRGB(x, y, averagedColor.getRGB());

				}
			}
		}
	}

	private static int totalColorForPixel(BufferedImage image, List<Point> neighborCoords, Function<Integer, Integer> colorGetter) {
		int color = 0;		
		for(Point p : neighborCoords) 
		{
			color += colorGetter.apply(image.getRGB(p.X(), p.Y()));
		}
		return color;
	}
	
	
	public static List<Point> getNeighborCoordinates(Point p, int range, boolean includeCurrent) {
		return CoordinateUtils.getNeighborCoordinates(p.X(), p.Y(), range, includeCurrent);
	}

	//good level = 0.8
	public static void threshold(BufferedImage image, double level){
		int white = Color.WHITE.getRGB();
		int black = Color.BLACK.getRGB();

		for (int i = 1; i < image.getWidth() -1; i++)  
		{
			for (int j = 1; j < image.getHeight() -1; j++)	
			{
				int rgb = image.getRGB(i, j);
				Color color = new Color(getRed.apply(rgb), getGreen.apply(rgb), getBlue.apply(rgb));

				float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				float brightness = hsb[2];

				if(brightness >= level){
					image.setRGB(i, j, white);
				}
				else {
					image.setRGB(i, j, black);
				}
			}
		}
	}

	public static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	public static BufferedImage getSubImageFrom(BufferedImage image, Point topLeft, Point bottomRight) {
		int leftX = topLeft.X();
		int topY = topLeft.Y();
		int xWidth = bottomRight.X() - topLeft.X();
		int yHeight = bottomRight.Y() - topLeft.Y();
//		int xWidth = Math.max(bottomRight.X(), topLeft.X()) - Math.min(bottomRight.X(), topLeft.X());
//		int yHeight = Math.max(bottomRight.Y(), topLeft.Y()) - Math.min(bottomRight.Y(), topLeft.Y());

		BufferedImage subImage = image.getSubimage(leftX, topY, xWidth, yHeight);
		return subImage;
	}
	
	
	public static BufferedImage makeImageFromPoints(List<Point> centeredImagePoints, int width, int height, int spacing) {
		BufferedImage newImage = new BufferedImage(width+spacing*2, height+spacing*2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D    graphics = newImage.createGraphics();
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
		
		for(Point blackPixel : centeredImagePoints) {
			newImage.setRGB(blackPixel.X()+spacing, blackPixel.Y()+spacing, Color.BLACK.getRGB());
		}
		 
//		newImage.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
		return newImage;
	}


	public static void createHorizontalRedLinesAt(BufferedImage image, List<Integer> ys) {
		for(int y : ys)  {
			createHorizontalRedLineAt(image, y);
		}
	}

	public static void createHorizontalRedLineAt(BufferedImage image, Integer y){
		for(int x = 0; x < image.getWidth(); x++){
			image.setRGB(x, y, Color.RED.getRGB());
		}
	}
	public static void createVerticalRedlineAt(BufferedImage image, int topY, int bottomY, int x){
		for(int y = topY; y < bottomY; y++) {
			image.setRGB(x, y, Color.GREEN.getRGB());
		}
	}



}
