package com.radiantai.gox.tests;

import java.nio.charset.Charset;

public class GoXTesting {

	public static void main(String[] args) {
		
		String test1 = "Тест"; 
		
		byte[] testByte1 = test1.getBytes(Charset.forName("Cp1251"));
		byte[] testByte2 = {(byte)'\u0422',(byte)'\u0435',(byte)'\u0441',(byte)'\u0442'};
		
		String test2 = new String(testByte1);
		String test3 = new String(testByte2);
	}

}
