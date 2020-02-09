package servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

	private List<Coordonnees> res = null;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		jdbc = (BookJDBC) request.getServletContext().getAttribute("BOOKJDBC");
		String requestUrl = request.getRequestURI();
		
		String regExp = requestUrl.substring("/BooksAPI/search/".length());
			if(res != null) {
				res.clear();
			}
			if (CheckInput.isAlphabetic(regExp)) {
				res = jdbc.getRadixBooksResult(regExp).stream().distinct().collect(Collectors.toList()); ;
			} else if (CheckInput.isRegExp(regExp)) {
				System.out.println("regExp");
				res = jdbc.getAutomataBooksResult(regExp);
			}
		Collections.sort(res);

		String json = new Gson().toJson(res);
		response.getOutputStream().println(json);

}}
