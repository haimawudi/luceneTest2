package lucene_day2.a_query;

import java.io.IOException;

import lucene.utils.Configuration;
import lucene.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Test;

// 执行查询测试用例
public class LuceneTest {
	@Test
	// 布尔查询
	public void demo9() throws Exception{
		// 创建Query
		BooleanQuery booleanQuery = new BooleanQuery();// 布尔查询，将多个Query组合
		Query query1 = new MatchAllDocsQuery(); // 查询所有记录 20 
		Query query2 = NumericRangeQuery.newIntRange("id", 5, 15, true, false); // 范围查询 5-14
		
		// Occur.MUST 、 Occur.MUST_NOT、 Occur.SHOULD
		
		// 交集效果
//		booleanQuery.add(query1, Occur.MUST);
//		booleanQuery.add(query2, Occur.MUST);
		
		// 并集效果
//		booleanQuery.add(query1, Occur.SHOULD);
//		booleanQuery.add(query2, Occur.SHOULD);
		
		// 补集效果
		booleanQuery.add(query1 , Occur.MUST);
		booleanQuery.add(query2 , Occur.MUST_NOT); // 1-4 15-20
		
		// 执行Query
		executeQueryAndPringResult(booleanQuery);
	}
	
	@Test
	// 相似度
	public void demo8() throws Exception{
		// 查询标题有title
		Query query = new FuzzyQuery(new Term("title", "titel"), 0.1f); // minimumSimilarity最小相似度，值0-1 不能写1
		
		// 执行Query
		executeQueryAndPringResult(query);
	}
	
	
	@Test
	// 模糊查询		模糊查询的时候，得分是相同的，得分都是1
	public void demo7() throws Exception{
		
		
		
		// 创建Query
		Query query = new WildcardQuery(new Term("title", "*2*"));
		
		// 执行Query
		executeQueryAndPringResult(query);
	}
	
	@Test
	// 查询索引库 所有索引内容
	public void demo6() throws Exception{
		// 创建Query
		Query query = new MatchAllDocsQuery();// 无需任何条件，查询所有数据
		
		// 执行Query
		executeQueryAndPringResult(query);
	}
	
	@Test
	// 查询 id 是 5-15 索引数据
	public void demo5() throws Exception{
		// 创建Query
		//Query query = new TermRangeQuery("id", "15", "5", true, true);
		Query query = NumericRangeQuery.newIntRange("id", 5, 15, true, false); // 包含5 ，不包含15
		
		// 执行Query
		executeQueryAndPringResult(query);
	}
	
	@Test
	// 批量插入20条索引数据
	public void demo4() throws Exception{
		for(int i=1 ;i<= 20 ;i++){
			// 将数据内容转换 Document对象
			Document document = new Document();
			//document.add(new Field("id", i+"", Store.YES, Index.NOT_ANALYZED)); // 字符串类型索引
			NumericField numericField = new NumericField("id", Store.YES, true); // 数字类型索引不需要分词的
			numericField.setIntValue(i);
			document.add(numericField);
			
			document.add(new Field("title","title"+i, Store.YES, Index.ANALYZED));
			document.add(new Field("content","content"+i, Store.YES, Index.ANALYZED));
			
			// 通过IndexWriter 写索引
			IndexWriter indexWriter = LuceneUtils.getIndexWriter();
			if(i==11){
				document.setBoost(100f);
			}
			indexWriter.addDocument(document);
		}
	}
	
	@Test
	// 解析目标内容，查询索引库， 先分词，查询对应词条
	public void demo3() throws Exception{
		// 创建Query
		String queryStr = "学习";
		QueryParser queryParser = new QueryParser(Configuration.getVersion(), "title", Configuration.getAnalyzer());
		Query query = queryParser.parse(queryStr);
		
		// 执行Query
		executeQueryAndPringResult(query);
	}
	
	@Test
	// 执行词条的查询
	public void demo2() throws Exception{
		// 创建Query
		Term term = new Term("title" ,"lucene");
		TermQuery termQuery = new TermQuery(term);
		
		// 执行Query
		executeQueryAndPringResult(termQuery);
	}
	
	// 执行查询 ，打印结果
	private void executeQueryAndPringResult(Query query) throws Exception {
		// 获得 IndexSearcher对象
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		// 执行Query
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("结果："+ topDocs.totalHits);
		// 遍历每一个结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("得分：" + scoreDoc.score);
			// 获得Document对象
			int docID = scoreDoc.doc;
			Document document = indexSearcher.doc(docID);
			
			System.out.println("id: " +document.get("id"));
			System.out.println("title :" + document.get("title") );
			System.out.println("content:" + document.get("content"));
		}
	}

	@Test
	// 创建索引库
	public void demo1() throws CorruptIndexException, IOException {
		// 原始数据
		Article article = new Article();
		article.setId(10);
		article.setTitle("学习lucene");
		article.setContent("lucene是一个全文检索技术，是apache提供的");
		
		// 将数据内容转换 Document对象
		Document document = new Document();
		document.add(new Field("id", article.getId().toString(), Store.YES, Index.NOT_ANALYZED));
		document.add(new Field("title",article.getTitle(), Store.YES, Index.ANALYZED));
		document.add(new Field("content",article.getContent(), Store.YES, Index.ANALYZED));
		
		// 通过IndexWriter 写索引
		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		indexWriter.addDocument(document);
	}
}
