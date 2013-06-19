package com.egor.hercule2000;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MDialog extends DialogFragment {

	/**
	 * Affiche le dialog de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_TEL = "DCSTT";
	
	/**
	 * Affiche le dialog de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_ACC = "DCSTA";
	
	/**
	 * Affiche le dialog d'erreur de connexion réseaux
	 */
	public static final String DIALOG_CONNEXION_SOCKET_ERREUR = "DCSTE";

	/**
	 * Affiche le dialog d'erreur de connexion réseaux
	 */
	public static final String DIALOG_WIFI_ACTIVER = "DWA";
	public static final String DIALOG_IP_INVALIDE = "DIPIN";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setCancelable(false);
		// Un objet qui construit des dialogues
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater factory = LayoutInflater.from(getActivity());
		
		/////////////////////////////////////////////////////////////////////////
		// Le dialog de connexion reseaux
		/////////////////////////////////////////////////////////////////////////
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_TEL) == 0) {
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

							((Telecommande) getActivity()).doPositiveClick(ip,port);

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
		
		
		
		/////////////////////////////////////////////////////////////////////////
		// Le dialog de connexion reseaux
		/////////////////////////////////////////////////////////////////////////
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ACC) == 0) {
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
		
		
		
		///////////////////////////////////////////////////////////////////////
		// Le Message d'erreur de connexion
		///////////////////////////////////////////////////////////////////////
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ERREUR) == 0) {
			builder.setTitle("Socket");
			builder.setMessage("Erreur de connexion");
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							startActivity(new Intent(getActivity(),
									Accueil.class));
						}
					});
		}
		
		////////////////////////////////////////////////////////////////////////
		// Le message pour activer le wifi
		////////////////////////////////////////////////////////////////////////
		if (getTag().compareTo(DIALOG_WIFI_ACTIVER) == 0) {
			// Le titre du dialog
			builder.setTitle(R.string.dialog_titre_wifi_desactiver);
			builder.setMessage(R.string.dialog_message_wifi_activer);
			// Evenemment sur le bouton OK
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							// On active le wifi
							((Accueil) getActivity()).startWifi();
						}
					});
			// Evenement sur le bouton Annuler
			builder.setNegativeButton(R.string.quitter,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// On quitte l'application
							System.exit(0);
						}
					});
		}
		
		/////////////////////////////////////////////////////////////////////////////
		// Le message l'ip invalide
		/////////////////////////////////////////////////////////////////////////////
		if (getTag().compareTo(DIALOG_IP_INVALIDE) == 0) {
			// Le titre du dialog
			builder.setTitle("Connexion Reseau");
			builder.setMessage("Adresse IP invalide");
			// Evenemment sur le bouton OK
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							//((MyActivity) getActivity()).afficherDialogue(MDialog.DIALOG_CONNEXION_SOCKET);
							startActivity(new Intent(getActivity(),	Accueil.class));
						}
					});
		}

		return builder.create();
	}
}
