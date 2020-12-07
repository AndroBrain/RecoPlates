package com.camera.classesAndPrograms;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CameraStream extends Application {
    private VideoCapture capture;

    public void start(Stage stage) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Path p= FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "PeopleWalking.mp4");
        //capture =  new VideoCapture(p.toString());  //path to the video files
        capture=  new VideoCapture(0); // 0 is the camera
        ImageView imageView = new ImageView();
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();
        new AnimationTimer(){
            @Override
            public void handle(long l) {
                imageView.setImage(getCaptureWithFaceDetection()); //calling function to capture and detect faces
            }
        }.start();
    }

    /*public Image getCapture() { //this function is not used!
        Mat mat = new Mat();
        capture.read(mat);
        return mat2Img(mat);
    }*/

    public Image getCaptureWithFaceDetection() {
        Mat mat = new Mat(); //new matrix
        capture.read(mat); //get new frame from capture source into that matrix
        Mat haarClassifiedImg = detectFace(mat); //calling detect face function and storing it in a matrix
        return mat2Img(haarClassifiedImg);
    }

    public Image mat2Img(Mat mat) {
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());
        Image img = new Image(inputStream);
        return img;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static Mat detectFace(Mat inputImage) {
        MatOfRect facesDetected = new MatOfRect(); //new matrix for detected faces
        CascadeClassifier cascadeClassifier = new CascadeClassifier(); //cascadeClassifier = new CascadeClassifier, make me say it one more time
        int minFaceSize = Math.round(inputImage.rows() * 0.1f);
        Path p= FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "haarcascade_frontalface_alt.xml");
        cascadeClassifier.load(p.toString()); //loading file with machine learned samples
        cascadeClassifier.detectMultiScale(inputImage, //source
                facesDetected, //results
                1.1, //settings
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                new Size()
        );
        Rect[] facesArray =  facesDetected.toArray(); //needed for for function
        for(Rect face : facesArray) { //displaying red squares
            Imgproc.rectangle(inputImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3 );
        }
        return inputImage; //return updated image matrix
    }
}