package com.radiantai.gox.tests;

import com.radiantai.gox.structures.RollingQueue;

public class GoXTesting {

	public static void main(String[] args) {
		RollingQueue<String> q = new RollingQueue<String>(5);
		q.add("1");
		q.add("2");
		q.add("3");
		q.add("4");
		q.add("5");
		q.add("6");
		q.add("7");
		q.add("8");
		String r = q.get();
	}

}
