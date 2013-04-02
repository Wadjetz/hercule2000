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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egor.robot.Robot;

public class CommandeManuelle extends Activity implements
		SeekBar.OnSeekBarChangeListener, OnClickListener {

	private float articulations[]  = new float[5];
	private float saveArticulations[] = new float[5];
	
	private EditText ET_Base = null, ET_Epaule = null, ET_Coude = null,
			ET_Tangage = null, ET_Roulis = null;

	private Button envoyer = null;

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
	 * Vitesse du d�placement du robot
	 */
	private int vitesse = 30;

	/**
	 * Socket pour envoyer les commandes
	 */
	private Socket socket = null;

	/**
	 * Le flux d'envoi des requ�tes
	 */
	private PrintWriter emetteur = null;

	/**
	 * Le flux de r�ception des donn�es
	 */
	private BufferedReader recepteur = null;

	/**
	 * Adresse IP du PC Contr�leur
	 */
	private String ip;

	/**
	 * Num�ro de port du PC Contr�leur
	 */
	private int port;

	/**
	 * Les dialogs
	 */
	private DialogFragment mDialog = new MDialog();

	/**
	 * Le Handler (Thread sp�cialis�) charger de modifier le IHM
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
					// r = recepteur.readLine();
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
	 * Thread de connexion r�seaux
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
						recepteur = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commande_manuelle);
		// Show the Up button in the action bar.
		setupActionBar();

		ET_Base = (EditText) findViewById(R.id.basePosition);
		ET_Epaule = (EditText) findViewById(R.id.epaulePosition);
		ET_Coude = (EditText) findViewById(R.id.coudePosition);
		ET_Tangage = (EditText) findViewById(R.id.tangagePosition);
		ET_Roulis = (EditText) findViewById(R.id.roulisPosition);
		envoyer = (Button) findViewById(R.id.B_Enoyer);
		envoyer.setOnClickListener(this);
		getPosition();
		setPositionView();
	}

	@Override
	protected void onDestroy() {
		Log.d(LOG_TAG, "onDestroy");
		if (socket != null) {
			// On ferme le Client socket � la fermeture de l'application
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
				// handler.sendEmptyMessage(AUTRES);
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

	/**
	 * SeekBar �v�nement : changement de vitesse
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
	public void onClick(View v) {
		
		getPositionView();
		afficherMessageToast("");
	}

	public void getPosition() {
		for(int i=0; i<5; i++) {
			articulations[i] = (float)Math.random() * 60;
		}
	}
	
	public void savePosition() {
		for(int i=0; i<5; i++) {
			articulations[i] = saveArticulations[i];
		}
	}
	
	public void getPositionView() {
		articulations[0] = Float.valueOf(ET_Base.getText().toString());
		articulations[1] = Float.valueOf(ET_Epaule.getText().toString());
		articulations[2] = Float.valueOf(ET_Coude.getText().toString());
		articulations[3] = Float.valueOf(ET_Tangage.getText().toString());
		articulations[4] = Float.valueOf(ET_Roulis.getText().toString());
	}
	
	public void setPositionView() {
		ET_Base.setText(String.valueOf(articulations[0]));
		ET_Epaule.setText(String.valueOf(articulations[1]));
		ET_Coude.setText(String.valueOf(articulations[2]));
		ET_Tangage.setText(String.valueOf(articulations[3]));
		ET_Roulis.setText(String.valueOf(articulations[4]));
	}

}
