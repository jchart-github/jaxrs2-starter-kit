package com.jchart.test.jaxrs.book.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.jchart.test.jaxrs.common.dto.Book;
import com.jchart.test.jaxrs.common.dto.Books;

/**
 * This class implements CRUD operations for a Book
 * 
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@Service
public class BooksService {
	
	private Map<String, Book> _booksCache = new HashMap<>(); //simulate a database
	
	@PostConstruct
	public void initBooks() {
		System.out.println("BooksService: @PostConstruct");
		List<Book> books = new ArrayList<>();
		for (int i = 0;i<10;i++) {
			String isbn = i + "";
			Book book = createBook("Paul Russo","API Guide", isbn);
			books.add(book);
			_booksCache.put(isbn, book);
		}
	}

	/**
	 * @return newly created book or null if book already exists
	 */
	public Book createBook(String author, String title, String isbn) {
		Book retval = null;
		if (_booksCache.get(isbn) == null) {
			retval = new Book();
			retval.setAuthor(author);
			retval.setTitle(title);
			retval.setIsbn(isbn);
			_booksCache.put(isbn, retval);
		}
		return retval;
	}


	/**
	 * @return the updated book or null if book does not exist.
	 */
	public Book updateBook(Book book) {
		Book existingBook = _booksCache.get(book.getIsbn());
		if (existingBook != null) {
			existingBook.setAuthor(book.getAuthor());
			existingBook.setTitle(book.getTitle());
			existingBook.setIsbn(book.getIsbn());
		}
		return existingBook;
	}

	/**
	 * @return existing book or null if book does not exist
	 */
	public Book getBook(String isbn) {
		return _booksCache.get(isbn);
	}
	
	/**
	 * @return delete book or null if book does not exist
	 */
	public Book deleteBook(String isbn) {
		return _booksCache.remove(isbn);
	}
	
	public void deleteAllBooks() {
		_booksCache.clear();
	}

	public Books getBooks() {
		Books retval = new Books();
		List<Book> books = new ArrayList<Book>(_booksCache.values());
		retval.setBooks(books);
		return retval;
	}

	public void getUpdateAll(List<Book> books) {
		for (Book book : books) {
			_booksCache.put(book.getIsbn(), book);			
		}
	}

}
