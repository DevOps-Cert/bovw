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
//import com.googlecode.javacv.cpp.opencv_core.Core

object Hello{
  
  // parameters used to detect SIFT key points
  
  val nFeatures = 0
  val nOctaveLayers = 3
  val contrastThreshold = 0.03
  val edgeThreshold = 10
  val sigma = 1.6
  val sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma)
  val des = sift.getDescriptorExtractor()
  // file writer
  val pw = new PrintWriter(new File("resources/features.txt"))
    
  def main(args:Array[String]){
    loadFeatures("resources/images")
    println("end")
    // test
    /*
    val image = cvLoadImageM("boldt.jpg", CV_LOAD_IMAGE_GRAYSCALE)
    val keyPoints = new KeyPoint()
    val sift = new SIFT(0, 3, 0.03, 10, 1.6)
    sift.detect(image, null, keyPoints)
    
    val cv_mat = new CvMat(null)
    des.compute(image, keyPoints, cv_mat)
    
    println(cv_mat.cols())
    println(cv_mat.rows())
    println(cv_mat.channels())
    // 551 * 128 matrix
    val points = toArray(keyPoints) // used to get all the positions and sizes of the key points
    println(points.size)
    // pt i.e. position(x, y)
    // println("postion, angle, octave, response, size, capacity, limit")
    pw.close()*/
  }
  
  def extractKeyPoints(filename : String) {
    // load image
    val image = cvLoadImageM(filename, CV_LOAD_IMAGE_GRAYSCALE)
    val keyPoints = new KeyPoint()  
    sift.detect(image, null, keyPoints)
    val points = toArray(keyPoints)
    val cv_mat = new CvMat(null)
    des.compute(image, keyPoints, cv_mat)
    writeToFile(cv_mat)
    println(filename + ":" + cv_mat.rows())
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
    pw.close()
  }
  
  def writeToFile(mat:CvMat)
  {
	for(i <- 0 until mat.rows())
    {
      val row = new ArrayList[Double]
      for(j<- 0 until mat.cols())
        row.add(mat.get(i, j))
       pw.println(row.toArray().mkString(" "))
       pw.flush()
    }
  }
  
}


