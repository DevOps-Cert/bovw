package py.mahout;

import java.io.IOException;

public class BotThread implements Runnable{
	
	String cmd;
	int num;
	
	String cmd0;
	String cmd1;
	String cmd2;
	
	public BotThread(String cmd, int num){
		this.cmd = cmd;
		this.num = num;
	}
	
	public BotThread(String cmd0, String cmd1, String cmd2, int num){
		this.cmd0 = cmd0;
		this.cmd1 = cmd1;
		this.cmd2 = cmd2;
		this.num = num;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*Runtime rt = Runtime.getRuntime();
		Process p;
		try {
			p = rt.exec(cmd);
			p.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */

		try {
			TopDownPipeline.log(cmd0);
			TopDownPipeline.run(cmd0);
			TopDownPipeline.log(cmd1);
			TopDownPipeline.run(cmd1);
			TopDownPipeline.log(cmd2);
			TopDownPipeline.run(cmd2);
			TopDownPipeline.log("thread" + num + "> ends");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
