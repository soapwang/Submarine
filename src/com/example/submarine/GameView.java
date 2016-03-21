package com.example.submarine;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//绘图线程在此类中工作
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	
	public final static int STOP = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int FRAME_RATE = 60;
	private final static int REFILL_TIME = 75;
	private final static int EXPLODE_TIME = 5;
	
	private ArrayList<Bomb> bombList;	//存放所有炸弹的表
	private ArrayList<Foe> foeList;		//存放敌人的表
 	private int ammo;	//剩余弹药量
	private int xPos;    
	private int yPos;
	private int width;	//屏幕宽度   
	private int height;	//屏幕高度
	private int boatSpeed;
	private int bombSpeed;
	private int boatWidthHalf; //船只宽度的一半
	private int boatHeightHalf;
	private int foeWidthHalf;
	private int foeHeightHalf;
	private int bombRadius;
	private int foeSpeedSlow;
	private int foeSpeedFast;
	private int refillTimer;
	private int explodeTimer;	//控制爆炸效果的时间
	private int layerHeight;
	private long time;
	private long cTime;
	private SurfaceHolder surfaceHolder; 
	private boolean exitThread = false;
	private boolean haveFoes = false;
	private int direction;
	private Canvas canvas;
	private Foe foe;
	private Bitmap backgroundPic;	//背景图片
	private Bitmap boatPic;
	private Bitmap subPicR;
	private Bitmap subPicL;
	private Bitmap explodePic;
	private Bitmap bombPic;
	public static final String UPDATE_AMMO = "com.example.submarine.updateammo";
	public static final String UPDATE_SCORE_10 = "com.example.submarine.updatescore10";
	private Rect surfaceFrame;	//确定sufaceView的显示范围
	private Context mContext;
	private Rect boatRect;	//船只占据的矩形区域
	private Rect explodeRect;
	private Rect bombRect;
	private Rect foeRect;
	private SoundPool soundPool;


	public GameView(Context context) {
		super(context);
		mContext = context;
	    surfaceHolder = this.getHolder(); // 获取SurfaceHolder对象  
        surfaceHolder.addCallback(this); // 添加回调  
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(mContext, R.raw.exploding_sound, 1);
        soundPool.load(mContext, R.raw.water_splash, 2);
        bombList = new ArrayList<Bomb>();
        foeList = new ArrayList<Foe>();
        ammo = 5;
        refillTimer = REFILL_TIME;
        explodeTimer = EXPLODE_TIME;
        backgroundPic = 
        		BitmapFactory.decodeResource(getResources(), R.drawable.bg); //加载背景图片
        boatPic = BitmapFactory.decodeResource(getResources(), R.drawable.destoryer);
        subPicR = BitmapFactory.decodeResource(getResources(), R.drawable.sub_1r);
        subPicL = BitmapFactory.decodeResource(getResources(), R.drawable.sub_1l);
        explodePic = BitmapFactory.decodeResource(getResources(), R.drawable.explode);
        bombPic = BitmapFactory.decodeResource(getResources(), R.drawable.depth_charge);
	}
	
	public void createBomb() {
		if(ammo > 0) {
			Bomb b = new Bomb(xPos, yPos, bombSpeed, height);
			bombList.add(b);
			b.startThread();
			ammo--;
			soundPool.play(2, 0.2f, 0.2f, 0, 0, 2f);
		}
	}
	
	
	public void refill() { //装填弹药
		if(ammo < 5)
			ammo++;
	}
	public int getAmmoCount() { //返回剩余弹药数量
		return ammo;
	}
	
	public void startThread() {
		exitThread = false;
		new Thread(this).start();
	}
	
	public void stopThread() {
		exitThread = true;
	}
	
	public void setDirection(int direct) {
		direction = direct;
	}
	
    public void run() {
    	/*
    	 *Canvas c;    
          while (!exitThread) {    
              long cTime = System.currentTimeMillis();    
              if ((cTime - time) <= (1000 / FRAME_RATE)) {    
                  c = null;    
                  try {    
                      c = surfaceHolder.lockCanvas(null);    
                      mView.updatePhysics();    
                      mView.onDraw(c);    
                  } finally {    
                      if (canvas != null) {    
                    	  surfaceHolder.unlockCanvasAndPost(canvas);  
                      }    
                  }    
              }    
              time = cTime;    
          }    
          */
    	while(!exitThread) {
    		if(ammo != 5) {
        		refillTimer--;
        		if(refillTimer < 0) {
        			refill();
        			refillTimer = REFILL_TIME;
        			Intent i = new Intent(UPDATE_AMMO);
        			mContext.sendBroadcast(i);
        			//Log.d("submarine","send update intent");
        		}
    		}

    		switch(direction) {
        	case LEFT: 
                moveLeftBoat();
                draw(); 
                try {  
                	Thread.sleep(20);  
                } catch (InterruptedException e) {   
                	e.printStackTrace();  
                }  
        		break;
        	case RIGHT:
                moveRightBoat();
                draw(); 
                try {  
                		Thread.sleep(20);  
                } catch (InterruptedException e) {   
                	e.printStackTrace();  
                } 
        		break;
        	default:
        		draw();
            	try {  
            		Thread.sleep(20);  
            	} catch (InterruptedException e) {   
            		e.printStackTrace();  
            	} 
        		break;
        	}
    	}
    	

    }  
	
	public void moveLeftBoat() {		
		if(xPos - boatWidthHalf < 0 ) {
			xPos = boatWidthHalf;
		} else {
			xPos -= boatSpeed;
		}
		
	}
	public void moveRightBoat() {
		if(xPos + boatWidthHalf > width ) {
			xPos = width - boatWidthHalf;
		} else {
			xPos += boatSpeed;
		}
	}

	 public void draw() {  
	     synchronized(surfaceHolder){  
	    	 canvas = surfaceHolder.lockCanvas();
	    	 canvas.drawBitmap(backgroundPic, null, surfaceFrame, null);	//绘制背景图像
	    	 Paint paint = new Paint();  	    	 
	         paint.setColor(Color.GREEN);
	         boatRect.set(xPos-boatWidthHalf, yPos-boatHeightHalf, xPos+boatWidthHalf, yPos+boatHeightHalf);
	         canvas.drawBitmap(boatPic, null, boatRect, null);
	     	 //由于要remove，用迭代器实现
	         Iterator<Bomb> bIter = bombList.iterator();
	         while (bIter.hasNext()) {
	             Bomb b = bIter.next();
	             for(int i=0; i<foeList.size(); i++) {
	            	 foe = foeList.get(i);
		             if(hit(b,foe)) {
		            	 b.setExplode(true);
		            	 foe.setHitStatus(true);
		            	 soundPool.play(1, 0.1f, 0.1f, 0, 0, 2.0f);
		            	 explodeRect.set(
		            			 b.getPositionX()-boatWidthHalf, b.getPositionY()-boatWidthHalf, b.getPositionX()+boatWidthHalf, b.getPositionY()+boatWidthHalf);
		            	 canvas.drawBitmap(explodePic, null, explodeRect, null);
		            	 explodeTimer--;
		             }
	             }
	             if (!b.isExplode()) {
	            	 bombRect.set(
	            			 b.getPositionX() - bombRadius, b.getPositionY() - bombRadius, b.getPositionX() + bombRadius, b.getPositionY() + bombRadius);
	            	 canvas.drawBitmap(bombPic, null, bombRect, null);	            	 
	             } else {
	                 bIter.remove();
	             }
	         }
	         if(haveFoes) {
	        	 for(int i=0; i<foeList.size(); i++) {
	        		 foe = foeList.get(i);
		        	 if(foe.getPositionX() <= width+foeWidthHalf && foe.getPositionX() >= -foeWidthHalf) {
			        		if(foe.getHitStatus()) {
				            	Intent intent = new Intent(UPDATE_SCORE_10);
				            	mContext.sendBroadcast(intent);
				            	foe.reset();
			        		} else {
				        		drawFoe(canvas,foe);
				        		foe.move();
			        		}			
			        	 }
		        	 else 
		        		 foe.reset();	
	        	 }     	 
	         }
	         if(explodeTimer < EXPLODE_TIME && explodeTimer >= 0) {
            	 canvas.drawBitmap(explodePic, null, explodeRect, null);
            	 explodeTimer--;
	         } else if(explodeTimer < 0) {
	        	 explodeTimer = EXPLODE_TIME;
	         }
	    	 surfaceHolder.unlockCanvasAndPost(canvas);
	     }
	 }
	 
	 public void drawFoe(Canvas canvas, Foe f) {	 
		 
		 int x = f.getPositionX();
		 int y = height - (f.getLayer() * layerHeight);
		 foeRect.set(x-foeWidthHalf, y-foeHeightHalf, x+foeWidthHalf, y+foeHeightHalf);
		 if(f.getDirection() == LEFT)
			 canvas.drawBitmap(subPicL, null, foeRect, null);
		 else
			 canvas.drawBitmap(subPicR, null, foeRect, null);
		 //canvas.drawRect(x-boatWidthHalf, y+boatHeightHalf, x+boatWidthHalf, y-boatHeightHalf, p);
	 }
	 
	 public boolean hit(Bomb b, Foe f) {
		 if(b.getPositionX() <= f.getPositionX() + foeWidthHalf 
				 && b.getPositionX() >= f.getPositionX() - foeWidthHalf) {
			 if(b.getPositionY() >= height - (f.getLayer() * layerHeight) - foeHeightHalf 
					 && b.getPositionY() <= height - (f.getLayer() * layerHeight) + foeHeightHalf)
				 return true;
			 else return false;
		 } else {
			 return false;
		 }
	 }
	  
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		double tempValue;
		surfaceFrame = holder.getSurfaceFrame();    
        width = surfaceFrame.width();    
        height = surfaceFrame.height();
        xPos = width / 2;
        tempValue = height / 4.25;
        yPos = (int)tempValue;
        boatSpeed = width / 80;
        foeSpeedSlow = boatSpeed / 3;
        tempValue = boatSpeed * 0.8;
        foeSpeedFast = (int)tempValue; //a faster foe
        bombSpeed = height / 90;
        boatWidthHalf = width /20;
        boatHeightHalf = boatWidthHalf / 2;
        foeWidthHalf = width /24;
        foeHeightHalf = foeWidthHalf / 3;
        bombRadius = boatWidthHalf / 4;
        layerHeight = height / 7; //每层高度固定为1/7屏幕高度 
        boatRect = new Rect();
        foeRect = new Rect();
   	 	explodeRect = new Rect();
   	 	bombRect = new Rect();
        draw();
        
        for(int i=0; i<3; i++) {
        	Foe f = new Foe(width, i+1, foeSpeedSlow);
        	foeList.add(f);
        }
        
        haveFoes = true;
        startThread();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		exitThread = true;
		haveFoes = false;
		//当surfaceView销毁时让线程暂停300ms
        try  
        {  
            Thread.sleep(300);  
        } catch (InterruptedException e)  
        {  
            e.printStackTrace();  
        } 
	}
	
	public SurfaceHolder getSurfaceHolder() {
		return surfaceHolder;
	}

}
