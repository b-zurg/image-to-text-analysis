package document.analysis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.Lists;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.image.*;
import java.util.Comparator;
import java.util.List;

import utils.CoordinateUtils;
import utils.ImageUtils;
import utils.MyImageIO;
import utils.Point;

public class testCurvedLineAnalyzer {
	MyImageIO imageio = new MyImageIO();
	ImageUtils imageUtils = new ImageUtils();
	
	CurvedLineAnalyzer cAnalyzer = new CurvedLineAnalyzer();

	@Test
	public void test() {
		BufferedImage img = imageio.openBufferedImage("C:\\Users\\zia\\Pictures\\curved.png");
		cAnalyzer.setUntouchedImage(img);
		cAnalyzer.setStandardBlur(5, 2);
		cAnalyzer.setHorizontalBlur(5, 2);
		cAnalyzer.setThreshold(0.8);
		
//		List<Point> controlPoints = cAnalyzer.getControlPointsForLetter();
//		
//		for(int i = 0; i < controlPoints.size(); i++) {
//			Point p = controlPoints.get(i);
//			ImageUtils.drawPointOnImage(img, p, 13, Color.BLUE);
//			System.out.println("new Point("+p.X()+", "+p.Y()+"), ");
//		}
//		
		List<Color> colors = Lists.newArrayList(Color.BLUE, Color.CYAN, Color.GREEN, 
				Color.MAGENTA, Color.ORANGE, Color.PINK, 
				Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, 
				Color.ORANGE, Color.PINK);
		
		
		List<Point> controlPoints = Lists.newArrayList(

				new Point(88, 99), 
				new Point(83, 185), 
				new Point(70, 319), 
				new Point(113, 404), 
				new Point(29, 58), 
				new Point(44, 274), 
				new Point(95, 361), 
				new Point(165, 7), 
				new Point(71, 437), 
				new Point(100, 138), 
				new Point(74, 225), 
				new Point(97, 55), 
				new Point(107, 270), 
				new Point(114, 225), 
				new Point(158, 266), 
				new Point(195, 308), 
				new Point(194, 217), 
				new Point(189, 437), 
				new Point(176, 49), 
				new Point(229, 130), 
				new Point(191, 177), 
				new Point(188, 90), 
				new Point(271, 346), 
				new Point(240, 256), 
				new Point(224, 45), 
				new Point(269, 84), 
				new Point(236, 171), 
				new Point(258, 437), 
				new Point(264, 300), 
				new Point(278, 392), 
				new Point(312, 40), 
				new Point(290, 209), 
				new Point(358, 158), 
				new Point(362, 437), 
				new Point(373, 286), 
				new Point(380, 241), 
				new Point(326, 119), 
				new Point(345, 384), 
				new Point(352, 202), 
				new Point(376, 72), 
				new Point(383, 112), 
				new Point(397, 193), 
				new Point(404, 330), 
				new Point(419, 376), 
				new Point(413, 21), 
				new Point(447, 106), 
				new Point(468, 23), 
				new Point(466, 0), 
				new Point(476, 186), 
				new Point(494, 319), 
				new Point(531, 364), 
				new Point(493, 225), 
				new Point(494, 273), 
				new Point(509, 99), 
				new Point(933, 7), 
				new Point(557, 174), 
				new Point(538, 265), 
				new Point(558, 218), 
				new Point(562, 91), 
				new Point(568, 13), 
				new Point(616, 304), 
				new Point(599, 258), 
				new Point(605, 86), 
				new Point(632, 207), 
				new Point(659, 163), 
				new Point(638, 348), 
				new Point(673, 250), 
				new Point(689, 80), 
				new Point(685, 437), 
				new Point(707, 294), 
				new Point(696, 345), 
				new Point(711, 200), 
				new Point(723, 247), 
				new Point(743, 155), 
				new Point(807, 437), 
				new Point(756, 338), 
				new Point(784, 243), 
				new Point(774, 199), 
				new Point(777, 76), 
				new Point(764, 292), 
				new Point(811, 154), 
				new Point(850, 293), 
				new Point(808, 341), 
				new Point(864, 198), 
				new Point(856, 76), 
				new Point(909, 247), 
				new Point(899, 342), 
				new Point(915, 158), 
				new Point(955, 437), 
				new Point(920, 78), 
				new Point(938, 296), 
				new Point(962, 205), 
				new Point(987, 82), 
				new Point(981, 163), 
				new Point(1009, 304), 
				new Point(994, 348), 
				new Point(1060, 214), 
				new Point(1096, 265), 
				new Point(1036, 354), 
				new Point(1055, 171), 
				new Point(1055, 87), 
				new Point(1095, 311), 
				new Point(1123, 365), 
				new Point(1086, 93), 
				new Point(1098, 177), 
				new Point(1128, 97), 
				new Point(1216, 194), 
				new Point(1137, 224), 
				new Point(1148, 320), 
				new Point(1209, 110), 
				new Point(1228, 32), 
				new Point(1206, 329), 
				new Point(1200, 236), 
				new Point(1241, 385), 
				new Point(1208, 281), 
				new Point(1424, 12), 
				new Point(1240, 437), 
				new Point(1301, 300), 
				new Point(1277, 251), 
				new Point(1265, 341), 
				new Point(1328, 130), 
				new Point(1335, 437), 
				new Point(1318, 45), 
				new Point(1380, 357), 
				new Point(1349, 400), 
				new Point(1341, 216), 
				new Point(1386, 268), 
				new Point(1402, 54), 
				new Point(1415, 227), 
				new Point(1427, 318), 
				new Point(1404, 408), 
				new Point(1441, 143), 
				new Point(1509, 414), 
				new Point(1459, 59), 
				new Point(1537, 237), 
				new Point(1470, 274), 
				new Point(1510, 149), 
				new Point(1563, 371), 
				new Point(1532, 64), 
				new Point(1549, 282), 
				new Point(1556, 327), 
				new Point(1575, 21), 
				new Point(1604, 65), 
				new Point(1653, 369));
		
		List<List<Point>> groupedPoints = cAnalyzer.getGroupedControlPoints(controlPoints);
		for(int i = 0; i < groupedPoints.size(); i++) {
			List<Point> pointGroup = groupedPoints.get(i);
			
			Color c;
			if(i >= colors.size()) c = Color.red;
			else c = colors.get(i);
			
			for(Point p : pointGroup){
				ImageUtils.drawPointOnImage(img, p, 12, c);			
			}
			
			Point first = pointGroup.stream().min(Comparator.comparing(p->p.X())).get();
			
			Path2D.Double path1 = new Path2D.Double();
			path1.moveTo(first.X(), first.Y());
			for(int z = 2; z < pointGroup.size(); z+=2) {
				Point p3 = pointGroup.get(z);
				Point p2 = pointGroup.get(z-1);
				Point p1 = pointGroup.get(z-2);
				
				path1.curveTo(p1.X(), p1.Y(), p2.X(), p2.Y(), p3.X(), p3.Y());
			}
			ImageUtils.drawCurveOnImage(c, img, path1);
			
			
		}
		
//		List<Point> topLeftPoints = cAnalyzer.getTopLeftPoints(controlPoints);
//		for(int i = 0; i < topLeftPoints.size(); i++) {
//			Color c = Color.red;
//
//			Point p = topLeftPoints.get(i);
////			p = new Point(50, p.Y());
//			System.out.println("X: " + p.X() + " Y: " + p.Y());
//			ImageUtils.drawPointOnImage(img, p, 12, c);			
//		}
//		
		
		imageio.saveImage(img, "C:\\Users\\zia\\Pictures\\", "DrawnCurvePointsTest");
	}
}
