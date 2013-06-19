package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class Telecommande extends MyActivity implements SeekBar.OnSeekBarChangeListener  {
	
	/* ----------------------- METHODES ---------------------- */
	/**
	 * Initialisation de l'IHM
	 */
	private void ihm() {
		vitesseTextView = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		vitesseSeekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		vitesseSeekBar.setOnSeekBarChangeListener(this);
		vitesseSeekBar.setProgress(vitesse);
		messageRecu = (TextView)findViewById(R.id.messageRecu);
		coupleSeekBar = (SeekBar) findViewById(R.id.coupleSeekBar);
		coupleTextView = (TextView) findViewById(R.id.coupleTextView);
		coupleSeekBar.setOnSeekBarChangeListener(this);
		coupleSeekBar.setProgress(400);
		
		lancerCapture = (Button)findViewById(R.id.lancerCapture);
		lancerCapture.setEnabled(false);
		
		((Button) findViewById(R.id.BaseNegatif))
				.setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.BasePositif))
				.setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.EpauleNegatif))
				.setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.EpaulePositif))
				.setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.CoudeNegatif))
				.setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.CoudePositif))
				.setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.TangageNegatif))
				.setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.TangagePositif))
				.setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.RoulisNegatif))
				.setOnTouchListener(mouvementNegatif);
		((Button) findViewById(R.id.RoulisPositif))
				.setOnTouchListener(mouvementPositif);
		((Button) findViewById(R.id.SerrerPince))
				.setOnTouchListener(serrerPince);
		((Button) findViewById(R.id.RelacherPince))
				.setOnTouchListener(relacherPince);
	}
	
	

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
					reception();
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				progressDialog.dismiss();
				afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
			}
			progressDialog.dismiss();
			if(socket.isConnected()){
				//reception();
			}
		}
	});
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.telecommande);
		// Show the Up button in the action bar.
		setupActionBar();
		progressDialog = new ProgressDialog(this);

		this.ihm();
		// On affiche le dialog de connexion
		afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET_TEL);
	}

	@Override
	protected void onDestroy() {
		// On ferme le socket
		Log.d(LOG_TAG, "onDestroy Telecommande");
		super.close();
		super.onDestroy();
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
			startActivity(new Intent(this, Accueil.class));
			//NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.connexion:
			startActivity(new Intent(this, Telecommande.class));
			return true;
		case R.id.reset:
			envoyer("R");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Listener : Fait bouger le Robot
	 */
	private OnTouchListener mouvementNegatif = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.droite_rouge)));
				t0 = System.currentTimeMillis();
				requete = "M:" + v.getTag().toString().substring(0, 1)+":-:" + vitesse;
				envoyer(requete);
//				reception();

			}

			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.droite_vert)));
				long current = System.currentTimeMillis();
				delais = current - t0;
				if (capture) {
					al.add(new Pair<String, Long>(requete, delais));
				}
				//Log.d(LOG_TAG, "delais : " + delais);
				envoyer("S:" + v.getTag().toString().substring(0, 1));
//				reception();
			}
			return true;
		}
	};

	/**
	 * Listener : Gere les boutons plus(+) de la télécommande
	 */
	private OnTouchListener mouvementPositif = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.gauche_rouge)));
				t0 = System.currentTimeMillis();
				requete = "M:" + v.getTag().toString().substring(0, 1) + ":+:" + vitesse;
				envoyer(requete);
//				reception();

			}

			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.gauche_vert)));
				long current = System.currentTimeMillis();
				delais = current - t0;
				if (capture) {
					al.add(new Pair<String, Long>(requete, delais));
				}
				envoyer("S:" + v.getTag().toString().substring(0, 1));
//				reception();
			}
			return true;
		}
	};
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//Log.d(LOG_TAG, "onStartTrackingTouch SeekBar");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//Log.d(LOG_TAG, "onStopTrackingTouch SeekBar");
	}

}
