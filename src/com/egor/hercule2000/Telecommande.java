package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
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

import com.egor.robot.Robot;

public class Telecommande extends Activity implements
		SeekBar.OnSeekBarChangeListener {

	private Button button1 = null;

	// Robot
	private Robot robot = new Robot();

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
	private static final int HANDLER_SEEK_BAR_CHANGMENT = 614300;

	/**
	 * Identifie l'action du Hundler - Test
	 */
	private static final int AUTRES = 0003;

	/**
	 * IHM : Affiche la vitesse de deplacement du robot
	 */
	private TextView vitesseTextView = null;
	/**
	 * IHM : Change la vitesse de deplacement du robot
	 */
	private SeekBar vitesseSeekBar = null;

	/**
	 * Vitesse du déplacement du robot
	 */
	private int vitesse = 30;

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
			case HANDLER_SEEK_BAR_CHANGMENT:
				Log.d(LOG_TAG, "HANDLER_SEEK_BAR_CHANGMENT");
				vitesse = vitesseSeekBar.getProgress() + 1;
				vitesseTextView.setText("Vitesse : " + vitesse);
				break;
			case AUTRES:
				try {
					String r = "rien";
					//r = recepteur.readLine();
					while ((r = recepteur.readLine()) != null) {
						Log.d(LOG_TAG, "Recu : " + r);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
					InetAddress ipDist = socket.getInetAddress();
					if (ipDist != null) {
						Log.d(LOG_TAG, "ipDist NOT NULL");
						emetteur = new PrintWriter(socket.getOutputStream(),
								true);
						recepteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						emission("M");
					} else {
						Log.d(LOG_TAG, "ipDist NULL");
						afficherMessageToast("Erreur de connexion");
						showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
					}
				} else {
					Log.d(LOG_TAG, "socket NULL");
					afficherMessageToast("Erreur de connexion");
					showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				}
			} catch (UnknownHostException e) {
				showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
			} catch (IOException e) {
				showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
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

		// On affiche le dialog de connexion
		showDialog(MDialog.DIALOG_CONNEXION_SOCKET);

		// TEST
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//t.start();
//				int action = event.getAction();
//				int i = 0;
//				while (i < 100) {
//					Log.d(LOG_TAG, "Envoie");
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					if (action == MotionEvent.ACTION_CANCEL) {
//						break;
//					}
//					i++;
//				}
//				Log.d(LOG_TAG, "Envoie FIN");
				return false;
			}
		});

	}

	public void afficherMessageToast(String msg) {
		Log.d(LOG_TAG, "Toast : " + msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void showDialog(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}

	public void rotationNegative(View view) {
		Log.d(LOG_TAG, "rotationNegative : " + view.getTag());
		emission(robot.calculeRotation(
				view.getTag().toString().substring(0, 1), Robot.NEGATIVE, 100,
				vitesse));
	}

	public void rotationPositive(View view) {
		Log.d(LOG_TAG, "rotationPositive : " + view.getTag());
		emission(robot.calculeRotation(
				view.getTag().toString().substring(0, 1), Robot.POSITIVE, 50,
				vitesse));
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
				//handler.sendEmptyMessage(AUTRES);
			} else {
				Log.d(LOG_TAG, "Emission Erreur Socket NOT CONNECTED");
			}
		} else {
			afficherMessageToast("Emission Erreur Socket NULL");
			Log.d(LOG_TAG, "Emission Erreur Socket NULL");
		}
	}
	
	private Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				String r;
				while ((r = recepteur.readLine()) != null) {
					Log.d(LOG_TAG, "Recu : " + r);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	});
	
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
		getMenuInflater().inflate(R.menu.commande_manuelle, menu);
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

	/**
	 * SeekBar événement : changement de vitesse
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.d(LOG_TAG, "onProgressChanged SeekBar");
		handler.sendEmptyMessage(HANDLER_SEEK_BAR_CHANGMENT);

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
