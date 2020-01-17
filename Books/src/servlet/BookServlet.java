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

import book.BookJDBC;
import index.Coord;

/**
 * Servlet implementation class BookServlet
 */
@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	BookJDBC jdbc = new BookJDBC();
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestUrl = request.getRequestURI();
		String regExp = requestUrl.substring("/Books/search/".length());
		
		Map<Integer, ArrayList<Coord>> res = jdbc.getBooksResult(regExp);

		
		//JSONArray arr = new JSONArray();
//arr.put(res);
		
		JSONObject json_arr = null;
		if (res != null) {
			
			json_arr = new JSONObject(res);
//			for (Entry<Integer, ArrayList<Coord>> entry : res.entrySet()) {
//				
//				int id_book = entry.getKey();
//				System.out.println(id_book);
//				JSONObject jsonBookId = new JSONObject(id_book);
//				ArrayList<Coord> coords = entry.getValue();
//				try {
//					json_arr.put(id_book, coords);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
			response.getOutputStream().println(json_arr.toString());
		} else {
			response.getOutputStream().println("{}");
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
