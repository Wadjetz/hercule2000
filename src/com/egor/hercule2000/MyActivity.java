package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;

public class MyActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
	
	/* ----------------------------------------------------- */
	/* ----------------------- ATTRIBUTS ------------------- */
	/* ----------------------------------------------------- */
	protected ProgressDialog progressDialog;
	/**
	 * Log Tag pour les messages de debuguages
	 */
	protected static final String LOG_TAG = "Egor";
	/**
	 * Identifie l'action du Hundler - Affiche le dialog de connexion
	 */
	protected static final int HANDLER_CONNEXION_SOCKET = 146113;
	
	/**
	 * Identifie l'action du Hundler - Change la valeur de la SeekBar de vitesse
	 */
	protected static final int HANDLER_SEEK_BAR_CHANGED_VITESSE = 6124300;

	/**
	 * Identifie l'action du Hundler - Test
	 */
	protected static final int HANDLER_SEEK_BAR_CHANGED_COUPLE = 123003;
	
	/**
	 * Adresse IP du PC Contr�leur
	 */
	protected String ip;

	/**
	 * Num�ro de port du PC Contr�leur
	 */
	protected int port;
	
	/**
	 * Vitesse du d�placement du robot
	 */
	protected int vitesse = 30;

	/**
	 * Couple de la pince
	 */
	protected int couple = 511;
	
	/**
	 * Les dialogs
	 */
	protected DialogFragment mDialog = new MDialog();
	
	/**
	 * Connexion Reseaux
	 */
	//protected Reseau reseaux = new Reseau();
	
	/**
	 * Socket pour envoyer les commandes
	 */
	private Socket socket = new Socket();
	/**
	 * Adresse du serveur
	 */
	InetSocketAddress socketAddress = null;
	
	/**
	 * Le flux d'envoi des requ�tes
	 */
	private PrintWriter emetteur = null;

	/**
	 * Le flux de r�ception des donn�es
	 */
	private BufferedReader recepteur = null;
	
	/**
	 * IHM : Affiche la vitesse de deplacement du robot
	 */
	protected TextView vitesseTextView = null;
	
	
	/**
	 * IHM : Change la vitesse de deplacement du robot
	 */
	protected SeekBar vitesseSeekBar = null;
	
	/**
	 * IHM : Affiche le couple de la pince
	 */
	protected TextView coupleTextView = null;

	/**
	 * IHM : Change le couple de la pince
	 */
	protected SeekBar coupleSeekBar = null;
	
	/**
	 * Le Handler (Thread sp�cialis�) charger de modifier le IHM
	 */
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CONNEXION_SOCKET:
				Log.d(LOG_TAG, "HANDLER_CONNEXION_SOCKET");
				//afficherMessageToast("Connexion en cours " + ip + ":" + port);
				connexion();
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
	 * Listener : Serre la pince du robot
	 */
	protected OnTouchListener serrerPince = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));
				emission("P:" + couple);

			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				emission("S:"
						+ v.getTag().toString().substring(0, 1));
				emission("S:"
						+ v.getTag().toString().substring(0, 1));
			}

			return false;
		}
	};
	
	/**
	 * Listener : Relache la pince du robot
	 */
	protected OnTouchListener relacherPince = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));
				emission("P:-");
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
			}
			return false;
		}
	};
	
	
	private Thread threadConnexionReseaux = new Thread(new Runnable() {
		@Override
		public void run() {
			Log.d(LOG_TAG, "threadConnexionReseaux RUN");
			try {
				socket = new Socket(ip, port);
				if (socket != null) {
					Log.d(LOG_TAG, "socket NOT NULL");
					emetteur = new PrintWriter(socket.getOutputStream(), true);
					recepteur = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					emission("M:1234");
				} else {
					Log.d(LOG_TAG, "socket NULL");
				}
			} catch (UnknownHostException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				progressDialog.dismiss();
				afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
			} catch (IOException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				progressDialog.dismiss();
				afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
			}
			progressDialog.dismiss();
		}
	});
	
	
	/* ----------------------------------------------------- */
	/* ---------------------- METHODES --------------------- */
	/* ----------------------------------------------------- */
	
	/**
	 * Connexion r�seau, instanciation du socket dans un thread
	 * @param ip Adresse IP du destinataire
	 * @param port Num�ro de port du serveur
	 */
	public void connexion() {
		// On ajoute un message � notre progress dialog
		progressDialog.setMessage("Connexion en cours");
		// On affiche notre message
		progressDialog.show();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.d(LOG_TAG, "threadConnexionReseaux RUN");
				socketAddress = new InetSocketAddress(ip, port);
				try {
					socket.connect(socketAddress, 2000);
					  if(socket.isConnected()){
						  emetteur = new PrintWriter(socket.getOutputStream(), true);
						  recepteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						  emission("T:1234");
					  }
				} catch (IOException e) {
					Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
					progressDialog.dismiss();
					afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				}
				/*try {
					
					socket.connect(socketAddress, 2000);
					if (socket != null) {
						Log.d(LOG_TAG, "socket NOT NULL");
						emetteur = new PrintWriter(socket.getOutputStream(), true);
						recepteur = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						emission("T:1234");
					} else {
						Log.d(LOG_TAG, "socket NULL");
					}
				} catch (UnknownHostException e) {
					Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
					progressDialog.dismiss();
					afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ACCELEROMETRE_ERREUR);
				} catch (IOException e) {
					Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
					progressDialog.dismiss();
					afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ACCELEROMETRE_ERREUR);
				}*/
				progressDialog.dismiss();
				
			}}).start();
		//threadConnexionReseaux.start();
	}
	
	/**
	 * Envoie les requ�tes vers le r�seau
	 * @param msg La requ�te a envoy�
	 */
	public void emission(String msg) {
		Log.d(LOG_TAG, "emission : " + msg);
		if (socket != null) {
			if (socket.isConnected()) {
				emetteur.println(msg);
			} else {
				Log.d(LOG_TAG, "Emission Erreur Socket NOT CONNECTED");
			}
		} else {
			Log.d(LOG_TAG, "Emission Erreur Socket NULL");
		}
	}
	
	/**
	 * Ferme le socket s'il a �tait cr��
	 */
	public void close() {
		Log.d(LOG_TAG, "reseau close");
		if (socket != null) {
			// On ferme le Client socket � la fermeture de l'application
			try {
				Log.d(LOG_TAG, "reseau close : Socket NOT NULL");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Affiche le dialog
	 * @param tag Le tag du dialog a affich�
	 */
	public void afficherDialogue(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}
	
	/**
	 * M�thode de callback ex�cuter par le click sur le bouton Annul� du dialogue
	 */
	public void doNegativeClick() {
		Log.d(LOG_TAG, "doNegativeClick");
		startActivity(new Intent(this, Accueil.class));
	}
	
	/**
	 * M�thode de callback ex�cuter par le click sur le bouton ok du dialogue de connexion r�seau
	 * @param ip Adresse IP du destinataire
	 * @param port Num�ro de port du serveur
	 */
	public void doPositiveClick(String ip, int port) {
		Log.d(LOG_TAG, "doPositiveClick : " + ip + ":" + port);
		this.ip = ip;
		this.port = port;
		handler.sendEmptyMessage(HANDLER_CONNEXION_SOCKET);
	}
	
	/**
	 * SeekBar �v�nement : changement de vitesse
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
