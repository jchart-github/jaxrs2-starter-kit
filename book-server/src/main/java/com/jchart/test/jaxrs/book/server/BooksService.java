package com.jchart.test.jaxrs.book.server;

import org.springframework.stereotype.Service;

import com.jchart.test.jaxrs.common.dto.Book;

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@Service
public class BooksService {

	public Book getBook(String id) {
		Book retval = new Book();
		retval.setAuthor("Paul Russo");
		retval.setTitle("API Guide");
		retval.setIsbn(id);
		return retval;
	}

}
