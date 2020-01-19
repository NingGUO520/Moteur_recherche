package automate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Map.Entry;

public class Automate {
	HashSet<Integer> caracteres;
	static final int MAXLENGTH = 9000;
	int nombreEtatsEtape2;
	int nombreEtatsEtape3;
	int nombreEtatsEtape4;

	/* Un tableau qui stocke les transitions dans etape2 */
	int[][] tab_trans_etape2;
	/* tableau qui stocke les etats initiaux dans etape2 */
	int[] tab_init_etape2;
	/* tableau qui stocke les etats finaux dans etape2 */
	int[] tab_final_etape2;
	/* tableau qui stocke les ε-transitions dans etape2 */
	int[][] tab_epsilon_etape2;

	/* Un tableau qui stocke les transitions dans etape3 */
	int[][] tab_trans_etape3;
	/* Etat initial */
	int init_etape3;
	/* tableau qui stocke les etats finaux dans etape3 */
	int[] tab_final_etape3;

	/* les tableaux d'etape4 */
	int[][] tab_trans_etape4;
	int[] tab_final_etape4;
	/* Etat initial */
	int init_etape4;

	public Automate(RegExTree tree) {

		caracteres = new HashSet<Integer>();
		nombreEtatsEtape2 = getNbEtatsWithEpsilon(tree);
		// System.out.println("nombre d'etats total dans un automate avec ε-transitions
		// "+nombreEtatsEtape2);
		// System.out.println("nombre de caractere "+caracteres.size());

		tab_trans_etape2 = new int[nombreEtatsEtape2][MAXLENGTH];
		// Initialiser le tableau de transition a -1
		for (int i = 0; i < nombreEtatsEtape2; i++)
			for (int j = 0; j < MAXLENGTH; j++)
				tab_trans_etape2[i][j] = -1;

		tab_init_etape2 = new int[nombreEtatsEtape2];
		tab_final_etape2 = new int[nombreEtatsEtape2];
		tab_epsilon_etape2 = new int[nombreEtatsEtape2][nombreEtatsEtape2];
		etape2(tree);

		tab_trans_etape3 = new int[nombreEtatsEtape2][MAXLENGTH];

		// Initialiser le tableau de transition a -1
		for (int i = 0; i < nombreEtatsEtape2; i++) {
			for (int j = 0; j < MAXLENGTH; j++) {
				tab_trans_etape3[i][j] = -1;
			}
		}
		etape3();
		etape4();
	}

	/**
	 * Etape 2 : transformer un arbre en un automate fini non-deterministe avec
	 * ε-transitions selon la methode Aho-Ullman
	 * 
	 * @param tree
	 */
	private void etape2(RegExTree tree) {
		int cpt = 0; // compteur pour le numero de l'etat
		cpt = construireAutomateAvecEpsilon(tree, cpt);
	}

	/**
	 * Etape 3 : transformer en un automate fini deterministe avec la methode des
	 * sous-ensembles automate fini déterministe (DFA)
	 */
	public void etape3() {

		int numGroupe = 0;
		HashMap<Integer, ArrayList<Integer>> renommage = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> groupeInitial = new ArrayList<Integer>();
		for (int i = 0; i < nombreEtatsEtape2; i++) {
			if (tab_init_etape2[i] == 1) {
				groupeInitial.add(i);
			}
		}
		groupeInitial = elargirEtats(groupeInitial);

		PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
		queue.add(numGroupe);
		renommage.put(numGroupe, groupeInitial);
		numGroupe++;
		while (!queue.isEmpty()) {
			int numero = queue.poll();
			ArrayList<Integer> etatsDeparts = renommage.get(numero);
			for (Integer a : caracteres) {
				ArrayList<Integer> etatsArrives = new ArrayList<Integer>();
				for (Integer depart : etatsDeparts) {
					if (tab_trans_etape2[depart][a] != -1) {
						etatsArrives.add(tab_trans_etape2[depart][a]);
					}
				}

				etatsArrives = elargirEtats(etatsArrives);
				if (!etatsArrives.isEmpty()) {
					if (!renommage.containsValue(etatsArrives)) {
						tab_trans_etape3[numero][a] = numGroupe;
						queue.add(numGroupe);
						renommage.put(numGroupe, etatsArrives);
						numGroupe++;
					} else {
						int arrive = getKey(renommage, etatsArrives);
						tab_trans_etape3[numero][a] = arrive;
					}
				}
			}
		}
		nombreEtatsEtape3 = renommage.size();
		// System.out.println("renommage.size() = " + renommage.size());
		tab_final_etape3 = new int[nombreEtatsEtape3];
		init_etape3 = 0;
		// remplir le tableau de etats finaux
		for (Entry<Integer, ArrayList<Integer>> entry : renommage.entrySet()) {
			int key = entry.getKey();
			ArrayList<Integer> value = entry.getValue();
			for (int v : value) {
				if (tab_final_etape2[v] == 1)
					tab_final_etape3[key] = 1;
			}
		}

	}

