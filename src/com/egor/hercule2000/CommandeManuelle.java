package com.egor.hercule2000;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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

	// Des constantes pour identifier les actions du Hundler
	protected static final int CONNEXION_SOCKET = 0001;
	protected static final int SEEK_BAR_CHANGMENT = 0002;
	protected static final int AUTRES = 0003;

	// Client socket pour communiquer en réseaux
	private Socket socket = null;
	private PrintWriter emetteur = null;
	// private BufferedReader recepteur = null;

	// Adresse IP du PC Contrôleur
	private String ip;
	// Numéro de port du PC Contrôleur
	private int port;
	//
	private DialogFragment connexionDialog = new MyDialog();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commande_manuelle);
		// Show the Up button in the action bar.
		setupActionBar();

		// Gestion du click sur le bouton test
		findViewById(R.id.test).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// On envoie un message au Hundler
				handler.sendEmptyMessage(AUTRES);
			}
		});

		// On affiche le dialog de connexion
		showDialog(MyDialog.DIALOG_CONNEXION_SOCKET);
	}

	@Override
	protected void onDestroy() {

		// On ferme le Client socket à la fermeture de l'application
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	// Le Handler (Thread) charger de modifier le IHM
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CONNEXION_SOCKET:
				Toast.makeText(CommandeManuelle.this,
						"Connexion en cours" + ip + ":" + port,
						Toast.LENGTH_SHORT).show();

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							Log.d("Egor", "Socket");
							socket = new Socket(ip, port);
							Log.d("Egor", "Socket Connecter");

							if (socket.isConnected()) {
								emetteur = new PrintWriter(socket
										.getOutputStream(), true);
								emission("Salut");
							}
						} catch (UnknownHostException e) {
							showDialog(MyDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
							Log.d("Egor", "Socket Erreur : " + e.getMessage());
							e.printStackTrace();
						} catch (IOException e) {
							showDialog(MyDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
							Log.d("Egor", "Socket Erreur : " + e.getMessage());
							e.printStackTrace();
						}
					}
				});
				t.start();
				break;
			case SEEK_BAR_CHANGMENT:

				break;
			case AUTRES:
				if (socket != null) {
					if (socket.isConnected()) {
						emission("Bonjour");
					} else {
						showDialog(MyDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
					}
				}
				else {
					showDialog(MyDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				}
				break;
			}
		};
	};

	public void doPositiveClick() {
		handler.sendEmptyMessage(CONNEXION_SOCKET);
		Log.i("Egor", "Positive click!");
	}

	public void emission(String msg) {
		emetteur.println(msg);
	}

	public void doNegativeClick() {
		startActivity(new Intent(this, MainActivity.class));
		Log.i("Egor", "Negative click!");
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

	class MyDialog extends DialogFragment {
		protected static final String DIALOG_CONNEXION_SOCKET = "DCS";
		protected static final String DIALOG_CONNEXION_SOCKET_ERREUR = "DCSE";

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater factory = LayoutInflater.from(CommandeManuelle.this);

			if (getTag().compareTo(DIALOG_CONNEXION_SOCKET) == 0) {
				final View alertDialogView = factory.inflate(
						R.layout.connexion_dialog, null);
				builder.setView(alertDialogView);
				builder.setTitle(R.string.connexion_reseau);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Click sur le boutton OK

								EditText ip_dialog = (EditText) alertDialogView
										.findViewById(R.id.edt_ip_connexion_dialog);
								EditText port_dialog = (EditText) alertDialogView
										.findViewById(R.id.edt_port_connexion_dialog);
								ip = ip_dialog.getText().toString();
								port = Integer.parseInt(port_dialog.getText()
										.toString());
								CommandeManuelle.this.doPositiveClick();
							}
						});
				builder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								CommandeManuelle.this.doNegativeClick();
							}
						});
			}

			if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ERREUR) == 0) {
				builder.setTitle("Erreur de Connexion");
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Click sur le boutton OK
								CommandeManuelle.this.doNegativeClick();
							}
						});
			}

			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

	public void showDialog(String tag) {
		connexionDialog.show(getFragmentManager(), tag);
	}

}
