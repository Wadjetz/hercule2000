package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyActivity extends Activity{

	/* ----------------------------------------------------- */
	/* ----------------------- ATTRIBUTS ------------------- */
	/* ----------------------------------------------------- */

	protected Button lancerCapture = null;

	protected String recu = "";

	public synchronized String getRecu() {
		return this.recu;
	}

	public synchronized void setRecu(String recu) {
		this.recu = recu;
	}

	protected long t0;
	/**
	 * La durer du mouvement
	 */
	protected long delais = 0;
	/**
	 * Liste où on enregistre les mouvements
	 */
	protected ArrayList<Pair<String, Long>> al = new ArrayList<Pair<String, Long>>();
	protected String requete;
	protected boolean capture = false;

	protected ProgressDialog progressDialog = null;
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
	 * Adresse IP du PC Contrôleur
	 */
	protected String ip;

	/**
	 * Numéro de port du PC Contrôleur
	 */
	protected int port;

	/**
	 * Vitesse du déplacement du robot
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
	// protected Reseau reseaux = new Reseau();

	/**
	 * Socket pour envoyer les commandes
	 */
	protected Socket socket = new Socket();
	/**
	 * Adresse du serveur
	 */
	protected InetSocketAddress socketAddress = null;

	/**
	 * Le flux d'envoi des requêtes
	 */
	protected PrintWriter emetteur = null;

	/**
	 * Le flux de réception des données
	 */
	protected BufferedReader recepteur = null;

	/**
	 * IHM : Affiche la vitesse de deplacement du robot
	 */
	protected TextView vitesseTextView = null;

	/**
	 * IHM : Affiche la vitesse de deplacement du robot
	 */
	protected TextView messageRecu = null;

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
	 * Listener : Serre la pince du robot
	 */
	protected OnTouchListener serrerPince = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(getResources().getColor(
						R.color.MyButtonHover));
				t0 = System.currentTimeMillis();
				requete = "P:+:" + couple;
				envoyer(requete);
//				reception();
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				long current = System.currentTimeMillis();
				delais = current - t0;
				if (capture) {
					al.add(new Pair<String, Long>(requete, delais));
				}
				envoyer("S:" + v.getTag().toString().substring(0, 1));
//				reception();

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

				requete = "P:-:0";
				envoyer(requete);
//				reception();
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				if (capture) {
					al.add(new Pair<String, Long>(requete, (long) 1500));
				}
				envoyer("S:" + v.getTag().toString().substring(0, 1));
//				reception();
			}
			return false;
		}
	};

	/* ----------------------------------------------------- */
	/* ---------------------- METHODES --------------------- */
	/* ----------------------------------------------------- */

	/**
	 * Envoie les requêtes vers le réseau
	 * 
	 * @param msg
	 *            La requête a envoyé
	 */
	public void envoyer(String msg) {
		//Log.d(LOG_TAG, "emission : " + msg);
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

	// Methode en cours de developpement
	public void reception() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Log.d(LOG_TAG, "debut : readLine");
					String s = recepteur.readLine();
					Log.d(LOG_TAG, "fin : readLine");
					setRecu(s);
					Log.d(LOG_TAG, getRecu());
					if(getRecu().compareTo("OC") == 0) {
						runOnUiThread(new Runnable() {
					        public void run() {
					          messageRecu.setText("Occupé");
					        }
					      });
					}
					if(getRecu().compareTo("OK") == 0) {
						runOnUiThread(new Runnable() {
					        public void run() {
					          messageRecu.setText("Libre");
					        }
					      });
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	/**
	 * Ferme le socket s'il a était créé
	 */
	public void close() {
		Log.d(LOG_TAG, "reseau close");
		if (socket != null) {
			// On ferme le Client socket à la fermeture de l'application
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
	 * 
	 * @param tag
	 *            Le tag du dialog a affiché
	 */
	public void afficherDialogue(String tag) {
		//Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}

	/**
	 * Méthode de callback exécuter par le click sur le bouton Annulé du
	 * dialogue
	 */
	public void doNegativeClick() {
		//Log.d(LOG_TAG, "doNegativeClick");
		startActivity(new Intent(this, Accueil.class));
	}

	
	public boolean isIp(String ip)
	{
		String[] tabIp = ip.split("\\.");
		boolean isIp = Boolean.TRUE;
		try {
			if (tabIp.length!=4) return false;
			int digitIp =0; 
			for (int i=0;i<tabIp.length;i++)
			{
				digitIp = Integer.parseInt(tabIp[i]);
				if (digitIp<0 || digitIp>255)
					isIp = Boolean.FALSE;
			}
		} catch (Exception e) {
			isIp = Boolean.FALSE;
		}
		return isIp;
	}
	
	/**
	 * Mode automatique
	 * @param view
	 */
	public void lancerCapture(View view) {
		if (capture != true) {

			// On ajoute un message à notre progress dialog
			progressDialog.setMessage("Execution en cours");
			// On affiche notre message
			progressDialog.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					envoyer("R");
					//reception();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for (int i = 0; i < al.size(); i++) {
						Pair<String, Long> p = al.get(i);
						//Log.d(LOG_TAG, p.first + ":" + p.second);
						try {
							envoyer(p.first);
							Thread.sleep(p.second);
							envoyer("S:B");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// A la fin du traitement, on fait disparaitre notre message
					progressDialog.dismiss();
				}

			}).start();
		}
		else {
			Toast.makeText(this, "Arreter la capture", Toast.LENGTH_SHORT).show();	
		}
	}
	
	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			al.clear();
			envoyer("R");
			// Enable vibrate
			capture = true;
			lancerCapture.setEnabled(false);
			Toast.makeText(this, "Capture", Toast.LENGTH_SHORT).show();
		} else {
			// Disable vibrate
			lancerCapture.setEnabled(true);
			capture = false;
		}
	}
	
}
