package py.kmeans;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.mahout.math.VectorWritable;

public class KMeansMapper extends MapReduceBase implements Mapper<LongWritable, VectorWritable, LongWritable, VectorWritable>{

	@Override
	public void map(LongWritable arg0, VectorWritable arg1,
			OutputCollector<LongWritable, VectorWritable> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}
