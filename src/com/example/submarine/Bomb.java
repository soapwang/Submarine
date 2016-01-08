package com.example.submarine;

public class Bomb implements Runnable {
	private int speed;
	private int xPos;
	private int yPos;
	private int screenHeight; 
	private boolean toRun = false;
	private boolean explode;
	
	public Bomb(int xPosition, int yPosition, int bombSpeed, int height) {
		screenHeight = height;
		speed = bombSpeed;
		xPos = xPosition;
		yPos = yPosition;
		explode = false;
	}
	
	public void setRunning(boolean run) {
		toRun = run;
	}
	
	public void setExplode(boolean e) {
		explode = e;
	}
	
	public boolean isExplode() {
		return explode;
	}
	
	public int getPositionX() {
		return xPos;
	}
	
	public int getPositionY() {
		return yPos;
	}
	
	private void move() {
		if(yPos <= screenHeight) {
			yPos += speed;
		} else {
			yPos = screenHeight + speed;
			toRun = false;
			explode = true;
		}
	}
	
	public void startThread() {
		toRun = true;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while(toRun) {
			move();
            try {  
             	Thread.sleep(33);  
            } catch (InterruptedException e) {   
             	e.printStackTrace();  
            } 
		}
		
	}

}
