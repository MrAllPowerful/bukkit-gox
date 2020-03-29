package com.radiantai.gox.structures;

import com.radiantai.gox.pathfinding.GoXNode;

public class PathNode implements Comparable<PathNode> {

	private final GoXNode current;
	private PathNode prev;
	private String fromPrev;
	private int distance;
	private int estimated;
	
	public PathNode(GoXNode current) {
		this.current = current;
		this.fromPrev = null;
		this.prev = null;
		this.distance = Integer.MAX_VALUE;
		this.estimated = Integer.MAX_VALUE;
	}
	
	public PathNode(GoXNode current, PathNode prev, String fromPrev, int distance, int estimated) {
		this.current = current;
		this.fromPrev = fromPrev;
		this.prev = prev;
		this.distance = distance;
		this.estimated = estimated;
	}
	
	public GoXNode getCurrent() {
		return current;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getEstimated() {
		return estimated;
	}

	public void setEstimated(int estimated) {
		this.estimated = estimated;
	}

	public String getFromPrev() {
		return fromPrev;
	}

	public PathNode setFromPrev(String fromPrev) {
		this.fromPrev = fromPrev;
		return this;
	}
	
	public PathNode getPrev() {
		return prev;
	}

	public PathNode setPrev(PathNode prev) {
		this.prev = prev;
		return this;
	}
	
	@Override
	public int compareTo(PathNode other) {
	    if (this.estimated > other.estimated) {
	        return 1;
	    } else if (this.estimated < other.estimated) {
	        return -1;
	    } else {
	        return 0;
	    }
	}

}
