package py.mahout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

public class TopDownPipeline {
	
	private static String dm = "org.apache.mahout.common.distance.CosineDistanceMeasure";
	private static double delta = 0.001;
	private static int x = 100;
	
	private static int topK = 0;
	private static int botK = 0;
	
	public static void main(String[] args) throws IOException{
		run(args);
	}
	
	public static void run(String[] args) throws IOException{
		
		topK = Integer.parseInt(args[0]);
		botK = Integer.parseInt(args[1]);
		
		String input = args[2];
		String cls = args[3];
		
		String top = args[4];
		String mid = args[5];
		String bot = args[6];
		
		String res = args[7];
		
		topLevel(input, cls, top, topK);
		//midLevel(top, mid);
		//botLevel(mid, bot, botK, res);
	}
	
	public static void topLevel(String input, String cls, String top, int topK) throws IOException{
		kmeans(input, cls, top, topK, delta, x);
	}
	
	public static void midLevel(String top, String mid) throws IOException{
		String command = "mahout clusterpp -i " + top + " -o " + mid;
		System.out.println(command);
		run(command);
	}
	
	public static void botLevel(String mid, String bot, int botK, String res) throws IOException{
		File folder = new File(mid);
		File[] list = folder.listFiles();
		//TODO: rewrite here to add hadoop fs results
		for(int i = 0; i < list.length; i++){
			// System.out.println(list[i].getName());
			File file = list[i];
			kmeans(file.getAbsolutePath(), bot + "/" + file.getName() + "/clusters-0", bot + "/" + file.getName(), botK, delta, x);
			saveResults(bot + "/" + file.getName() + "/clusters-*-final", res + "/" + file.getName());
		}
	}
	
	public static void kmeans(String input, String clusters, String output, int k, double cd, int x) throws IOException{
		String command = "mahout kmeans -i " + input + " -c " + clusters + " -o " + output + " -k " + k + " "
				+ "-dm " + dm + " -cd " + cd + " -x " + x + " -ow -cl" ;
		System.out.println(command);
		run(command);
	}
	
	public static void saveResults(String cs, String res) throws IOException{
		File src = new File(cs);
		File dst = new File(res);
		FileUtils.copyDirectory(src, dst);
		//TODO: probably need to rewritten by using hadoop fs commands
	}
	
	public static void run(String command) throws IOException{
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec(command);
		// output the results
		String line;
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()) );
	    while ((line = in.readLine()) != null) {
	    	System.out.println(line);
	    }
	    in.close();
	}
}
