package com.egor.hercule2000;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "ValidFragment" })
public class CommandeManuelle extends Activity {

	protected static final int CONNEXION_SOCKET = 0001;
	protected static final int SEEK_BAR_CHANGMENT = 0002;
	protected static final int AUTRES = 0003;

	private Client client;
	private String ip;
	private String port;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commande_manuelle);
		// Show the Up button in the action bar.
		setupActionBar();
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(AUTRES);
			}
		});
		showDialog();
	}

	@Override
	protected void onDestroy() {
		client.close();
		super.onDestroy();
	}
	
	// Le Handler (Thread) charger de modifier le IHM
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CONNEXION_SOCKET:
				String s = "Connexion en cours" + ip + ":" + port;
				Toast.makeText(CommandeManuelle.this, s, Toast.LENGTH_SHORT)
						.show();
				onCreateClient();
				break;
			case SEEK_BAR_CHANGMENT:

				break;
			case AUTRES:
				if (client.isActive()) {
					client.emission("Salut");
				}
				else {
					doNegativeClick();
				}
				break;
			}
		};
	};

	public void connexionHandlerMessage(String ip, int port) {
		this.handler.sendEmptyMessage(CONNEXION_SOCKET);
	}

	private void showDialog() {
		DialogFragment connexionDialog = new ConnexionDialog();
		connexionDialog.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
		handler.sendEmptyMessage(CONNEXION_SOCKET);
		Log.i("FragmentAlertDialog", "Positive click!");
	}

	public void doNegativeClick() {
		startActivity(new Intent(this, MainActivity.class));
		Log.i("FragmentAlertDialog", "Negative click!");
	}

	public void onCreateClient() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				client = new Client(ip, port);
			}
		});
		t.start();
	}

	public void test() {
		if (client.isActive()) {
			Toast.makeText(CommandeManuelle.this, "Connexion Reseaux OK",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(CommandeManuelle.this, "Erreur Connexion Reseaux",
					Toast.LENGTH_SHORT).show();
		}
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

	class ConnexionDialog extends DialogFragment {
		private View alertDialogView;
		private LayoutInflater inflater;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Instanciation de Builder pour la construction du dialogue
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			inflater = getActivity().getLayoutInflater();
			builder.setMessage(R.string.connexion_reseau)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Click sur le boutton OK
									alertDialogView = inflater.inflate(R.layout.connexion_dialog, null);
									EditText ip_dialog = (EditText) alertDialogView
											.findViewById(R.id.edt_ip_connexion_dialog);
									EditText port_dialog = (EditText) alertDialogView
											.findViewById(R.id.edt_port_connexion_dialog);
									ip = ip_dialog.getText().toString();
									port = port_dialog.getText().toString();
									CommandeManuelle.this.doPositiveClick();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
									CommandeManuelle.this.doNegativeClick();
								}
							});
			builder.setView(inflater.inflate(R.layout.connexion_dialog, null));
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}
}
