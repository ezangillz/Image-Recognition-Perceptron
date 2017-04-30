# Image Recognition Perceptron

## What is this?

It is a neural network that tries hard to determine what object is shown in the picture. Due to that it is just a simple single-layer perceptron it can only work with two classes of objects, but I thought it might be nice to have something like this, that works with images.

Also it is my first neural network, so please, feel free to criticize, I need it!

## How to use this?
First you'll need to compile the whole thing. The main class is *ImageRecognition*.
```
javac ImageRecognition.java
```

To train this perceptron you need to have a set of images for each one of the two objects.

So to start training you just need to do something like that:
```
java ImageRecognition -learn A(first object) 1.png 2.png 3.png ... N.png
```
and you should do the same for the B object. 
Or simply do the following:
```
java ImageRecognition -learn A Object1/*
java ImageRecognition -learn B Object2/*
```
Next, you can use this trained perceptron to recognize these objects in other images:
```
jave ImageRecognition [path_to_image.png]
```
