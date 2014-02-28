package bovw.pipeline.cluster;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

public class PostProcessor {
	
	public static void main(String[] args){
		
		//TODO: rename the files and copy them into one folder
		
		//TODO: run a map/reduce job to store the content into Text instead of Sequence file
	}
	
	//
	public static void cleanCopyMerge(String clusters, String output) throws IOException{
		Path cpath = new Path(clusters);
		Path opath = new Path(output);
		Configuration conf = new Configuration();
		FileUtil.copyMerge(cpath.getFileSystem(conf), cpath, opath.getFileSystem(conf), opath, false, conf, "");
	}
	
	/**
	 * (1) hadoop fs -cp copy the results to a destined path, e.g. clusters/
	 * (2) mahout clusterdump to transform files into one text file to local position
	 */
	
}
