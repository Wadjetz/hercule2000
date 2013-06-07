package com.egor.hercule2000;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class AccelerometreView extends View {
	/* ----------------------- ATTRIBUTS ------------------- */
	/**
	 * Log Tag pour les messages de debuguages
	 */
	public static final String LOG_TAG = "CM_Egor";

	/**
	 * La peinture pour dessiner
	 */
	private Paint paint = new Paint();

	/**
	 * The Canvas to draw within
	 */
	private Canvas canvas;

	/**
	 * Largeur du canvas
	 */
	private int width;

	/**
	 * Hauteur du canvas
	 */
	private int height;
	/**
	 * Centre du canvas
	 */
	private int xCenter;
	private int yCenter;

	/**
	 * le contexte
	 */
	private Accelerometre activity;

	/**
	 * Handler redessine le canvas
	 */
	private Handler redessiner = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// redraw();
			invalidate(); // On redessine le dessin
		}
	};
	
	private Thread background;

	/** * Un booléen atomique pour gérer la destruction du thread extérieur */
	public AtomicBoolean isRunning = new AtomicBoolean(false);
	/** * Un booléen atomique pour gérer la destruction du thread extérieur */
	public AtomicBoolean isPausing = new AtomicBoolean(false);

	public AccelerometreView(Context context) {
		super(context);
		activity = (Accelerometre) context;

		background = new Thread(new Runnable() {
			/**
			 * The message exchanged between this thread and the handler
			 */
			Message myMessage;

			public void run() {
				try {
					while (isRunning.get()) {
						if (isPausing.get()) {
							Thread.sleep(2000);
						} else {
							// On redessine 30 fois par seconde
							Thread.sleep(1000 / 30);
							myMessage = redessiner.obtainMessage();
							redessiner.sendMessage(myMessage);
						}
					}
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		});

		isRunning.set(true);
		isPausing.set(false);

		background.start();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		width = this.getWidth();
		height = this.getHeight();
		// On récupère les valeurs de X et Y de centre du canvas
		xCenter = width / 2;
		yCenter = height / 2;

		dessinerTous();
		super.onDraw(canvas);
	}

	private void dessinerTous() {

		// Rayon maximale
		int maxRayon = Math.max(width, height) / 2;

		dessinerCercleLimite();
		dessinerCercleNeutre();

		dessinerLignesDiagonale();
		dessinerCerclePointAccelerometre(maxRayon, activity.getaX(), activity.getaY());
	}

	private void dessinerCercleLimite() {
		paint.setARGB(255, 0, 255, 0);
		canvas.drawCircle(xCenter, yCenter, yCenter / 2, paint);
	}

	private void dessinerCercleNeutre() {
		paint.setARGB(255, 255, 255, 0);
		canvas.drawCircle(xCenter, yCenter, yCenter / 4, paint);
	}

	private void dessinerLignesDiagonale() {
		paint.setARGB(255, 0, 0, 0);

		// Dessine une ligne
		canvas.drawLine(0, 0, xCenter, yCenter, paint);
		canvas.drawLine(width, 0, xCenter, yCenter, paint);
		canvas.drawLine(0, height, xCenter, yCenter, paint);
		canvas.drawLine(width, height, xCenter, yCenter, paint);

	}

	private void dessinerCerclePointAccelerometre(int maxRayon, float x, float y) {
		// Calcule des coordonée a afficher
		float xToDisp = (activity.getaX() * maxRayon) / activity.getPorteeMax();
		float yToDisp = (activity.getaY() * maxRayon) / activity.getPorteeMax();
		float zToDisp = (activity.getaZ() * maxRayon) / activity.getPorteeMax();
		activity.calculeRequete(xToDisp, yToDisp, zToDisp, xCenter / 4, yCenter / 4);

		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 15, paint);
		paint.setARGB(255, 255, 255, 255);
		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 13, paint);
		paint.setARGB(130, 255, 0, 0);
		canvas.drawCircle(xCenter - xToDisp, yToDisp + yCenter, 13, paint);
	}

}
