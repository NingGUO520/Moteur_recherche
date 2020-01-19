package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import aho_ullman.Automata;
import aho_ullman.DFA;
import aho_ullman.RegEx;
import aho_ullman.RegExTree;
import aho_ullman.Text;
import index.Radix;
import index.Radix.Coordonnees;
import index.Radix.RadixTree;

public class BookJDBC {

	RadixTree tree;
	Radix radix;
	private List<String> booksContent;

	public BookJDBC() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "daar");

			stmt = c.createStatement();
			radix = new Radix();
			ResultSet count = stmt.executeQuery("SELECT COUNT(*)  AS count FROM livres_livre ");

			while (count.next()) {
				booksContent = new ArrayList<String>(count.getInt("count"));
			}

			List<Map<String, Coordonnees>> mapIndexList = new ArrayList<Map<String, Coordonnees>>();

			ResultSet rs = stmt.executeQuery("SELECT id,contenu FROM livres_livre");
			while (rs.next()) {
				int id = rs.getInt("id");
				String contenu = rs.getString("contenu");
				booksContent.add(contenu);
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

	public List<String> getBooksContent() {
		return booksContent;
	}

	public List<Coordonnees> getRadixBooksResult(String pattern) {
		return radix.rechercher(tree, pattern);
	}

	public Map<Integer, ArrayList<Integer>> getAutomataBooksResult(String pattern) {
		Map<Integer, ArrayList<Integer>> books = new HashMap<Integer, ArrayList<Integer>>();
		try {
			RegEx regEx = new RegEx();
			regEx.setRegEx(pattern);
			int id = 1;
			for (String text : booksContent) {
				RegExTree tree = regEx.parse();
				Automata a = new Automata();
				Automata a_res = a.automata_complete(tree);
				DFA dfa = new DFA(a_res);
				Text t = new Text(text, dfa);
				ArrayList<Integer> lines = t.getLines();
				if (!lines.isEmpty()) {
					books.put(id, lines);
				}
				id++;
			}
			return books;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) {
//		BookJDBC jdbc = new BookJDBC();
//		List<Coordonnees> list = jdbc.getRadixBooksResult("people");
//		list.remove(0);
//		for(Coordonnees c :list ) {
//			System.out.println(c);
//		}
//	}
}