	/**
	 * Etape 4 : Minimisation d'un automate fini deterministe
	 */
	public void etape4() {

		ArrayList<HashSet<Integer>> ensembles = minimisation();
		construireAutomateMinimal(ensembles);

	}

	/**
	 * Minimisation d'un automate fini
	 */
	public ArrayList<HashSet<Integer>> minimisation() {

		// Ensemble des etats finaux
		ArrayList<Integer> finaux = new ArrayList<Integer>();

		// Ensemble des etats non finaux
		ArrayList<Integer> nonFinal = new ArrayList<Integer>();

		for (int i = 0; i < nombreEtatsEtape3; i++) {
			if (tab_final_etape3[i] == 1) {
				finaux.add(i);
			} else {
				nonFinal.add(i);
			}
		}

		HashSet<Pair> P = new HashSet<Pair>();
		// On represente une paire des etats par un tableau de taille 2
		for (Integer f1 : finaux) {
			for (Integer f2 : finaux) {
				Pair pair = new Pair(f1, f2);

				P.add(pair);
			}
		}

		for (Integer nf1 : nonFinal) {
			for (Integer nf2 : nonFinal) {
				Pair pair = new Pair(nf1, nf2);

				P.add(pair);
			}
		}

		HashSet<Pair> Pprime = new HashSet<Pair>();
		while (!P.equals(Pprime)) {
			for (Pair couple : P) {
				if (couple.p1 == couple.p2) {
					Pprime.add(couple);
					// System.out.println ("1 Prime add "+ couple[0]+ " "+ couple[1] );

				} else {

					boolean ok = true;
					for (int a : caracteres) {
						int p1 = tab_trans_etape3[couple.p1][a];
						int p2 = tab_trans_etape3[couple.p2][a];

						if (p1 == -1 && p2 == -1)
							continue;
						Pair pair = new Pair(p1, p2);
						if (!P.contains(pair)) {
							ok = false;
						}
					}

					if (ok) {
						Pprime.add(couple);
						// System.out.println ("2 Prime add "+ couple.p1+ " "+ couple.p2 );

						Pair pair = new Pair(couple.p2, couple.p1);
						Pprime.add(pair);
						// System.out.println ("3 Prime add "+ pair.p1+ " "+ pair.p2 );

					}
				}
			}
			if (P.equals(Pprime))
				break;
			P = (HashSet<Pair>) Pprime.clone();
			Pprime = new HashSet<Pair>();

		}
		// System.out.println (" P = ");
		// for(Pair c : P) {
		// System.out.print(c.p1+" "+c.p2+" | ");
		// }

		// System.out.println ("\n Prime = ");
		//
		// for(Pair c : Pprime) {
		// System.out.print(c.p1+" "+c.p2+" | ");
		// }
		// System.out.println ("\n Prime = "+Pprime.size());
		//

		ArrayList<HashSet<Integer>> ensembles = new ArrayList<HashSet<Integer>>();
		for (Pair pair : P) {

			int index1 = chercherIndexDeEnsemble(pair.p1, ensembles);
			int index2 = chercherIndexDeEnsemble(pair.p2, ensembles);
			if (index1 != -1) {
				ensembles.get(index1).add(pair.p2);
			} else if (index2 != -1) {
				ensembles.get(index2).add(pair.p1);
			} else {

				HashSet<Integer> ensemble = new HashSet<Integer>();
				ensemble.add(pair.p1);
				ensemble.add(pair.p2);
				ensembles.add(ensemble);
			}
		}
		return ensembles;

	}

