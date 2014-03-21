package py

import java.io.PrintWriter
import java.io.File
import scala.io.Source

object PostProcessor {
	  
  val info = new PrintWriter("resources/info.txt")
  val fes = new PrintWriter("resources/features.txt");
  
  
  def main(args : Array[String]){
	 // read all files in features folder and merge them into two files
	 val folder = new File("resources/features_new")
     val files  = folder.list()
     var index = 1;
     files.foreach(file => {
       write(file)
       println(index + " " + file + ": processed")
       index = index + 1})
	 info.close()
	 fes.close()
  }
  
  def write(filename : String){
    val lines = Source.fromFile("resources/features_new/" + filename).getLines
    lines.foreach(line => {
      val array = line.split(" ");
      info.println(filename + " " + array.slice(0, 10).mkString(" "))
      fes.println(array.slice(10, array.size).mkString(" "))
    })
    
  }
}