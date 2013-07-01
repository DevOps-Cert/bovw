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
    //val file = new File("resources/oxbuild_images/ashmolean_000214.jpg")
    //extractFeatures(file)
    //run
    println(sift.info())
    //println(des.info())
    drawFromFile("resources/oxbuild_images/all_souls_000001.jpg")
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
    
    if (cv_mat.isNull()) {println(new Date().toGMTString() + " " + this.getClass().getName() + " " + "empty cv_mat");return} // handling exception
    // write the cv_mat into a file
    val pw = new PrintWriter("resources/features/" + file.getName() + ".txt")
    for(i <- 0 until cv_mat.rows())
    {
      val row = new ArrayList[Double]
      for(j<- 0 until cv_mat.cols())
        row.add(cv_mat.get(i, j))
      pw.print(points(i).angle() + " " + (points(i).octave() & 255) + " " + (points(i).octave() & 65280) +  
         " " + points(i).size() + " " + points(i).position() + " " + points(i).response() + " " + points(i).pt() + " ")
      pw.println(row.toArray().mkString(" "))
      pw.flush()
    }
    
    val end = System.nanoTime()
    // write the statistics about this image
    stat.println(file.getName() + " " + (end - begin) / 1000000.0 + " " + cv_mat.size() / cv_mat.cols() + " " + cv_mat.cols() + " " + cv_mat.size())
    stat.flush()
    println(new Date().toGMTString() + " " + this.getClass().getName() + " " + file.getName() + ": processed")
    
    // clear memory and garbage collection
    file.delete()
    //image.release()
    keyPoints.deallocate()
    cv_mat.release()
    file = null
    image = null
    keyPoints = null
    points = null
    cv_mat = null
    System.gc()
    
  }
  
  def draw(keyPoints:KeyPoint, image:CvMat){
    // Draw keyPoints
    val featureImage = cvCreateImage(cvGetSize(image), image.depth(), 3)
    drawKeypoints(image, keyPoints, featureImage, CvScalar.WHITE, DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    show(featureImage, "SIFT Features")
  }
  
  def drawFromFile(filename:String){
    // to draw an arbitrary image file
       // Read input image
      // parameters used to detect SIFT key points
    val nFeatures = 100
    val nOctaveLayers = 3
    val contrastThreshold = 0.03
    val edgeThreshold = 10
    val sigma = 1.6
    val sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma)
    val image = loadAndShowOrExit(new File(filename))
    var keyPoints = new KeyPoint()
    sift.detect(image, null, keyPoints)
    var points = toArray(keyPoints)
    points.foreach(point => {
      var octave = if ((point.octave & 255) < 128) (point.octave & 255) else (point.octave & 255) | -128
      var scale = if (octave >= 0)  1.0f/(1 << octave) else (1 << -octave).asInstanceOf[Float]
      scale = scale * 0.5f
      octave = octave + 1
      println("angle " + point.angle() + " octave " + point.octave() + " " + octave
            + " " + ((point.octave() & 65280) >> 8) +  " scale " + scale + 
      " size " + point.size() + " position " + point.position() + " response " + point.response() + " pt " + point.pt())     
    })
        
    println("the last keypoint: angle " + points(points.size - 1).angle() + " octave " + points(points.size - 1).octave() + 
        " pt " + points(points.size - 1).pt() + " size " + points(points.size - 1).size() + " capacity " + points(points.size - 1).capacity() + 
        " limit " + points(points.size - 1).limit() + " position " + points(points.size - 1).position() + " response " + points(points.size - 1).response())
        
    println("key points number " + points.size)
    // Draw keyPoints
    val featureImage = cvCreateImage(cvGetSize(image), image.depth(), 3)
    drawKeypoints(image, points(0), featureImage, CvScalar.GREEN, DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    show(featureImage, "SIFT Features")
    save(new File("ex.jpg"), featureImage)
  }
  
  
}