package com.example.submarine;

import java.util.Random;

public class Foe {
	public static int LEFT = 1;
	public static int RIGHT = 2;
	private int xPosInit;
	private int xPos;
	private int yPos;
	private int width;
	private int speed;
	private int layer;	//潜艇所在的层(深度)
	private int direction;
	private int delay;
	private boolean hit;
	private Random r = new Random();
	
	public Foe(int screenWidth, int layer, int speed) {
		this.layer = layer;
		this.speed = speed;
		width = screenWidth;
		/*
		direction = r.nextInt(10)%2 + 1;
		delay = r.nextInt(50);
		if(direction == LEFT)
			xPos = screenWidth + screenWidth/24;
		else xPos = 0 - screenWidth/24;
		xPosInit = xPos;
		*/
		reset();
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void move() {
		if(delay > 0)
			delay--;
		else {
			if(direction == RIGHT)
				xPos += speed;
			else if(direction == LEFT)
				xPos -= speed;
		}
	}
	
	public int getLayer() {
		return layer;
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
	
	public int getDirection() {
		return direction;
	}
	
	public void setHitStatus(boolean hit) {
		this.hit = hit;
	}
	
	public void reset() {
		direction = r.nextInt(10)%2 + 1;
		delay = 5+r.nextInt(50);
		if(direction == LEFT)
			xPos = width + width/24;
		else xPos = 0 - width/24;
//		xPosInit = xPos;
//		xPos = xPosInit;
//		delay = r.nextInt(50);
		hit = false;
	}
}
