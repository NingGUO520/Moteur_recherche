package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import automate.Automate;

import java.util.Map.Entry;

public class Radix {

	public class RadixTree{

		String chars;
		ArrayList<RadixTree> fils ;
		List<Coordonnees> valeur;

		public RadixTree() {
			valeur = new ArrayList<Coordonnees>();
			chars = null;
			fils = new ArrayList<RadixTree>();
		}

		public RadixTree(String lettres) {
			valeur = new ArrayList<Coordonnees>();
			chars = lettres;
			fils = new ArrayList<RadixTree>();
		}
		public ArrayList<RadixTree> getSousArbre(){
			return fils;
		}
		public String getKeys() {
			return chars;
		}

		public boolean est_feuille() {
			return fils.isEmpty();

		}
	}

	public class Pair{
		int ligne;
		int col;
		public Pair(int l,int c) {
			ligne=l;
			col = c;
		}

	}

	public class Coordonnees{
		public int id;
		public ArrayList<Pair> coords;

		public Coordonnees(int id, int l, int c) {
			this.id = id;
			coords= new ArrayList<Pair>();
			Pair pair = new Pair(l,c);
			coords.add(pair);

		}
		public void addCoord(int l, int c) {
			Pair pair = new Pair(l,c);
			coords.add(pair);
		}
		public String toString() {
			String r = "";
			r += id;
			for(Pair p : coords) {
				r+=" ("+ p.ligne+","+p.col+") ";
			}
			return r;
		}
	}

