package allTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import models.Punycode;

class PunycodeTest {

	@Test
	void testToPunycode() {
		assertEquals("xn--hkyrky-ptac70bc.cz",Punycode.toPunycode("h��ky��rky.cz"));
	}
	
	@Test
	void testFromPunnycode() {
		assertEquals("h��ky��rky.cz",Punycode.fromPunnycode("xn--hkyrky-ptac70bc.cz"));
	}

}
