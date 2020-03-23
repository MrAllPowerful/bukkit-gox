package com.radiantai.gox.pathfinding;

import java.util.LinkedList;
import java.util.Stack;

public class GoXPath {
	private Stack<String> path;
	
	public GoXPath() {
		path = new Stack<String>();
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
}