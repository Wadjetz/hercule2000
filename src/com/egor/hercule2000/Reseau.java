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
	 * Le flux d'envoi des requ�tes
	 */
	private PrintWriter emetteur = null;

	/**
	 * Le flux de r�ception des donn�es
	 */
	@SuppressWarnings("unused")
	private BufferedReader recepteur = null;

	/**
	 * Adresse IP du PC Contr�leur
	 */
	private String ip;

	/**
	 * Num�ro de port du PC Contr�leur
	 */
	private int port;
	
	/**
	 * Indique si le socket est connect�
	 */
	boolean connecter = false;
	
	/**
	 * Thread de connexion r�seaux
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
	 * Connexion r�seau, instanciation du socket dans un thread
	 * @param ip Adresse IP du destinataire
	 * @param port Num�ro de port du serveur
	 */
	public void connexion(String ip, int port) {
		this.ip = ip;
		this.port = port;
		threadConnexionReseaux.start();
	}
	
	
	/**
	 * Envoie les requ�tes vers le r�seau
	 * @param msg La requ�te a envoy�
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
	 * Indique si le socket est connect�
	 * @return true si le socket est connect�, false s'il y a un probl�me
	 */
	public boolean isConnecter() {
		return connecter;
	}
	
	/**
	 * Ferme le socket s'il a �tait cr��
	 */
	public void close() {
		Log.d(LOG_TAG, "reseau close");
		if (socket != null) {
			// On ferme le Client socket � la fermeture de l'application
			try {
				Log.d(LOG_TAG, "reseau close : Socket NOT NULL");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
