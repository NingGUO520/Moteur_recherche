package kmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class KMP {


	
	public void egrep(String pattern,String filename) {
		char [] facteur = pattern.toCharArray();
		int [] retenue = getRetenue(facteur);
		File file = new File(filename); 
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String l;
		    while ((l = br.readLine()) != null) {
		    	char [] texte = l.toCharArray();
		    	if(matchingAlgo(facteur,retenue,texte) != -1) {
		    		System.out.println(l);
		    	}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int [] getRetenue(char [] facteur) {
		char first = facteur[0];
		int [] retenue = new int [facteur.length+1];
		ArrayList<String> prefix = new ArrayList<String>();
		for (int i = 0; i < facteur.length; i++) {
			if(facteur[i] == first) {
				retenue[i] = -1;
			}
			else {
				for (String pre : prefix) {
					int  len_pre = pre.length();
					String suffix ="";
					for(int k = i-len_pre;k<i;k++) {
						suffix += facteur[k];
					}
					String p = suffix +facteur[i];
					
					if(prefix.contains(p)) {
						retenue[i] = 0;
					}
					else if(prefix.contains(suffix)) {
						retenue[i] = len_pre;
					}
				}
				
			}
			String s ="";
			for (int j = 0; j < i; j++) {
				s+= facteur[j];
			}
			prefix.add(s);
		}
		return retenue;
	}
	
	
	 public int matchingAlgo (char[] facteur, int[] retenue, char[] text){
	        int i = 0,j = 0;
	        char [] texte = addPadding(text);
	        while(i<texte.length){
	            if(j==facteur.length){
	                return i-facteur.length;
	            }
	            if(texte[i] == facteur[j]){
	                i++;
	                j++;
	            }else{
	                if(retenue[j]==-1){
	                    i++;
	                    j=0;
	                }else{
	                    j=retenue[j];
	                }
	            }
	        }
	        return -1;
	}
	
	public void printRetenue(int [] retenue) {
		for (int i = 0; i < retenue.length; i++) {
			System.out.print(retenue[i]);
		}
	}
	
	public char[] addPadding(char[] texte) {
		char [] new_text = new char[texte.length+1];
		
		for (int i = 0; i < new_text.length-1; i++) {
			new_text[i] = texte[i];
		}
		new_text[new_text.length-1] = ' ';
		
		return new_text;
	}
}
