package py.kmeans;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class KMeansReducer extends MapReduceBase implements Reducer<LongWritable, VectorWritable, LongWritable, VectorWritable>{

	@Override
	public void reduce(LongWritable arg0, Iterator<VectorWritable> arg1,
			OutputCollector<LongWritable, VectorWritable> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		 int num = 0;
		 Vector center = new DenseVector(KMeans.d);
		 while (arg1.hasNext()) {
			 //sum += values.next().get();
			 num++;
			 center = center.plus(arg1.next().get());
		 }
		 arg2.collect(arg0, new VectorWritable(center));
	}
	

}
