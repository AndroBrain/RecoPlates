package com.camera.classesAndPrograms;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class Main{

    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        return imageCodecs.imread(imagePath);
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs imgcodecs = new Imgcodecs();
        imgcodecs.imwrite(targetPath, imageMatrix);
    }

    public static Image mat2Img(Mat mat) {
        MatOfByte bytes = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, bytes);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());
        Image img = new Image(inputStream);
        return img;
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Path videoPath = FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "samochodzik.bmp"); //Cars
        String sourceImagePath = videoPath.toString();
        String targetImagePath = "C:\\Users\\Michal\\Desktop\\jaEdited.jpg";




        Mat loadedImage = loadImage(sourceImagePath);
        MatOfRect facesDetected = new MatOfRect();

        mat2Img(loadedImage);

        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        int minFaceSize = Math.round(loadedImage.rows() * 0.1f);
        Path haarcascadePath = FileSystems.getDefault().getPath("necessaryFiles" + File.separator + "haarcascadeForCars.xml"); //xml file
        cascadeClassifier.load(haarcascadePath.toString());
        cascadeClassifier.detectMultiScale(loadedImage,
                facesDetected,
                1.1,
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                new Size()
        );

        Rect[] facesArray = facesDetected.toArray();
        for(Rect face : facesArray) {
            Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3);
        }

        saveImage(loadedImage, targetImagePath);

    }
}
