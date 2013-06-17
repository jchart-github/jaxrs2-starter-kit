package com.jchart.test.jaxrs.book.server;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Hex;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jchart.test.jaxrs.common.dto.Book;
import com.jchart.test.jaxrs.common.dto.Books;

/*
  REST Implementation Rules
   
  Resource /books
  @POST create a new book
  @GET list all books
  @PUT bulk update books
  @Delete delete all books
  
  Resources /books/{id}
  @POST Error
  @GET return a book 
  @PUT if exists, update book; if not ERROR
  @Delete delete a book
  
*/

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@Path("/books")
public class BooksResource {
	
	@Context
	private ServletContext _servletContext;
	
	@Context
	private UriInfo _uriInfo;
	
	// Spring managed bean
	private BooksService _booksService;
	
	public BooksResource() {
		System.out.println("BooksResource: constructor");
	}
	
	@PostConstruct 
	public void init() {
		System.out.println("BooksResource: @PostConstruct");
		_booksService = getBooksService();
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
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSingle(@FormParam("author") String author, 
			@FormParam("title") String title,
			@FormParam("isbn") String isbn) {
			
		// if book exists return NOT_MODIFIED 
		// only allow updates using PUT
		if (_booksService.getBook(isbn) != null) {
			return Response.status(Status.NOT_MODIFIED).build();
		}
		
		Response retval = null;
		// create a book
		Book book = _booksService.createBook(author, title, isbn);
		if (book != null) {
			// set the Location Header of the newly created book
			UriBuilder uriBuilder = UriBuilder.fromUri(_uriInfo.getRequestUri());
			uriBuilder.path("{isbn}");
			EntityTag etag = new EntityTag(Hex.encodeHexString(book.toString().getBytes()));
			retval = Response.status(Status.CREATED).
					location(uriBuilder.build(isbn)).
					tag(etag).
					entity(book).
					type(MediaType.APPLICATION_JSON).
					build();
		}
		return retval;
	}
	
	/**
	 * this POST accepts a book in request body 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSingle(Book book) {
		Response retval = null;
		Book bookCreated = _booksService.createBook(book.getAuthor(), book.getTitle(), book.getIsbn());
		if (bookCreated != null) {
			// set the Location Header of the newly created book
			UriBuilder uriBuilder = UriBuilder.fromUri(_uriInfo.getRequestUri());
			uriBuilder.path("{isbn}");
			EntityTag etag = new EntityTag(Hex.encodeHexString(book.toString().getBytes()));
			retval = Response.status(Status.CREATED).
					location(uriBuilder.build(bookCreated.getIsbn())).
					tag(etag).
					entity(book).
					type(MediaType.APPLICATION_JSON).
					build();
		}
		return retval;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Books readAll() {
		System.out.println("BooksResource: readAll");
		Books retval = _booksService.getBooks();
		return retval;
	}
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON, 
		MediaType.APPLICATION_XML})
	public Response readSingle(@PathParam("id") String id) {
		Response retval = null;
		// create a book
		Book book = _booksService.getBook(id);
		if (book != null) {
			EntityTag etag = new EntityTag(Hex.encodeHexString(book.toString().getBytes()));
			retval = Response.status(Status.OK).
					tag(etag).
					entity(book).
					type(MediaType.APPLICATION_JSON).
					build();
		} else {
			retval = Response.status(Status.NOT_FOUND).build();
		}
		return retval;
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void updateAll(Books books) {
		System.out.println("BooksResource: updateAll");
		_booksService.getUpdateAll(books.getBooks());
	}

	/**
	 * if exists update, else error
	 * do not create, use post instead
	 * NOTE: DO NOT allow client to create resources using put.
	 * Reasoning: client would then be in control URI creation
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateSingle(Book book,
			@PathParam("id") String id) {
		Response retval = null;
		System.out.println("BooksResource: updateSingle");
		// update existing book
		Book existingBook = _booksService.getBook(id);
		if (existingBook != null) {
			Book updatedBook = _booksService.updateBook(book);
			if (updatedBook != null) {
				retval = Response.status(Status.NO_CONTENT).build();
			} else {
				retval = Response.status(Status.NOT_FOUND).build();
			}
		} else {
			retval = Response.status(Status.NOT_FOUND).build();
		}
		return retval;
		
	}

	@DELETE
	public void deleteAll() {
		System.out.println("BooksResource: deleted all books");
		_booksService.deleteAllBooks();
	}
	
	@DELETE
	@Path("{id}")
	public void deleteSingle(@PathParam("id") String id) {
		_booksService.deleteBook(id);
		System.out.println("BooksResource: delete " + id);
	}

	private BooksService getBooksService() {
		BooksService retval = null;
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(_servletContext);
		retval = appContext.getBean(BooksService.class);
		return retval;
		
	}
	
}
