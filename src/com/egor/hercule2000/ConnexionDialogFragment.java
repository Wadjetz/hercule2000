package com.egor.hercule2000;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

public class ConnexionDialogFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Instanciation de Builder pour la construction du dialogue
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setMessage(R.string.connexion_reseau)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Click sur le boutton OK
								//((FragmentAlertDialog)getActivity()).doPositiveClick();
								//CommandeManuelle.this.doPositiveClick();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								startActivity(new Intent(getActivity(), MainActivity.class));
							}
						});
		builder.setView(inflater.inflate(R.layout.connexion_dialog, null));
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
