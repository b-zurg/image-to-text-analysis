package recognition;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestLetterOCR {

	@Test
	public void test() {

		String toTest = "The hot days drifted by in easy sociability, dividing themselves into a pliant routine. The morning was devoted to golf on the canvas covered deck over a nine-hole course chalked around ventilators, chicken-coops and deck-houses. Crook-handled canes furnished the clubs and three sets of checkers were lost overboard before we reached the Guayas River, the little round men skidding flatly over the deck with a pleasing accuracy only at the end to rise up maliciously on one ear and roll, plop, into the sea. In the white-hot afternoon, when the scant breeze would quite as likely drift with us, the hours were sacred to the siesta, and the evenings were devoted to standardizing an international, polyglot poker.A rope stretched across the after-deck marked 28 off the steerage. There was no second class as a thrifty French tailor, a fine young man, and his soft-voiced Mediterranean bride found out. They had bought second class through to Lima and at the Boca were flung in aft among the half-breeds, a squabbling lot of steerage scum, together with a gang of Chinamen. A line of piled baggage ran lengthwise, on one side of which were supposed to be the bachelors’ quarters, though somewhere between decks were hutches where, if one really insisted on privacy, the tropical night could be passed in a fetid broil.";
		String recognized = "The hot days drifted by in easy sociabiIity, dividing themselves into a pliant routine. The morning "
							+ "was devoted to goIfon the canvas covered deck over a nine-hole course chaIked around "
							+ "ventiIators, chicken-coops and deck-houses. Crook-handled canesfurnished the clubs and three "
							+ "sets ofcheckers were Iost overboard before we reached the Guayas River, the IittIe round men "
							+ "skiddingfIatIy over the deck with a pIeasing accuracy onIy at the end to rise up maliciousIy on "
							+ "one ear and roIl, pIop, into the sea. ln the white-hot afternoon, when the scant breeze wouId "
							+ "quite as likely drift with us, the hours were sacred to the siesta, and the evenings were devoted to "
							+ "standardizing an internationaI, poIygIot poker "
							+ "A rope srretched across the after-deck marked 28 ojfthe steerage. T7Itere was no second cIas. "
							+ "a thrtfty French taiIor, afine young man, and his soft-voiced Mediterranean bridefound out. "
							+ "They had bought second cIass through to Lima and at the Boca werefIung in aft among the haIf- "
							+ "breeds, a squabbIing lot ofsteerage scum, together with a gang ofChinamen. A Iine ofpiled "
							+ "baggage ran lengthwise, on one side ofwhich were supposed to be the bacheIors ' quarters, "
							+ "though somewhere between decks were hutches where, one reaIly insisted on privacy, the "
							+ "vig. ";

		System.out.println("len: " + toTest.length());


		System.out.println(StringUtils.getLevenshteinDistance(toTest, recognized));
	}

}
