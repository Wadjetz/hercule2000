package com.egor.hercule2000;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.egor.robot.Robot;

@SuppressLint("HandlerLeak")
public class CommandeManuelle extends Activity {

	/* ----------------------- ATTRIBUTS ------------------- */

	// Des constantes pour identifier les actions du Hundler
	protected static final int CONNEXION_SOCKET = 0001;
	protected static final int SEEK_BAR_CHANGMENT = 0002;
	protected static final int AUTRES = 0003;

	// Client socket pour communiquer en réseaux
	private Socket socket = null;
	private PrintWriter emetteur = null;
	// private BufferedReader recepteur = null;
	
	private TextView txv_vitesse = null;
	private SeekBar seekBar = null;
	// Adresse IP du PC Contrôleur
	private String ip;
	// Numéro de port du PC Contrôleur
	private int port;
	private int vitesse = 30;
	// Dialog
	private DialogFragment connexionDialog = new MDialog();
	// Robot
	private Robot robot = new Robot();
	
	SeekBar.OnSeekBarChangeListener seekBarEvent = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					handler.sendEmptyMessage(SEEK_BAR_CHANGMENT);
				}
			});
			t.start();
		}
	};
	
	/* ----------------------- METHODES ---------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commande_manuelle);
		// Show the Up button in the action bar.
		setupActionBar();
		
		seekBar = (SeekBar) findViewById(R.id.seekBarVitesse);
		txv_vitesse = (TextView) findViewById(R.id.txv_vitesse_commande_manuelle);
		seekBar.setOnSeekBarChangeListener(seekBarEvent);
		seekBar.setProgress(30);
		// On affiche le dialog de connexion
		showDialog(MDialog.DIALOG_CONNEXION_SOCKET);
	}

	public void rotationNegative(View view) {
		emission(robot
				.calculeRotation(view.getTag().toString().substring(0, 1), Robot.NEGATIVE, 100, vitesse));
		Log.d("Egor", "rotationNegative" + view.getTag());
	}

	public void rotationPositive(View view) {
		emission(robot
				.calculeRotation(view.getTag().toString().substring(0, 1), Robot.POSITIVE, 50, vitesse));
		Log.d("Egor", "rotationPositive" + view.getTag());
	}

	public void showDialog(String tag) {
		connexionDialog.show(getFragmentManager(), tag);
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
								emetteur = new PrintWriter(
										socket.getOutputStream(), true);
								emission("Salut");
							}
						} catch (UnknownHostException e) {
							showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
							Log.d("Egor", "Socket Erreur : " + e.getMessage());
							e.printStackTrace();
						} catch (IOException e) {
							showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
							Log.d("Egor", "Socket Erreur : " + e.getMessage());
							e.printStackTrace();
						}
					}
				});
				t.start();
				break;
			case SEEK_BAR_CHANGMENT:
				vitesse = seekBar.getProgress() + 1;
				txv_vitesse.setText("Vitesse : " + vitesse);
				break;
			case AUTRES:
				if (socket != null) {
					if (socket.isConnected()) {
						emission("Bonjour");
					} else {
						showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
					}
				} else {
					showDialog(MDialog.DIALOG_CONNEXION_SOCKET_ERREUR);
				}
				break;
			}
		};
	};

	public void doPositiveClick(String ip, int port) {
		this.ip = ip;
		this.port = port;
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

}
