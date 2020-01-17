package aho_ullman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Text {
	
	protected String s;
	protected DFA dfa;

	public Text(String s,DFA dfa) {
		this.s =s;
		this.dfa = dfa;
	}
	
	@SuppressWarnings("resource")
	public ArrayList<String> getAcceptedWord(){
		File file = new File(s);
		ArrayList<String> list = new ArrayList<String>();
		   try {
			Scanner input = new Scanner(file);
			while (input.hasNext()) {
			      String word  = input.next();
			      if((isAccepted(word) || isAcceptedWholeWord(word)) && !list.contains(word)) {
			    	  list.add(word);
			      }
			    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		   System.out.println(list);
		  return list;
	}
	
	public ArrayList<String> getLines() throws FileNotFoundException, IOException{
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> acceptedWords = getAcceptedWord();
		try (BufferedReader br = new BufferedReader(new FileReader(s))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	for (String w : acceptedWords) {
		    		  if(line.contains(w)) {
		    			  list.add(line);
		    			  break;
		    		  }
		    	}
		     
		    }
		}
		  return list;
	}
	
	//this method checks if the whole word is equal to the regEx
	public boolean isAcceptedWholeWord(String str) {

		 
		char [] c = str.toCharArray();
		int [][] t = dfa.n_trans;
		
		//check if first letter is in init state
		boolean init = false;
		for(int i = 0 ;i<t.length;i++) {
			for(int j = 0;j<Automata.CSTE;j++) {
				if(t[i][j] != -1 && dfa.init.contains(i) && j == c[i]) {			
						init = true;			
						break;
					}		
			}
		}
		
		
		//check if last letter is in accepting state
		boolean accept = false;
		for(int i = 0 ;i<t.length;i++) {
			for(int j = 0;j<Automata.CSTE;j++) {
				if(t[i][j] != -1 && dfa.accept.contains(t[i][j]) && j == c[c.length-1]) {
					accept = true;			
					break;
				}
			}
		}
		
		int cmp =0;
		int point = 0;
		while(cmp<c.length) {
			boolean exist = false;
			for(int j=0; j<Automata.CSTE;j++) {
				if(t[point][j] != -1) {
					boolean b = false;
					if(j == c[cmp]) {
						cmp++;
						point = t[point][j];
						exist = true;
						b = true;
						
					}
					if(b)break;
				}
			}
			if(!exist)  return false;
		}
		
		return init && accept;
	}
	
	//this method can only detect if the word contains the RegEx, but not if the word is the RegEx
	public boolean isAccepted(String str) {
		 
		char [] c = str.toCharArray();
		int [][] t = dfa.n_trans;
		
		
		boolean init = false;
		boolean accept =  false;
		int cmp = 0;
		int point = 0;
		while(cmp<c.length) {
			if(!init) {
				for(Integer i : dfa.init) {
					for(int j = 0;j<Automata.CSTE;j++) {
						if(t[i][j] != -1 && j == c[cmp]) {
							init = true;
							point = t[i][j];
							break;
						}
					}
				}
			}
			else if(init) {
				
				if(!accept) {
					if(dfa.accept.contains(point)) {
						return true;
					}
				}
				
				boolean exist = false;
				for(int j=0; j<Automata.CSTE;j++) {
					if(t[point][j] != -1) {
						boolean b = false;
						if(j == c[cmp]) {
							point = t[point][j];
							exist = true;
							b = true;
						}
						if(b)break;
					}
				}
				if(!exist)  return false;
				
			
			}
			
			cmp++;
			if(init && accept) break;
		}
		
		return init && accept;
	}
}
