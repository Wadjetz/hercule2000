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
	public static final String DIALOG_CONNEXION_SOCKET = "DCS";
	public static final String DIALOG_CONNEXION_SOCKET_ERREUR = "DCSE";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater factory = LayoutInflater.from(getActivity());
		
		// Le dialog de connexion reseaux
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET) == 0) {
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
							((Telecommande) getActivity()).doPositiveClick(
									ip, port);
						}
					});
			// Evenement sur le bouton Annuler
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							((Telecommande) getActivity())
									.doNegativeClick();
						}
					});
		}
		
		// Le Message d'erreur de connexion
		if (getTag().compareTo(DIALOG_CONNEXION_SOCKET_ERREUR) == 0) {
			builder.setTitle("Erreur de Connexion");
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Click sur le boutton OK
							((Telecommande) getActivity())
									.doNegativeClick();
						}
					});
		}

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
