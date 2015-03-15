package document.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import document.analysis.ImageComponentAnalyzer;

public class TestImageComponentAnalyzer {

	@Test
	public void test() throws IOException {
		ImageComponentAnalyzer analyzer = new ImageComponentAnalyzer("D:\\Code\\workspace\\ImageToText\\src\\test\\resources\\documents\\paragraph3.png");
//		List<Line> lines = analyzer.getAllLinesFromImage();
		List<Integer> ll = analyzer.getLineSplits();
	}
}
