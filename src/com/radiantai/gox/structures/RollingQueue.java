package com.radiantai.gox.structures;

import java.util.LinkedList;

public class RollingQueue<T> {
	private LinkedList<T> queue;
	private int max;
	
	public RollingQueue(int max) {
		this.max = max;
		queue = new LinkedList<T>();
	}
	
	public T get() {
		return queue.peekFirst();
	}
	
	public void add(T obj) {
		queue.add(obj);
		if (queue.size() > max) {
			queue.pollFirst();
		}
	}
	
	public String toString() {
		String result = "";
		for (T el : queue) {
			result+="\n"+el.toString();
		}
		return result;
	}
}
