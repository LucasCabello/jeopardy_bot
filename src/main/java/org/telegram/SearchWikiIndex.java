package org.telegram;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchWikiIndex {

    public enum FieldNames {
        URL, TITLE, ABSTRACT, RANK
    }

    public static final HashMap<String,Float> BOOSTS = new HashMap<String,Float>();
    static {
        BOOSTS.put(FieldNames.ABSTRACT.name(), 1f);
        BOOSTS.put(FieldNames.TITLE.name(), 5f);
    }

    public static String startSearchApp(String line) throws IOException {
        String inDirectory = System.getenv("DUCKS_DIR");
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(inDirectory)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_48);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                Version.LUCENE_48,
                new String[] {FieldNames.TITLE.name(), FieldNames.ABSTRACT.name()},
                analyzer, BOOSTS);
        if(line!=null){
            line = line.trim();
            if(!line.isEmpty()){
                try{
                    Query query = queryParser.parse(line);

                    TopDocs results = searcher.search(query, 1);
                    ScoreDoc[] hits = results.scoreDocs;

                    System.out.println("Running query: "+line);
                    System.out.println("Parsed query: "+query);
                    System.out.println("Matching documents: "+results.totalHits);
                    System.out.println("Showing top result");

                    Document doc = searcher.doc(hits[0].doc);
                    String title = doc.get(FieldNames.TITLE.name());
                    String abst = doc.get(FieldNames.ABSTRACT.name());
                    String url = doc.get(FieldNames.URL.name());
                    String rank = doc.get(FieldNames.RANK.name());
                    System.out.println(rank+"\t"+"\t"+url+"\t"+title+"\t"+abst);
                    abst = abst.replaceAll("\\s*\\([^\\)]*\\)\\s*", "");
                    SummaryTool stool = new SummaryTool();
                    String sent = stool.bestSentence(abst, stool.sentencesRank(abst));
                    sent = sent.replaceAll("[0-9]+px", "");
                    sent = sent.replaceAll("(?i)"+title, "***");
                    sent = sent.replaceAll("(?i)"+line, "***");
                    return sent;

                } catch(Exception e){
                    System.err.println("Error with query '"+line+"'");
                    e.printStackTrace();
                }
            }
        }
        return "No hay resultados, intente con otro concepto.";
    }
}