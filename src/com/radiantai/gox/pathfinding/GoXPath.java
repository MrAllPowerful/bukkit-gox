package com.radiantai.gox.pathfinding;

import java.util.Stack;

public class GoXPath {
	private Stack<GoXDirection> path;
	private int distance;
	
	public GoXPath() {
		path = new Stack<GoXDirection>();
		distance = 0;
	}
	
	public void Push(GoXDirection d) {
		path.push(d);
	}
	
	public GoXDirection Peek() {
		return path.peek();
	}
	
	public GoXDirection Pop() {
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
