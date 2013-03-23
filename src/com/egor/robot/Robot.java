package com.egor.robot;


public class Robot {
	
	public static final int POSITIVE = 1;
	public static final int NEGATIVE = -1;
	
	public Axe base = new Axe(Articulation.BASE);
	public Axe epaule = new Axe(Articulation.EPAULE);
	public Axe coude = new Axe(Articulation.COUDE);
	public Axe tangage = new Axe(Articulation.TANGAGE);
	public Axe roulis = new Axe(Articulation.ROULIS);
	public Pince pince = new Pince();
	
	public String calculeRotation(String art, int sense) {
		return art;
	}
	
	
	public String temporisationt(int delais) {
		String requete = new String();
		
		if(Math.abs(delais) > 240) {
			delais = 240;
		}
		if(delais == 0) {
			delais = 1;
		}
		requete = "D+" + delais;
		return requete ;
	}
	
	public String input( int inputMask ) {
		String requete = new String();
		
		if(inputMask > 255){
			inputMask = 255;
		}
		
		requete = "I" + Math.abs(inputMask) ;
		
		return requete;
	}
	
	public String outputs( int outputsMask){
String requete = new String();
		
		if(outputsMask > 255){
			outputsMask = 255;
		}
		
		requete = "O" + Math.abs(outputsMask) ;
		
		return requete;
	}
	
	
}
