package com.egor.hercule2000;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MDialog extends DialogFragment {
	/**
	 * Affiche le dialog de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_TELECOMMANDE = "DCST";
	/**
	 * Affiche le dialog d'erreur de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_TELECOMMANDE_ERREUR = "DCSTE";

	/**
	 * Affiche le dialog de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_ACCELEROMETRE = "DCSA";
	/**
	 * Affiche le dialog d'erreur de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_ACCELEROMETRE_ERREUR = "DCSAE";
	
	/**
	 * Affiche le dialog d'erreur de connexion réseaux
	 */
	public static final String DIALOG_WIFI_ACTIVER = "DWA";
	
	public static final int DIALOG_ACTIVITY_TELECOMANDE = 126454;
	public static final int DIALOG_ACTIVITY_ACCELEROMETRE = 1264;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater factory = LayoutInflater.from(getActivity());

		// Le dialog de connexion reseaux
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_TELECOMMANDE) == 0) {
			// On recupere l'IHM du dialog
			final View alertDialogView = factory.inflate(
					R.layout.connexion_dialog, null);
			// On associe l'IHM a notre dialog
			builder.setView(alertDialogView);
			// Le titre du dialog
			builder.setTitle(R.string.connexion_reseau);
			// Evenemment sur le bouton OK
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK

							EditText ip_dialog = (EditText) alertDialogView
									.findViewById(R.id.edt_ip_connexion_dialog);
							EditText port_dialog = (EditText) alertDialogView
									.findViewById(R.id.edt_port_connexion_dialog);
							String ip = ip_dialog.getText().toString();
							int port = Integer.parseInt(port_dialog.getText()
									.toString());
							((Telecommande) getActivity()).doPositiveClick(ip,
									port);
						}
					});
			// Evenement sur le bouton Annuler
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							((Telecommande) getActivity()).doNegativeClick();
						}
					});
		}

		// Le Message d'erreur de connexion
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_TELECOMMANDE_ERREUR) == 0) {
			builder.setTitle("Telecommande");
			builder.setMessage("Erreur de connexion");
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							((Telecommande) getActivity()).showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_TELECOMMANDE);
						}
					});
		}

		// Le dialog de connexion reseaux
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ACCELEROMETRE) == 0) {
			// On recupere l'IHM du dialog
			final View alertDialogView = factory.inflate(
					R.layout.connexion_dialog, null);
			// On associe l'IHM a notre dialog
			builder.setView(alertDialogView);
			// Le titre du dialog
			builder.setTitle(R.string.connexion_reseau);
			// Evenemment sur le bouton OK
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK

							EditText ip_dialog = (EditText) alertDialogView
									.findViewById(R.id.edt_ip_connexion_dialog);
							EditText port_dialog = (EditText) alertDialogView
									.findViewById(R.id.edt_port_connexion_dialog);
							String ip = ip_dialog.getText().toString();
							int port = Integer.parseInt(port_dialog.getText()
									.toString());
							((Accelerometre) getActivity()).doPositiveClick(ip,
									port);
						}
					});
			// Evenement sur le bouton Annuler
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							((Accelerometre) getActivity()).doNegativeClick();
						}
					});
		}

		// Le Message d'erreur de connexion
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ACCELEROMETRE_ERREUR) == 0) {
			builder.setTitle("Accelerometre");
			builder.setMessage("Erreur de connexion");
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							((Accelerometre) getActivity()).showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_ACCELEROMETRE);
						}
					});
		}
		
		if (getTag().compareTo(DIALOG_WIFI_ACTIVER) == 0) {
			// Le titre du dialog
						builder.setTitle(R.string.dialog_titre_wifi_desactiver);
						builder.setMessage(R.string.dialog_message_wifi_activer);
						// Evenemment sur le bouton OK
						builder.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// Click sur le boutton OK

										
										((Accueil) getActivity()).startWifi();
									}
								});
						// Evenement sur le bouton Annuler
						builder.setNegativeButton(R.string.quitter,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// User cancelled the dialog
										System.exit(0);
									}
								});
		}
		
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
