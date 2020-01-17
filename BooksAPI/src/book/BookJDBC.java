package book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import index.Coord;
import index.IndexingFile;
import index.RadixTree;

public class BookJDBC {

	private RadixTree radixTree;

	public BookJDBC() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "daar");

			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT id,contenu FROM livres_livre LIMIT 3;");

			radixTree = new RadixTree();
			while (rs.next()) {
				int id = rs.getInt("id");
				String contenu = rs.getString("contenu");
				IndexingFile index = new IndexingFile(contenu);
				radixTree.addIndexingFile(id, index);
			}

//			print("people", t);
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

	public Map<Integer, ArrayList<Coord>> getBooksResult(String pattern) {
		Map<Integer, ArrayList<Coord>> books = radixTree.search(pattern);
		return books;

//		System.out.println(books.size());
//		for(Entry<Integer, ArrayList<Coord>> entry : books.entrySet()) {
//			Integer k = entry.getKey();
//			ArrayList<Coord> coords = entry.getValue();
//			System.out.println("For the book : "+ k);
//			for(Coord c : coords) {
//				System.out.println(c);
//			}
//			
//		}
	}
}
