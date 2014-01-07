package py.utils

import java.io.File
import org.apache.commons.io.FileUtils

// apache common io is needed

object fileUtils {
	def main(args : Array[String]){
	  
         //genCommands("/home/hadoop/Desktop/mid")
	     //copyFiles("/home/hadoop/Desktop/1/", "/home/hadoop/Desktop/2/")
	     fetchCentroids("/home/hadoop/Desktop/bottom", "/home/hadoop/Desktop/centroids")
	}
	
	// generate intermediate commands
	def genCommands(dir : String){
	     val folder = new File(dir)
         val files  = folder.list()
         files.foreach(file => {
        	 println("bin/mahout kmeans -i mid/" + file + "/part-m-0 -c bottom/cls/"+ file +"/ -o bottom/" + file + 
        	      "/ -k 33 -dm org.apache.mahout.common.distance.CosineDistanceMeasure -cd 0.01 -x 100 " +
        			"-xm sequential -ow -cl")
         })
	}
	
	// copy files from src directories to dest directories
	def copyFiles(src: String, dest : String){
	  val srcDir = new File(src)
	  val destDir = new File(dest)
	  FileUtils.copyDirectory(srcDir, destDir)
	  FileUtils.moveDirectoryToDirectory(srcDir, destDir, false)
	}
	
	// fetch centroids from bottom-level clustering results to a destined folder
	def fetchCentroids(src : String, dest : String){
	  val srcDir = new File(src)
	  srcDir.list().foreach(folder => {
	    if (!folder.equals("cls")){
	      val subDir = new File (src + "/" + folder)
	      subDir.listFiles().foreach(file=>{
	        if (file.getName().contains("final")){
	          val destPath = dest + "/" + folder + "/"
	          println(destPath)
	          FileUtils.copyDirectory(file, new File(destPath))
	        }
	      })
	    }
	  })
	}
	
}
