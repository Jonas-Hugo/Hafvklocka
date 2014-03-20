package com.hugo.hafvklocka;

import java.util.ArrayList;

public class Settings {
	
	public static int MIC_SENSITIVITY = 30000;
	public static int MAX_NUMBER_OF_SERIEROUNDS = 24;

	public Settings(){
		MIC_SENSITIVITY = 30000;
		MAX_NUMBER_OF_SERIEROUNDS = 24;
	}
	public Settings(ArrayList<String> attr){
		for(String s : attr){
			String key = s.split("=")[0];
			String value = s.split("=")[1];
			
			if(key.equals("MIC_SENSITIVITY")){
				MIC_SENSITIVITY = Integer.parseInt(value);
			}else if(value.equals("MAX_NUMBER_OF_SERIEROUNDS")){
				MAX_NUMBER_OF_SERIEROUNDS = Integer.parseInt(value);
			}
		}
	}
	
	public static String getOutput(){
		String output = "MIC_SENITIVITY=" + MIC_SENSITIVITY + "\n" + 
						"MAX_NUMBER_OF_SERIEROUNDS=" + MAX_NUMBER_OF_SERIEROUNDS; 
		return output;
	}
	
	
}