	public boolean rechercher(String texte) {
		// fdvscsadaaaaaab
		// si c'est un mot vide

		if (texte.length() == 0) {
			return false;
		}
		int etatCourant = init_etape4;
		int i = 0;
		int a = texte.charAt(i);
		// System.out.println("a "+a);
		//
		// System.out.println("etatCourant "+etatCourant);
		if (tab_trans_etape4[etatCourant][a] != -1 || tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
			while (tab_trans_etape4[etatCourant][a] != -1 || tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
				if (tab_trans_etape4[etatCourant][a] != -1) {
					etatCourant = tab_trans_etape4[etatCourant][a];
				} else if (tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
					etatCourant = tab_trans_etape4[etatCourant][RegEx.DOT];
				}

				i++;
				if (i >= texte.length())
					break;
				a = texte.charAt(i);
			}
			if (tab_final_etape4[etatCourant] == 1) {
				return true;
			} else {
				return rechercher(texte.substring(i));
			}
		} else {
			while (tab_trans_etape4[etatCourant][a] == -1 && tab_trans_etape4[etatCourant][RegEx.DOT] == -1
					&& i < texte.length()) {
				i++;
				if (i >= texte.length())
					break;
				a = texte.charAt(i);
			}
			return rechercher(texte.substring(i));
		}
	}

	public void construireAutomateMinimal(ArrayList<HashSet<Integer>> ensembles) {
		/* debuger */
		// System.out.println("partition : ");
		// for(HashSet<Integer> ens : ensembles) {
		// for(int i : ens) {
		// System.out.print(i+" ");
		// }
		// System.out.println("fin");
		// }

		nombreEtatsEtape4 = ensembles.size();
		tab_trans_etape4 = new int[nombreEtatsEtape4][MAXLENGTH];
		tab_final_etape4 = new int[nombreEtatsEtape4];

		// Initialiser le tableau de transition a -1
		for (int i = 0; i < nombreEtatsEtape4; i++) {
			for (int j = 0; j < MAXLENGTH; j++) {
				tab_trans_etape4[i][j] = -1;
			}
		}

		HashMap<Integer, ArrayList<Integer>> renommage = new HashMap<Integer, ArrayList<Integer>>();

		for (int i = 0; i < nombreEtatsEtape2; i++)
			for (int j = 0; j < MAXLENGTH; j++)
				if (tab_trans_etape3[i][j] != -1) {
					int arrive = tab_trans_etape3[i][j];
					int indexDepart = chercherIndexDeEnsemble(i, ensembles);
					int indexArrive = chercherIndexDeEnsemble(arrive, ensembles);
					tab_trans_etape4[indexDepart][j] = indexArrive;
				}

		init_etape4 = chercherIndexDeEnsemble(init_etape3, ensembles);

		for (int i = 0; i < tab_final_etape3.length; i++) {
			if (tab_final_etape3[i] != 0) {
				int index = chercherIndexDeEnsemble(i, ensembles);
				tab_final_etape4[index] = 1;
			}
		}

	}

	public int chercherIndexDeEnsemble(int target, ArrayList<HashSet<Integer>> ensembles) {
		for (int i = 0; i < ensembles.size(); i++) {
			HashSet<Integer> ens = ensembles.get(i);
			if (ens.contains(target))
				return i;
		}

		return -1;
	}

