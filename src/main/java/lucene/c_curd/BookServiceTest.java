package lucene.c_curd;

import java.util.List;

import org.junit.Test;

public class BookServiceTest {
	@Test
	public void testAdd() {
		Book book = new Book();
		book.setName("全文检索入门");
		book.setPrice(30d);
		book.setInfo("最好的入门级图书");

		BookService bookService = new BookService();
		bookService.addBook(book);
	}

	@Test
	public void testUpdate() {
		Book book = new Book();
		book.setId(1);
		book.setName("lucene入门");
		book.setPrice(50d);
		book.setInfo("最好的入门级图书");

		BookService bookService = new BookService();
		bookService.updateBook(book);
	}

	@Test
	public void testFindById() {
		BookService bookService = new BookService();
		Book book = bookService.findById(1);
		System.out.println(book);
	}

	@Test
	public void testDeleteBook() {
		Book book = new Book();
		book.setId(1);

		BookService bookService = new BookService();
		bookService.deleteBook(book);

	}

	@Test
	public void testFindNameLike() {
		BookService bookService = new BookService();
		List<Book> books = bookService.findByNameLike("spring");

		System.out.println(books);
	}
}
