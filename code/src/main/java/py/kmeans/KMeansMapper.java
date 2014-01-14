package py.kmeans;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class KMeansMapper extends MapReduceBase implements Mapper<LongWritable, VectorWritable, LongWritable, VectorWritable>{

	@Override
	public void map(LongWritable arg0, VectorWritable arg1,
			OutputCollector<LongWritable, VectorWritable> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		
		// load centers to memory
		double[][] centers = getCenters(KMeans.centers);
		
		long id = -1;
		double ds = 0;
		double[] vec = getVector(arg1.get());
		
		for(int i = 0; i < centers.length; i++) {
			if (id == -1){
				id = i;
				ds = calculateDistance(vec, centers[i]);
			}
			else{
				double tmp = calculateDistance(vec, centers[i]);
				if (tmp < ds){
					ds = tmp;
					id = i;
				}
			}
		}
		arg2.collect(new LongWritable(id), new VectorWritable(new DenseVector(centers[(int) id])));
		
	}
	
	public double[][] getCenters(String filename) throws IOException{
		double [][] centers = new double[KMeans.k][KMeans.d];
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(filename);
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
		Text key = new Text();
	    ClusterWritable val = new ClusterWritable();
	    int i = 0;
	    while(reader.next(key, val)){
	    	//System.out.println(key);
	    	//System.out.println(val.getValue().getCenter());
	    	centers[i] = getVector(val.getValue().getCenter());
	    	i++;
	    }
    	
		return centers;
	}
	
	public double[] getVector(Vector vec){
		double[] vd = new double[KMeans.d];
		for(int i = 0; i < KMeans.d; i++)
			vd[i] = vec.get(i);
		return vd;
	}
	
	public double calculateDistance(double[] a, double[] b){
		double ds = 0;
		double t1 = 0;
		double t2 = 0;
		double t3 = 0;
		for(int i = 0; i < a.length; i++){
			t1 += a[i] * a[i];
			t2 += b[i] * b[i];
			t3 += a[i] * b[i];
		}
		ds = Math.abs(t3) / (Math.sqrt(t1) * Math.sqrt(t2));
		return ds;
	} 

}
