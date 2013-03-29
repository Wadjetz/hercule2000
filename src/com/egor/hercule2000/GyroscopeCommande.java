package com.egor.hercule2000;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class GyroscopeCommande extends Activity implements SensorEventListener {
	protected static final int AUTRES = 0003;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float x, y, z;

	private TextView tX = null;
	private TextView tY = null;
	private TextView tZ = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gyroscope_commande);
		// Show the Up button in the action bar.
		setupActionBar();

		tX = (TextView) findViewById(R.id.aX);
		tY = (TextView) findViewById(R.id.aY);
		tZ = (TextView) findViewById(R.id.aZ);
		// Instanicer le SensorManager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// Instancier l'accéléromètre
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	@Override
	protected void onResume() {

		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		sensorManager.unregisterListener(this);
		super.onDestroy();
	}

	// Le Handler (Thread) charger de modifier le IHM
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AUTRES:
				tX.setText("X:" + x);
				tY.setText("Y:" + y);
				tZ.setText("Z:" + z);
				break;
			}
		};
	};

	/**
	 * ------------------------------------------------------------------------
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gyroscope_commande, menu);
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Récupérer les valeurs du capteur
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		    x = event.values[0];
		    y = event.values[1];
		    z = event.values[2];
		    
		    handler.sendEmptyMessage(AUTRES);
		}
	}

}
