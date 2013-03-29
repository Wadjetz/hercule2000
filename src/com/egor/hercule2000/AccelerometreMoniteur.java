package com.egor.hercule2000;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class AccelerometreMoniteur extends View {
	
	private static final String LOG_TAG = "Egor";
	
	private int width;
	private int height;
	
	private Paint paint = new Paint();	
	private Canvas canvas;
	
	private boolean init = false;
	private float x = 0, y = 0, vx, vy;
	private float xLin = 0, yLin = 0;
	private long t = 0, dt;
	
	AtomicBoolean isRunning = new AtomicBoolean(false);
	AtomicBoolean isPausing = new AtomicBoolean(false);	
	
	private Handler hundler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	private Thread background = new Thread(new Runnable() {
		
		Message myMessage;
		
		@Override
		public void run() {
			try {
				while (isRunning.get()) {
					if (isPausing.get()) {
						Thread.sleep(2000);
					} else {
						// Redraw to have 30 images by second
						Thread.sleep(1000 / 30);
						// Send the message to the handler (the handler.obtainMessage is more
						// efficient that creating a message from scratch)
						// create a message, the best way is to use that method:
						myMessage = hundler.obtainMessage();
						// then send the message

						hundler.sendMessage(myMessage);
					}
				}
			} catch (Throwable t) {
				// just end the background thread
			}
		}});
	
	public AccelerometreMoniteur(Context context) {
		super(context);
		
		
		isRunning.set(true);
		isPausing.set(false);
		background.start();
		
		
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		// Log.d(tag, "onDraw");
		// add a condition to slow down
		// retrieve the height and width
		width = this.getWidth();
		height = this.getHeight();
		if (!init) {
			// initialize the coordinates in the middle of the screen
			x = width / 2;
			y = height / 2;
			xLin = x;
			yLin = y;
			// initialize the speed to 0
			vx = vy = 0;
			// initialize the time to now
			t = System.nanoTime();
			// and said it's initiliazed
			init = true;
		}
		// the canvas in which we draw
		this.canvas = canvas;
		// Log.d(tag, "View width " + this.getWidth() + " height: " + this.getHeight());
		// Log.d(tag, "Canvas width " + width + " height: " + height);
		drawAccelerationMoniteur();
	}
	
	
	private void drawAccelerationMoniteur() {
		// Draw the background
//		paint.setARGB(255, 255, 255, 255);
//		canvas.drawRect(0, 0, width, 3 * height, paint);
//		// Inistanciate the variables (acceleration vector, min and max acceleration point
//		float xAcceleration = activity.x, yAcceleration = activity.y, zAcceleration = activity.z;
//		float xMaxAcceleration = activity.maxX, yMaxAcceleration = activity.maxY;
//		float xMinAcceleration = activity.minX, yMinAcceleration = activity.minY;
//		float sensorMaxRange = activity.maxRange;
		int xCenter = width / 2, yCenter = height / 2;
//		int maxRadiusSize = Math.max(width, height) / 2;
//		// First draw circle (the background)
//		drawCircles(xCenter, yCenter, maxRadiusSize, zAcceleration, sensorMaxRange);
//		// draw the accelerator point
//		drawAcceleratorPoint(xCenter, yCenter, maxRadiusSize, xAcceleration, yAcceleration);
//		// then draw axis
		drawAxis(xCenter, yCenter);
//		// and draw MaxX and MaxY
//		drawMaxMinAccelerationAxis(xMaxAcceleration, yMaxAcceleration, xMinAcceleration, yMinAcceleration,
//				sensorMaxRange, xCenter, yCenter, maxRadiusSize);
//		// // draw the trajectory of a point that have such an acceleration
//		drawAcceleratePointInVoid(xAcceleration, yAcceleration);
//		// // show a point that move only when acceleration is applied on it
//		drawFakePoint(xAcceleration, yAcceleration);

	}
	
	private void drawAxis(int xCenter, int yCenter) {
		//Select the paint to use (the color)
		paint.setARGB(255, 0, 0, 0);
		// draw an axis of 3 pixels
		for (int i = -1; i < 2; i++) {
			canvas.drawLine(xCenter + i, 0, xCenter + i, height, paint);
			canvas.drawLine(0, yCenter + i, width, yCenter + i, paint);
		}
	}
	
	
	/**
	 * The method to redraw the view
	 */
	private void redraw() {
		// Log.d(tag, "redraw");
		// and make sure to redraw asap
		invalidate();
	}
	
}
