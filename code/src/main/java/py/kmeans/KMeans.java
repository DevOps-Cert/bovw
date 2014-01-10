package py.kmeans;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.HashPartitioner;
import org.apache.mahout.math.VectorWritable;

public class KMeans {
	
	
	public static void main(String[] args){
		// setup an experiment with no smaller vectors
	}
	
	public static void run(String[] args) throws IOException{
		JobConf conf = new JobConf(KMeans.class);
		conf.setJobName("kmeans");
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(VectorWritable.class);
		
		conf.setMapperClass(KMeansMapper.class);
		conf.setCombinerClass(KMeansReducer.class);
		conf.setPartitionerClass(HashPartitioner.class);
		conf.setReducerClass(KMeansReducer.class);
			
		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);
			
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
			
		JobClient.runJob(conf);
	}
}
