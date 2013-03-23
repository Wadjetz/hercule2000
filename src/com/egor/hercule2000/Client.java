package com.egor.hercule2000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Client {
	private Socket socket = null;
	private PrintWriter emetteur = null;
	private BufferedReader recepteur = null;
	private String message = "";
	boolean active = false;

	public boolean isActive() {
		return active;
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Client(String ip, String port) {
		Log.v("Egor", "Client Constructeur", null);
		try {
			socket = new Socket(ip, Integer.parseInt(port));
			if (socket.isConnected()) {
				active = true;
			} else {
				active = false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			active = false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			active = false;
		} catch (IOException e) {
			e.printStackTrace();
			active = false;
		}

	}

	public void emission(String msg) {
		message = msg;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (socket.isConnected()) {
						emetteur = new PrintWriter(socket.getOutputStream(),
								true);
						emetteur.println(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
					//active = false;
				}
			}
		});
		t.start();

	}
	public String reception() {
		String s = "";
		try {
			recepteur = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			s = recepteur.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

}
