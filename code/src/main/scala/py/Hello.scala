package py

import com.googlecode.javacv.cpp.opencv_highgui._
import com.googlecode.javacv.cpp.opencv_core._
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
//import com.googlecode.javacv.cpp.opencv_core.Core

object Hello{
  def main(args:Array[String]){
    loadFeatures("resources/images")
  }
  
  def extractKeyPoints(filename : String) : KeyPoint = {
    // load image
    val image = cvLoadImage(filename)
    // Detect SIFT features
    val keyPoints = new KeyPoint()
    val nFeatures = 0
    val nOctaveLayers = 3
    val contrastThreshold = 0.03
    val edgeThreshold = 10
    val sigma = 1.6
    val sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma)
    sift.detect(image, null, keyPoints)
    
    println(keyPoints)
    
    keyPoints
    
    // keyPoints.
    // Draw keyPoints
    // val image = loadAndShowOrExit(new File("boldt.jpg"))    
    // val featureImage = cvCreateImage(cvGetSize(image), image.depth(), 3)
    // drawKeypoints(image, keyPoints, featureImage, CvScalar.WHITE, DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    // show(featureImage, "SIFT Features")
  }
  
  def loadFeatures(dir : String)
  {
    val folder = new File(dir)
    val files  = folder.listFiles()
    val pw = new PrintWriter(new File("resources/features.txt"))
    files.foreach(f => {
      println(f.getName())
      extractKeyPoints(f.getAbsolutePath())
    })
  
  
  }
  
  
}


