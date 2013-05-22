package com.egor.hercule2000;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class MyActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
	
	/* ----------------------------------------------------- */
	/* ----------------------- ATTRIBUTS ------------------- */
	/* ----------------------------------------------------- */
	
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
	protected Reseau reseaux = new Reseau();
	
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
	 * Le Handler (Thread spécialisé) charger de modifier le IHM
	 */
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CONNEXION_SOCKET:
				Log.d(LOG_TAG, "HANDLER_CONNEXION_SOCKET");
				//afficherMessageToast("Connexion en cours " + ip + ":" + port);
				reseaux.connexion(ip, port, 0);
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
				reseaux.emission("P:" + couple);

			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
				reseaux.emission("S:"
						+ v.getTag().toString().substring(0, 1));
				reseaux.emission("S:"
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
				reseaux.emission("P:-");
			}
			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(getResources().getColor(R.color.MyButton));
			}
			return false;
		}
	};
	
	/* ----------------------------------------------------- */
	/* ---------------------- METHODES --------------------- */
	/* ----------------------------------------------------- */
	
	/**
	 * Affiche le dialog
	 * @param tag Le tag du dialog a affiché
	 */
	public void afficherDialogue(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}
	
	/**
	 * Méthode de callback exécuter par le click sur le bouton Annulé du dialogue
	 */
	public void doNegativeClick() {
		Log.d(LOG_TAG, "doNegativeClick");
		startActivity(new Intent(this, Accueil.class));
	}
	
	/**
	 * Méthode de callback exécuter par le click sur le bouton ok du dialogue de connexion réseau
	 * @param ip Adresse IP du destinataire
	 * @param port Numéro de port du serveur
	 */
	public void doPositiveClick(String ip, int port) {
		Log.d(LOG_TAG, "doPositiveClick : " + ip + ":" + port);
		this.ip = ip;
		this.port = port;
		handler.sendEmptyMessage(HANDLER_CONNEXION_SOCKET);
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
