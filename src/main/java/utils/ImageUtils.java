package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import javaxt.io.*;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinAttributes;
import marvin.util.MarvinPluginLoader;

import org.marvinproject.image.transform.rotate.*;

import com.google.common.collect.Lists;

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
		BufferedImage referenceImage = ImageUtils.copyImage(image);
		for(int level = 0; level < blurLevel; level++) 
		{
			for(int y = 0; y < image.getHeight(); y++) 
			{
				int leftx = 0; 
				int rightx = Math.min(image.getWidth()-1, radius);

				List<Point> neighborCoords = Lists.newArrayList();
				for(int xs= leftx; xs < rightx; xs++) 
				{
					neighborCoords.add(new Point(xs, y));
				}
				int totalPixels = neighborCoords.size();
				int totalblue = totalColorForPixel(referenceImage, neighborCoords, getBlue);
				int totalred = totalColorForPixel(referenceImage, neighborCoords, getRed);
				int totalgreen = totalColorForPixel(referenceImage, neighborCoords, getGreen);

				Color averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);
				image.setRGB(0, y, averagedColor.getRGB());

				for(int x = 1; x < image.getWidth(); x++)
				{	
					leftx = Math.max(0, x - radius);
					rightx = Math.min(image.getWidth()-1, x + radius);
					
					int range = rightx - leftx;
					int prevRGB = referenceImage.getRGB(leftx, y);
					int nextRGB = referenceImage.getRGB(rightx, y);

					if(leftx == 0) {
						totalblue = totalblue + getBlue.apply(nextRGB);
						totalred = totalred + getRed.apply(nextRGB);
						totalgreen = totalgreen + getGreen.apply(nextRGB);
					}
					else if(rightx == image.getWidth()-1) {
						totalblue = totalblue - getBlue.apply(prevRGB);
						totalred = totalred - getRed.apply(prevRGB);
						totalgreen = totalgreen - getGreen.apply(prevRGB);
						range--;
					}
					else {
						totalblue = totalblue - getBlue.apply(prevRGB) + getBlue.apply(nextRGB);
						totalred = totalred - getRed.apply(prevRGB) + getRed.apply(nextRGB);
						totalgreen = totalgreen - getGreen.apply(prevRGB) + getGreen.apply(nextRGB);
					}
					int newred = Math.min(255, Math.max(0, totalred/range));
					int newgreen = Math.min(255, Math.max(0, totalgreen/range));
					int newblue = Math.min(255, Math.max(0, totalblue/range));
					averagedColor = new Color(newred, newgreen, newblue);
					image.setRGB(x, y, averagedColor.getRGB());
				}
			}
			referenceImage = ImageUtils.copyImage(image);
		}
	}

	public static void blurImageVertical(BufferedImage image, int radius, int blurLevel) {
		BufferedImage referenceImage = ImageUtils.copyImage(image);
		
		for(int level = 0; level < blurLevel; level++) 
		{
			for(int x = 0; x < image.getWidth(); x++)
			{
				int topy = 0;
				int bottomy = Math.min(image.getHeight()-1, radius);

				List<Point> neighborCoords = Lists.newArrayList();
				for(int ys = topy; ys < bottomy; ys++) {
					neighborCoords.add(new Point(x, ys));
				}
				int totalPixels = neighborCoords.size();
				int totalblue = totalColorForPixel(referenceImage, neighborCoords, getBlue);
				int totalred = totalColorForPixel(referenceImage, neighborCoords, getRed);
				int totalgreen = totalColorForPixel(referenceImage, neighborCoords, getGreen);

				Color averagedColor = new Color(totalred/totalPixels, totalgreen/totalPixels, totalblue/totalPixels);

				image.setRGB(x, 0, averagedColor.getRGB());

				for(int y = 1; y < image.getHeight(); y++)
				{
					topy = Math.max(0, y - radius);
					bottomy = Math.min(image.getHeight()-1, y + radius);
					
					int range = bottomy - topy;
					int whiteRGB = Color.WHITE.getRGB();
					int prevRGB = referenceImage.getRGB(x, topy);
					int nextRGB = referenceImage.getRGB(x, bottomy);

					if(topy == 0) {
						totalblue = totalblue + getBlue.apply(nextRGB);
						totalred = totalred + getRed.apply(nextRGB);
						totalgreen = totalgreen + getGreen.apply(nextRGB);
					}
					else if(bottomy == image.getHeight()-1) {
						totalblue = totalblue - getBlue.apply(prevRGB);
						totalred = totalred - getRed.apply(prevRGB);
						totalgreen = totalgreen - getGreen.apply(prevRGB);
						range--;
					}
					else {
						totalblue = totalblue - getBlue.apply(prevRGB) + getBlue.apply(nextRGB);
						totalred = totalred - getRed.apply(prevRGB) + getRed.apply(nextRGB);
						totalgreen = totalgreen - getGreen.apply(prevRGB) + getGreen.apply(nextRGB);
					}
					
					int newred = totalred/range;
					int newgreen = totalgreen/range;
					int newblue = totalblue/range;
					
					newred = Math.min(255, Math.max(0, newred));
					newgreen = Math.min(255, Math.max(0, newgreen));
					newblue = Math.min(255, Math.max(0, newblue));
					averagedColor = new Color(newred, newgreen, newblue);					
					image.setRGB(x, y, averagedColor.getRGB());
				}
			}
			referenceImage = ImageUtils.copyImage(image);
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
	
	public static BufferedImage openImageFromFile(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}
	
	public static BufferedImage rotateImage(BufferedImage image, int degrees) {
		double angles = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(angles)), cos = Math.abs(Math.cos(angles));
        
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
        int transparency = image.getColorModel().getTransparency();
        
        BufferedImage result = new BufferedImage(neww, newh, image.getType());
        Graphics2D g = result.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON) ;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC) ;
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY) ;
        
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angles, w/2, h/2);
        g.drawRenderedImage(image, null);
        return result;
	}
	
	public static BufferedImage skewImage(BufferedImage image, Point ul, Point ur, Point lr, Point ll) {
		javaxt.io.Image jxtimage = new javaxt.io.Image(ImageUtils.copyImage(image));
		int width = image.getWidth();
		int height = image.getHeight();
		jxtimage.setCorners(ul.X(), ul.Y(),              //UL
		                 ur.X(), ur.Y(),         //UR
		                 lr.X(), lr.Y(), //LR
		                 ll.X(), ll.Y());         //LL
		return jxtimage.getBufferedImage();
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
			int x = Math.min(Math.max(blackPixel.X()+spacing, 0), newImage.getWidth()-1);
			int y = Math.min(Math.max(blackPixel.Y()+spacing, 0), newImage.getHeight()-1);
			
			newImage.setRGB(x, y, Color.BLACK.getRGB());
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
	public static void createVerticalRedlineAt(BufferedImage image, int x){
		for(int y = 0; y < image.getHeight(); y++) {
			image.setRGB(x, y, Color.GREEN.getRGB());
		}
	}
	
	public static void createVerticalRedLinesAt(BufferedImage image, List<Integer> xs) {
		for(int x : xs) {
			createVerticalRedlineAt(image, x);
		}
	}
	
	public static void drawLineBetweenPoints(Color color, Point p1, Point p2, BufferedImage image) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.drawLine(p1.X(), p1.Y(), p2.X(), p2.Y());
	}
	
	public static void drawPointOnImage(BufferedImage image, Point p1, int radius, Color color) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillOval(p1.X()-radius, p1.Y()-radius, radius, radius);
	}
	
	public static void drawCurveOnImage(Color color, BufferedImage image, Path2D.Double path) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(color);
		g.setStroke(new BasicStroke(3));
		g.draw(path);
	}

	
	public static List<Point> getBlackPixelPositions(BufferedImage image){
		List<Point> pixelPositions = Lists.newArrayList();
//		int black = Color.BLACK.getRGB();
		int white = Color.WHITE.getRGB();
		
		for(int x = 0; x < image.getWidth(); x++)
		{
			for(int y = 0; y < image.getHeight(); y++)
			{
				int imageRGB = image.getRGB(x, y);
				if(white > imageRGB){
					pixelPositions.add(new Point(x, y));
				}
			}
		}
		return pixelPositions;
	}


}
