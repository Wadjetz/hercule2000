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
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private float axisX = 0;
	private float axisY = 0;
	private float axisZ = 0;

	private TextView tX = null;
	private TextView tY = null;
	private TextView tZ = null;

	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gyroscope_commande);
		// Show the Up button in the action bar.
		setupActionBar();

		tX = (TextView) findViewById(R.id.aX);
		tY = (TextView) findViewById(R.id.aY);
		tZ = (TextView) findViewById(R.id.aZ);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onDestroy() {
		mSensorManager.unregisterListener(this);
		super.onDestroy();
	}

	// Le Handler (Thread) charger de modifier le IHM
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AUTRES:
				tX.setText("X:" + axisX);
				tY.setText("Y:" + axisY);
				tZ.setText("Z:" + axisZ);
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

		// axisX = event.values[0];
		// axisY = event.values[1];
		// axisZ = event.values[2];
		//

		// This timestep's delta rotation to be multiplied by the current
		// rotation
		// after computing it from the gyro sample data.
		if (timestamp != 0) {
			final float dT = (event.timestamp - timestamp) * NS2S;
			// Axis of the rotation sample, not normalized yet.
			axisX = event.values[0];
			axisY = event.values[1];
			axisZ = event.values[2];

			// Calculate the angular speed of the sample
			float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY
					* axisY + axisZ * axisZ);

			// Normalize the rotation vector if it's big enough to get the axis
			// (that is, EPSILON should represent your maximum allowable margin
			// of error)
			if (omegaMagnitude > 100) {
				axisX /= omegaMagnitude;
				axisY /= omegaMagnitude;
				axisZ /= omegaMagnitude;
			}

			// Integrate around this axis with the angular speed by the timestep
			// in order to get a delta rotation from this sample over the
			// timestep
			// We will convert this axis-angle representation of the delta
			// rotation
			// into a quaternion before turning it into the rotation matrix.
			float thetaOverTwo = omegaMagnitude * dT / 2.0f;
			float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
			float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
			deltaRotationVector[0] = sinThetaOverTwo * axisX;
			deltaRotationVector[1] = sinThetaOverTwo * axisY;
			deltaRotationVector[2] = sinThetaOverTwo * axisZ;
			deltaRotationVector[3] = cosThetaOverTwo;
		}
		timestamp = event.timestamp;
		float[] deltaRotationMatrix = new float[9];
		SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
				deltaRotationVector);
		// User code should concatenate the delta rotation we computed with the
		// current rotation
		// in order to get the updated rotation.
		// rotationCurrent = rotationCurrent * deltaRotationMatrix;

		handler.sendEmptyMessage(AUTRES);
	}

}
