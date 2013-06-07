package com.egor.hercule2000;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Accueil extends Activity {
	public static final String LOG_TAG = "Egor";
	/**
	 * L'objet qui g�re le wi-fi du t�l�phone
	 */
	private WifiManager wifiManager = null;
	private ListView listeVue = null;
	/**
	 * Les dialogs
	 */
	private DialogFragment mDialog = new MDialog();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accueil);
		
		// On v�rifie si le wi-fi est activ�
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifiManager != null) {
			if (wifiManager.isWifiEnabled()) {
				Log.d(LOG_TAG, "Wifi : OK");
			} else {
				showDialoge(MDialog.DIALOG_WIFI_ACTIVER);
			}
		}

		// On r�cup�re la ListView
		listeVue = (ListView) findViewById(R.id.listView_acceuil_commandes);

		String[] listCommandes = new String[] { "T�l�commande", "Accelerometre" };

		ArrayAdapter<String> adaptateur = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1, listCommandes);

		listeVue.setAdapter(adaptateur);

		listeVue.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// Clique sur T�l�commande
					startActivity(new Intent(Accueil.this, Telecommande.class));
					break;
				case 1:
					// Clique sur Accelerometre
					startActivity(new Intent(Accueil.this, Accelerometre.class));
					break;
				}
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
	 * @param tag le dialogue a afficher
	 */
	public void showDialoge(String tag) {
		Log.d(LOG_TAG, "showDialog : " + tag);
		mDialog.show(getFragmentManager(), tag);
	}
}
