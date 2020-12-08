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
    private final Path pathToHaarcascade;
    private VideoCapture capture;

    public ObjectDetector(Path pathToVideo, Path pathToHaarcascade) {
        this.pathToHaarcascade = pathToHaarcascade;
        capture =  new VideoCapture(pathToVideo.toString());  //path to the video files
    }

    public Image getCaptureWithObjectDetection() {
        Mat mat = new Mat(); //new matrix
        capture.read(mat); //get new frame from capture source into that matrix
        Mat haarClassifiedImg = detectObject(mat); //calling detect object function and storing it in a matrix
        return mat2Img(haarClassifiedImg);        //and returning an image of that new matrix
    }

    private Mat detectObject(Mat inputImage) {
        MatOfRect objectsDetected = new MatOfRect(); //new matrix for detected faces
        CascadeClassifier cascadeClassifier = new CascadeClassifier(); //cascadeClassifier = new CascadeClassifier, make me say it one more time
        int minFaceSize = Math.round(inputImage.rows() * 0.1f);
        cascadeClassifier.load(this.pathToHaarcascade.toString()); //loading file with machine learned samples
        cascadeClassifier.detectMultiScale(inputImage, //source
                objectsDetected, //results
                1.1, //settings
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                new Size()
        );
        Rect[] objectsArray =  objectsDetected.toArray(); //needed for for function
        for(Rect face : objectsArray) { //displaying red squares
            Imgproc.rectangle(inputImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3 );
        }
        return inputImage; //return updated image matrix
    }

    private Image mat2Img(Mat mat) { //converting matrix to img
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());
        Image img = new Image(inputStream);
        return img;
    }

}
