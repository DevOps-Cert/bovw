package bovw.pipeline.index;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

import bovw.pipeline.util.HadoopUtil;

public class Search {
	
	public static String featureFolder = "/home/hadoop/bovw/code/resources/features_new/";
	public static String fnames = "data/fnames.txt";
	
	public static String clusterFile = "data/clusters-30-60.txt";
	//public static String clusterFile = "data/clusters.txt";
	public static int clusterNum = 1800;
	//public static int clusterNum = 900;
	public static String termFile = "data/tf-1800.txt";
	//public static String termFile = "data/tf.txt";
	public static int featureSize = 128;
	
	
	public static void main(String[] args) throws IOException, SolrServerException{
		// transform files of features into files of cluster ids
		//getWordFrequence(fnames, "bw", termFile);
		// index the cluster ids files
		//InvertedIndexing.index(termFile);
		// evaluate the search system pipeline
		Evaluate.evaluate("gt");
	}
	
	public static void getWordFrequence(String fnames,String bw, String termFile) throws IOException{
		HadoopUtil.delete(bw);
		FrequencyExtractor.run(fnames, bw);
		HadoopUtil.copyMerge(bw, termFile);
	}
	
	
}
