package aho_ullman;

public class Automata {
	
	static final int EPSILON = 0xE15104;
	static final int VIDE = 0x41DE11;
	static final int CSTE = 256;
	
	protected int [][] trans;
	protected boolean [] init;
	protected boolean [] accept;
	protected boolean [][] eps;
	protected int nb_state;
	
	//init the automata
	public Automata(RegExTree t) {
		//count the number of state
		nb_state = getStateNumber(t);
		//initialize the transitions and epsilons-trans
		trans = new int [nb_state][CSTE];
		eps = new boolean[nb_state][nb_state];
		init = new boolean[nb_state];
		accept = new boolean[nb_state];
		
		for(int i = 0 ; i<trans.length;i++) {
			for (int j = 0; j < trans[i].length; j++) {
				trans[i][j] =-1;
			}
		}
		
	}
	
	public Automata() {
		
	}
	
	public int getStateNumber(RegExTree t) {
		if(t.subTrees.isEmpty()) return 2;
		else {
			/*
			 * we are supposing that each ERE character is either unary or binary. The root
			 * has at most 2 subtrees
			 */
			if(t.root == RegEx.ETOILE) return 2+ getStateNumber(t.subTrees.get(0));
			else if (t.root == RegEx.CONCAT) return  getStateNumber(t.subTrees.get(0)) + getStateNumber(t.subTrees.get(1));
			else if (t.root == RegEx.DOT) return getStateNumber(t.subTrees.get(0)) + getStateNumber(t.subTrees.get(1))+1;
			else return 2 + getStateNumber(t.subTrees.get(0)) + getStateNumber(t.subTrees.get(1));
		}
	}
	
	//construct a automata from a regextree
	public Automata automata_complete(RegExTree t) {
		switch(t.root) {
			case RegEx.CONCAT : return concat(t);
			case RegEx.ALTERN : return altern(t); 
			case RegEx.ETOILE : return etoile(t); 
			case RegEx.DOT : return dot(t);
			default : return basis(t);
		}
	}
	
	


