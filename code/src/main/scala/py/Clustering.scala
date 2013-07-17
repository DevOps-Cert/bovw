package py

import java.util.ArrayList
import weka.core.Attribute
import weka.core.Instances
import weka.core.FastVector
import weka.core.Instance
import weka.core.Instance
import scala.io.Source
import weka.clusterers._

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
	
    val t0 = System.nanoTime()
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
    //val ass = kmeans.getAssignments()
    //ass.slice(0, 50).foreach(a =>{println(a)})
    val t1 = System.nanoTime()
    println("k-means clustering " + (t1 - t0)/1000000000.0)
    
    val em = new EM();   // new instance of clusterer
    // set the options
    em.setMaxIterations(100)
    em.setSeed(10)
    em.setNumClusters(5)
    em.buildClusterer(features);    // build the clusterer
    val t2 = System.nanoTime()
    println("EM clustering " + (t2 - t1)/1000000000.0)
    
    
    val ha = new HierarchicalClusterer()
    ha.setNumClusters(5)
    ha.buildClusterer(features)
    val t3 = System.nanoTime()
    println("hierarchical clustering " + (t3 - t2)/1000000000.0)
    
    // Cobweb
    val cb = new Cobweb()
    cb.setSeed(10)
    cb.buildClusterer(features)
    val t4 = System.nanoTime()
    println("Cobweb clustering " + (t4 - t3)/1000000000.0)
    
    val eval = new ClusterEvaluation()
    //eval.setClusterer(kmeans)
    //eval.evaluateClusterer(features)
    //println("# of clusters: " + eval.getNumClusters())
    eval.setClusterer(em)
    eval.evaluateClusterer(features)
    println("# of clusters: " + eval.getNumClusters())
     
    // clustering new instance
    // kmeans.clusterInstance(instance)
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