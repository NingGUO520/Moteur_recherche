package servlet;

import javax.servlet.ServletContextListener;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import book.BookJDBC;

@WebListener
public class ServletListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		System.out.println("Loading index from DB...");
		long startTime = System.nanoTime();
		BookJDBC jdbc = new BookJDBC();
		long endTime = System.nanoTime();
		ctx.setAttribute("BOOKJDBC", jdbc);
		System.out.println("Index ready!");
		long convert = TimeUnit.SECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS);
		System.out.println(" Time : " + (convert) + "s.");
	}
}
