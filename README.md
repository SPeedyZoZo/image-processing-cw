# README for Image Processing Project

## Project Overview

This Java application was developed as part of the coursework for the Image Processing course at Queen Mary University of London. The program demonstrates various image processing techniques such as negative transformation, rescaling, shifting, arithmetic operations, boolean operations, and more, using a graphical user interface. This project is intended as an educational tool to explore image manipulation techniques and is implemented in Java using AWT and Swing.

## Features

- Multiple Image Operations: The application supports a variety of image processing operations including Negative, Rescale, Shift, Arithmetic, Boolean, Point Processing, and more.
- Graphical User Interface: Provides a user-friendly GUI for selecting image processing operations and displaying results.
- Image Format Compatibility: Allows images to be saved in multiple formats such as BMP, GIF, JPEG, JPG, and PNG.

## Installation and Setup

1. Java Version: Ensure Java version "21.0.1" 2023-10-17 LTS or later is installed on your system.
2. Download: Clone or download the .java file from the repository.
3. Compilation: Navigate to the directory containing the downloaded file and compile it using the following command:
    ```
    javac ImageProcessing.java
    ```
4. Execution: Run the application with the command:
    ```
    java ImageProcessing
    ```

## Usage

1. Launching the Application: Double-click the compiled file or run it from the command line to open the GUI.
2. Loading Images: Start by loading an image using the interface. Example images named image1, image2, etc., up to image4 are provided in the repository in various file types to test compatibility.
3. Applying Operations: Select an image processing operation from the dropdown menu to apply to the loaded image.
4. Saving Images: To save a processed image, choose the desired format from another dropdown menu and save the file.

## Detailed Description of Operations

- Original: Displays the original image without any modifications.
- Negative: Inverts all color values in the image.
- Rescale: Multiplies the color values by a factor to enhance brightness or contrast.
- Shift: Adds a constant value to the color values to shift the image's brightness.
- Arithmetic Operations: Performs addition, subtraction, multiplication, or division on pixel values between two images.
- Boolean Operations: Applies bitwise AND, OR, and XOR operations between two images.
- Point Processing: Maps pixel values to new values using a specified lookup table.
- Bit-Plane Slicing: Isolates specific bits from each pixel's color value to highlight different levels of brightness.
- Smoothing: Applies a smoothing filter to reduce image noise and detail.
- Edge Detection: Highlights the edges within the images by detecting discontinuities in brightness.
- Order Statistic Filtering: Uses statistical methods like median filtering to remove noise while preserving edges.
- Histogram Equalisation: Improves the contrast of the image by effectively spreading out the most frequent intensity values.
- Thresholding: Segments the image by setting all pixel values above a certain threshold to the maximum value and those below to zero, often used for binary image creation.

## Contributing

Contributions are welcome to improve and expand the functionalities of this application. If you'd like to contribute:

1. Fork the repository on GitHub.
2. Create a new branch for your feature or fix.
3. Commit your changes with clear, descriptive messages.
4. Push your branch and submit a pull request for review.

## License

This project is released under the MIT License, which allows free use, modification, and distribution of the software.

## Take Down Request

If there are any concerns regarding the content of this repository, such as copyright issues or requests for removal by course administrators or others, please contact me directly. I am committed to addressing such concerns promptly and will remove the content if necessary.

## Acknowledgements

I, Zaid Kareem Chughtai, programmed this project from the Demo.java code template provided by QMUL.
This project was supervised by the faculty of Queen Mary University of London and is submitted as part of the academic requirements of the Image Processing course. The application serves as an educational tool to understand and apply basic as well as advanced image processing techniques in a practical setting.

## Note on Example Images

The repository includes several example images (image1.jpg, image2.jpg, etc.), stored in various file formats to demonstrate the compatibility and functionality of the image processing operations within this application. The usage of these image names are hard-coded in the program. Some of the operations may not work if an image is missing. If you wish to use your own images, ensure you either update the code appropriately with the correct image names, or change the names of your images to `image1` until `image4`. 

## Contact

For more information, assistance, or to provide feedback, please do not hesitate to reach out through the university's contact channels or directly via the repository's issue tracker on GitHub.

This README aims to provide all necessary details to get started with and effectively use the Image Processing application. Should there be any additional information or further clarification required, users are encouraged to contact the developer.