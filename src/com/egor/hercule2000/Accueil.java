package com.egor.hercule2000;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Accueil extends Activity {
	public static final String LOG_TAG = "Egor";
	/**
	 * L'objet qui gère le wi-fi du téléphone
	 */
	private WifiManager wifiManager = null;
	/**
	 * Les dialogs
	 */
	private DialogFragment mDialog = new MDialog();
	private Button accueil_accelerometre = null;
	private Button accueil_telecommande = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accueil);

		// On vérifie si le wi-fi est activé
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifiManager != null) {
			if (wifiManager.isWifiEnabled()) {
				Log.d(LOG_TAG, "Wifi : OK");
			} else {
				showDialoge(MDialog.DIALOG_WIFI_ACTIVER);
			}
		}

		accueil_accelerometre = (Button) findViewById(R.id.accueil_accelerometre);
		accueil_accelerometre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Clique sur Accelerometre
				startActivity(new Intent(Accueil.this, Accelerometre.class));
			}
		});

		accueil_telecommande = (Button) findViewById(R.id.accueil_telecommande);
		accueil_telecommande.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Clique sur Télécommande
				startActivity(new Intent(Accueil.this, Telecommande.class));
			}
		});
	}

	/**
	 * Active le wifi
	 */
	public void startWifi() {
		wifiManager.setWifiEnabled(true);
	}

	/**
	 * Affiche le dialogue
	 * 
	 * @param tag
	 *            le dialogue a afficher
	 */
	public void showDialoge(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}
}