	//2 state automata
	public Automata basis(RegExTree t) {
		if(!t.subTrees.isEmpty()) {
			try {
				throw new Exception("this is not a terminal case");
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}
		
		Automata a = new Automata(t);
		//for epsilon transition
		if(t.root == Automata.EPSILON) {
			a.setEpsilon(0, 1);
			a.init[0] = true;
			a.accept[1] = true;
		}
		//for x character
		else if(t.root ==  Automata.VIDE){
			a.init[0] = true;
			a.accept[1] = true;
		}
		
		//no language
		else {
			a.trans[0][t.root] = 1;
			a.init[0] = true;
			a.accept[1] = true;
		}
		return a;
	}
	
	//processing the concatenation of two subtrees
	public Automata concat(RegExTree t) {
		RegExTree t1 = t.subTrees.get(0);
		RegExTree t2 = t.subTrees.get(1);
		
		Automata a1 = automata_complete(t1);
		Automata a2 = automata_complete(t2);
		Automata a3 = new Automata(t);
		
		//copy the transitions of the first subtree
		for(int i = 0 ; i<a1.trans.length; i++) {
			for(int j = 0; j<Automata.CSTE;j++) {
				if(a1.trans[i][j] !=-1)a3.trans[i][j] = i+1;
			}
		}
	
		//second one
		for(int i = a3.trans.length-a2.trans.length ; i< a3.trans.length; i++) {
			for(int j = 0; j<Automata.CSTE;j++) {
				if(a2.trans[i-a1.trans.length][j] != -1)a3.trans[i][j] = i+1;
			}
		}	
		
		//set init
		a3.init[0] = true;
		//set accept
		a3.accept[a3.trans.length-1] = true;
		
		
	
		//but don't forget to get the former epsilon transition from the subtrees
		for(int i = 0 ; i<a1.eps.length; i++) {
			for(int j = 0; j<a1.eps.length;j++) {
				a3.eps[i][j] = a1.eps[i][j];
			}
		}
		
		for(int i = 0 ; i<a2.eps.length; i++) {
			for(int j = 0; j<a2.eps.length;j++) {
				a3.eps[i+a1.eps.length][j+a1.eps.length] = a2.eps[i][j];
			}
		}
		
		//set epsilon transition
		a3.setEpsilon(a1.trans.length-1, a1.trans.length);
		
		
		return a3;
	}
	
	///processing the alternative of two subtrees
	private Automata altern(RegExTree t) {
			RegExTree t1 = t.subTrees.get(0);
			RegExTree t2 = t.subTrees.get(1);
			
			Automata a1 = automata_complete(t1);
			Automata a2 = automata_complete(t2);
			Automata a3 = new Automata(t);
		
		//copy the transitions of the first subtree
			for(int i = 1 ; i<a1.trans.length+1; i++) {
				for(int j = 0; j<Automata.CSTE;j++) {
					if(a1.trans[i-1][j] != -1) a3.trans[i][j] = i+1;
				}
			}
			
		//second one
			for(int i = a1.nb_state+1 ; i< a3.nb_state-1; i++) {
				for(int j = 0; j<Automata.CSTE;j++) {
					if(a2.trans[i-a1.trans.length-1][j] != -1)a3.trans[i][j] = i+1;
				}
			}
				
		//set init
			a3.init[0] = true;
		//set accept
			a3.accept[a3.trans.length-1] = true;

		
		//but don't forget to get the former epsilon transition from the subtrees
			for(int i = 1 ; i<a1.eps.length+1; i++) {
				for(int j = 1; j<a1.eps.length+1;j++) {
					a3.eps[i][j] = a1.eps[i-1][j-1];
				}
			}
			
			for(int i =  0; i<a2.eps.length; i++) {
				for(int j = 0; j<a2.eps.length;j++) {
					a3.eps[i+a1.eps.length+1][j+a1.eps.length+1] = a2.eps[i][j];
				}
			}
			
			
			//set epsilon transitions
				a3.setEpsilon(0,1);
				a3.setEpsilon(0, a1.trans.length+1);
				a3.setEpsilon(a1.trans.length, a3.nb_state-1);
				a3.setEpsilon(a3.nb_state-2, a3.nb_state-1);
				
				
		return a3;
	}
	
	private Automata etoile(RegExTree t) {
		RegExTree t1 = t.subTrees.get(0);
		
		Automata a1 = automata_complete(t1);
		Automata a3 = new Automata(t);
		
		//copy transitions of automata
		for(int i = 1 ; i<a1.trans.length+1; i++) {
			for(int j = 0; j<Automata.CSTE;j++) {
				if(a1.trans[i-1][j] !=-1)a3.trans[i][j] = i+1;
			}
		}
		
		a3.init[0] = true;
		a3.accept[a3.nb_state-1] = true;
		
	
		
		
		//to do : copy the E-transitions
		for(int i = 1 ; i<a1.eps.length+1; i++) {
			for(int j = 1; j<a1.eps.length+1;j++) {
				a3.eps[i][j] = a1.eps[i-1][j-1];
			}
		}
		
		//epsilon transition
		a3.setEpsilon(0,a3.nb_state-1);
		a3.setEpsilon(0, 1);
		a3.setEpsilon(a3.nb_state-2, a3.nb_state-1);
		a3.setEpsilon(a3.nb_state-2, 1);

		return a3;
	}


	private Automata dot(RegExTree t) {
		Automata a = new Automata(t);
		//link to all existing characters
		for(int i = 0;i<a.CSTE;i++) {
			a.trans[0][i]= 1;
		}
		return a;
	}

	
	private void setEpsilon(int i,int j) {
		eps[i][j] = true;
	}
	
	public int getInitState() {
		return 0;
	}
	
	public int getFinalState() {
		return accept.length-1;
	}
	
	public String toString() {
		String s = "";
		
		s+= "\n  >> Automata unminimized : \n\n";
		for(int i=0; i< trans.length;i++) {
			for(int j = 0; j<trans[i].length;j++) {
				if(trans[i][j] != -1) s += i+" "+ trans[i][j]+" "+(char) j+"\n";
			}
		}
		
		s+= "\n  >> The epsilon-transitions are : \n";
		for(int i = 0; i<eps.length;i++) {
			for(int j = 0 ;j<eps.length;j++) {
				if(eps[i][j] == true) {
					s+= i+"-"+j+"\n";
				}
			}
		}
		
		for(int i= 0; i<init.length;i++) {
			if(init[i] == true)	s+= "\n  >>  The only starting state is "+i+" \n\n";
		}
		
		for(int i= 0; i<accept.length;i++) {
			if(accept[i] == true) s+= "  >> The only accepting state is "+i+" \n";
		}
		
	
		
	
		return s;
	}
}
