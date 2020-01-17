package index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import kmp.KMP;

public class IndexingFile {
	


	protected Map<String,ArrayList<Coord>> unsorted; //unsorted index with each words and its occurences
	protected Map<String, ArrayList<Coord>> index; //sorted index 
	protected KMP kmp; //for getting the occurencces of each word

	
	public IndexingFile(String s) {
		kmp = new KMP();
		unsorted = new TreeMap<String,ArrayList<Coord>>();
		Scanner scanner = new Scanner(s);
		  
		    int line_number = 1;

		    while (scanner.hasNextLine()) {
		    	  String l = scanner.nextLine();
		    	if(l!= "") {
			    	String [] words = l.split("[^a-zA-Z'-]");
				   
			    	for(int i = 0; i<words.length; i++) {
			    		if(words[i].length() != 0 && words[i].length() >2) {
			    			/*
			    			 * apply kmp : facteur, retenue, texte
			    			 */
			    				int cmp = 0;
				    			char [] texte;
				    			char [] facteur = words[i].toCharArray();
				    			int [] retenue = kmp.getRetenue(facteur);
				    			if(cmp == 0) texte = l.toCharArray();
				    			else {
				    				texte = l.substring(cmp).toCharArray();
				    			}
				    			int occ = kmp.matchingAlgo(facteur, retenue, texte);
				    			
				    			//first encounter
				    			if(occ != -1 && !unsorted.containsKey(words[i])) {
				    				ArrayList <Coord> list = new ArrayList<Coord>();
				    				list.add(new Coord(line_number, occ+1+cmp));
				    				unsorted.put(words[i], list);
				    			}
				    			else if(occ != -1) {
				    				unsorted.get(words[i]).add(new Coord(line_number, occ+1+cmp));
				    			}
				    			cmp += occ;
			    		}
			    	}
		    	}
		    	line_number ++;
		    }
		
		
		for(Entry<String, ArrayList<Coord>> ent1 : unsorted.entrySet()) {
			int t = ent1.getValue().size();
			String s1 = ent1.getKey();
			for(Entry<String, ArrayList<Coord>> ent2 : unsorted.entrySet()) {
				String s2 = ent2.getKey();
				if(!s1.equals(s2) && s2.contains(s1)) {
						ent1.getValue().addAll(ent2.getValue());
				}
			}
			int x = ent1.getValue().size();
			if(t != x) {
				ent1.getValue().sort(Comparator.comparing(Coord::getLine));
			}
		}
		
//		index = sortByValue(unsorted);
		index = unsorted;
}

	
	// function to sort hashmap by values 
    public Map<String, ArrayList<Coord>> sortByValue(Map<String, ArrayList<Coord>> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, ArrayList<Coord>>> list = new LinkedList<Map.Entry<String, ArrayList<Coord>> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<Coord>>>() { 
            public int compare(Map.Entry<String, ArrayList<Coord>> o1,  
                               Map.Entry<String, ArrayList<Coord>> o2) 
            { 
            	Integer s1 =Integer.valueOf(o1.getValue().size());
            	Integer s2 =Integer.valueOf(o2.getValue().size());
            	return s1.compareTo(s2); 
            }
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String,  ArrayList<Coord>> temp = new LinkedHashMap<String,  ArrayList<Coord>>(); 
        for (Map.Entry<String,  ArrayList<Coord>> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    }

	public Map<String, ArrayList<Coord>> getIndex() {
		return index;
	} 
    
    
    
    

	
	
}
