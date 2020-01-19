package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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
	List<Map<String, Coordonnees>> mapIndexList ;

	public BookJDBC() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "daar");
			stmt = c.createStatement();
			radix = new Radix();
			mapIndexList = new ArrayList<Map<String, Coordonnees>>();

			ResultSet rs = stmt.executeQuery("SELECT id,contenu FROM livres_livre");
			while (rs.next()) {
				int id = rs.getInt("id");
				String contenu = rs.getString("contenu");
				mapIndexList.add(radix.lireTexte(id, contenu));
			}

			tree = radix.construireTree(mapIndexList);
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

	public List<Coordonnees> getRadixBooksResult(String pattern) {
		return radix.rechercher(tree, pattern);
	}
	
	public List<Coordonnees> getAutomataBooksResult(String regExp){
		List <Coordonnees> listCoords = new ArrayList<Coordonnees>();
		RegEx regex = new RegEx();
		Automate automate = regex.creerAutomate(regExp);
		for(Map<String, Coordonnees> mapIndex : mapIndexList) {
			for(Entry<String,Coordonnees> e :mapIndex.entrySet()) {
				String mot = e.getKey();
				if(automate.rechercherMot(mot)) {
					Coordonnees c = e.getValue();
					listCoords.add(e.getValue());                                               
				}
			}
		}
		
		int i = 0;
		while(i < listCoords.size()-1) {
			int id = listCoords.get(i).id;
			if(listCoords.get(i+1) != null) {
				int idNext = listCoords.get(i+1).id;
				if(id == idNext) {
					listCoords.get(i).coords.addAll(listCoords.get(i+1).coords);
					listCoords.remove(i+1);
				}
				else {
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
