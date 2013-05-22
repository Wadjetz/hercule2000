package com.egor.hercule2000;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.Toast;
import android.widget.ToggleButton;

public class Telecommande extends MyActivity {
	long t0;
	long delais = 0;
	ArrayList<Pair<String, Long>> al = new ArrayList<Pair<String, Long>>();
	String requete;
	boolean capture = false;
	
	/* ----------------------- METHODES ---------------------- */

	private void ihm() {
		vitesseTextView = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		vitesseSeekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		vitesseSeekBar.setOnSeekBarChangeListener(this);
		vitesseSeekBar.setProgress(vitesse);

		coupleSeekBar = (SeekBar) findViewById(R.id.coupleSeekBar);
		coupleTextView = (TextView) findViewById(R.id.coupleTextView);
		coupleSeekBar.setOnSeekBarChangeListener(this);
		coupleSeekBar.setProgress(400);

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.telecommande);
		// Show the Up button in the action bar.
		setupActionBar();
		progressDialog = new ProgressDialog(this);

		this.ihm();
		// On affiche le dialog de connexion
		afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET);
	}

	@Override
	protected void onDestroy() {
		close();
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.connexion:
			startActivity(new Intent(this, Telecommande.class));
			return true;
		case R.id.reset:
			emission("RESET");
		case R.id.capturer:
			capture = true;
			return true;
		case R.id.lancer:
			for (int i = 0; i < al.size(); i++) {
				Pair<String, Long> p = al.get(i);
				Log.d(LOG_TAG, p.first + ":" + p.second);
			}
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
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.gauche_rouge)));
				t0 = System.currentTimeMillis();
				requete = "M:" + v.getTag().toString().substring(0, 1)+":-:" + vitesse;
				emission(requete);

			}

			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.droite_vert)));
				long current = System.currentTimeMillis();
				delais = current - t0;
				if (capture) {
					al.add(new Pair<String, Long>(requete, delais));
				}
				Log.d(LOG_TAG, "delais : " + delais);
				emission("S:" + v.getTag().toString().substring(0, 1));
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
				emission(requete);

			}

			if (action == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(getResources().getDrawable((R.drawable.droite_vert)));
				long current = System.currentTimeMillis();
				delais = current - t0;
				if (capture) {
					al.add(new Pair<String, Long>(requete, delais));
				}
				emission("S:" + v.getTag().toString().substring(0, 1));
			}
			return true;
		}
	};

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			// Enable vibrate
			capture = true;
			Toast.makeText(this, "Capture", Toast.LENGTH_SHORT).show();
		} else {
			// Disable vibrate
			capture = false;
		}
	}

	public void lancerCapture(View view) {
		if (capture != true) {

			// On ajoute un message à notre progress dialog
			progressDialog.setMessage("Execution en cours");
			// On affiche notre message
			progressDialog.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					emission("R");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for (int i = 0; i < al.size(); i++) {
						Pair<String, Long> p = al.get(i);
						Log.d(LOG_TAG, p.first + ":" + p.second);
						try {
							emission(p.first);
							Thread.sleep(p.second);
							emission("S");
							Thread.sleep(500);
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
}
