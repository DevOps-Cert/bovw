package bovw.pipeline.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;

public class Search {
	
	public static String feature_folder = "/home/hadoop/bovw/code/resources/features_new";
	
	public static void main(String[] args) throws IOException, SolrServerException{
		
		// generate frequency vectors or TF-IDF vectors from the features and clusters
		//FrequencyExtractor.run("data/fnames.txt", "bw");
		// the index process
		//InvertedIndexing.index("bw/part-00000");
		// the whole search process
		//search("img/test.png");
		
		//getImageFeatures(feature_folder + "/all_souls_000013.jpg.txt", 136.5, 34.1, 648.5, 955.7);
		evaluate("gt");
	}
	
	public static void search(String image) throws IOException, SolrServerException{

		// get query from a set of images and measure the mean F1 score
		String query = InvertedIndexing.createQueryDoc(image);
		// run query
		InvertedIndexing.query(query);
	}
	
	public static void evaluate(String folder) throws IOException{
		//TODO: compare the word occurrence and TF-IDF
		// read all the files with "query"
		File fd = new File(folder);
		String[] files = fd.list();
		
		for(String file : files){
			if(file.contains("query")){
				BufferedReader reader = new BufferedReader(new FileReader(new File(folder + "/" + file)));
				String line = reader.readLine();
				reader.close();
				String[] array = line.split(" ");
				String query = array[0].substring("oxc1_".length()) + ".jpg.txt" ;
				double lowX = Double.parseDouble(array[1]);
				double lowY = Double.parseDouble(array[2]);
				double highX = Double.parseDouble(array[3]);
				double highY = Double.parseDouble(array[4]);
				//System.out.println(line);
				//System.out.println(query + " " + lowX + " " + lowY + " " + highX + " " + highY);
				String[] features = getImageFeatures(query, lowX, lowY, highX, highY);
				
			}
		}
	}
	
	public static String[] getImageFeatures(String filename, double lowX, double lowY, double highX, double highY) throws IOException{
		// currently use local I/O, it is very easy to transfer to HDFS I/O
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		ArrayList<String> list = new ArrayList<String>();
		String line;
		while((line = reader.readLine()) != null){
			String cs = line.split("\\(")[1].split("\\)")[0];
			//System.out.println(line);
			double x = Double.parseDouble(cs.split(", ")[0]);
			double y = Double.parseDouble(cs.split(", ")[1]);
			//System.out.println(cs + " " + x + " " + y);
			if(lowX <= x && x <= highX && lowY <= y && y <= highY){
				list.add(line);
				//System.out.println(line);
			}
		}
		reader.close();
		return list.toArray(new String[list.size()]);
	}
	
}