	/**
	 * tester si pre est un prefix de mot
	 * @param mot
	 * @param pre
	 * @return
	 */
	public boolean estPrefix(String mot,String pre) {
		if(pre.length()>mot.length()) {
			return false;
		}
		for(int i = 0; i<pre.length();i++) {
			if(pre.charAt(i)!=mot.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Chercher le prefix commun entre mot et pre
	 * @param mot
	 * @param pre
	 * @return
	 */
	public String prefixCommun(String mot,String pre) {
		String result = "";
		int size = pre.length();
		if(mot.length()<size) {
			size = mot.length();
		}
		int i=0;
		while(i<size && mot.charAt(i)==pre.charAt(i) ) {
			result+=mot.charAt(i);
			i++;
		}
//		System.out.println("prefixCommun de "+ mot + "et  "+ pre+ " = "+ result);
		return result;
	}
	
	/**
	 * Inserer le mot mot et sa valeur dans l'arbre
	 * @param arbre : arbre a inserer
	 * @param mot : le mot a inserer
	 * @param v
	 */
	public void insertion(RadixTree arbre, String mot,Coordonnees v) {
		if(mot.isEmpty()) {
			arbre.valeur.add(v);
		}else{
			if(arbre.est_feuille()) {
				RadixTree nouvelle = new RadixTree(mot);
				nouvelle.valeur.add(v);
				arbre.fils.add(nouvelle);

			}else {
				boolean prefixCommun = false;
				for(int i = 0; i< arbre.fils.size();i++) {
					RadixTree fil = arbre.fils.get(i);
					if(estPrefix(mot,fil.chars)){
						insertion(fil,mot.substring(fil.chars.length()),v);
						prefixCommun = true;
					}else if(!prefixCommun(mot,fil.chars).isEmpty()) {
						String commun = prefixCommun(mot,fil.chars);
						RadixTree nouvelle = new RadixTree(commun);
						arbre.fils.add(nouvelle);
						fil.chars = fil.chars.substring(commun.length());
						nouvelle.fils.add(fil);
						arbre.fils.remove(fil);

						insertion(nouvelle,mot.substring(commun.length()),v);
						prefixCommun = true;

					}
				}
				if(!prefixCommun) {
					RadixTree nouvelle = new RadixTree(mot);
					nouvelle.valeur.add(v);
					arbre.fils.add(nouvelle);
				}
			}
		}
	}

	public List<Coordonnees> rechercher(RadixTree arbre, String motif) {
		if( motif.isEmpty()) {
			return arbre.valeur;
		}else if(arbre.est_feuille()) {
			return null;
		}else {
			for(RadixTree fil:arbre.fils) {
				if(estPrefix(motif,fil.chars)){
					return rechercher(fil,motif.substring(fil.chars.length()));
				}
			}

		}
		return null;
	}
	

	public RadixTree construireTree(List<Map<String,Coordonnees>> mapIndexList) {
		RadixTree tree = new RadixTree();
		for(Map<String,Coordonnees> mapIndex : mapIndexList) {
			for(Entry<String,Coordonnees> e :mapIndex.entrySet()) {
				String mot = e.getKey();
				Coordonnees v = e.getValue();
				insertion(tree,mot,v);
			}
		}
		return tree;
	}

	public HashMap<String,Coordonnees> lireTexte(int id,String contenu) {

		HashMap<String,Coordonnees> mapIndex = new HashMap<String,Coordonnees>();
		ArrayList<String> lignes = new ArrayList<String>();
		String ligne;
		Scanner scanner = new Scanner(contenu);

		while (scanner.hasNextLine()) {
			String l = scanner.nextLine();
			lignes.add(l);
		}
		for(int i = 0 ;i< lignes.size();i++) {
			String s = lignes.get(i);
			int j = 0;
			String mot ="";
			int debut = 1;
			while(j<s.length()) {
				char c = s.charAt(j);
				int n = (int)c;
				if((n>=97 && n<=122) || (n>=224 && n<=252)
						|| (n>=65 && n<=90 ) || n == 39 || n == 45) {
					mot+=c;
					if(j == s.length()-1) {
						if(mapIndex.containsKey(mot)) {
							mapIndex.get(mot).addCoord(i+1, debut);
						}else {
							Coordonnees coord = new Coordonnees(id,i+1,debut);
							mapIndex.put(mot,coord);
						}
						mot ="";
					}
				}else {
					if(mot.length()>0 ) {
						if(mapIndex.containsKey(mot)) {
							mapIndex.get(mot).addCoord(i+1, debut);
						}else {
							Coordonnees coord = new Coordonnees(id,i+1,debut);
							mapIndex.put(mot,coord );
						}
						mot ="";
						debut = j+2;
					}
				}
				j++;
			}
		}
		return mapIndex;
	}
	
	

//	public  RadixTree genererRadixTree(int id,String nomFichier) {
//		System.out.println("  >> generer le tableau de cache ... ");
////		HashMap<String,Coordonnees> mapIndex = lireTexte(nomFichier);
//		System.out.println("  >> Generation du tableau a reussi ! ");
//		
//		System.out.println("  >> Construire le Radix Tree ... ");
////		RadixTree tree = construireTree(id, mapIndex);
//		System.out.println("  >> Construction de Radix Tree a reussi ! ");
////		return tree;
//	}
	
//	public void chercherMotif(RadixTree tree,String motif) {
//		List<Coordonnees> result = rechercher(tree,motif );
//		if(result!=null) {
//			System.out.println("les positions du mot sont : ");
//			System.out.println(result.toString());
//		}else {
//			System.out.println(" >>>> Ce mot n'existe pas dans ce texte");
//		}
//	}
	
//	public static void main(String[] args) {
//		Radix t = new Radix();
//		Scanner scanner = null;
//		String motif = null;
//		String nomFichier = null ;
//		if (args.length!=0) {
//			if(args.length==2) {
//				motif = args[0];
//				nomFichier = args[1];
//			}else {
//				System.out.print("  >> Please enter two parameters: ");
//			}
//		} else {
//			scanner = new Scanner(System.in);
//			System.out.print("  >> Please enter file name: ");
//			nomFichier = scanner.next();
//		}
//		System.out.println("  >> Search word \""+motif+"\".");
//		System.out.println("  >> generer le tableau de cache ... ");
//		HashMap<String,Coordonnees> mapIndex = t.lireTexte(nomFichier);
//		System.out.println("  >> Generation du tableau a reussi ! ");
//		
//		/*debug*/
//		for(Entry<String,Coordonnees> e :mapIndex.entrySet()) {
//			System.out.println(e.getKey()+e.getValue().toString());
//		}
//
//		System.out.println("  >> Construire le Radix Tree ... ");
//		RadixTree tree = t.construireTree(mapIndex);
//		System.out.println("  >> Construction de Radix Tree a reussi ! ");
//		
//		//------------------------------------------------------------------------------
//		System.out.print("  >> Please enter a word: ");
//		motif = scanner.next();
//				if(t.rechercher(tree,motif )){
//					System.out.print(mapIndex.get(motif).toString());
//				}else {
//					System.out.println(" >>>> Ce mot n'existe pas dans ce texte");
		//		}
//		if(t.rechercher(tree,motif )!=null) {
//			System.out.println(t.rechercher(tree,motif ).toString());
//		}else {
//			System.out.println(" >>>> Ce mot n'existe pas dans ce texte");
//		}
	}