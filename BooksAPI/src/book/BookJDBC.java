package book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import aho_ullman.Automata;
import aho_ullman.DFA;
import aho_ullman.RegEx;
import aho_ullman.RegExTree;
import aho_ullman.Text;
import index.Coord;
import index.IndexingFile;
import index.RadixTree;

public class BookJDBC {

	private RadixTree radixTree;
	private List<String> booksContent;

	public BookJDBC() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "daar");

			stmt = c.createStatement();

			radixTree = new RadixTree();	
			ResultSet count = stmt.executeQuery("SELECT COUNT(*)  AS count FROM livres_livre");

			while(count.next()) {
				booksContent = new ArrayList<String>(count.getInt("count"));
			}
			
			ResultSet rs = stmt.executeQuery("SELECT id,contenu FROM livres_livre;");
			while (rs.next()) {
				int id = rs.getInt("id");
				String contenu = rs.getString("contenu");
				booksContent.add(contenu);
				IndexingFile index = new IndexingFile(contenu);
				if(id == 37) {
					System.out.println();
				}
				radixTree.addIndexingFile(id, index);
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

	public List<String> getBooksContent() {
		return booksContent;
	}

	public Map<Integer, ArrayList<Coord>> getRadixBooksResult(String pattern) {
		Map<Integer, ArrayList<Coord>> books = radixTree.search(pattern);
		return books;
	}
	
	public Map<Integer,ArrayList<String>> getAutomataBooksResult(String pattern){
		Map<Integer, ArrayList<String>> books = new HashMap<Integer,ArrayList<String>>();
		try {
			RegEx regEx = new RegEx();
			regEx.setRegEx(pattern);
			
			for(String text : booksContent) {
				RegExTree tree = regEx.parse();
				Automata a = new Automata();
		        Automata a_res = a.automata_complete(tree);
		        DFA dfa = new DFA(a_res);
		        Text t = new Text(text,dfa);
		        ArrayList<String> lines = t.getLines();
		        if(!lines.isEmpty()) {
		        	books.put(booksContent.indexOf(text),lines);
		        }
		    	return books;
//		        for(String s : lines) {
//		        	System.out.println(s);
//		        }
			}
		}catch (Exception e) {
				e.printStackTrace();
		}	
		return null;
	}
}
