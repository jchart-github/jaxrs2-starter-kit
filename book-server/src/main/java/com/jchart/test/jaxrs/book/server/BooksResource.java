package com.jchart.test.jaxrs.book.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jchart.test.jaxrs.common.dto.Book;
import com.jchart.test.jaxrs.common.dto.Books;

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@Path("/books")
public class BooksResource {
	
	@Context
	private ServletContext _servletContext;
	
	public BooksResource() {
		System.out.println("BooksResource: constructor");
	}

	/**
	 * RFC 2616 9.5
	 * The POST method is used to request that the origin server 
	 * accept the entity enclosed in the request as a new
	 * subordinate of the resource identified by the Request-URI
	 * in the Request-Line.
	 * 
	 * If a resource has been created on the origin server, the 
	 * response SHOULD be 201 (Created) and contain an entity which 
	 * describes the status of the request and refers to the new 
	 * resource, and a Location header 

	 */
	@POST
	public void createSingle() {
		// update or create book
		// set Location header 
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Books readAll() {
		Books retval = new Books();
		System.out.println("BooksResource: readAll");
		List<Book> books = new ArrayList<>();
		for (int i = 0;i<10;i++) {
			books.add(getBooksService().getBook(i+""));
		}
		retval.setBooks(books);
		return retval;
	}
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON, 
		MediaType.APPLICATION_XML})
	public Book readSingle(@PathParam("id") String id) {
		System.out.println("BooksResource: readSingle");
		return getBooksService().getBook(id);
	}

	/**
	 * RFC 2616 9.6
	 * The PUT method requests that the enclosed entity 
	 * be stored under the supplied Request-URI.
	 * 
	 * If the Request-URI refers to an already existing resource, 
	 * the enclosed entity SHOULD be considered as a modified version
	 * of the one residing on the origin server. 
	 * 
	 * If the Request-URI does not point to an existing resource, 
	 * and that URI is capable of being defined as a new resource
	 * by the requesting user agent, the origin server can create 
	 * the resource with that URI.
	 * 
	 */
	@PUT
	public void updateAll() {
		System.out.println("BooksResource: updateAll");
		
	}
	
	@PUT
	@Path("{id}")
	public void updateSingle() {
		System.out.println("BooksResource: updateSingle");
		// if exists update else error 
		// do not create, use post for that
		// NOTE: DO NOT allow client to create resources using put.
		// Reasoning: client would then be in control URI creation
	}

	@DELETE
	public void deleteAll() {
		System.out.println("BooksResource: deleted all books");
	}
	
	@DELETE
	@Path("{id}")
	public void deleteSingle(@PathParam("id") String id) {
		System.out.println("BooksResource: delete " + id);
	}

	private BooksService getBooksService() {
		BooksService retval = null;
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(_servletContext);
		retval = appContext.getBean(BooksService.class);
		return retval;
		
	}
	
}
