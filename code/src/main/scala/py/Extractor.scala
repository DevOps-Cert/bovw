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
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object Extractor{
  
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
  // a logger
  val log = LoggerFactory.getLogger(this.getClass().getName())
  
  def main(args : Array[String]){
    run
  }
  
  def run(){
    log.info("start of run over the Oxford Building 5K dataset")
    // for each file in the directory, extract features, store into files
    val folder = new File("resources/oxbuild_images")
    val files  = folder.listFiles()
    files.foreach(file => extractFeatures(file))
    log.info("end of run over the Oxford Building 5K dataset")
  }
  
  // read one file and then output the features to one file
  def extractFeatures(file : File){
    val begin = System.nanoTime()
    // load image
    val image = cvLoadImageM(file.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE)
    val keyPoints = new KeyPoint()  
    sift.detect(image, null, keyPoints)
    val points = toArray(keyPoints)
    val cv_mat = new CvMat(null)
    des.compute(image, keyPoints, cv_mat)
    
    val pw = new PrintWriter("resources/features/" + file.getName() + ".txt")
    // write the cv_mat into a file
    for(i <- 0 until cv_mat.rows())
    {
      val row = new ArrayList[Double]
      for(j<- 0 until cv_mat.cols())
        row.add(cv_mat.get(i, j))
       pw.println(row.toArray().mkString(" "))
       pw.flush()
    }
    
    val end = System.nanoTime()
    // write the statistics about this image
    stat.println(file.getName() + " " + (end - begin) / 1000000.0 + " " + cv_mat.size() / cv_mat.cols() + " " + cv_mat.cols() + " " + cv_mat.size())
    stat.flush()
    log.info("" + file.getName() + ": processed")
  }
}