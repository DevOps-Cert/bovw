package py

import java.util.ArrayList
import weka.core.Attribute
import weka.clusterers.SimpleKMeans
import weka.core.Instances
import weka.core.FastVector
import weka.core.Instance
import weka.core.Instance
import scala.io.Source

object Clustering {
  // TODO: weka.jar and Mahout
  // TODO: use top-down hierarchical clustering
  // TODO: scale up the algorithm by using map-reduce paradigm
  
  
  def main(args : Array[String]){
    
	// set the attribute parameters
    val fv = new FastVector(128)
	for (i <- 0 until 128)
      fv.addElement(new Attribute("feature" + i))  
    
	// read instances from feature files
    val features = getFeatures("resources/features/all_souls_000000.jpg.txt", fv)  
    println(features.numInstances())
	
    // use weka k-means cluster
	val kmeans = new SimpleKMeans()
	kmeans.setSeed(10)
	// cluster parameters
	kmeans.setMaxIterations(100)
	kmeans.setNumClusters(5)
	kmeans.setPreserveInstancesOrder(true);
    // learn the clusters
    kmeans.buildClusterer(features)
    // evaluate the clusters
    val ass = kmeans.getAssignments()
    ass.foreach(a =>{println(a)})
    
  }
  
  def getFeatures(filename : String, fv : FastVector) = {
    val ins = new Instances("SIFT", fv, 0)
    val lines = Source.fromFile(filename).getLines
    lines.foreach(line => {
      //TODO: filter lines here to choose the useful scale
      val array = line.split(" ").slice(9, 128 + 9)
      val dv = new Array[Double](128)
      var i = 0
      array.foreach(d => {dv(i) = java.lang.Double.parseDouble(d).toDouble; i = i + 1})
      // add one instance
      ins.add(new Instance(1.0, dv))    
    })
    
    ins
  }
  
  
}