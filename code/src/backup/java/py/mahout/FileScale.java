package py.mahout;



import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.math.VectorWritable;

public class FileScale {
	
	private static int size = 2269865;
	
	public static void main(String[] args) throws IOException{
		// count("/home/yp/Desktop/fs.seq");
		scale(0.5, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs05.seq");
		//scale(0.1, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs01.seq");
		//scale(0.05, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs005.seq");
		//scale(0.01, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs001.seq");
		//scale(0.005, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs0005.seq");
		//scale(0.001, "/home/yp/Desktop/fs.seq", "/home/yp/Desktop/fs0001.seq");
	}
	
	public static void scale(double scale, String filename, String outfile) throws IOException{
		 Path path = new Path(filename);
         Configuration conf = new Configuration();
         FileSystem fs = FileSystem.get(conf);
         
         SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
         Path out = new Path(outfile); 
         SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, out, LongWritable.class, VectorWritable.class);
        
         LongWritable key = new LongWritable();
         VectorWritable val = new VectorWritable();
         
         for(int i = 0; i < size * scale; i++){
        	 reader.next(key, val);
        	 writer.append(key, val);
         }
         
         reader.close();
         writer.close();
         
	}
	
	public static void count(String filename) throws IOException{
		 Path path = new Path(filename);
         Configuration conf = new Configuration();
         FileSystem fs = FileSystem.get(conf);
         
         SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
         int num = 0;
         LongWritable key = new LongWritable();
         VectorWritable val = new VectorWritable();
         
         while(reader.next(key, val)){
        	 num++;
         }
         reader.close();
         System.out.println(num);
	}
}
