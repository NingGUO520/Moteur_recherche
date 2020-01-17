package aho_ullman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DFA {

	protected Automata a;	
	protected boolean [][] reachability;
	protected ArrayList<Integer> init;
	protected ArrayList<Integer>accept;
	protected HashMap<Integer,ArrayList<Integer>> trans;
	protected int [][] n_trans;
	
	public class Tuple{
		Integer s,t;

		public Tuple(Integer x, Integer y) {
			this.s = x;
			this.t = y;
		}

		public Integer getS() {
			return s;
		}

		public Integer getT() {
			return t;
		}
	}
	
	public DFA (Automata a) {
		this.a = a;
		getDFA();
	}
	
	public void getDFA() {	
		
		reachability= getReachability();	
		
		System.out.println("\n >>  Reachability table : \n");
		for (int i = 0; i < reachability.length; i++) {
			for (int j = 0; j < reachability.length; j++) {
				if(reachability[i][j]) System.out.print(1);
				else System.out.print(0);
			}
			System.out.println();
		}
		HashMap<Integer,Integer> map = getCharacterEnteredState();
		int automata_final_state = a.getFinalState();
		int automata_init_state = a.getInitState();
		trans = new HashMap<Integer,ArrayList<Integer>>();
		
		for (int i = 0; i < reachability.length; i++) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			for (int j = 0; j < reachability.length; j++) {
				if(reachability[i][j] && map.containsKey(j)) {
					l.add(map.get(j));	
				}
			}
			if(!l.isEmpty())trans.put(i, l);
			if(i>0 && !map.containsValue(i)) {
				trans.remove(i);
			}
		}
		
		init = new ArrayList<Integer>();
		accept = new ArrayList<Integer>();

		
		n_trans = new int[a.trans.length][Automata.CSTE];
		for (int i = 0; i < n_trans.length; i++) {
			for (int j = 0; j < n_trans[i].length; j++) {
				n_trans[i][j] = -1;
			}
		}
		
//		System.out.println("\n >>  Transitions DFA : \n");
		for (Map.Entry<Integer, ArrayList<Integer>> entry : trans.entrySet()) {
			ArrayList<Integer> l = entry.getValue(); 
			for (Integer i : l) {
				ArrayList<Integer> characters = getCharacter(i);
				for (Integer ch : characters) {
					n_trans[entry.getKey()][ch.intValue()] = i;
//					System.out.println(entry.getKey() + "/"+ i+" : "+ (char)ch.intValue());	
				}
				   if(reachability[i][automata_final_state] && !accept.contains(i)) accept.add(i);
				   if(reachability[i][automata_init_state] && !init.contains(i)) init.add(i);
				   
			}
			   if(reachability[entry.getKey()][automata_init_state] && !init.contains(entry.getKey())) init.add(entry.getKey());
			   if(reachability[entry.getKey()][automata_final_state] && !accept.contains(entry.getKey())) accept.add(entry.getKey());
		}
		
		
		
		
	//	System.out.println("Mini");
		getMinimisation();
	}
	
	//Minimisation
	public void getMinimisation() {
		 
		 ArrayList<Integer> state = new ArrayList<Integer>();
		 for (Integer key : trans.keySet()) {
			    state.add(key);
			}
		 
		 ArrayList<Tuple> equivalent_state = new ArrayList<Tuple>();
		 //basis
		for (int i = 0; i < state.size(); i++) {
		  for (int j = i+1; j < state.size(); j++) {
			  Integer i1 = state.get(i);
			  Integer i2 = state.get(j);
			
			  
			  if((accept.contains(i1) && accept.contains(i2))){
				 equivalent_state.add(new Tuple(i1,i2)); 
			  }
			}
		 }
		
		for (int i = 0; i < state.size(); i++) {
			 Integer i1 = state.get(i);
			if(accept.contains(i1)) equivalent_state.add(new Tuple(i1,i1));
		}
		
		//Induction
		ArrayList <Tuple> to_remove = new ArrayList<Tuple>();
		//on vérifie pour tous les states équivalents en basis
		for(Tuple T : equivalent_state) {
			int s = T.getS(); //getKey
			int t = T.getT();
			
			ArrayList<Integer> s_list = trans.get(s);//getvalue
			ArrayList<Integer> t_list = trans.get(t);

			
			HashMap<Integer,ArrayList<Integer>> s_map = new HashMap<Integer,ArrayList<Integer>>(); // CHAR - état
			HashMap<Integer,ArrayList<Integer>> t_map = new HashMap<Integer,ArrayList<Integer>>(); // CHAR - état

			for(Integer i : s_list) {
					ArrayList<Integer> characters = getCharacter(i); 
					for (Integer ch : characters) {
						ArrayList<Integer> receiver = getNext(i,ch);
						s_map.put(ch, receiver);
					}
			}
			
			for(Integer i : t_list) {
				ArrayList<Integer> characters = getCharacter(i); 
				for (Integer ch : characters) {
					ArrayList<Integer> receiver = getNext(i,ch);
					t_map.put(ch, receiver);
				}
			}
			
			for (Map.Entry<Integer, ArrayList<Integer>> entry : s_map.entrySet()) {
				Integer character = entry.getKey();
				if(t_map.containsKey(character)) {
					/*
					 *lists of states accepting input "x" of both "basis" equivalent states
					 *if these states aren't equivalent then s and t aren't equivalent
					 */

					for (int i = 0; i < state.size(); i++) {
						  for (int j = i+1; j < state.size(); j++) {
							  Tuple comb1 = new Tuple(i,j);
							  Tuple comb2 = new Tuple(j,i);
							  
							  if(!equivalent_state.contains(comb1) && !equivalent_state.contains(comb2)) {
								  to_remove.add(new Tuple(s,t));
							  }
						  }
					}
				}
			}
		}
		
		//remove the states that are not equivalent
		equivalent_state.removeAll(to_remove);
		
		
		
		ArrayList<Integer> remove_state = new ArrayList<Integer>();
		//now we merge the equivalent states
		for (Tuple tuple : equivalent_state) {
			int s = tuple.getS();
			int t = tuple.getT();
			
			//as s<t
			if(s<t) {
				//copy the transitions of t to s
				
				ArrayList<Integer> s_trans = trans.get(s);
				ArrayList<Integer> t_trans = trans.get(t);
				
				for (Integer i : t_trans) {
					if(!s_trans.contains(i)) {
						s_trans.add(i); 
						for(Integer c : getCharacter(i)) {
							if(i == t) n_trans[s][c.intValue()] = i; 
							else n_trans[s][c.intValue()] = i; 
						}
					}
					if(i==t) {
						s_trans.add(s);
						for(Integer c : getCharacter(i)) {
							n_trans[s][c.intValue()] = s; 
						}
					}
				}
				remove_state.add(t);
			}
		}
		
		for(Integer i : remove_state) {
			for(int j = 0; j<n_trans[0].length;j++) {
				n_trans[i][j] = -1;
			}
			trans.remove(i);
			init.remove(i);
			accept.remove(i);
			
			for (Map.Entry<Integer, ArrayList<Integer>> entry : trans.entrySet()) {
				if(entry.getValue().contains(i)) {
					entry.getValue().remove(i);
				}
			}
		}
		
		System.out.println("\n >>  Initiating State : \n");
		for (Integer i : init) {
			System.out.println(i);
		}
		
		System.out.println("\n >>  Accepting State : \n");
		for (Integer i : accept) {
			System.out.println(i);
		}
		
		System.out.println("\nMinimized Automata : \n");
		for(int i = 0;i< n_trans.length;i++) {
			boolean d = false;
			for (int j = 0; j < n_trans[i].length; j++) {
				if(n_trans[i][j]  != -1) {
					d= true;
					System.out.println(i +" " +n_trans[i][j] + " "+  (char)j);
				}
			}
			if(d)	System.out.println();
		}

	}
		 
	
	
	//get the important states 
	public HashMap<Integer,Integer> getCharacterEnteredState(){
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0; i< a.trans.length;i++) {
			for(int j = 0; j<a.trans[i].length;j++) {
				if(a.trans[i][j] != -1) {
					map.put(i, a.trans[i][j]);
				}
			}
		}
		return map;
	}
	
	//get the characters which can get to the state j
	public ArrayList<Integer> getCharacter(int j) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i< a.trans.length;i++) {
			for(int x = 0; x<a.trans[i].length;x++) {
				if(a.trans[i][x] == j) {
					list.add(x);
				}
			}
		}
		return list;
	}
	
	//get all the states that comes from the state s through the value val
	public ArrayList<Integer> getNext(int s, int val){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i< a.trans.length;i++) {
				if(a.trans[i][val] != -1) {
					list.add(a.trans[i][val]);
				}
		}
		return list;
	}
	
	public boolean [][] getReachability(){
		//reachability through initial epsilon
		boolean [][] r = a.eps;

		//each state can reach itself 
		for(int i =0; i<r.length; i++) {
			r[i][i] = true;
		}
		
		HashMap<Integer,ArrayList<Integer>> map = new HashMap<Integer,ArrayList<Integer>>();
		for(int i =0;i<r.length;i++) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			for(int j = 0; j< r.length;j++) {
				if(a.eps[i][j] && i!=j) l.add(j);
			}
			map.put(i, l);
		}
		
		for(int i= r.length-1; i>=0;i--) {
			ArrayList<Integer> l = map.get(i);
			for (Integer i_list : l) {
				for(int j = 0;j<r.length;j++) {
					if(!r[i][j]) r[i][j] = r[i_list][j]; 
				}
			}
		}
		
		//second 
		for(int i= r.length-1; i>=0;i--) {
			ArrayList<Integer> l = map.get(i);
			for (Integer i_list : l) {
				for(int j = 0;j<r.length;j++) {
					if(!r[i][j]) r[i][j] = r[i_list][j]; 
				}
			}
		}
		
		return r;	
	}
	
	
}
