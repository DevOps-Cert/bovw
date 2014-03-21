package bovw.pipeline.feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import bovw.pipeline.index.FrequencyExtractor;

public class FeatureExtractor {
	
	public static String img_folder = "/home/hadoop/bovw/code/resources/oxbuild_images";
	public static String feature_folder = "features";
	
	public static void main(String[] args) throws IOException{
		// get the image names for future feature extraction
		//getNames(img_folder, "data/inames.txt");
		// run Map-Reduce job for Feature Extraction
		runMapReduce("data/inames.txt", "features");
	}
	
	public static void getNames(String img_folder, String file) throws FileNotFoundException{
		File folder = new File(img_folder);
		String[] list = folder.list();
		PrintWriter pw = new PrintWriter(file);
		for(int i = 0; i < list.length; i++){
			pw.println(list[i]);
		}
		pw.close();
	}

	public static void runMapReduce(String infile, String outfile) throws IOException{
		

		JobConf conf = new JobConf(FrequencyExtractor.class);
		conf.setJobName("FeatureExtractor");
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(FEMap.class);
		//conf.setReducerClass(FEReduce.class);
		//conf.setNumReduceTasks(1);
		
		conf.setInputFormat(TextInputFormat.class);
	    conf.setOutputFormat(TextOutputFormat.class);
	    
		FileInputFormat.setInputPaths(conf, new Path(infile));
		FileOutputFormat.setOutputPath(conf, new Path(outfile));
		
		JobClient.runJob(conf);
	}
	
	public static class FEMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		
		@Override
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			
			// get the image path
			String file = img_folder + "/" + value.toString();
			// extract the SIFT features
			String[] features = SIFTExtractor.extract(file);
			// store them into a file
			store(features, feature_folder + "/" + value.toString() + ".txt");
			
			System.out.println(file + " processed");
		}
		
		public void store(String[] features, String filename){
			try {
				Configuration conf = new Configuration();
				FileSystem fs;
				fs = FileSystem.get(conf);
				Path outFile = new Path(filename);
				FSDataOutputStream out = fs.create(outFile);
				PrintWriter pw = new PrintWriter(out.getWrappedStream());
				for(String feature : features){
					pw.println(feature + "\n");
				}
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
}
