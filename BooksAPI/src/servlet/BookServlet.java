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
import org.json.JSONObject;
import com.google.gson.*;
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
		
		List<Coordonnees> res = null;
		if (CheckInput.isAlphabetic(regExp)) {
			res = jdbc.getRadixBooksResult(regExp);
			res.remove(0);
			
		} else if (CheckInput.isRegExp(regExp)) {
			res = jdbc.getAutomataBooksResult(regExp);
		}
		
		String json = new Gson().toJson(res);
		response.getOutputStream().println(json);

	}
}
