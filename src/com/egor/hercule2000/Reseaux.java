package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Reseaux {
	/**
	 * Log Tag pour les messages de debuguages
	 */
	private static final String LOG_TAG = "CM_Egor";
	
	/**
	 * Socket pour envoyer les commandes
	 */
	private Socket socket = null;

	/**
	 * Le flux d'envoi des requêtes
	 */
	private PrintWriter emetteur = null;

	/**
	 * Le flux de réception des données
	 */
	@SuppressWarnings("unused")
	private BufferedReader recepteur = null;

	/**
	 * Adresse IP du PC Contrôleur
	 */
	private String ip;

	/**
	 * Numéro de port du PC Contrôleur
	 */
	private int port;
	Telecommande telecommande = null;
	Accelerometre accelerometre = null;
	Activity contexte = null;
	boolean ok = false;
	int activity;
	/**
	 * Thread de connexion réseaux
	 */
	private Thread threadConnexionReseaux = new Thread(new Runnable() {
		@Override
		public void run() {
			Log.d(LOG_TAG, "threadConnexionReseaux RUN");
			try {
				socket = new Socket(ip, port);
				if (socket != null) {
					Log.d(LOG_TAG, "socket NOT NULL");
					emetteur = new PrintWriter(socket.getOutputStream(), true);
					recepteur = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					emission("M");
					ok = true;
				} else {
					Log.d(LOG_TAG, "socket NULL");
					ok = false;
					error();
				}
			} catch (UnknownHostException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				error();
			} catch (IOException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
				error();
			}
		}

		private void error() {
			if(telecommande != null) {
				telecommande.showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_TELECOMMANDE_ERREUR);
			}
			if(accelerometre != null) {
				accelerometre.showDialoge(MDialog.DIALOG_CONNEXION_SOCKET_ACCELEROMETRE_ERREUR);
			}
		}
	});
	
	public void connexion(String ip, int port, Context context, int activity) {
		this.ip = ip;
		this.port = port;
		if(activity == MDialog.DIALOG_ACTIVITY_TELECOMANDE) {
			this.contexte = (Telecommande) context;
		}
		if(activity == MDialog.DIALOG_ACTIVITY_ACCELEROMETRE) {
			this.contexte = (Accelerometre) context;
		}
		threadConnexionReseaux.start();
	}

	public void emission(String msg) {
		Log.d(LOG_TAG, "emission : " + msg);
		if (socket != null) {
			if (socket.isConnected()) {
				emetteur.println(msg);
			} else {
				Log.d(LOG_TAG, "Emission Erreur Socket NOT CONNECTED");
			}
		} else {
			Log.d(LOG_TAG, "Emission Erreur Socket NULL");
		}
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public void close() {
		Log.d(LOG_TAG, "reseau close");
		if (socket != null) {
			// On ferme le Client socket à la fermeture de l'application
			try {
				Log.d(LOG_TAG, "reseau close : Socket NOT NULL");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
