package it.haima.luceneTest0711;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest {

	@Test
	public void indexMake() throws Exception {
		Article article= new Article();
		article.setAuthor("haima");
		article.setContent("我是海马");
		article.setTitle("在学lunece");
		article.setDesc("我是海马,在学lunece,apache lucene");
		
		Document document= new Document();
		
		
		//Field(String name, String value, Store store, Index index)
		/**
		 * @param name The name of the field
		   * @param value The string to process
		   * @param store Whether <code>value</code> should be stored in the index
		   * @param index Whether the field should be indexed, and if so, if it should		ANALYZED  NOT_ANALYZED   ANALYZED_NO_NORMS  NOT_ANALYZED_NO_NORMS
		 */
		document.add(new Field("title",article.getTitle(),Store.YES,Field.Index.ANALYZED));
		document.add(new Field("content",article.getContent(),Store.YES,Field.Index.ANALYZED));
		document.add(new Field("desc",article.getDesc(),Store.YES,Field.Index.ANALYZED));
		document.add(new Field("author",article.getAuthor(),Store.YES,Field.Index.ANALYZED));
		
		Directory d= FSDirectory.open(new File("index"));
		Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_36, analyzer);
		
		IndexWriter writer = new IndexWriter(d, conf);
		writer.addDocument(document);
		writer.close();

	}
}

class Article {
	
	
	
	private String id;
	private String content;
	private String title;
	private String author;
	private String desc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}