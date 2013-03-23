package com.egor.robot;

public class Pince {
	private boolean etat;
	
	public String serrer(int couple, int duree){
		String requete = new String();
		
		if(Math.abs(couple) > 511){
			couple = 511;
		}
		if(Math.abs(duree) > 30) {
			duree = 30;
		}
		
		requete = "P+" + Math.abs(couple) + ":" + Math.abs(duree) ;
		return requete;
	}
	
	public String relacher(int couple, int duree) {
		String requete = new String();
		
		
		if(Math.abs(couple) > 511){
			couple = 511;
		}
		if(Math.abs(duree) > 30) {
			duree = 30;
		}
		if(couple == 0){
			requete = "P" + Math.abs(couple) + ":" + Math.abs(duree) ;
		}
		else{
			requete = "P-" + Math.abs(couple) + ":" + Math.abs(duree) ;
		}
		
		return requete;
	}
	

	public boolean isEtat() {
		return etat;
	}

	public void setEtat(boolean etat) {
		this.etat = etat;
	}
	
}
