package com.radiantai.gox.pathfinding;

import java.util.LinkedList;
import java.util.Stack;

public class GoXPath {
	private Stack<String> path;
	private int distance;
	
	public GoXPath() {
		path = new Stack<String>();
		distance = 0;
	}
	
	public void Push(String s) {
		path.push(s);
	}
	
	public String Peek() {
		return path.peek();
	}
	
	public String Pop() {
		return path.pop();
	}
	
	public boolean IsEmpty() {
		return path.isEmpty();
	}
	
	public String toString() {
		return path.toString();
	}
	
	public void addDistance(int distance) {
		this.distance += distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
}
