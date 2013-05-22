package com.egor.hercule2000;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class Accelerometre extends MyActivity implements SensorEventListener {

	/**
	 * Les valeurs de l'accéléromètre
	 */
	float aX, aY, aZ;

	/**
	 * SensorManager donne accès aux capteurs de l'appareil
	 */
	SensorManager sensorManager;

	boolean appuisVerticale = false;
	boolean appuisHorisontal = false;
	String tag = "";
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
	private Display mDisplay;

	/**
	 * IHM : Repère de l'accéléromètre
	 */
	LinearLayout AccelerationLayout;

	/**
	 * AccelerometreView
	 */
	AccelerometreView accelerometreView;

	private OnTouchListener buttonListenerVerticale = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));

				tag = v.getTag().toString().substring(0, 1);
				appuisVerticale = true;
			}

			if (action == MotionEvent.ACTION_UP) {
				// v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				appuisVerticale = false;
				reseaux.emission("STOP");
			}
			return true;
		}
	};

	private OnTouchListener buttonListenerHorisontale = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));
				tag = v.getTag().toString().substring(0, 1);
				appuisHorisontal = true;
			}

			if (action == MotionEvent.ACTION_UP) {
				// v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				appuisHorisontal = false;
				reseaux.emission("STOP");
			}
			return true;
		}
	};

	private void ihm() {
		xTextView = (TextView) findViewById(R.id.xTextView);
		yTextView = (TextView) findViewById(R.id.yTextView);
		zTextView = (TextView) findViewById(R.id.zTextView);

		vitesseTextView = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		vitesseSeekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		vitesseSeekBar.setOnSeekBarChangeListener(this);
		vitesseSeekBar.setProgress(vitesse);

		coupleSeekBar = (SeekBar) findViewById(R.id.coupleSeekBar);
		coupleTextView = (TextView) findViewById(R.id.coupleTextView);
		coupleSeekBar.setOnSeekBarChangeListener(this);
		coupleSeekBar.setProgress(400);

		// Verticale
		((Button) findViewById(R.id.epauleButton))
				.setOnTouchListener(buttonListenerVerticale);
		((Button) findViewById(R.id.coudeButton))
				.setOnTouchListener(buttonListenerVerticale);
		((Button) findViewById(R.id.tangageButton))
				.setOnTouchListener(buttonListenerVerticale);

		// Horisontale
		((Button) findViewById(R.id.baseButton))
				.setOnTouchListener(buttonListenerHorisontale);
		((Button) findViewById(R.id.roulisButton))
				.setOnTouchListener(buttonListenerHorisontale);

		// Pince
		((Button) findViewById(R.id.SerrerPince))
				.setOnTouchListener(serrerPince);
		((Button) findViewById(R.id.RelacherPince))
				.setOnTouchListener(relacherPince);
		
		AccelerationLayout = (LinearLayout) findViewById(R.id.reperAccelerometreLayaout);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometre);
		// Show the Up button in the action bar.
		setupActionBar();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// On instancie le SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// On instancie l'accelerometre
		accelerometre = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// and instantiate the display to know the device orientation
		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		porteeMax = accelerometre.getMaximumRange();

		this.ihm();

		accelerometreView = new AccelerometreView(this);
		// LinearLayout.LayoutParams layoutParam = new
		// LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		AccelerationLayout.addView(accelerometreView /* , layoutParam */);

		// On affiche le dialog de connexion
		afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ACCELEROMETRE);
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
		reseaux.close();
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

	private boolean HorisontalDroite = false;
	private boolean HorisontalDroiteFin = false;
	private boolean HorisontalGauche = false;
	private boolean HorisontalGaucheFin = false;

	private boolean VerticalDroite = false;
	private boolean VerticalDroiteFin = false;
	private boolean VerticalGauche = false;
	private boolean VerticalGaucheFin = false;

	public void setAxes(float x, float y, float z, int declencheurX,
			int declencheurY) {

		if (appuisVerticale) {
			// fin gauche
			if ((y < declencheurY)) {
				if (VerticalDroiteFin == false) {
					Log.d(LOG_TAG, "STOP:Sud");
					reseaux.emission("STOP:Sud");
					VerticalDroiteFin = true;
				}
				VerticalDroite = false;
			}
			// debut gauche
			if (y > declencheurY) {
				if (VerticalDroite == false) {
					Log.d(LOG_TAG, "Sud");
					reseaux.emission("MOVE:" + tag + ":-:" + 25);
					VerticalDroite = true;
				}
				VerticalDroiteFin = false;
			}

			// debut droite
			if (y < -declencheurY) {
				if (VerticalGauche == false) {
					Log.d(LOG_TAG, "Nord");
					reseaux.emission("MOVE:" + tag + ":+:" + 25);
					VerticalGauche = true;
				}
				VerticalGaucheFin = false;
			}

			if ((y > -declencheurY)) {
				if (VerticalGaucheFin == false) {
					Log.d(LOG_TAG, "STOP:Nord");
					reseaux.emission("STOP:Nord");
					VerticalGaucheFin = true;
				}
				VerticalGauche = false;
			}
		}
		if (appuisHorisontal) {
			// fin gauche
			if ((x < declencheurX)) {
				if (HorisontalDroiteFin == false) {
					Log.d(LOG_TAG, "STOP:Ouest");
					reseaux.emission("STOP:Ouest");
					HorisontalDroiteFin = true;
				}
				HorisontalDroite = false;
			}
			// debut gauche
			if (x > declencheurX) {
				if (HorisontalDroite == false) {
					Log.d(LOG_TAG, "Ouest");
					reseaux.emission("MOVE:" + tag + ":-:" + 25);
					HorisontalDroite = true;
				}
				HorisontalDroiteFin = false;
			}

			// debut droite
			if (x < -declencheurX) {
				if (HorisontalGauche == false) {
					Log.d(LOG_TAG, "Est");
					reseaux.emission("MOVE:" + tag + ":+:" + 25);
					HorisontalGauche = true;
				}
				HorisontalGaucheFin = false;
			}

			if ((x > -declencheurX)) {
				if (HorisontalGaucheFin == false) {
					Log.d(LOG_TAG, "STOP:Est");
					reseaux.emission("STOP:Est");
					HorisontalGaucheFin = true;
				}
				HorisontalGauche = false;
			}
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.connexion:
			startActivity(new Intent(this, Accelerometre.class));
			return true;
		case R.id.reset:
			reseaux.emission("RESET");
		}
		return super.onOptionsItemSelected(item);
	}

}
