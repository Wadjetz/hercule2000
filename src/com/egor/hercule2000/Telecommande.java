package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Telecommande extends Activity implements
		SeekBar.OnSeekBarChangeListener {

	/* ----------------------- ATTRIBUTS ------------------- */
	/**
	 * Log Tag pour les messages de debuguages
	 */
	private static final String LOG_TAG = "CM_Egor";

	/**
	 * Identifie l'action du Hundler - Affiche le dialog de connexion
	 */
	private static final int HANDLER_CONNEXION_SOCKET = 146113;

	/**
	 * Identifie l'action du Hundler - Change la valeur de la SeekBar de vitesse
	 */
	private static final int HANDLER_SEEK_BAR_CHANGED_VITESSE = 614300;

	/**
	 * Identifie l'action du Hundler - Test
	 */
	private static final int HANDLER_SEEK_BAR_CHANGED_COUPLE = 0003;

	/**
	 * IHM : Affiche la vitesse de deplacement du robot
	 */
	private TextView vitesseTextView = null;

	/**
	 * IHM : Affiche le couple de la pince
	 */
	private TextView coupleTextView = null;

	/**
	 * IHM : Change la vitesse de deplacement du robot
	 */
	private SeekBar vitesseSeekBar = null;

	/**
	 * IHM : Change le couple de la pince
	 */
	private SeekBar coupleSeekBar = null;

	/**
	 * Vitesse du déplacement du robot
	 */
	private int vitesse = 30;

	/**
	 * Couple de la pince
	 */
	private int couple = 511;

	/**
	 * Socket pour envoyer les commandes
	 */
	private Socket socket = null;

	/**
	 * Le flux d'envoi des requêtes
	 */
	private PrintWriter emetteur = null;

	/**
	 * Le flux de réception des données
	 */
	private BufferedReader recepteur = null;

	/**
	 * Adresse IP du PC Contrôleur
	 */
	private String ip;

	/**
	 * Numéro de port du PC Contrôleur
	 */
	private int port;
	
	/**
	 * Listener : Serre la pince du robot
	 */
	private OnTouchListener serrerPince = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButtonHover));
				emission("SERRER:"+couple);
				
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				emission("STOP:"+v.getTag().toString().substring(0, 1));
			}
			
			return false;
		}
	};
	
	/**
	 * Listener : Relache la pince du robot
	 */
	private OnTouchListener relacherPince = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButtonHover));
				emission("RELACHER");
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
			}
			return false;
		}
	};
	
	/**
	 * Listener : Fait bouger le Robot
	 */
	private OnTouchListener mouvementNegatif = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButtonHover));
				emission("MOVE:"+v.getTag().toString().substring(0, 1)+":-:"+vitesse);

			}

			if (action == MotionEvent.ACTION_UP) {
				//v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				String requete = "STOP:"
						+ v.getTag().toString().substring(0, 1);
				Log.d(LOG_TAG, "onTouch : " + requete);
				emission(requete);
			}
			return true;
		}
	};

	private OnTouchListener mouvementPositif = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(getResources().getColor(
							R.color.MyButtonHover));

					String requete = "MOVE:"
							+ v.getTag().toString().substring(0, 1) + ":+:"
							+ vitesse;

					Log.d(LOG_TAG, "onTouch : " + requete);
					emission(requete);
			}

			if (action == MotionEvent.ACTION_UP) {
				//v.setFocusable(false);
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				String requete = "STOP:"
						+ v.getTag().toString().substring(0, 1);

				Log.d(LOG_TAG, "onTouch : " + requete);
				emission(requete);
			}
			return true;
		}
	};

	/**
	 * Les dialogs
	 */
	private DialogFragment mDialog = new MDialog();

	/**
	 * Le Handler (Thread spécialisé) charger de modifier le IHM
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CONNEXION_SOCKET:
				Log.d(LOG_TAG, "HANDLER_CONNEXION_SOCKET");
				afficherMessageToast("Connexion en cours " + ip + ":" + port);
				threadConnexionReseaux.start();
				break;
			case HANDLER_SEEK_BAR_CHANGED_VITESSE:
				// Log.d(LOG_TAG, "HANDLER_SEEK_BAR_CHANGMENT");
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
	 * Thread de connexion réseaux
	 */
	
	
	
	
	private Thread threadConnexionReseaux = new Thread(new Runnable() {
		@Override
		public void run() {
			Log.d(LOG_TAG, "threadConnexionReseaux RUN");
			try {
				socket = new Socket(ip, port);
				if (socket != null) {
					Log.d(LOG_TAG, "socket NOT NULL");
//					InetAddress ipDist = socket.getInetAddress();
//					if (ipDist != null) {
						Log.d(LOG_TAG, "ipDist NOT NULL");
						emetteur = new PrintWriter(socket.getOutputStream(),
								true);
						recepteur = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						emission("M");
//					} else {
//						Log.d(LOG_TAG, "ipDist NULL");
//						afficherMessageToast("Erreur de connexion");
//						showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
//					}
				} else {
					Log.d(LOG_TAG, "socket NULL");
					afficherMessageToast("Erreur de connexion");
					showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				}
			} catch (UnknownHostException e) {
				showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
			} catch (IOException e) {
				showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
			}
		}
	});

	/* ----------------------- METHODES ---------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.telecommande);
		// Show the Up button in the action bar.
		setupActionBar();
		Log.d(LOG_TAG, "onCreate");

		vitesseTextView = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		vitesseSeekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		vitesseSeekBar.setOnSeekBarChangeListener(this);
		vitesseSeekBar.setProgress(vitesse);

		coupleSeekBar = (SeekBar) findViewById(R.id.coupleSeekBar);
		coupleTextView = (TextView) findViewById(R.id.coupleTextView);
		coupleSeekBar.setOnSeekBarChangeListener(this);
		coupleSeekBar.setProgress(400);
		
		((Button) findViewById(R.id.BaseNegatif)).setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.BasePositif)).setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.EpauleNegatif)).setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.EpaulePositif)).setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.CoudeNegatif)).setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.CoudePositif)).setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.TangageNegatif)).setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.TangagePositif)).setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.RoulisNegatif)).setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.RoulisPositif)).setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.SerrerPince)).setOnTouchListener(serrerPince);
		((Button) findViewById(R.id.RelacherPince)).setOnTouchListener(relacherPince);
		// On affiche le dialog de connexion
		showDialoge(MDialog.DIALOG_CONNEXION_SOCKET);
	}
	
	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");
		if (socket != null) {
			// On ferme le Client socket à la fermeture de l'application
			try {
				Log.d(LOG_TAG, "onDestroy : Socket NUT NULL");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
	
	public void afficherMessageToast(String msg) {
		Log.d(LOG_TAG, "Toast : " + msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void showDialoge(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}

	public void doPositiveClick(String ip, int port) {
		Log.d(LOG_TAG, "doPositiveClick : " + ip + ":" + port);
		this.ip = ip;
		this.port = port;
		handler.sendEmptyMessage(HANDLER_CONNEXION_SOCKET);

	}

	public void doNegativeClick() {
		Log.d(LOG_TAG, "doNegativeClick");
		startActivity(new Intent(this, Accueil.class));
	}

	public void emission(String msg) {
		Log.d(LOG_TAG, "emission : " + msg);
		if (socket != null) {
			if (socket.isConnected()) {
				emetteur.println(msg);
				// handler.sendEmptyMessage(AUTRES);
			} else {
				Log.d(LOG_TAG, "Emission Erreur Socket NOT CONNECTED");
			}
		} else {
			afficherMessageToast("Emission Erreur Socket NULL");
			Log.d(LOG_TAG, "Emission Erreur Socket NULL");
		}
	}

	/* Menu et Navigation */

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.telecommande, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.connexion:
			startActivity(new Intent(this, Telecommande.class));
			return true;
		case R.id.reset:
			emission("RESET");
		}
		return super.onOptionsItemSelected(item);
	}

	
	/**
	 * SeekBar événement : changement de vitesse
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.d(LOG_TAG, "onProgressChanged SeekBar");
		if (seekBar.getTag().toString().compareTo("VITESSE") == 0) {
			handler.sendEmptyMessage(HANDLER_SEEK_BAR_CHANGED_VITESSE);
		}
		if (seekBar.getTag().toString().compareTo("COUPLE") == 0) {
			handler.sendEmptyMessage(HANDLER_SEEK_BAR_CHANGED_COUPLE);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d(LOG_TAG, "onStartTrackingTouch SeekBar");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d(LOG_TAG, "onStopTrackingTouch SeekBar");
	}

}
