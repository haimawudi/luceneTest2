package lucene.c_curd;

import lucene.utils.HibernateUtils;

import org.hibernate.Session;

// 图书管理数据层
public class BookDAO {

	public void save(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		session.save(book);
	}

	public void update(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		session.update(book);
	}

	public void delete(Book book) {
		Session session = HibernateUtils.getCurrentSession();
		session.delete(book);
	}

	public Book findById(Integer id) {
		Session session = HibernateUtils.getCurrentSession();
		return (Book) session.get(Book.class, id);
	}

}
