package com.camera.classesAndPrograms;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CameraStream extends Application {

    public void start(Stage stage){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Path videoPath = FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "dashCam.mp4"); //Cars
        Path haarcascadePath = FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "haarcascadeForCars.xml"); //xml file
        // Setting up stage
        ImageView imageView = new ImageView();
        HBox hbox = new HBox(imageView);
        Scene scene = new Scene(hbox);
        stage.setScene(scene);
        stage.show();
        ObjectDetector objectDetector = new ObjectDetector(videoPath, haarcascadePath ); // constractor with paths to video and xml file
        new AnimationTimer(){
            @Override
            public void handle(long l) {
                imageView.setImage(objectDetector.getCaptureWithObjectDetection());
            }
        }.start();
    }

}