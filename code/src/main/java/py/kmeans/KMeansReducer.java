package py.kmeans;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.mahout.math.VectorWritable;

public class KMeansReducer extends MapReduceBase implements Reducer<LongWritable, VectorWritable, LongWritable, VectorWritable>{

	@Override
	public void reduce(LongWritable arg0, Iterator<VectorWritable> arg1,
			OutputCollector<LongWritable, VectorWritable> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}
