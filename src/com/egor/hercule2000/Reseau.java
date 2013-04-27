package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Reseau {
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
	
	/**
	 * Indique si le socket est connecté
	 */
	boolean connecter = false;
	
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
					emission("M:1234");
					connecter = true;
				} else {
					Log.d(LOG_TAG, "socket NULL");
					connecter = false;
				}
			} catch (UnknownHostException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
			} catch (IOException e) {
				Log.d(LOG_TAG, "Socket Erreur : " + e.getMessage());
			}
		}
	});
	
	
	/**
	 * Connexion réseau, instanciation du socket dans un thread
	 * @param ip Adresse IP du destinataire
	 * @param port Numéro de port du serveur
	 */
	public void connexion(String ip, int port) {
		this.ip = ip;
		this.port = port;
		threadConnexionReseaux.start();
	}
	
	
	/**
	 * Envoie les requêtes vers le réseau
	 * @param msg La requête a envoyé
	 */
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
	/**
	 * Indique si le socket est connecté
	 * @return true si le socket est connecté, false s'il y a un problème
	 */
	public boolean isConnecter() {
		return connecter;
	}
	
	/**
	 * Ferme le socket s'il a était créé
	 */
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
