package com.jchart.test.jaxrs.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@XmlRootElement
public class Book {
	
	private String author;
	private String isbn;
	private String title;
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Book [author=" + author + ", isbn=" + isbn + ", title=" + title
				+ "]";
	}

}
