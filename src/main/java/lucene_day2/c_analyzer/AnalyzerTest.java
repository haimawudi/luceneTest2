package lucene_day2.c_analyzer;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerTest {
	
	@Test
	// 测试 Lucene分词器
	public void demo1() throws Exception{
		String msg = "今天天气不错，挺风和日丽的";
		
		// 标准分词器
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		testAnalyzer(analyzer, msg);
		
		// ChineseAnalyzer和CJKAnalyzer
		Analyzer analyzer2 = new ChineseAnalyzer();
		testAnalyzer(analyzer2, msg);
		
		Analyzer analyzer3 = new CJKAnalyzer(Version.LUCENE_36);
		testAnalyzer(analyzer3, msg);
		
		// 使用IK分词器
		Analyzer analyzer4 = new IKAnalyzer();
		testAnalyzer(analyzer4, msg);
		
		String text = "传智播客-聚集众多java培训,net培训,php培训,网页培训大师|先...IT培训的龙头老大,口碑最好的java培训、.net培训、php培训、网页培训机构,问天下java培训";
		testAnalyzer(analyzer4, text);
		
	}
	
	// 测试分词器 切词效果
	private void testAnalyzer(Analyzer analyzer, String text) throws Exception {
		  System.out.println("分词器：" + analyzer.getClass().getSimpleName());
		  TokenStream tokenStream = analyzer.tokenStream("content", new  StringReader(text));
		   tokenStream.addAttribute(TermAttribute.class);
		   while (tokenStream.incrementToken()) {
		    TermAttribute termAttribute = tokenStream.getAttribute(TermAttribute.class);
		    System.out.println(termAttribute.term());
		   }
		   System.out.println();
		}

}
