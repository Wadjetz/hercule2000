package com.egor.hercule2000;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Accueil extends Activity {
	
	private ListView listeVue = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accueil);
		
		// On récupère la ListView
		listeVue = (ListView)findViewById(R.id.listView_acceuil_commandes);
		
		String[] listCommandes = new String[]{
				"Télécommande",
				"Gyroscope",
				"Commande Accelerometre",
				"Commande Manuelle"
		};
		
		ArrayAdapter<String> adaptateur = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, listCommandes);
		
		listeVue.setAdapter(adaptateur);
		
		listeVue.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(Accueil.this, Telecommande.class));
					break;
				case 1:
					startActivity(new Intent(Accueil.this, GyroscopeCommande.class));
					break;
				case 2:
					startActivity(new Intent(Accueil.this, SensorAccelerationTutoActivity.class));
					break;
				case 3:
					startActivity(new Intent(Accueil.this, CommandeManuelle.class));
				}
			}
			
		});
	}
}
