# Image Recognition Single-Layer Perceptron

## What is this?

It is a neural network that tries hard to determine what object is shown in the picture. Due to that it is just a simple single-layer perceptron it can only work with two classes of objects, but I thought it might be nice to have something like this, that works with images.

Also it is my first neural network, so please, feel free to criticize, I need it!

## How to use this?
First you'll need to compile the whole thing. The main class is __ImageRecognition__.
```
javac ImageRecognition.java
```

To train this perceptron you need to have a set of images for each one of the two objects.

So to start the training you need to create two folders __Object1__ and __Object2__ and move corresponding images to each folder.
Then you need to do the following:
```
java ImageRecognition -learn -Object1Name Object1/* -Object2Name Object2/*
```
Next, you can use this trained perceptron to recognize these objects in other images:
```
java ImageRecognition [path_to_image.png]
```
Or reset the perceptron to retrain.
```
java ImageRecognition -reset
```