	/**
	 * @param numGroupe : le numero de groupe
	 * @return un ensemble des etats dans ce groupe
	 */
	public ArrayList<Integer> getEtatsDeGroupe(int numGroupe, int[] P) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < P.length; i++) {
			if (P[i] == numGroupe) {
				result.add(i);
			}
		}
		return result;
	}

	public int getMax(int[] tab) {
		int max = tab[0];
		for (int i = 1; i < tab.length; i++) {
			if (tab[i] > max)
				max = tab[i];
		}
		return max;
	}

	/**
	 * @param etats : le groupe d'etats a elargir
	 * @return
	 */
	public ArrayList<Integer> elargirEtats(ArrayList<Integer> etats) {
		ArrayList<Integer> target = (ArrayList<Integer>) etats.clone();
		ArrayList<Integer> result = elargirEtatsBis(target);
		while (result.size() > target.size()) {
			target = (ArrayList<Integer>) result.clone();
			result = elargirEtatsBis(target);
		}

		return result;
	}

	/**
	 * On agrandit le groupe en ajoutant les etats qui sont les etats arrives par
	 * ε-transitions
	 * 
	 * @param etats
	 * @return
	 */
	public ArrayList<Integer> elargirEtatsBis(ArrayList<Integer> etats) {
		ArrayList<Integer> result = (ArrayList<Integer>) etats.clone();
		for (Integer etat : etats) {
			for (int i = 0; i < nombreEtatsEtape2; i++) {
				if (tab_epsilon_etape2[etat][i] == 1) {
					if (!result.contains(i))
						result.add(i);
				}
			}
		}

		return result;
	}

	/**
	 * En etape 2 Construire un automate fini non-deterministe avec ε-transitions
	 * selon la methode Aho-Ullman
	 * 
	 * @param tree
	 * @param cpt  : le numero du premier etat
	 * @return renvoyer le compteur
	 */
	private int construireAutomateAvecEpsilon(RegExTree tree, int cpt) {

		int root = tree.root;

		if (root == RegEx.CONCAT) {

			int deuxieme = construireAutomateAvecEpsilon(tree.subTrees.get(0), cpt);
			tab_epsilon_etape2[deuxieme - 1][deuxieme] = 1;
			tab_final_etape2[deuxieme - 1] = 0;
			cpt = construireAutomateAvecEpsilon(tree.subTrees.get(1), deuxieme);
			tab_init_etape2[deuxieme] = 0;

		} else if (root == RegEx.ALTERN) {

			/* On ajoute 4 ε-transitions total, et on augmente le compteur de 2 */
			int debut = cpt;
			int premier = cpt + 1;
			int deuxieme = construireAutomateAvecEpsilon(tree.subTrees.get(0), premier);
			cpt = construireAutomateAvecEpsilon(tree.subTrees.get(1), deuxieme);

			tab_final_etape2[cpt] = 1;
			tab_init_etape2[debut] = 1;

			tab_epsilon_etape2[debut][premier] = 1;
			tab_epsilon_etape2[debut][deuxieme] = 1;
			tab_epsilon_etape2[deuxieme - 1][cpt] = 1;
			tab_epsilon_etape2[cpt - 1][cpt] = 1;
			tab_init_etape2[premier] = 0;
			tab_init_etape2[deuxieme] = 0;
			tab_final_etape2[deuxieme - 1] = 0;
			tab_final_etape2[cpt - 1] = 0;
			cpt++;

		} else if (root == RegEx.ETOILE) {

			/* On ajoute 4 ε-transitions total, et on augmente le compteur de 2 */
			tab_init_etape2[cpt] = 1;
			cpt++;
			int cpt_debut = cpt;
			tab_epsilon_etape2[cpt_debut - 1][cpt_debut] = 1;
			cpt = construireAutomateAvecEpsilon(tree.subTrees.get(0), cpt_debut);

			tab_final_etape2[cpt - 1] = 0;
			tab_init_etape2[cpt_debut] = 0;
			tab_epsilon_etape2[cpt - 1][cpt_debut] = 1;
			tab_epsilon_etape2[cpt_debut - 1][cpt] = 1;
			tab_epsilon_etape2[cpt - 1][cpt] = 1;
			tab_final_etape2[cpt] = 1;
			cpt++;

		} else if (root == RegEx.DOT) {

			tab_trans_etape2[cpt][RegEx.DOT] = cpt + 1;
			tab_init_etape2[cpt] = 1;
			tab_final_etape2[cpt + 1] = 1;
			cpt = cpt + 2;
		}

		else {/* root == RegEx.DOT */
			// System.out.println("lettre = " + cpt );

			tab_trans_etape2[cpt][root] = cpt + 1;
			tab_init_etape2[cpt] = 1;
			tab_final_etape2[cpt + 1] = 1;
			cpt = cpt + 2;
		}
		return cpt;
	}

	/**
	 * Obtenir le nombre d'etats total dans un automate avec ε-transitions
	 * 
	 * @param tree
	 * @return
	 */

	private int getNbEtatsWithEpsilon(RegExTree tree) {

		int nombre = 0;
		int root = tree.root;
		if (root == RegEx.CONCAT) {
			// faire rien
		} else if (root == RegEx.ETOILE) {
			nombre += 2;
		} else if (root == RegEx.ALTERN) {
			nombre += 2;
		} else {/* root == RegEx.DOT */
			/* quand c'est un caractere */
			caracteres.add(root);
			nombre += 2;
		}
		if (!tree.subTrees.isEmpty()) {
			for (RegExTree arbre : tree.subTrees) {
				nombre += getNbEtatsWithEpsilon(arbre);
			}
		}
		return nombre;
	}

	public <K, V> K getKey(HashMap<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public class Pair {
		int p1;
		int p2;

		public Pair(int p1, int p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		@Override
		public int hashCode() {
			return Objects.hash(p1, p2);
		}

		@Override
		public boolean equals(Object obj) {
			Pair pair = (Pair) obj;
			if (this.p1 == pair.p1 && this.p2 == pair.p2)
				return true;
			return false;

		}
	}

	public void printAutomate() {
		System.out.println("\n etat initial  etape3:" + init_etape3);
		System.out.print("\n tab_final etape3 :");
		for (int a : tab_final_etape3)
			System.out.print(a);

		System.out.print("\n  tab_trans etape3 :");
		for (int i = 0; i < nombreEtatsEtape2; i++)
			for (int j = 0; j < MAXLENGTH; j++)
				if (tab_trans_etape3[i][j] != -1) {
					System.out.print("[" + i + "][" + j + "]= " + +tab_trans_etape3[i][j] + "  ");
				}

		System.out.println("nombreEtatsEtape3 : " + nombreEtatsEtape3);
		for (int a : caracteres) {
			System.out.println("caractere = " + a);
		}

		System.out.println("etat init  etape4:" + init_etape4);
		System.out.print("\n tab_final etape4 :");
		for (int a : tab_final_etape4)
			System.out.print(a);

		System.out.print("\n  tab_trans etape4 :");
		for (int i = 0; i < nombreEtatsEtape4; i++)
			for (int j = 0; j < MAXLENGTH; j++)
				if (tab_trans_etape4[i][j] != -1) {
					System.out.print("[" + i + "][" + j + "]= " + +tab_trans_etape4[i][j] + "  ");
				}

		System.out.println("nombreEtatsEtape4 : " + nombreEtatsEtape4);
	}

	public void lireTexte(String nomFichier) {

		ArrayList<String> results = new ArrayList<String>();
		String ligne;
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(nomFichier));
			String premierligne = reader.readLine();
			if (premierligne != null)
				if (rechercher(premierligne.substring(1)))
					results.add(premierligne);

			while ((ligne = reader.readLine()) != null) {

				////
				// for(int i = 0;i<ligne.length();i++) {
				// if((int)ligne.charAt(i)>7000)
				// System.out.println(" char i = "+ i + " is "+ ligne.charAt(i) +" ascci = "+
				//// (int)ligne.charAt(i) );
				// }
				//
				if (rechercher(ligne)) {
					results.add(ligne);

				}
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// System.out.println("size of results = "+results.size());
		for (String result : results) {
			System.out.println(result);
		}
	}

	
	/**
	 * Rechercher si un mot est accepté par l'automate
	 * @param mot
	 * @return
	 */
	public boolean rechercherMot(String mot) {
		// fdvscsadaaaaaab
		// si c'est un mot vide

		if (mot.length() == 0) {
			return false;
		}
		int etatCourant = init_etape4;
		int i = 0;
		int a = mot.charAt(i);
		// System.out.println("a "+a);
		//
		// System.out.println("etatCourant "+etatCourant);
		if (tab_trans_etape4[etatCourant][a] != -1 || tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
			while (tab_trans_etape4[etatCourant][a] != -1 || tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
				if (tab_trans_etape4[etatCourant][a] != -1) {
					etatCourant = tab_trans_etape4[etatCourant][a];
				} else if (tab_trans_etape4[etatCourant][RegEx.DOT] != -1) {
					etatCourant = tab_trans_etape4[etatCourant][RegEx.DOT];
				}

				i++;
				if (i >= mot.length())
					break;
				a = mot.charAt(i);
			}
			if (tab_final_etape4[etatCourant] == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			while (tab_trans_etape4[etatCourant][a] == -1 && tab_trans_etape4[etatCourant][RegEx.DOT] == -1
					&& i < mot.length()) {
				i++;
				if (i >= mot.length())
					break;
				a = mot.charAt(i);
			}
			return false;
		}
	}
}
