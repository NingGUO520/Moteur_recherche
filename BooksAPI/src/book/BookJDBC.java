package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import automate.Automate;
import automate.RegEx;
import index.Radix;
import index.Radix.Coordonnees;
import index.Radix.RadixTree;

public class BookJDBC {

	RadixTree tree;
	Radix radix;
	List<Map<String, Coordonnees>> mapIndexList;
	int limit = 600;
	int batchSize = 100;

	/**
	 * Instantiation de BookJDBC : indexage et construction du radix tree   
	 */
	public BookJDBC() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			//connexion à la base de données
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "daar");
			stmt = c.createStatement();
			stmt.setFetchSize(batchSize);
			radix = new Radix();
			mapIndexList = new ArrayList<Map<String, Coordonnees>>();
			ResultSet rs = stmt.executeQuery("SELECT id,contenu FROM livres_livre LIMIT " + limit);
			int i = 1;
			for (int j = 0; j < limit; j = j + batchSize) {
				while (rs.next()) {
					//récupération de l'id du livre
					int id = rs.getInt("id");
					//récpération du contenu du livre
					String contenu = rs.getString("contenu");
					//ajout dans l'index global
					mapIndexList.add(radix.lireTexte(id, contenu));
					System.out.println(i);
					i++;
				}
				// construction de l'arbre radix
				radix.construireTree(mapIndexList);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation done successfully");
	}

	/**
	 * Recherche d'un mot dans un radix tree et retourne ses coordonnées
	 * @param pattern
	 * @return list
	 */
	public List<Coordonnees> getRadixBooksResult(String pattern) {
		List<Coordonnees> listCoords = radix.rechercher(radix.getRadixTree(), pattern);
		return listCoords;
	}

	/**
	 * Recherche des mots acceptés par la regExp et retourne leur coordonnées
	 * @param regExp
	 * @return
	 */
	public List<Coordonnees> getAutomataBooksResult(String regExp) {
		List<Coordonnees> listCoords = new ArrayList<Coordonnees>();
		RegEx regex = new RegEx();
		Automate automate = regex.creerAutomate(regExp);
		for (Map<String, Coordonnees> mapIndex : mapIndexList) {
			for (Entry<String, Coordonnees> e : mapIndex.entrySet()) {
				String mot = e.getKey();
				if (automate.rechercherMot(mot)) {
					Coordonnees c = e.getValue();
					listCoords.add(e.getValue());
				}
			}
		}
		return merge(listCoords);
	}

	/**
	 * Retire les coordonnées doublons 
	 * 
	 * @param listCoords
	 * @return
	 */
	public List<Coordonnees> merge(List<Coordonnees> listCoords) {
		int i = 0;
		while (i < listCoords.size() - 1) {
			int id = listCoords.get(i).id;
			if (listCoords.get(i + 1) != null) {
				int idNext = listCoords.get(i + 1).id;
				if (id == idNext) {
					listCoords.get(i).coords.addAll(listCoords.get(i + 1).coords);
					listCoords.remove(i + 1);
				} else {
					i++;
				}
			}
		}
		return listCoords;
	}

	/*
	 * DEBUG
	 */
//	public static void main(String[] args) {
//		BookJDBC jdbc = new BookJDBC();
//		List<Coordonnees> list = jdbc.getRadixBooksResult("people");
//		list.remove(0);
//		for(Coordonnees c :list ) {
//			System.out.println(c);
//		}
//		
//		List<Coordonnees> listRegexp = jdbc.getAutomataBooksResult("peopl.");
//		for(Coordonnees c :listRegexp ) {
//			System.out.println(c);
//		}
//	}
}
