
/*package com.example.submarine;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class UpdateThread extends Thread {
	 private long time;    
	 private final int fps = 30;    
	 private boolean toRun = false;    
	 private BasicView mView;    
	 private SurfaceHolder surfaceHolder; 
	 
	 public UpdateThread(BasicView basicView) {    
	        mView = basicView;    
	        surfaceHolder = mView.getHolder();    
	 }
	 
	public void setRunning(boolean run) {    
	        toRun = false;    
	}    
	
	@SuppressLint("WrongCall") 
	public void run() {    
          Canvas c;    
          while (toRun) {    
              long cTime = System.currentTimeMillis();    
              if ((cTime - time) <= (1000 / fps)) {    
                  c = null;    
                  try {    
                      c = surfaceHolder.lockCanvas(null);    
                      mView.updatePhysics();    
                      mView.onDraw(c);    
                  } finally {    
                      if (c != null) {    
                    	  surfaceHolder.unlockCanvasAndPost(c);    
                      }    
                  }    
              }    
              time = cTime;    
          }    
      }  
}
*/
