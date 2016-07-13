package lucene.c_curd;

import java.util.ArrayList;
import java.util.List;

import lucene.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;

// 图书管理业务层
public class BookService {
	// 添加图书
	public void addBook(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		// 将数据保存到数据库
		BookDAO bookDAO = new BookDAO();
		bookDAO.save(book);
		// 保存到数据库同时建立索引
		BookIndexDAO indexDAO = new BookIndexDAO();
		indexDAO.createIndex(book);

		transaction.commit();
	}

	// 修改图书
	public void updateBook(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		// 修改数据
		BookDAO bookDAO = new BookDAO();
		bookDAO.update(book);
		// 修改索引
		BookIndexDAO indexDAO = new BookIndexDAO();
		indexDAO.updateIndex(book);
		transaction.commit();
	}

	// 删除图书
	public void deleteBook(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		// 删除图书
		BookDAO bookDAO = new BookDAO();
		bookDAO.delete(book);
		// 删除索引库
		BookIndexDAO indexDAO = new BookIndexDAO();
		indexDAO.deleteIndex(book);
		transaction.commit();
	}

	// 根据id 查询图书
	public Book findById(Integer id) {
		Session session = HibernateUtils.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		BookDAO bookDAO = new BookDAO();
		Book book = bookDAO.findById(id);
		transaction.commit();
		return book;
	}

	// 根据名称查询
	public List<Book> findByNameLike(String name) {
		// 先查询索引库，获得id
		BookIndexDAO indexDAO = new BookIndexDAO();
		List<Integer> ids = indexDAO.findIdByName(name);
		// 再根据id查询数据库
		List<Book> books = new ArrayList<Book>();
		Session session = HibernateUtils.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		BookDAO bookDAO = new BookDAO();
		for (Integer id : ids) {
			Book book = bookDAO.findById(id);
			books.add(book);
		}

		transaction.commit();
		return books;
	}
}
