package py

import com.googlecode.javacv.cpp.opencv_highgui._
import com.googlecode.javacv.cpp.opencv_core._
import com.googlecode.javacv.cpp.opencv_core.CvMat
import com.googlecode.javacv.cpp.opencv_features2d._
import java.io.File
import com.googlecode.javacv.CanvasFrame
import com.googlecode.javacv.cpp.opencv_nonfree.SIFT
import com.googlecode.javacv.cpp.opencv_imgproc._
import com.googlecode.javacv.CanvasFrame
import com.googlecode.javacv.cpp.opencv_highgui._
import javax.swing.JFrame._
import OpenCVUtils._
import java.io.PrintWriter
import java.util.ArrayList
import java.util.Date

object Extractor {
  
  // parameters used to detect SIFT key points
  val nFeatures = 0
  val nOctaveLayers = 3
  val contrastThreshold = 0.03
  val edgeThreshold = 10
  val sigma = 1.6
  val sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma)
  val des = sift.getDescriptorExtractor()
  
  // open a file to store all the statistics
  val stat = new PrintWriter("resources/stat.txt")
  
  
  def main(args : Array[String]){
    //run
    //drawFromFile("resources/oxbuild_images/all_souls_000001.jpg")
    matchFeatures(2)
  }
  
  def run(){
    println(new Date().toGMTString() + " " + this.getClass().getName() + " " + "start of run over the Oxford Building 5K dataset")
    // for each file in the directory, extract features, store into files
    val folder = new File("resources/oxbuild_images")
    val files  = folder.list()
    files.foreach(file => extractFeatures("resources/oxbuild_images/" + file))
    println(new Date().toGMTString() + " " + this.getClass().getName() + " " + "end of run over the Oxford Building 5K dataset")
  }
  
  // read one file and then output the features to one file
  def extractFeatures(filename: String){
    //println(new Date().toGMTString() + " " + this.getClass().getName() + " " + file.getName() + ": processing")
    val begin = System.nanoTime()
    // load image
    var file = new File(filename)
    var image = cvLoadImageM(file.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE)
    var keyPoints = new KeyPoint()  
    sift.detect(image, null, keyPoints)
    var points = toArray(keyPoints)
    var cv_mat = new CvMat(null)
    des.compute(image, keyPoints, cv_mat)
    
    // used to count the number of points in different scales
    var s = Array(0,0,0,0,0,0,0,0); 
    
    if (cv_mat.isNull()) {println(new Date().toGMTString() + " " + this.getClass().getName() + " " + "empty cv_mat");return} // handling exception
    // write the cv_mat into a file
    val pw = new PrintWriter("resources/features/" + file.getName() + ".txt")
    for(i <- 0 until cv_mat.rows())
    {
      val row = new ArrayList[Double]
      for(j<- 0 until cv_mat.cols())
        row.add(cv_mat.get(i, j))
      // write the key point information as well as the 128B feature into file  
      pw.print(points(i).position() + " " + points(i).pt() + " " + points(i).response() + " " + points(i).angle() + " " + points(i).size() + " ")
      val paras = getSIFTParameters(points(i)) // result as a tuple
      pw.print((paras._1 + 1) + " " + paras._2 + " " + paras._3 * 0.5f + " " + paras._4  + " ")
      pw.println(row.toArray().mkString(" "))
      pw.flush()
      // add the scale number information
      s(paras._1 + 1) = s(paras._1 + 1) + 1
      
    }
    
    val end = System.nanoTime()
    // write the statistics about this image
    stat.println(file.getName() + " " + (end - begin) / 1000000.0 + " " + cv_mat.rows() + " " + s.mkString(" "))
    stat.flush()
    println(new Date().toGMTString() + " " + this.getClass().getName() + " " + file.getName() + ": processed")
    
    // clear memory and garbage collection
    keyPoints.deallocate()
    cv_mat.release()
    file = null
    image = null
    keyPoints = null
    points = null
    cv_mat = null
    System.gc()
    
  }
  
  def getSIFTParameters(point : KeyPoint) = {
    // get octave
	var octave = point.octave & 255;
    octave = if (octave < 128) octave else octave | -128
    // get layer
    var layer = (point.octave >> 8) & 255;
    // get scale
    var scale = if (octave >= 0)  1.0f/(1 << octave) else (1 << -octave).asInstanceOf[Float]
    // multiply the point's radius by the calculated scale
    var scl = point.size * 0.5f * scale;
    // determines the size of a single descriptor orientation histogram
    var histWidth = 3.0f * scl;
    // descWidth is the number of histograms on one side of the descriptor
    val radius = (histWidth * 1.4142135623730951f * (4 + 1) * 0.5f).toInt
    (octave, layer, scale, radius) 
  }
  
  
  def draw(keyPoints:KeyPoint, image:IplImage, i : Integer = 0){
    // Draw keyPoints
    val featureImage = cvCreateImage(cvGetSize(image), image.depth(), 3)
    drawKeypoints(image, keyPoints, featureImage, CvScalar.RED, DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    show(featureImage, "SIFT Features scale = " + i)
    save(new File("resources/s" + i + ".jpg"), featureImage)
  }
  
  def drawFromFile(filename:String){
    // to draw an arbitrary image file
       // Read input image
      // parameters used to detect SIFT key points
    val nFeatures = 0
    val nOctaveLayers = 3
    val contrastThreshold = 0.03
    val edgeThreshold = 10
    val sigma = 1.6
    val sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma)
    val image = loadAndShowOrExit(new File(filename))
    var keyPoints = new KeyPoint()
    sift.detect(image, null, keyPoints)
    var points = toArray(keyPoints)        
    println("key points number " + points.size)
    drawScales(image, keyPoints)
  }
  
  def drawScales(image : IplImage, point : KeyPoint){
    // divide the key points by the scales
    val marks = Array(-1, -1, -1, -1, -1, -1, -1, -1)
    val points = toArray(point)
    for(i <-0 until points.size){
      var octave = points(i).octave & 255;
      octave = if (octave < 128) octave else octave | -128
      marks(octave + 1) = i
    }
    println(marks.mkString(" "))
    for(i<-0 until marks.size){
      // create a new key point vector
      var ps : Array[KeyPoint] = null
      if (i == 0) ps = points.slice(0, marks(0) + 1)
      else ps = points.slice(marks(i-1) + 1, marks(i) + 1)
      // show image
      println(ps.size)      
      if(ps.size > 0){ 
        ps(0).limit(ps(0).position() + ps.size)
        println(ps(0).position() + " " + ps(0).limit() + " " + ps(0).pt() + " " + ps(0).response() + " " + ps(0).angle() + " " + ps(0).size() + " ")
        draw(ps(0), image, i)
      }
     }
  }
  
  def matchFeatures(scale : Integer){
    val image0 = cvLoadImage("resources/oxbuild_images/all_souls_000006.jpg", CV_LOAD_IMAGE_GRAYSCALE)
    val image1 = cvLoadImage("resources/oxbuild_images/all_souls_000015.jpg", CV_LOAD_IMAGE_GRAYSCALE)
    
    // cleanup here
    var point0 = new KeyPoint()  
    sift.detect(image0, null, point0)
    val marks0 = Array(-1, -1, -1, -1, -1, -1, -1, -1)
    val points0 = toArray(point0)
    for(i <-0 until points0.size){
      var octave = points0(i).octave & 255;
      octave = if (octave < 128) octave else octave | -128
      marks0(octave + 1) = i
    }
    points0(marks0(scale) + 1).limit(points0(marks0(scale) + 1).position() + points0.slice(marks0(scale) + 1, marks0(scale + 1) + 1).size)
    var mat0 = new CvMat(null)
    des.compute(image0, points0(marks0(scale) + 1), mat0)
    
    
    var point1 = new KeyPoint()  
    sift.detect(image1, null, point1)
    val marks1 = Array(-1, -1, -1, -1, -1, -1, -1, -1)
    val points1 = toArray(point1)
    for(i <-0 until points1.size){
      var octave = points1(i).octave & 255;
      octave = if (octave < 128) octave else octave | -128
      marks1(octave + 1) = i
    }
    points1(marks1(scale) + 1).limit(points1(marks1(scale) + 1).position() + points1.slice(marks1(scale) + 1, marks1(scale + 1) + 1).size)
    var mat1 = new CvMat(null)
    des.compute(image1, points1(marks1(scale) + 1), mat1)
    
    pointMatch(image0, mat0, points0(marks0(scale) + 1), image1, mat1, points1(marks1(scale) + 1))
  }
  
  def pointMatch(image0 : IplImage, mat0 : CvMat, point0 : KeyPoint, image1 : IplImage, mat1 : CvMat, point1 : KeyPoint){
    
    //val matcher = new BFMatcher(NORM_L2, false)
    val matcher = new FlannBasedMatcher()
    val matches = new DMatch()
    
    matcher.`match`(mat0, mat1, matches, null)
    println("Matched: " + matches.capacity)
    println(mat0.rows() + " " + mat1.rows())
    // Select only 25 best matches
    val bestMatches = selectBest(matches, 20)

    // Draw best matches
    val imageMatches = cvCreateImage(new CvSize(image0.width + image1.width, image0.height), image0.depth, 3)
    drawMatches(image0, point0, image1, point1,
        bestMatches, imageMatches, CvScalar.BLUE, CvScalar.RED, null, DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    show(imageMatches, "Best SIF Feature Matches")
    save(new File("resources/match.jpg"), imageMatches)

  }
      /** Select only the best matches from the list. Return new list. */
  def selectBest(matches: DMatch, numberToSelect: Int): DMatch = {
        // Convert to Scala collection, and sort
        val sorted = toArray(matches).sortWith(_ compare _)
        // Select the best, and return in native vector
        toNativeVector(sorted.take(numberToSelect))
    }
}
