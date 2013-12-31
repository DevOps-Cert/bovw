package ufl.cise.py;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;


//preprocess the data feature vectors
public class Preprocessor {
	
	static int size = 128;
	static int cNum = 1000;

	
	public static void main(String[] args) throws IOException{
		writePointsToFile("data/features.txt");
		//writeRandomSeeds("data/cluster");
	}
	
	
	public static void writePointsToFile(String filename ) throws IOException{
		Scanner scan = new Scanner(new File(filename));
		
		// configurations and system variables
		Path path = new Path("data/input/fs1.seq");
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		// file writer and vector writer
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, LongWritable.class, VectorWritable.class);
		long recNum = 0;
		VectorWritable vw = new VectorWritable();
		
		
		while(scan.hasNext()){
			String s = scan.nextLine();
			// read each line to get feature
			double[] feature = getPoints(s.split(" "));
			// transform data points to vectors
			//Vector vec = new RandomAccessSparseVector(feature.length);
			Vector vec = new DenseVector(feature.length);
			vec.assign(feature);
			// write vectors to sequence files
			vw.set(vec);
			writer.append(new LongWritable(recNum++), vw);
			if(recNum % 1000 ==0){
				System.out.println("one thosuand vectors written into sequence files");
			}
		}
		writer.close();		
	}
	
	public static double[] getPoints(String[] args){
		double[] points = new double[size];
		for (int i = 0; i < size; i++)
			points[i] = Double.parseDouble(args[i]);
		return points;
	}
	
	// write random clusters
	public static void writeRandomSeeds(String filename) throws IOException{
		Path input = new Path("data/input/fs.seq");
		Configuration conf = new Configuration();
		Path output = new Path(filename);
		RandomSeedGenerator.buildRandom(conf, input, output, cNum, new EuclideanDistanceMeasure());
		System.out.println("random generation is done");
	}
}
