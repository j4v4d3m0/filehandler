package com.github.javademo.filehandler.transform;

import static java.lang.Math.round;
import static org.opencv.core.Mat.zeros;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageHistogramCreator implements Creator {

  static {
    nu.pattern.OpenCV.loadLocally();
  }

  private static final int WIDTH = 256;

  private static final int HEIGHT = 100;

  @Override
  public Object create(File file) {
    List<Mat> matList = new LinkedList<Mat>();
    matList.add(Imgcodecs.imread(((File) file).getAbsolutePath()));

    Mat histogram = new Mat();

    MatOfFloat ranges = new MatOfFloat(0, 256);
    MatOfInt histSize = new MatOfInt(WIDTH);

    Imgproc.calcHist(matList, new MatOfInt(0), new Mat(), histogram, histSize, ranges);
    Mat histImage = zeros(HEIGHT, (int) histSize.get(0, 0)[0], CvType.CV_8UC1);
    Core.normalize(histogram, histogram, 1, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

    for (int i = 0; i < (int) histSize.get(0, 0)[0]; i++) {
      Imgproc.line(
          histImage,
          new Point(i, histImage.rows()),
          new Point(i, ((double)histImage.rows()) - (double)round(histogram.get(i, 0)[0])),
          new Scalar(255, 255, 255),
          1,
          8,
          0);
    }

    MatOfByte matOfByte = new MatOfByte();
    Imgcodecs.imencode(".bmp", histImage, matOfByte);

    return matOfByte.toArray();
  }
}
