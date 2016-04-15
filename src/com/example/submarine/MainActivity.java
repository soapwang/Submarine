package com.example.submarine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private GameView gameView;
	private FrameLayout mFrame;
	private Button leftButton;	
	private Button rightButton;
	private Button bombButton;
	private Button pauseButton;
	private View gameWidgets;
	private TextView ammoText;
	private TextView scoreText;	
	private MyReceiver receiver = null;	//接收GameView的更新信息，用于更新UI数据
	private int score;
	private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        score = 0;
        receiver = new MyReceiver();  
        IntentFilter filter = new IntentFilter();  
        filter.addAction(GameView.UPDATE_AMMO); 
        filter.addAction(GameView.UPDATE_SCORE_10);
        this.registerReceiver(receiver, filter);
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
        		  (Context.LAYOUT_INFLATER_SERVICE);      
        gameWidgets = inflater.inflate(R.layout.ui_layout,null);
        ammoText = (TextView) gameWidgets.findViewById(R.id.ammoText);
        scoreText = (TextView) gameWidgets.findViewById(R.id.scoreText);
        leftButton = (Button) gameWidgets.findViewById(R.id.button1);
        leftButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_UP){
		        	gameView.setDirection(GameView.STOP);
		            return true;
		        } else if(event.getAction() == MotionEvent.ACTION_DOWN){
		        	gameView.setDirection(GameView.LEFT);
		            return true;
		        }
		        return false;
			}
        	
        });
        
        rightButton = (Button) gameWidgets.findViewById(R.id.button2);
        rightButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_UP){
		        	gameView.setDirection(GameView.STOP);
		            return true;
		        } else if(event.getAction() == MotionEvent.ACTION_DOWN){
		        	gameView.setDirection(GameView.RIGHT);
		            return true;
		        }
		        return false;
			}
        	
        });
        
        bombButton = (Button) gameWidgets.findViewById(R.id.button3);
        bombButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.createBomb();
				int ammo = gameView.getAmmoCount();
				ammoText.setText("X" + ammo);
			}
				
        });
        
        pauseButton = (Button) gameWidgets.findViewById(R.id.pause_button);
        gameView = new GameView(this);
        mFrame = new FrameLayout(this);
        mFrame.addView(gameView);
        mFrame.addView(gameWidgets);
        //mFrame.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        setContentView(mFrame);
        pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.pauseGame();				
				alertDialog.setMessage("Game paused");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Resume",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				            gameView.resumeGame();
				        }
				    });
				alertDialog.show();
			}
				
        });
        alertDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				gameView.resumeGame();			
			}
        	
        });
    }
    
    public class MyReceiver extends BroadcastReceiver  
    {  
        @Override  
        public void onReceive(Context context, Intent intent)  
        {  
            if (intent.getAction().equals(GameView.UPDATE_AMMO))  
            {  
            	//Log.d("submarine","get update intent");
            	int ammo = gameView.getAmmoCount();
            	ammoText.setText("X" + ammo);
            }  else if(intent.getAction().equals(GameView.UPDATE_SCORE_10)) {
            	score += 10;
            	Log.d("submarine","update score");
            	scoreText.setText("Score:" + score);
            }
        }
 
    } 
    
    protected void onStop() {
    	super.onStop();
    	gameView.surfaceDestroyed(gameView.getSurfaceHolder());
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	//gameView.resumeGame();
        	return super.onKeyDown(keyCode, event);
        	/*
            if(dialogFlag) {
            	alertDialog.dismiss();
            	gameView.resumeGame();
            	dialogFlag = false;
            	return true;
            } else {
            	return super.onKeyDown(keyCode, event);
            }
            */
        }

        return super.onKeyDown(keyCode, event);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	if(receiver!=null)  
            this.unregisterReceiver(receiver);
    }

}
