package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
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

public class Accelerometre extends MyActivity implements SensorEventListener, SeekBar.OnSeekBarChangeListener  {

	/**
	 * Les valeurs de l'accéléromètre
	 */
	private float aX, aY, aZ;

	/**
	 * SensorManager donne accès aux capteurs de l'appareil
	 */
	private SensorManager sensorManager;
	/**
	 * Indique si on veut commander avec l'axe X
	 */
	private boolean appuisVerticale = false;
	/**
	 * Indique si on veut commander avec l'axe Y
	 */
	private boolean appuisHorisontal = false;
	
	private String tag = "";
	/**
	 * Accéléromètre
	 */
	private Sensor accelerometre;

	/**
	 * Portée maximale du capteur
	 */
	private float porteeMax;

	/**
	 * Display récupère l'orientation de l'appareil
	 */
	private Display mDisplay;

	/**
	 * IHM : Repère de l'accéléromètre
	 */
	private LinearLayout AccelerationLayout;

	/**
	 * AccelerometreView
	 */
	private AccelerometreView accelerometreView;

	private OnTouchListener buttonListenerVerticale = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			// Toucher sur le bouton
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));

				tag = v.getTag().toString().substring(0, 1);
				appuisVerticale = true;
			}
			// Bouton relacher
			if (action == MotionEvent.ACTION_UP) {
				// v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				appuisVerticale = false;
//				long current = System.currentTimeMillis();
//				delais = current - t0;
//				if (capture) {
//					al.add(new Pair<String, Long>(requete, delais));
//				}
				envoyer("S");
			}
			return true;
		}
	};

	private OnTouchListener buttonListenerHorisontale = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			// Toucher sur le bouton
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));
				tag = v.getTag().toString().substring(0, 1);
				appuisHorisontal = true;
			}
			// Bouton relacher
			if (action == MotionEvent.ACTION_UP) {
				// v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				appuisHorisontal = false;
//				long current = System.currentTimeMillis();
//				delais = current - t0;
//				if (capture) {
//					al.add(new Pair<String, Long>(requete, delais));
//				}
				envoyer("S");
			}
			return true;
		}
	};
	
	
	/**
	 * Thread de connexion reseaux
	 */
	protected Thread connexion = new Thread(new Runnable() {

		@Override
		public void run() {
			Log.d(LOG_TAG, "threadConnexionReseaux RUN");
			socketAddress = new InetSocketAddress(ip, port);
			try {
				socket.connect(socketAddress, 10000);
				if (socket.isConnected()) {
					emetteur = new PrintWriter(socket.getOutputStream(), true);
					recepteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					envoyer("D:1234");
					//reception();
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				progressDialog.dismiss();
				afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
			}
			progressDialog.dismiss();
			if(socket.isConnected()){
				reception();
			}
		}
	});
	

	/**
	 * Le Handler (Thread spécialisé) charger de modifier le IHM
	 */
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CONNEXION_SOCKET:
				//Log.d(LOG_TAG, "HANDLER_CONNEXION_SOCKET");
				// progressDialog = null;
				// On ajoute un message à notre progress dialog
				progressDialog.setMessage("Connexion en cours");
				// On affiche notre message
				progressDialog.show();
				// Empeche l'interuption du dialog
				progressDialog.setCanceledOnTouchOutside(false);
				connexion.start();
				break;
			case HANDLER_SEEK_BAR_CHANGED_VITESSE:
				vitesse = vitesseSeekBar.getProgress() + 1;
				vitesseTextView.setText("Vitesse : " + vitesse);
				break;
			case HANDLER_SEEK_BAR_CHANGED_COUPLE:
				couple = coupleSeekBar.getProgress() + 1;
				coupleTextView.setText("Couple : " + couple);
				break;
			}
		};
	};
	
	/**
	 * SeekBar événement : changement de vitesse
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		//Log.d(LOG_TAG, "onProgressChanged SeekBar");
		if (seekBar.getTag().toString().compareTo("VITESSE") == 0) {
			handler.sendEmptyMessage(HANDLER_SEEK_BAR_CHANGED_VITESSE);
		}
		if (seekBar.getTag().toString().compareTo("COUPLE") == 0) {
			handler.sendEmptyMessage(HANDLER_SEEK_BAR_CHANGED_COUPLE);
		}
	}
	
	/**
	 * Méthode de callback exécuter par le click sur le bouton ok du dialogue de
	 * connexion réseau
	 * 
	 * @param ip
	 *            Adresse IP du destinataire
	 * @param port
	 *            Numéro de port du serveur
	 */
	public void doPositiveClick(String ip, int port) {
		//Log.d(LOG_TAG, "doPositiveClick : " + ip + ":" + port);
		this.ip = ip;
		this.port = port;
		if(isIp(ip)) {
			handler.sendEmptyMessage(HANDLER_CONNEXION_SOCKET);
		}
		else {
			//Intent intent = new Intent();
			//Log.d(LOG_TAG, "IP Invalide");
			MDialog md = new MDialog();
			md.show(getFragmentManager(), MDialog.DIALOG_IP_INVALIDE);
		}
	}
	
	
	
	/**
	 * Initialisation de l'IHM
	 */
	private void ihm() {
		messageRecu = (TextView)findViewById(R.id.messageRecu);
		vitesseTextView = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		vitesseSeekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		vitesseSeekBar.setOnSeekBarChangeListener(this);
		vitesseSeekBar.setProgress(vitesse);
//		lancerCapture = (Button)findViewById(R.id.lancerCapture);
//		lancerCapture.setEnabled(false);
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
		// On fixe l'oriantation en mode portrait
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
		// Porté maximal du capteur
		porteeMax = accelerometre.getMaximumRange();

		this.ihm();

		accelerometreView = new AccelerometreView(this);
		// LinearLayout.LayoutParams layoutParam = new
		// LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		AccelerationLayout.addView(accelerometreView /* , layoutParam */);

		// On affiche le dialog de connexion
		progressDialog = new ProgressDialog(this);
		afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ACC);
	}

	@Override
	protected void onResume() {
		// On active le capteur
		sensorManager.registerListener(this, accelerometre,
				SensorManager.SENSOR_DELAY_UI);
		// on arrete la pause du thread
		accelerometreView.isPausing.set(false);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// on désactive le capteur
		sensorManager.unregisterListener(this, accelerometre);
		// On met en pause le capteur
		accelerometreView.isPausing.set(true);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "onDestroy Accelerometre");
		// on désactive le capteur
		sensorManager.unregisterListener(this, accelerometre);
		super.close();
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
	/**
	 * En fonction dès valeur du capteur on envoie les requêtes
	 * @param x Valeur de l'axe X du capteur
	 * @param y Valeur de l'axe Y du capteur
	 * @param z Valeur de l'axe Z du capteur
	 * @param declencheurX Seuil de déclenchement de la requête pour l'axe X
	 * @param declencheurY Seuil de déclenchement de la requête pour l'axe Y
	 */
	public void calculeRequete(float x, float y, float z, int declencheurX,
			int declencheurY) {

		if (appuisVerticale) {
			// fin gauche
			if ((y < declencheurY)) {
				if (VerticalDroiteFin == false) {
					//Log.d(LOG_TAG, "STOP:Sud");
					long current = System.currentTimeMillis();
					delais = current - t0;
					if (capture) {
						al.add(new Pair<String, Long>(requete, delais));
					}
					envoyer("S:" + tag);
					VerticalDroiteFin = true;
				}
				VerticalDroite = false;
			}
			// debut gauche
			if (y > declencheurY) {
				if (VerticalDroite == false) {
					//Log.d(LOG_TAG, "Sud");
					t0 = System.currentTimeMillis();
					requete = "M:" + tag + ":-:" + vitesse;
					envoyer(requete);
					VerticalDroite = true;
				}
				VerticalDroiteFin = false;
			}

			// debut droite
			if (y < -declencheurY) {
				if (VerticalGauche == false) {
					//Log.d(LOG_TAG, "Nord");
					t0 = System.currentTimeMillis();
					requete = "M:" + tag + ":+:" + vitesse;
					envoyer(requete);
					VerticalGauche = true;
				}
				VerticalGaucheFin = false;
			}

			if ((y > -declencheurY)) {
				if (VerticalGaucheFin == false) {
					//Log.d(LOG_TAG, "STOP:Nord");
					long current = System.currentTimeMillis();
					delais = current - t0;
					if (capture) {
						al.add(new Pair<String, Long>(requete, delais));
					}
					envoyer("S:" + tag);
					VerticalGaucheFin = true;
				}
				VerticalGauche = false;
			}
		}
		if (appuisHorisontal) {
			// fin gauche
			if ((x < declencheurX)) {
				if (HorisontalDroiteFin == false) {
					long current = System.currentTimeMillis();
					delais = current - t0;
					if (capture) {
						al.add(new Pair<String, Long>(requete, delais));
					}
					envoyer("S:" + tag);
					HorisontalDroiteFin = true;
				}
				HorisontalDroite = false;
			}
			// debut gauche
			if (x > declencheurX) {
				if (HorisontalDroite == false) {
					t0 = System.currentTimeMillis();
					requete = "M:" + tag + ":-:" + vitesse;
					envoyer(requete);
					HorisontalDroite = true;
				}
				HorisontalDroiteFin = false;
			}

			// debut droite
			if (x < -declencheurX) {
				if (HorisontalGauche == false) {
					t0 = System.currentTimeMillis();
					requete = "M:" + tag + ":+:" + vitesse;
					envoyer(requete);
					HorisontalGauche = true;
				}
				HorisontalGaucheFin = false;
			}

			if ((x > -declencheurX)) {
				if (HorisontalGaucheFin == false) {
					long current = System.currentTimeMillis();
					delais = current - t0;
					if (capture) {
						al.add(new Pair<String, Long>(requete, delais));
					}
					envoyer("S:" + tag);
					HorisontalGaucheFin = true;
				}
				HorisontalGauche = false;
			}
		}
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
			startActivity(new Intent(this, Accueil.class));
			//NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.connexion:
			startActivity(new Intent(this, Accelerometre.class));
			return true;
		case R.id.reset:
			envoyer("R");
		}
		return super.onOptionsItemSelected(item);
	}

	public float getaX() {
		return aX;
	}

	public float getaY() {
		return aY;
	}

	public float getaZ() {
		return aZ;
	}

	public float getPorteeMax() {
		return porteeMax;
	}
	
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//Log.d(LOG_TAG, "onStartTrackingTouch SeekBar");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//Log.d(LOG_TAG, "onStopTrackingTouch SeekBar");
	}
}
