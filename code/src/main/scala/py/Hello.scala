package py

import com.googlecode.javacv.cpp.opencv_highgui._

object Hello{
  def main(args:Array[String]){
    println("Hello World")
    val image = cvLoadImage("boldt.jpg")
  }
}
