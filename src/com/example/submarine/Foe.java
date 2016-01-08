package com.example.submarine;

public class Foe {
	private int xPos;
	private int yPos;
	private int halfWidth;
	private int speed;
	private boolean hit;
	
	public Foe(int x, int y, int speed) {
		xPos = x;
		yPos = y;
		this.speed = speed;
	}
	
	public void moveRight() {
		xPos += speed;
	}
	
	public Point getPosition() {
		Point p =new Point(xPos,yPos);
		return p;
	}
	
	public int getPositionX() {
		return xPos;
	}
	
	public int getPositionY() {
		return yPos;
	}
	
	public boolean getHitStatus() {
		return hit;
	}
	
	public void setHitStatus(boolean hit) {
		this.hit = hit;
	}
	
	public void setPosition(int x, int y) {
		xPos = x;
		yPos = y;
	}
}
