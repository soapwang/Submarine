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
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

//绘图线程在此类中工作，fps=30
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	
	public final static int STOP = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	private final static int REFILL_TIME = 60;
	
	private ArrayList<Bomb> bombList;	//用来存放所有炸弹信息
	private int ammo;	//剩余弹药量
	private int xPos;    
	private int yPos;
	private int width;	//屏幕宽度   
	private int height;	//屏幕高度
	private int boatSpeed;
	private int bombSpeed;
	private int boatWidthHalf; //船只宽度的一半
	private int boatHeightHalf;
	private int bombRadius;
	private int refillTimer;
	private SurfaceHolder surfaceHolder; 
	private boolean exitThread = false;
	private boolean haveFoes = false;
	private int direction;
	private Canvas canvas;
	private Foe foe1;
	private Bitmap backgroundPic;	//背景图片
	private Bitmap boatPic;
	private Bitmap subPic;
	public static final String UPDATE_AMMO = "com.example.submarine.updateammo";
	public static final String UPDATE_SCORE_10 = "com.example.submarine.updatescore10";
	private Rect surfaceFrame;	//确定sufaceView的显示范围
	private Context mContext;
	private Rect boatRect;	//船只占据的矩形区域


	public GameView(Context context) {
		super(context);
		mContext = context;
	    surfaceHolder = this.getHolder(); // 获取SurfaceHolder对象  
        surfaceHolder.addCallback(this); // 添加回调  
        bombList = new ArrayList<Bomb>();
        ammo = 5;
        refillTimer = REFILL_TIME;
        backgroundPic = 
        		BitmapFactory.decodeResource(getResources(), R.drawable.bg); //加载背景图片
        boatPic = BitmapFactory.decodeResource(getResources(), R.drawable.destoryer);
        subPic = BitmapFactory.decodeResource(getResources(), R.drawable.sub1);
        
	}
	
	public void createBomb() {
		if(ammo > 0) {
			Bomb b = new Bomb(xPos, yPos, bombSpeed, height);
			bombList.add(b);
			b.startThread();
			ammo--;
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
                	Thread.sleep(30);  
                } catch (InterruptedException e) {   
                	e.printStackTrace();  
                }  
        		break;
        	case RIGHT:
                moveRightBoat();
                draw(); 
                try {  
                		Thread.sleep(30);  
                } catch (InterruptedException e) {   
                	e.printStackTrace();  
                } 
        		break;
        	default:
        		draw();
            	try {  
            		Thread.sleep(30);  
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
	         //canvas.drawRect(xPos-boatWidthHalf, yPos+boatHeightHalf, xPos+boatWidthHalf, yPos-boatHeightHalf, paint);
	         Iterator<Bomb> iter = bombList.iterator();	//由于要remove，用迭代器实现
	         while (iter.hasNext()) {
	             Bomb b = iter.next();
	             if(hit(b,foe1)) {
	            	 b.setExplode(true);
	            	 foe1.setHitStatus(true);
	             }
	             if (!b.isExplode()) {
	            	 canvas.drawCircle(b.getPositionX(), b.getPositionY(), bombRadius, paint);
	             } else {
	                 iter.remove();
	             }
	         }
	         if(haveFoes) {
	        	 if(foe1.getPositionX() <= width+boatWidthHalf) {
	        		if(foe1.getHitStatus()) {
		            	Intent i = new Intent(UPDATE_SCORE_10);
		            	mContext.sendBroadcast(i);
	        			foe1.setPosition(0, height - yPos);
	        			foe1.setHitStatus(false);
	        		} else {
		        		drawFoe(canvas);
		     			foe1.moveRight();
	        		}			
	        	 }
	        	 else {
	        		 foe1.setPosition(0, height - yPos);
	        	 }
	        	 
	         }
	    	 surfaceHolder.unlockCanvasAndPost(canvas);
	     }
	 }
	 
	 public void drawFoe(Canvas canvas) {
		 //Paint p = new Paint();
		 //p.setColor(Color.RED);		 
		 Point pos = foe1.getPosition();
		 int x = pos.x;
		 int y = pos.y;
		 boatRect.set(x-boatWidthHalf, y-boatHeightHalf, x+boatWidthHalf, y+boatHeightHalf);
		 canvas.drawBitmap(subPic, null, boatRect, null);
		 //canvas.drawRect(x-boatWidthHalf, y+boatHeightHalf, x+boatWidthHalf, y-boatHeightHalf, p);
	 }
	 
	 public boolean hit(Bomb b, Foe f) {
		 if(b.getPositionX() <= f.getPositionX() + boatWidthHalf 
				 && b.getPositionX() >= f.getPositionX() - boatWidthHalf) {
			 if(b.getPositionY() >= f.getPositionY() - boatHeightHalf && b.getPositionY() <= f.getPositionY() + boatHeightHalf)
				 return true;
			 else return false;
		 } else {
			 return false;
		 }
	 }
	  
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceFrame = holder.getSurfaceFrame();    
        width = surfaceFrame.width();    
        height = surfaceFrame.height();
        xPos = width / 2;
        yPos = height / 4;
        boatSpeed = width / 90;
        bombSpeed = height / 90;
        boatWidthHalf = width /24;
        boatHeightHalf = boatWidthHalf / 2;
        bombRadius = boatWidthHalf / 5;
        boatRect = new Rect();
        draw();
        foe1 = new Foe(0, height - yPos, boatSpeed - 4);
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
		foe1 = null;
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
