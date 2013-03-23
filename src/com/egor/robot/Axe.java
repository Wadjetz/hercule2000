package com.egor.robot;



public class Axe {
	private String articulation;
	
	private float position;
	private float posMax;
	private float posMin;
	
	
	public Axe(Articulation param){
		switch (param) {
			case BASE :
				articulation = "B"; 
				posMax = 160f;
				posMin = -160f;
				break;
				
			case EPAULE :
				articulation = "E";
				posMax = 91f;
				posMin = -115f;
				break;
			case COUDE :
				articulation = "C";
				posMax = 82f;
				posMin = -124f;
				break;
			case TANGAGE :
				articulation = "T";
				posMax = 90f;
				posMin = -90f;
				break;
			case ROULIS :
				articulation = "R";
				posMax = 160f;
				posMin = -160f;
				break;
		}
	}
	
	public String rotation(float degre, int vitesse){
		String requete = new String();
		//Limitation
		if(degre > posMax){
			degre = posMax;
		}
		if(degre < posMin){
			degre = posMin;
		}
		
		if(vitesse == 0){
			vitesse = 1;
		}
		if(Math.abs(vitesse) > 30){
			vitesse = 30;
		}
		
		
		if(degre > 0) {
			requete = articulation + "+" + (int)degre + ":" + Math.abs(vitesse);
		}
		else {
			requete = articulation  + (int)degre + ":" + Math.abs(vitesse);
		}
		return requete;
	}
	
	public float getPosMax() {
		return posMax;
	}

	public void setPosMax(float posMax) {
		this.posMax = posMax;
	}

	public float getPosMin() {
		return posMin;
	}

	public void setPosMin(float posMin) {
		this.posMin = posMin;
	}

	public void setPosition(float position){
		
		this.position = position;
	}
	
	public float getPosition(){
		return position;
	}
	
}
