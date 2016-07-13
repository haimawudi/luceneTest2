package lucene.c_curd;

import java.util.ArrayList;
import java.util.List;

import lucene.utils.Configuration;
import lucene.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * 对索引库 完成Book对象 增删改查
 * 
 * @author seawind
 * 
 */
public class BookIndexDAO {
	// 创建索引
	public void createIndex(Book book) {
		// 转换book对象为Document对象
		Document document = new Document();
		// 通过id直接找数据库，id不需要索引
		document.add(new Field("id", book.getId().toString(), Store.YES,
				Index.NOT_ANALYZED));
		document.add(new Field("name", book.getName(), Store.NO, Index.ANALYZED));
		document.add(new Field("info", book.getInfo(), Store.NO, Index.ANALYZED));

		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		try {
			indexWriter.addDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("索引创建失败！");
		}
	}

	// 删除索引库
	public void deleteIndex(Book book) {
		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		Term term = new Term("id", book.getId().toString());
		// 根据id索引删除
		try {
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("索引删除失败！");
		}

	}

	// 修改索引
	public void updateIndex(Book book) {
		// 转换book对象为Document对象
		Document document = new Document();
		// 通过id直接找数据库，id不需要索引
		document.add(new Field("id", book.getId().toString(), Store.YES,
				Index.NOT_ANALYZED));
		document.add(new Field("name", book.getName(), Store.NO, Index.ANALYZED));
		document.add(new Field("info", book.getInfo(), Store.NO, Index.ANALYZED));

		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		Term term = new Term("id", book.getId().toString());
		try {
			// indexWriter.updateDocument(term, document); // 性能比较差
			indexWriter.deleteDocuments(term);
			indexWriter.addDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("索引修改失败！");
		}
	}

	// 根据name 查询id
	public List<Integer> findIdByName(String name) {
		List<Integer> ids = new ArrayList<Integer>();

		QueryParser queryParser = new QueryParser(Configuration.getVersion(),
				"name", Configuration.getAnalyzer());
		try {
			Query query = queryParser.parse(name);
			// 执行查询
			IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
			TopDocs topDocs = indexSearcher.search(query, 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < scoreDocs.length; i++) {
				Document document = indexSearcher.doc(scoreDocs[i].doc);
				String id = document.get("id");
				ids.add(Integer.parseInt(id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ids;
	}
}
