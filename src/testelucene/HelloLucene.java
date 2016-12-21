package testelucene;

import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class HelloLucene {

    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try (IndexWriter w = new IndexWriter(index, config)) {
            addDoc(w, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis eu hendrerit erat. Duis pretium ut mauris eget finibus. Aenean interdum semper varius. Nam in gravida felis. Vestibulum venenatis tristique mi, sed viverra metus euismod mollis. Phasellus sed rutrum nisi, sit amet vestibulum massa. Pellentesque eleifend lacus id erat imperdiet aliquam. Suspendisse tincidunt arcu eget gravida elementum. Nulla tempus luctus rutrum.", "193398817");
            addDoc(w, "Sed mi metus, maximus id vestibulum sit amet, imperdiet quis elit. Vivamus vitae lacus nec dolor lobortis eleifend. Nunc iaculis turpis sit amet libero mollis, a aliquet mi egestas. Curabitur tincidunt dolor vel elit cursus ullamcorper. Phasellus ac ligula est. Aliquam tincidunt non massa vel interdum. Cras non accumsan metus, maximus commodo ante. In vitae condimentum metus.", "55320055Z");
            addDoc(w, "Phasellus vel purus a nunc ornare pulvinar nec id quam. Duis sed blandit sem. Donec aliquam orci in augue commodo condimentum. In porttitor, velit sit amet mattis maximus, risus nunc pretium tortor, et fringilla nulla arcu sed est. Morbi feugiat euismod urna vel venenatis. Quisque tristique eu felis eget fermentum. Suspendisse varius lacus eros, sed sodales leo dictum sed. Donec eleifend laoreet erat quis posuere. Nullam vel consequat eros.", "55063554A");
            addDoc(w, "Morbi in sem eu velit dictum venenatis ac ut sem. Aliquam erat volutpat. Nullam accumsan cursus erat, ut condimentum metus blandit at. Duis sed nibh sed nunc interdum elementum. Nullam tempus purus felis, ut eleifend ligula iaculis ut. Nunc maximus placerat neque ac mollis. Nam nec magna ac ante viverra accumsan ac et ante.", "9900333X");
            addDoc(w, "Vivamus quis mauris eu neque sodales sagittis vitae sed tellus. Curabitur tempus maximus velit at ullamcorper. Sed elit mauris, lobortis sit amet commodo sed, tristique vitae felis. Mauris non magna vehicula, volutpat sapien vel, venenatis orci. Nullam placerat, dui eget vehicula semper, sem arcu vehicula sapien, a cursus massa massa sit amet arcu. Sed fermentum neque varius lacus porttitor pulvinar. Donec tristique enim ipsum, sit amet gravida purus cursus lacinia. Mauris quis venenatis enim, quis tempor sapien. Etiam in facilisis metus. Pellentesque molestie facilisis ligula. Proin pulvinar ligula nec ipsum volutpat, eget consequat magna varius. Praesent non elit tellus. Nunc id fermentum diam. Ut in commodo nisi. Quisque lacinia risus a libero imperdiet auctor.", "9900334X");
        }

        // 2. query
        String querystr = args.length > 0 ? args[0] : "Lorem";

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = null;
        try {
            q = new QueryParser("title", analyzer).parse(querystr);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
        }

        // 3. search
        int hitsPerPage = 10;
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, new ScoreDoc(hitsPerPage, (float) 1.0));
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
            }
            // reader can only be closed when there
            // is no need to access the documents any more.
        }
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}