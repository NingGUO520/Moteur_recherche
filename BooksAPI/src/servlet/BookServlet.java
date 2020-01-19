package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.google.gson.*;
import aho_ullman.Automata;
import aho_ullman.DFA;
import aho_ullman.RegEx;
import aho_ullman.RegExTree;
import aho_ullman.Text;
import book.BookJDBC;
import index.Radix.Coordonnees;
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
			List<Coordonnees> res = jdbc.getRadixBooksResult(regExp);
			res.remove(0);
			String json = new Gson().toJson(res);
			JSONObject obj = new JSONObject(res);
//			json_arr = new JSONObject(res);
//			System.out.println(obj.toString());
			response.getOutputStream().println(json);
		} else if (CheckInput.isRegExp(regExp)) {
			Map<Integer, ArrayList<Integer>> res = jdbc.getAutomataBooksResult(regExp);
			String json = new Gson().toJson(res);
			response.getOutputStream().println(json);
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
