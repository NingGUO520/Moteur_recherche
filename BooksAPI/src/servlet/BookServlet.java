package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import aho_ullman.Automata;
import aho_ullman.DFA;
import aho_ullman.RegEx;
import aho_ullman.RegExTree;
import aho_ullman.Text;
import book.BookJDBC;
import index.Coord;
import util.CheckInput;

/**
 * Servlet implementation class BookServlet
 */
@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static BookJDBC jdbc;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		jdbc = (BookJDBC) request.getServletContext().getAttribute("BOOKJDBC");
		String requestUrl = request.getRequestURI();
		String regExp = requestUrl.substring("/BooksAPI/search/".length());

		if (CheckInput.isAlphabetic(regExp)) {
			Map<Integer, ArrayList<Coord>> res = jdbc.getRadixBooksResult(regExp);
			System.out.println(res.size());
			JSONArray arr = new JSONArray();
			arr.put(res);
			JSONObject json_arr = null;
			if (res != null) {
				json_arr = new JSONObject(res);
				response.getOutputStream().println(json_arr.toString());
			} else {
				response.getOutputStream().println("{}");
			}
		} else if (CheckInput.isRegExp(regExp)) {
			Map<Integer, ArrayList<String>> res = jdbc.getAutomataBooksResult(regExp);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
