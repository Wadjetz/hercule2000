package com.egor.hercule2000;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class Accelerometre extends Activity implements SensorEventListener {

	public static final String LOG_TAG = "CM_Egor";

	/**
	 * Les valeurs de l'accéléromètre
	 */
	float aX, aY, aZ;

	/**
	 * SensorManager donne accès aux capteurs de l'appareil
	 */
	SensorManager sensorManager;
	
	/**
	 * Accéléromètre
	 */
	Sensor accelerometre;
	
	/**
	 * Portée maximale du capteur
	 */
	float porteeMax;
	
	/**
	 * IHM : affiche les valeurs de l'accéléromètre
	 */
	private TextView xTextView = null, yTextView = null, zTextView = null;

	/**
	 * Display récupère l'orientation de l'appareil
	 */
	Display mDisplay;

	/**
	 * IHM : Repère de l'accéléromètre
	 */
	LinearLayout AccelerationLayout;

	/**
	 * AccelerometreView
	 */
	AccelerometreView accelerometreView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometre);
		// Show the Up button in the action bar.
		setupActionBar();

		// On instancie le SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// On instancie l'accelerometre
		accelerometre = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// and instantiate the display to know the device orientation
		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		porteeMax = accelerometre.getMaximumRange();

		xTextView = (TextView) findViewById(R.id.xTextView);
		yTextView = (TextView) findViewById(R.id.yTextView);
		zTextView = (TextView) findViewById(R.id.zTextView);

		AccelerationLayout = (LinearLayout) findViewById(R.id.reperAccelerometreLayaout);
		accelerometreView = new AccelerometreView(this);
		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		AccelerationLayout.addView(accelerometreView, layoutParam);
	}

	@Override
	protected void onResume() {
		sensorManager.registerListener(this, accelerometre,
				SensorManager.SENSOR_DELAY_UI);
		accelerometreView.isPausing.set(false);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// unregister every body
		sensorManager.unregisterListener(this, accelerometre);
		// and don't forget to pause the thread that redraw the
		// xyAccelerationView
		accelerometreView.isPausing.set(true);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		sensorManager.unregisterListener(this, accelerometre);
		super.onDestroy();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// En fonction de l'orientation on récupère les valeurs de
			// l'accéléromètre
			switch (mDisplay.getRotation()) {
			case Surface.ROTATION_0:
				aX = event.values[0];
				aY = event.values[1];
				break;
			case Surface.ROTATION_90:
				aX = -event.values[1];
				aY = event.values[0];
				break;
			case Surface.ROTATION_180:
				aX = -event.values[0];
				aY = -event.values[1];
				break;
			case Surface.ROTATION_270:
				aX = event.values[1];
				aY = -event.values[0];
				break;
			}
			// L'axe Z n'est pas utiliser
			aZ = event.values[2];
		}
	}

	private boolean xed = false;
	private boolean xedf = false;
	private boolean xeg = false;
	private boolean xegf = false;

	private boolean yed = false;
	private boolean yedf = false;
	private boolean yeg = false;
	private boolean yegf = false;

	public void setAxes(float x, float y, float z) {
		// fin gauche
		if ((x < 100)) {
			if (xedf == false) {
				Log.d(LOG_TAG, "STOP:Ouest");
				xedf = true;
			}
			xed = false;
		}
		// debut gauche
		if (x > 100) {
			if (xed == false) {
				Log.d(LOG_TAG, "Ouest");
				xed = true;
			}
			xedf = false;
		}

		// debut droite
		if (x < -100) {
			if (xeg == false) {
				Log.d(LOG_TAG, "Est");
				xeg = true;
			}
			xegf = false;
		}

		if ((x > -100)) {
			if (xegf == false) {
				Log.d(LOG_TAG, "STOP:Est");
				xegf = true;
			}
			xeg = false;
		}

		// YYYY
		// fin gauche
		if ((y < 100)) {
			if (yedf == false) {
				Log.d(LOG_TAG, "STOP:Nord");
				yedf = true;
			}
			yed = false;
		}
		// debut gauche
		if (y > 100) {
			if (yed == false) {
				Log.d(LOG_TAG, "Nord");
				yed = true;
			}
			yedf = false;
		}

		// debut droite
		if (y < -100) {
			if (yeg == false) {
				Log.d(LOG_TAG, "Sud");
				yeg = true;
			}
			yegf = false;
		}

		if ((y > -100)) {
			if (yegf == false) {
				Log.d(LOG_TAG, "STOP:Sud");
				yegf = true;
			}
			yeg = false;
		}

		xTextView.setText("X : " + x);
		yTextView.setText("Y : " + y);
		zTextView.setText("Z : " + z);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accelerometre, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
