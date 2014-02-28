package bovw.pipeline.index;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import bovw.pipeline.feature.SIFTExtractor;

/**
 * @author Yang Peng
 * @library: different libraries need to be loaded from other programs
 */

public class InvertedIndexing {
	
	public static void main(String[] args) throws SolrServerException, IOException{
		//testSolr();
		index("bw/part-00000");
		//String q = transform("/home/hadoop/bovw/code/resources/oxbuild_images/" + "all_souls_000001.jpg");
		String q = transform("/home/hadoop/Desktop/test.png");
		//String q = "1";
		query(q);
		
	}
	
	public static void index(String filename) throws IOException, SolrServerException{//indexing existing index matrix
		
		String urlString = "http://localhost:8983/solr";
		HttpSolrServer server = new HttpSolrServer(urlString);
		server.deleteByQuery( "*:*" );//clean the data in server
		
		//read index matrix from file
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		String line;
		while((line = br.readLine()) != null){
			SolrInputDocument doc = getDocument(line);
			docs.add(doc);
		}
		br.close();
		
		System.out.println(docs.toArray()[0]);
		System.out.println(docs.toArray()[1]);
		
		server.add(docs);
	    server.commit();
	    System.out.println("indexing is done");
	}
	
	//for each line, construct an document
	public static SolrInputDocument getDocument(String line){
		SolrInputDocument doc = new SolrInputDocument();
		// add the id field
		String name = line.split("\t")[0];
		doc.addField("id", name);
		// add the cluster fields
		// index a numeric vector as a string
		String s = line.split("\t")[2];
		doc.addField("text", s);
		//String[] array = s.split(" ");
		//for(String c : array){
		//	doc.addField("*_is", Integer.parseInt(c));
		//}
		return doc;
	}
	
	
	public static String transform(String image) throws IOException{//transform an image into a Solr document or a field
		int cnum = 900;
		int fd = 128;
		String[] features = SIFTExtractor.extract(image);
		System.out.println("query: " + image);
		double[][] clusters = FrequencyExtractor.FEMap.readClusters("/home/hadoop/Desktop/clusters.txt");
		boolean[] marks = new boolean[cnum];
		
		for(int i = 0; i < features.length; i++){
			double[] feature = new double[fd];
			String[] args = features[i].split(" ");
			for (int j = 0; j < fd; j++)
				feature[j] = Double.parseDouble(args[j + 10]);
			int index = FrequencyExtractor.FEMap.findBestCluster(feature, clusters);
			//if(index != -1)
			marks[index] = true;
			//if(index == -1){
			//	System.out.println(feature);
			//}
		}
		
		String result = "";
		for(int i = 0; i < cnum; i++){
			if(marks[i]){
				if(result.length() == 0) result += i;
				else result += " " + i;
			}	
		}
		
		return result;
	}
	
	public static void query(String s) throws SolrServerException{//query and output results
		//query a numeric vector as a string
		String urlString = "http://localhost:8983/solr";
		HttpSolrServer server = new HttpSolrServer(urlString);
		// search
	    SolrQuery query = new SolrQuery();
	    query.setQuery(s);
	    //query.setQuery("*_is:" + s);
	    query.setFields("id");
	    QueryResponse qresponse = server.query(query);
	    // print results
	    SolrDocumentList list = qresponse.getResults();
	    for(int i = 0; i < list.size(); i++){
	    	System.out.println(list.get(i));
	    }
	    System.out.println("query is done");
	}
	
	
	public static void testSolr() throws SolrServerException, IOException{
		
		// initialize the connection to server
		String urlString = "http://localhost:8983/solr";
		HttpSolrServer server = new HttpSolrServer(urlString);
		//TODO: ConcurrentUpdateSolrServer
		//server.setParser(new XMLResponseParser());
		server.deleteByQuery( "*:*" );// CAUTION: deletes everything!
		
		// indexing
		// create a new document
	    SolrInputDocument doc0 = new SolrInputDocument();
	    doc0.addField("id", "552199");
	    doc0.addField("name", "Gouda cheese wheel");
	    doc0.addField("price", "49.99");
	    
		SolrInputDocument doc1 = new SolrInputDocument();
	    doc1.addField( "id", "id1", 1.0f );
	    doc1.addField( "name", "doc1", 1.0f );
	    doc1.addField( "price", 10 );
	    doc1.addField("*_i", "10");
		
	    SolrInputDocument doc2 = new SolrInputDocument();
	    doc2.addField( "id", "id2", 1.0f );
	    doc2.addField( "name", "doc2", 1.0f );
	    doc2.addField( "price", 20 );
	    
	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	    docs.add(doc0);
	    docs.add(doc1);
	    docs.add(doc2);
	    
	    server.add(docs);
	    server.commit();	    
		
	    // search
	    SolrQuery parameters = new SolrQuery();
	    String mQueryString = "552199";
	    parameters.set("q", mQueryString);
	    QueryResponse qresponse = server.query(parameters);
	    // print results
	    SolrDocumentList list = qresponse.getResults();
	    for(int i = 0; i < list.size(); i++){
	    	System.out.println(list.get(i));
	    }
	}
	
}


