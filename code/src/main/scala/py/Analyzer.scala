package py

import java.util.ArrayList
import scala.io.Source
import java.lang._
import java.util.HashSet
import java.io.File

object Analyzer {
  val times = new ArrayList[Double] 
  val features = new ArrayList[Integer]
  /**
  	running time mean: 1557.5700291541505 min: 286.982477 max: 5013.199839
	features mean: 17272 min: 24 max: 99228
  **/
  def main(args : Array[String]){
    extract("resources/stat.txt")
    evaluateTime()
    evaluateFeature()
    checkDuplicate("resources/features", 5000)
  }
  
  // extract statistics from file
  def extract(filename:String){
    val lines = Source.fromFile(filename).getLines
    lines.foreach(line => {
      val array = line.split(" ")
      times.add(Double.parseDouble(array(1)))
      features.add(Integer.parseInt(array(2)))
    })
  }
  
  def evaluateTime(){
    var mean: Double = 0.0
    var max: Double = 0.0
    var min: Double = 1000000.0
    var sum: Double = 0.0
    
    times.toArray(Array[Double]()).foreach(time =>{
      sum = sum + time
      if (time > max) max = time
      if (time < min) min = time
    })
    
    mean = sum / times.size()
    println("running time mean: " + mean + " min: " + min + " max: " + max)
    
  }
  
  def evaluateFeature(){
    var mean: Double = 0
    var max: Integer = 0
    var min: Integer = 1000000
    var sum: Double = 0
    
    features.toArray(Array[Integer]()).foreach(feature =>{
      sum = sum + feature
      //if (feature > 17272) println(feature)
      if (feature > max) max = feature
      if (feature < min) min = feature
    })
    
    mean = sum / features.size()
    println("featurs size " + features.size())
    println("features mean: " + mean + " min: " + min + " max: " + max)
  }
  
  
  def checkDuplicate(foldername : String, num : Integer){
    val folder = new File(foldername)
    val files  = folder.list().slice(0, num)
    files.foreach(file => findDuplicate(foldername + "/" + file))
  }
  
  def findDuplicate(filename:String){
    // read the matrix from file
    val lines = Source.fromFile(filename).getLines
    val matrix = new ArrayList[Array[Double]](lines.size)
    lines.foreach(line => {
      val array = new ArrayList[Double]()
      val ds = line.split(" ")
      ds.foreach(d => {
        array.add(Double.parseDouble(d))
      })
      matrix.add(array.toArray(Array[Double]()))
    })
    
    val set  = new HashSet[Array[Double]]()
    val rets = new HashSet[Array[Double]]()
    
    matrix.toArray(Array[Array[Double]]()).foreach(row => {
      if (!set.add(row)) rets.add(row)
    })
    
    if (!rets.isEmpty())
    	println(filename + " has duplicates")
    //else
        //println(filename + " has no duplicates")
  }
  
  
  
}