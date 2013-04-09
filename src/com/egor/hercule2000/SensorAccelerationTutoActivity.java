package com.egor.hercule2000;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class SensorAccelerationTutoActivity extends Activity implements SensorEventListener {
	//private static final String LOG_TAG = "SensorsAccelerometer";
	float x, y, z;
	float maxX = 0, maxY = 0, maxZ = 0;
	float minX = 0, minY = 0, minZ = 0;
	/**
	 * Max range of the sensor
	 */
	float maxRange;
	
	LinearLayout.LayoutParams lParamsName;
	LinearLayout xyAccelerationLayout;
	XYAceleromterView xyAccelerationView;
	
	private Display mDisplay;
	
	SensorManager sensorManager;
	
	Sensor accelerometer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// build the GUI
		setContentView(R.layout.main);
		
		// Instantiate the SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Instantiate the accelerometer
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// Instantiate the gravity
		
		// and instantiate the display to know the device orientation
		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		maxRange = accelerometer.getMaximumRange();
		// Then build the GUI:
		// Build the acceleration view
		// first retrieve the layout:
		xyAccelerationLayout = (LinearLayout) findViewById(R.id.layoutOfXYAcceleration);
		// then build the view
		xyAccelerationView = new XYAceleromterView(this);
		// define the layout parameters and add the view to the layout
		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// add the view in the layout
		xyAccelerationLayout.addView(xyAccelerationView, layoutParam);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// unregister every body
		sensorManager.unregisterListener(this, accelerometer);
		// and don't forget to pause the thread that redraw the xyAccelerationView
		xyAccelerationView.isPausing.set(true);
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		/*
		 * It is not necessary to get accelerometer events at a very high
		 * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
		 * automatic low-pass filter, which "extracts" the gravity component
		 * of the acceleration. As an added benefit, we use less power and
		 * CPU resources.
		 */
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		// and don't forget to re-launch the thread that redraws the xyAccelerationView
		xyAccelerationView.isPausing.set(false);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// Log.d(LOG_TAG, "onDestroy()");
		// kill the thread
		xyAccelerationView.isRunning.set(false);
		super.onDestroy();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to do
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// update only when your are in the right case:
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// Log.d(LOG_TAG, "TYPE_ACCELEROMETER");
			// Depending on the device orientation get the x,y value of the acceleration
			switch (mDisplay.getRotation()) {
			case Surface.ROTATION_0:
				x = event.values[0];
				y = event.values[1];
				break;
			case Surface.ROTATION_90:
				x = -event.values[1];
				y = event.values[0];
				break;
			case Surface.ROTATION_180:
				x = -event.values[0];
				y = -event.values[1];
				break;
			case Surface.ROTATION_270:
				x = event.values[1];
				y = -event.values[0];
				break;
			}
			// the z value
			z = event.values[2];
		}
	}
}