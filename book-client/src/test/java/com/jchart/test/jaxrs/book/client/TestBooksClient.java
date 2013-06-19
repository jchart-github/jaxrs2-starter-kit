package com.jchart.test.jaxrs.book.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
@DELETE delete a book

*/

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
public class TestBooksClient {
	
	private WebTarget _baseWebTarget;
	
	@Before
	public void setUp() {
		Client client = ClientBuilder.newClient();
		_baseWebTarget = client.target("http://localhost:8080/book-service/api");
	}
	
	/**
	 * get a list of books
	 */
	@Test
	public void testGetBooks() {
		WebTarget webTarget = _baseWebTarget.path("books");
		Response response = webTarget.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Books books = response.readEntity(Books.class);
		Assert.assertNotNull(books);
		Assert.assertEquals(10, books.getBooks().size());
		for (Book book : books.getBooks()) {
			System.out.println(book);
		}
	}

	/**
	 * get a single book
	 * 
	 * demonstrates content negotiation and
	 * command pattern for request invocation 
	 */
	@Test
	public void testContentNegotiation() {
		WebTarget webTarget = _baseWebTarget.path("books/1");
		List<Invocation> invocations = new ArrayList<>();
		invocations.add(webTarget.request(MediaType.APPLICATION_JSON).buildGet());
		invocations.add(webTarget.request(MediaType.APPLICATION_XML).buildGet());
		// Command Pattern - print String results
		for (Invocation invocation : invocations) {
			Response response = invocation.invoke();
			Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
			System.out.println(response.readEntity(String.class));
		}
		// Invoke again, this time marshal into Book 
		for (Invocation invocation : invocations) {
			Response response = invocation.invoke();
			Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
			System.out.println(response.readEntity(Book.class));
		}
	}
	
	@Test
	public void testDeleteAllCreate() {
		// first save books
		WebTarget webTarget = _baseWebTarget.path("books");
		Response response = webTarget.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Books booksSave = response.readEntity(Books.class);
		Assert.assertNotNull(booksSave);
		Assert.assertEquals(10, booksSave.getBooks().size());

		// delete all books
		Response responseDel = webTarget.request().buildDelete().invoke();
		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), responseDel.getStatus());
		
		// make sure the books are gone
		Response responseGet = webTarget.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), responseGet.getStatus());
		Books books = responseGet.readEntity(Books.class);
		Assert.assertNotNull(books);
		Assert.assertEquals(0, books.getBooks().size());

		// re-create books one at a time from the saved list using POST
		List<Invocation> invocations = new ArrayList<>();
		for (Book book : booksSave.getBooks()) {
			Entity<?> entity = Entity.entity(book, MediaType.APPLICATION_JSON);
			Response responseCreate = webTarget.request(MediaType.APPLICATION_JSON).buildPost(entity).invoke();
			Assert.assertEquals(Status.CREATED.getStatusCode(), responseCreate.getStatus());
			String location = responseCreate.getHeaderString("Location"); 
			invocations.add(ClientBuilder.newClient().target(location).request().buildGet());
		}
		
		// make sure the books got created
		// Invoke based on the returned location headers 
		for (Invocation invocation : invocations) {
			Assert.assertEquals(Status.OK.getStatusCode(), invocation.invoke().getStatus());
		}

		// run this test
		testGetBooks();
		
	}

	@Test
	public void testDeleteSingleBook() {
		WebTarget webTarget = _baseWebTarget.path("books/8");
		Invocation invocationGet = webTarget.request(MediaType.APPLICATION_JSON).buildGet();
		Response response = invocationGet.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Book bookSaved = response.readEntity(Book.class);
	
		// delete the book resource on the server
		webTarget.request().buildDelete().invoke();

		// make sure it is gone
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), invocationGet.invoke().getStatus());

		// re-create from saved book
		Entity<?> entity = Entity.entity(bookSaved, MediaType.APPLICATION_JSON);
		
		WebTarget webTargetCreate = _baseWebTarget.path("books");
		Response responseCreate = webTargetCreate.request(MediaType.APPLICATION_JSON).buildPost(entity).invoke();
		Assert.assertEquals(Status.CREATED.getStatusCode(), responseCreate.getStatus());
		
		// run this test
		testGetBooks();
		
	}
	
	@Test
	public void testUpdateSingle() {
		WebTarget webTarget = _baseWebTarget.path("books/3");
		Invocation invocationGet = webTarget.request(MediaType.APPLICATION_JSON).buildGet();
		Response response = invocationGet.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Book book = response.readEntity(Book.class);
		book.setTitle("Gone with the Wind");
		book.setAuthor("Margaret Mitchell");
		Entity<?> entity = Entity.entity(book, MediaType.APPLICATION_JSON);
		Response responsePut = webTarget.request().buildPut(entity).invoke();
		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), responsePut.getStatus());

		// read the updated book from the server
		Response responseGet2 = invocationGet.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Book book2 = responseGet2.readEntity(Book.class);
		Assert.assertEquals("Gone with the Wind", book2.getTitle());
		Assert.assertEquals("Margaret Mitchell", book2.getAuthor());
	}

	
	@Test
	public void testUpdateAll() {
		WebTarget webTarget = _baseWebTarget.path("books");
		Invocation invocation = webTarget.request(MediaType.APPLICATION_JSON).buildGet();
		
		// first save books for later restore
		Response response1 = invocation.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response1.getStatus());
		Books booksSave = response1.readEntity(Books.class);
		
		// get books again using same Invocation object
		Response response2 = invocation.invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
		Books books = response2.readEntity(Books.class);
		
		Assert.assertNotNull(books);
		Assert.assertEquals(10, books.getBooks().size());
		for (Book book : books.getBooks()) {
			book.setTitle("Title " + book.getIsbn());
		}

		// do the bulk update
		Entity<?> entity = Entity.entity(books, MediaType.APPLICATION_JSON);
		Response responsePut = webTarget.request().buildPut(entity).invoke();
		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), responsePut.getStatus());
		
		// test a book to make sure that it was updated
		WebTarget webTarget5 = _baseWebTarget.path("books/5");
		Response response5 = webTarget5.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response5.getStatus());
		Book book5 = response5.readEntity(Book.class);
		Assert.assertEquals("Title 5", book5.getTitle());
		
		// restore saved books
		Entity<?> entity2 = Entity.entity(booksSave, MediaType.APPLICATION_JSON);
		Response responsePut2 = webTarget.request().buildPut(entity2).invoke();
		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), responsePut2.getStatus());

		// test a book to make sure that it was updated
		WebTarget webTarget6 = _baseWebTarget.path("books/6");
		Response response6 = webTarget6.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		Assert.assertEquals(Status.OK.getStatusCode(), response5.getStatus());
		Book book6 = response6.readEntity(Book.class);
		Assert.assertEquals("API Guide", book6.getTitle());

	}
	
}
