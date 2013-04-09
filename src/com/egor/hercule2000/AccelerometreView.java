package com.egor.hercule2000;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class AccelerometreView extends View {
	/* ----------------------- ATTRIBUTS ------------------- */
	/**
	 * Log Tag pour les messages de debuguages
	 */
	public static final String LOG_TAG = "CM_Egor";
	
	/**
	 * Largeur du canvas
	 */
	private int width;
	
	/**
	 * Hauteur du canvas
	 */
	private int height;
	
	/**
	 * Les valeurs de X et Y de l'accéléromètre
	 */
	private float aX = 0, aY = 0;
	
	/**
	 * Vitesse de X et Y
	 */
	private float vitesseX, vitesseYy;
	
	/**
	 * The paint to draw the view
	 */
	private Paint paint = new Paint();
	
	/**
	 * The Canvas to draw within
	 */
	private Canvas canvas;
	
	/**
	 * le contexte
	 */
	private Accelerometre activity;

	private Handler slowDownDrawingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// redraw();
			invalidate();
		}
	};
	
	private Thread background;
	
	/** * An atomic boolean to manage the external thread's destruction */
	AtomicBoolean isRunning = new AtomicBoolean(false);
	/** * An atomic boolean to manage the external thread's destruction */
	AtomicBoolean isPausing = new AtomicBoolean(false);	
	
	public AccelerometreView(Context context) {
		super(context);
		activity = (Accelerometre) context;
		
		background = new Thread(new Runnable() {
			/**
			 * The message exchanged between this thread and the handler
			 */
			Message myMessage;

			// Overriden Run method
			public void run() {
				try {
					while (isRunning.get()) {
						if (isPausing.get()) {
							Thread.sleep(2000);
						} else {
							Thread.sleep(1000 / 30);
							myMessage = slowDownDrawingHandler.obtainMessage();
							slowDownDrawingHandler.sendMessage(myMessage);
						}
					}
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		});
		// Initialize the threadSafe booleans
		isRunning.set(true);
		isPausing.set(false);
		// and start it
		background.start();
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		width = this.getWidth();
		height = this.getHeight();
		dessinerTous();
		super.onDraw(canvas);
	}

	private void dessinerTous() {
		
		// On récupère les valeurs de X et Y de l'accéléromètre
		this.aX = activity.aX ;
		this.aY = activity.aY;
		// On récupère les valeurs de X et Y de centre du canvas
		int xCenter = width / 2;
		int yCenter = height / 2;
		// Rayon maximale
		int maxRayon = Math.max(width, height) / 2;
		
		dessinerCercleLimite(xCenter, yCenter, yCenter/2);
		dessinerCercleNeutre(xCenter, yCenter, yCenter/4);
		dessinerLignesDiagonale(xCenter, yCenter);
		dessinerCerclePointAccelerometre(xCenter, yCenter, maxRayon, aX, aY);
	}

	private void dessinerCercleLimite(float xCenter, float yCenter, float rayon) {
		paint.setARGB(255, 0, 255, 0);
		canvas.drawCircle(xCenter, yCenter, rayon, paint);
	}

	private void dessinerLignesDiagonale(int xCenter, int yCenter) {
		paint.setARGB(255, 0, 0, 0);
		
		//Dessine une ligne
		canvas.drawLine(0, 0, xCenter, yCenter, paint);
		canvas.drawLine(width, 0, xCenter, yCenter, paint);
		canvas.drawLine(0, height, xCenter, yCenter, paint);
		canvas.drawLine(width, height, xCenter, yCenter, paint);
		
	}

	private void dessinerCercleNeutre(float x, float y, float rayon) {
		paint.setARGB(255, 255, 255, 0);
		canvas.drawCircle(x, y, rayon, paint);
	}
	
	private void dessinerCerclePointAccelerometre(int xCenter, int yCenter, int maxRayon, float x, float y) {
		// Calcule des coordonée a afficher
		float xToDisp = (activity.aX * maxRayon) / activity.maxRange;
		float yToDisp = (activity.aY * maxRayon) / activity.maxRange;
		float zToDisp = (activity.aZ * maxRayon) / activity.maxRange;
		activity.setAxes(xToDisp, yToDisp, zToDisp);
		
		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 15, paint);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 13, paint);
		paint.setARGB(130, 255, 0, 0);
		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 13, paint);
	}

}
