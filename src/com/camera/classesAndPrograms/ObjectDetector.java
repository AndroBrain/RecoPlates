package com.camera.classesAndPrograms;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

public class ObjectDetector {
    private final CascadeClassifier entirePictureClassifier;
    private final CascadeClassifier nestedPictureClassifier;
    private VideoCapture capture;

    public ObjectDetector(Path pathToVideo, Path pathToFirstHaarcascade, Path pathToNestedHaarcascade) {
        entirePictureClassifier = new CascadeClassifier();
        entirePictureClassifier.load(pathToFirstHaarcascade.toString());

        nestedPictureClassifier = new CascadeClassifier();
        nestedPictureClassifier.load(pathToNestedHaarcascade.toString());

        capture =  new VideoCapture(pathToVideo.toString());  //path to the video files

//        capture =  new VideoCapture(0);  // your Camera
    }

    public Image getCaptureWithObjectDetection() {
        Mat mat = new Mat(); //new matrix
        capture.read(mat); //get new frame from capture source into that matrix
        Mat haarClassifiedImg = detectObject(mat); //calling detect object function and storing it in a matrix
        return mat2Img(haarClassifiedImg); //returning image of that new matrix
    }

    private Mat detectObject(Mat inputImage) {
        MatOfRect objectsDetected = new MatOfRect(); //new matrix for detected objects

        detect(inputImage, objectsDetected, entirePictureClassifier, Math.round(inputImage.rows() * 0.1f));

        Rect[] objectsArray =  objectsDetected.toArray(); //needed for for function
        for(Rect object : objectsArray) {
            Imgproc.rectangle(inputImage, object.tl(), object.br(), new Scalar(0, 0, 255), 3);//displaying red squares

            Mat platePosition = inputImage.submat(object);
            MatOfRect platesDetected = new MatOfRect();
            detect(platePosition, platesDetected, nestedPictureClassifier, Math.round(platePosition.rows() * 0.1f));

            for (Rect plate:platesDetected.toArray()) { // Needed to move position
                Point point1 = new Point(plate.tl().x + object.tl().x, plate.tl().y + object.tl().y);
                Point point2 = new Point(plate.br().x + object.tl().x, plate.br().y + object.tl().y);
                Imgproc.rectangle(inputImage, point1, point2, new Scalar(255, 0, 0), 3);//displaying blue squares inside red square
            }
        }
        return inputImage; //return updated image matrix
    }

//    Detects all objects in inputImage for given classifier and saves them into MatOfRect
    private void detect(Mat inputImage, MatOfRect allSquares, CascadeClassifier classifier, int minObjectSize){
        classifier.detectMultiScale(inputImage, //source
                allSquares, //results
                1.1, //settings
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minObjectSize, minObjectSize),
                new Size()
        );
    }

    private Image mat2Img(Mat mat) { //converting matrix to img
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());
        Image img = new Image(inputStream);
        return img;
    }

// Prepage image for reading
    private Mat prepareImage(Mat img){
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);

//        Mat imgGaussianBlur = new Mat();
//        Imgproc.GaussianBlur(imgGray, imgGaussianBlur, new Size(3, 3), 0);

//        Mat imtAdaptiveThreshold = new Mat();
//        Imgproc.adaptiveThreshold(imgGaussianBlur, imtAdaptiveThreshold, 255, 0, 0, 99, 4);
        return imgGray;
    }

}
