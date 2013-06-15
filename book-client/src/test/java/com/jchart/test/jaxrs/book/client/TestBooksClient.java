package com.jchart.test.jaxrs.book.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jchart.test.jaxrs.common.dto.Book;
import com.jchart.test.jaxrs.common.dto.Books;


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
		Invocation invocation = webTarget.request(MediaType.APPLICATION_JSON).buildGet();
		Response response = invocation.invoke();
		Assert.assertEquals(200, response.getStatus());
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
		WebTarget webTarget = _baseWebTarget.path("books/1215");
		webTarget.register(JacksonJaxbJsonProvider.class);
		List<Invocation> invocations = new ArrayList<>();
		invocations.add(webTarget.request(MediaType.APPLICATION_JSON).buildGet());
		invocations.add(webTarget.request(MediaType.APPLICATION_XML).buildGet());
		// Command Pattern - print String results
		for (Invocation invocation : invocations) {
			Response response = invocation.invoke();
			Assert.assertEquals(200, response.getStatus());
			System.out.println(response.readEntity(String.class));
		}
		// Invoke again, this time marshal into Book 
		for (Invocation invocation : invocations) {
			Response response = invocation.invoke();
			Assert.assertEquals(200, response.getStatus());
			System.out.println(response.readEntity(Book.class));
		}
	}
	
}
