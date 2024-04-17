
// Importing libraries
import java.io.*; 
import java.util.TreeSet; 
// AWT and Swing libraries for the GUI
import java.awt.*; 
import java.awt.event.*; 
import java.awt.image.*; 
import javax.imageio.*; 
import javax.swing.*; 
import java.util.Arrays; 

// Class declaration
// Extending the Component class because we want to display the image on a frame
// Implementing the ActionListener interface to handle the events like selecting the image processing operation and saving the image
public class ImageProcessing extends Component implements ActionListener {

    
    // Array to store the description of the processing operations
    String descs[] = {"Original", "Negative", "Rescale", "Shift", "Rescale and Shift", "Arithmetic", "Boolean", "Point Processing",
    "Bit-Plane Slicing", "Smoothing", "Edge Detection", "Order Statistic Filtering", "Histogram Equalisation", "Thresholding"};
    // storing the index of the operation
    int opIndex; 
    int lastOp; 

    //  variables to store the image and the filtered image (after processing)
    private BufferedImage bi, biFiltered;

    // width and height of the image
    int w, h;
    // constructor to read the image
    public ImageProcessing() {
        try {

            // Reading the image
            bi = ImageIO.read(new File("image1.jpg"));

            // getting and storing width and height of the image
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            
            // checking if the image is not of type INT_RGB
            // this is done to convert the image to INT_RGB type
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics(); // bi2 is the new image and big represents the graphics of the new image
                big.drawImage(bi, 0, 0, null); 
                biFiltered = bi = bi2; 
            }
        } catch (IOException e) {
            // error handling
            System.out.println("Image could not be read");
            System.exit(1);
        }
    }

    // necessary function to get the preferred size of the image
    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    // getting the description of the image processing operations
    String[] getDescriptions() {
        return descs;
    }

    // getting the formats of the image
    String[] getFormats() {
        String[] formats = {"bmp", "gif", "jpeg", "jpg", "png"};  // 5 formats hardcoded
        TreeSet<String> formatSet = new TreeSet<String>(); 
        for (String s : formats) { 
            formatSet.add(s.toLowerCase()); 
        }
        // getting the formats in an array so that it can be returned
        return formatSet.toArray(new String[0]); 
    }

    // setting the index of the operation
    // necessary for the image to be processed
    void setOpIndex(int i) {
        opIndex = i;
    }
    
    // function to paint the image which just means displaying the image on a frame
    public void paint(Graphics g) {
        filterImage(); 
        g.drawImage(biFiltered, 0, 0, null); 
    }

    // function to convert the image to a 3D array
    // this is done to perform processing on the image
    private static int[][][] convertToArray(BufferedImage image) {
        int width = image.getWidth(); 
        int height = image.getHeight(); 
        int[][][] result = new int[width][height][4]; 
        // pixel value retrieval
        // a - alpha, r - red, g - green, b - blue
        // alpha is the transparency value
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y); 
                int a = (p >> 24) & 0xff; 
                int r = (p >> 16) & 0xff; 
                int g = (p >> 8) & 0xff; 
                int b = p & 0xff; 
                result[x][y][0] = a; 
                result[x][y][1] = r; 
                result[x][y][2] = g; 
                result[x][y][3] = b; 
            }
        }
        return result; 
    }

    // converting the 3d array into a buffered image so that it can be displayed on the frame
    public BufferedImage convertToBimage(int[][][] TmpArray) {
        int width = TmpArray.length;
        int height = TmpArray[0].length;
        //  TYPE_INT_RGB is used to store the pixel values in the format of 0xAARRGGBB - alpha, red, green, blue
        BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                int p = (a << 24) | (r << 16) | (g << 8) | b; // These number choices are due to the TYPE_INT_RGB format, it is a 32 bit integer so 8 bits for each color
                tmpimg.setRGB(x, y, p);
            }
        }
        return tmpimg;
    }

    // function to convert the image negative
    // This is achieved by subtracting the pixel values from 255 - meaning inverting the pixel values
    public BufferedImage ImageNegative(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = 255 - ImageArray[x][y][1];
                ImageArray[x][y][2] = 255 - ImageArray[x][y][2];
                ImageArray[x][y][3] = 255 - ImageArray[x][y][3];
            }
        }
        return convertToBimage(ImageArray);
    }

    /*
    function to rescale the image and/or shift the pixel values.
    this is done by multiplying the pixel values by a factor and then adding a shift value in that order.
    Since the function is called modify image, it can be used to rescale and/or shift the pixel values
    */
    public BufferedImage modifyImage(BufferedImage timg, double factor, int shiftValue) { 
        int w = timg.getWidth(); 
        int h = timg.getHeight(); 
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
        int[][][] result = convertToArray(timg); 
    
        // minimum and maximum pixel values found here
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int x = 0; x < w; x++) { 
            for (int y = 0; y < h; y++) { 
                for (int c = 0; c < 3; c++) { 
                    min = Math.min(min, result[x][y][c]); 
                    max = Math.max(max, result[x][y][c]); 
                }
            }
        }
    
        // rescaling the pixel values
        //done by multiplying the pixel values by the factor and then adding the shift value to it
        for (int x = 0; x < w; x++) { 
            for (int y = 0; y < h; y++) { 
                for (int c = 0; c < 3; c++) { 
                    int rescaledValue = (int) ((result[x][y][c] - min) * factor); 
                    int randomValue = (int) (Math.random() * 256) - 128; 
                    int newValue = rescaledValue + randomValue + shiftValue; 
                    newValue = Math.max(0, Math.min(255, newValue)); 
                    img.setRGB(x, y, (newValue << 16) | (newValue << 8) | newValue); 
                }
            }
        }
    //returning the image as a buffered image
        return img;
    }

    // function to perform arithmetic operations on the image like addition, subtraction, multiplication and division
    //this is done by performing the operation on the pixel values of the two images
    public BufferedImage arithmeticOperation(BufferedImage img1, BufferedImage img2, char operation) { 
        int width = img1.getWidth(); 
        int height = img1.getHeight(); 
        int[][][] ImageArray1 = convertToArray(img1); 
        int[][][] ImageArray2 = convertToArray(img2); 
        int[][][] ResultArray = new int[width][height][4]; 
        
        //two for loops to go over the pixel values of the two images
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < width; x++) {
                ResultArray[x][y][0] = ImageArray1[x][y][0]; 
                switch (operation) {
                    case '+': 
                        ResultArray[x][y][1] = ImageArray1[x][y][1] + ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] + ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] + ImageArray2[x][y][3];
                        break;
                    case '-': 
                        ResultArray[x][y][1] = ImageArray1[x][y][1] - ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] - ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] - ImageArray2[x][y][3];
                        break;
                    case '*': 
                        ResultArray[x][y][1] = ImageArray1[x][y][1] * ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] * ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] * ImageArray2[x][y][3];
                        break; 
                    case '/': 
                        ResultArray[x][y][1] = ImageArray2[x][y][1] != 0 ? ImageArray1[x][y][1] / ImageArray2[x][y][1] : 0;
                        ResultArray[x][y][2] = ImageArray2[x][y][2] != 0 ? ImageArray1[x][y][2] / ImageArray2[x][y][2] : 0;
                        ResultArray[x][y][3] = ImageArray2[x][y][3] != 0 ? ImageArray1[x][y][3] / ImageArray2[x][y][3] : 0;
                        break;
                }
            }
        }
        
        return convertToBimage(ResultArray);
    }

    // function to perform boolean operations on two images like AND, OR, XOR
    public BufferedImage booleanOperation(BufferedImage img1, BufferedImage img2, char operation) { 
        int width = img1.getWidth(); 
        int height = img1.getHeight();  
        int[][][] ImageArray1 = convertToArray(img1); 
        int[][][] ImageArray2 = convertToArray(img2); 
        int[][][] ResultArray = new int[width][height][4]; 

        
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < width; x++) {
                ResultArray[x][y][0] = ImageArray1[x][y][0]; 
                switch (operation) {
                    case '&': // AND operation
                        ResultArray[x][y][1] = ImageArray1[x][y][1] & ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] & ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] & ImageArray2[x][y][3];
                        break;
                    case '|': // OR operation
                        ResultArray[x][y][1] = ImageArray1[x][y][1] | ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] | ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] | ImageArray2[x][y][3];
                        break;
                    case '^': // XOR operation
                        ResultArray[x][y][1] = ImageArray1[x][y][1] ^ ImageArray2[x][y][1];
                        ResultArray[x][y][2] = ImageArray1[x][y][2] ^ ImageArray2[x][y][2];
                        ResultArray[x][y][3] = ImageArray1[x][y][3] ^ ImageArray2[x][y][3];
                        break;
                }
            }
        }
        return convertToBimage(ResultArray);
    }

    // The difference between arithmetic + and boolean & is that arithmetic + is used to perform operations on the pixel values
    // whereas boolean & is used to perform operations on the bits of the pixel values - bitwise operations

    // function to perform point processing on the image
    //uses a lut also known as a look up table to perform the point processing
    // the lut is a 2D array which stores the pixel values to be replaced also known as the mapping
    public BufferedImage pointProcessing(BufferedImage timg, int[][] lut) { 
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < width; x++) {
                
                // the pixel values are replaced by the values in the lut. A modulo % operation is performed to ensure that the pixel values are within the range
                ImageArray[x][y][1] = lut[ImageArray[x][y][1] % lut.length][0]; 
                ImageArray[x][y][2] = lut[ImageArray[x][y][2] % lut.length][1]; 
                ImageArray[x][y][3] = lut[ImageArray[x][y][3] % lut.length][2]; 
            }
        }
        return convertToBimage(ImageArray);
    }

    // this function is used to perform bit plane slicing on the image - by setting the pixel values to 0 or 255 based on the bit Plane
    public BufferedImage bitPlaneSlicing(BufferedImage timg, int bitPlane) {
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int mask = 1 << bitPlane; 
        
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = (ImageArray[x][y][1] & mask) != 0 ? 255 : 0; 
                ImageArray[x][y][2] = (ImageArray[x][y][2] & mask) != 0 ? 255 : 0; 
                ImageArray[x][y][3] = (ImageArray[x][y][3] & mask) != 0 ? 255 : 0; 
            }
        }
        return convertToBimage(ImageArray);
    }

    // function to smooth the image done by applying a smoothing kernel to the image
    public BufferedImage smoothImage(BufferedImage timg) {
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ResultArray = new int[width][height][4]; 
        
        // kernel for smoothing, using a 3x3 kernel with all values as 1/9 to ensure that the pixel values are averaged.
        // 1/9 is used because the sum of the kernel values should be 1
        float[][] kernel = {{1 / 9.0f, 1 / 9.0f, 1 / 9.0f},
                            {1 / 9.0f, 1 / 9.0f, 1 / 9.0f},
                            {1 / 9.0f, 1 / 9.0f, 1 / 9.0f}};
        
        for (int y = 1; y < height - 1; y++) { // ignoring the border pixels as the kernel can't be applied to them
            for (int x = 1; x < width - 1; x++) { 
                float sumR = 0, sumG = 0, sumB = 0; // sum of the pixel values after applying kernel 
                for (int j = -1; j <= 1; j++) { // applying the kernel to the pixel values
                    for (int i = -1; i <= 1; i++) { 
                        sumR += ImageArray[x + i][y + j][1] * kernel[j + 1][i + 1];  // multiplying pixel values with the kernel values
                        sumG += ImageArray[x + i][y + j][2] * kernel[j + 1][i + 1]; 
                        sumB += ImageArray[x + i][y + j][3] * kernel[j + 1][i + 1]; 
                    }
                }
                
                ResultArray[x][y][0] = ImageArray[x][y][0];
                ResultArray[x][y][1] = (int) sumR;
                ResultArray[x][y][2] = (int) sumG;
                ResultArray[x][y][3] = (int) sumB;
            }
        }
        return convertToBimage(ResultArray);
    }
    /*
    function to perform edge detection on the image by applying a kernel to the image again
    the kernel used here is a 3x3 kernel with the center value as 8 and the surrounding values as -1.
    The reason the center value is 8 is because the sum of the kernel values should be 0.
    the kernel values should be 0 because edge detection is done by finding the difference between the pixel values
    */
    public BufferedImage edgeDetection(BufferedImage timg) {
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ResultArray = new int[width][height][4]; 
        
        
        float[][] kernel = {{-1, -1, -1},
                            {-1, 8, -1},
                            {-1, -1, -1}};
        
        for (int y = 1; y < height - 1; y++) {
            
            for (int x = 1; x < width - 1; x++) {
                float sumR = 0, sumG = 0, sumB = 0; 
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        sumR += ImageArray[x + i][y + j][1] * kernel[j + 1][i + 1]; 
                        sumG += ImageArray[x + i][y + j][2] * kernel[j + 1][i + 1]; 
                        sumB += ImageArray[x + i][y + j][3] * kernel[j + 1][i + 1]; 
                    }
                }
                
                // ensuring that the pixel values are within the range
                // ResultArray[x][y][0] = ImageArray[x][y][0]; is not required as alpha value isnt changed, but kept due to consistency
                ResultArray[x][y][0] = ImageArray[x][y][0];
                ResultArray[x][y][1] = (int) Math.max(0, Math.min(255, sumR));
                ResultArray[x][y][2] = (int) Math.max(0, Math.min(255, sumG));
                ResultArray[x][y][3] = (int) Math.max(0, Math.min(255, sumB));
            }
        }
        return convertToBimage(ResultArray);
    }

    // function to perform order statistic filtering on the image - this is done by finding the median value of the pixel values
    // the median value is found by sorting the pixel values and then finding the middle value
    public BufferedImage orderStatisticFiltering(BufferedImage timg, int filterSize) { 
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ResultArray = new int[width][height][4]; 
        int radius = filterSize / 2; // radius of the filter is calculated to ensure that the filter is applied to the correct pixels
        
        for (int y = radius; y < height - radius; y++) {
            // applying the filter to all the pixel values
            for (int x = radius; x < width - radius; x++) {
                int[] redValues = new int[filterSize * filterSize]; 
                int[] greenValues = new int[filterSize * filterSize]; 
                int[] blueValues = new int[filterSize * filterSize]; 
                int index = 0;
                
                for (int j = -radius; j <= radius; j++) {
                    
                    for (int i = -radius; i <= radius; i++) {
                        redValues[index] = ImageArray[x + i][y + j][1]; 
                        greenValues[index] = ImageArray[x + i][y + j][2]; 
                        blueValues[index] = ImageArray[x + i][y + j][3]; 
                        index++;
                    }
                }
                // sorting the pixel values using the inbuilt sort function - saving time
                Arrays.sort(redValues); 
                Arrays.sort(greenValues);
                Arrays.sort(blueValues);
                int medianIndex = filterSize * filterSize / 2;
                ResultArray[x][y][0] = ImageArray[x][y][0];
                ResultArray[x][y][1] = redValues[medianIndex];
                ResultArray[x][y][2] = greenValues[medianIndex];
                ResultArray[x][y][3] = blueValues[medianIndex];
            }
        }
        return convertToBimage(ResultArray);
    }
    // function to filter the image - done by checking the operation index and then performing the operation
    public void filterImage() {
        if (opIndex == lastOp) {
            return;
        }
    //switch case to check the operation index and then perform the processing on the image
        lastOp = opIndex; // storing the last operation index to make sure that the operation is not repeated
        switch (opIndex) {
            case 0:
                biFiltered = bi; // original image
                return;
            case 1:
                biFiltered = ImageNegative(bi); // negative
                return;
            case 2:
                biFiltered = modifyImage(bi, 1.5, 0); // rescale by 1.5 without shifting
                return;
            case 3:
                biFiltered = modifyImage(bi, 1.0, 50); // shift by 50 without rescaling
                return;
            case 4:
                biFiltered = modifyImage(bi, 1.2, 30); // rescale by 1.2 and shift by 30
                return;
            case 5:
            //arithmetic operation - addition. The operation can be changed by changing the hardcoded operator
                try {
                    BufferedImage img2 = ImageIO.read(new File("image2.jpg")); // uses the 2nd image to perform the operation alongside the 1st image
                    biFiltered = arithmeticOperation(bi, img2, '-'); 
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
                return;
            case 6:
            // boolean operation - AND. The operation can be changed by changing the hardcoded operator
                try {
                    BufferedImage img3 = ImageIO.read(new File("image3.jpg")); // uses the 3rd image to perform the operation alongside the 1st image
                    biFiltered = booleanOperation(bi, img3, '&'); 
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
                return;
            case 7:
            /*
            Point processing - the pixel values are replaced by the values in the Look up table. 
            A modulo (%) operation is performed to ensure that the pixel values are within the range
            */
                int[][] lut = {{0, 0, 0}, {64, 64, 64}, {128, 128, 128}, {192, 192, 192}, {255, 255, 255}};
                biFiltered = pointProcessing(bi, lut); 
                return;
            case 8:
                biFiltered = bitPlaneSlicing(bi, 2); // bit plane slicing
                return;
            case 9:
                biFiltered = smoothImage(bi); // smoothing
                return;
            case 10:
                biFiltered = edgeDetection(bi); // edge detection
                return;
            case 11:
                biFiltered = orderStatisticFiltering(bi, 3);// order statistic filtering 
                return;
            case 12:
                biFiltered = histogramEqualisation(bi); // histogram equalisation
                return;
            case 13:
                biFiltered = thresholding(bi); // thresholding
                return;
        }
    }

    // function to handle the action events - handling the events like selecting the operation and saving the image

    public void actionPerformed(ActionEvent e) { 
        JComboBox cb = (JComboBox) e.getSource(); 
        if (cb.getActionCommand().equals("SetFilter")) { 
            setOpIndex(cb.getSelectedIndex()); 
            repaint(); // Repaint the image after the processing is completed
        } else if (cb.getActionCommand().equals("Formats")) { 
            String format = (String) cb.getSelectedItem(); 
            File saveFile = new File("savedimage." + format); // saving the image
            JFileChooser chooser = new JFileChooser(); 
            chooser.setSelectedFile(saveFile); 
            int rval = chooser.showSaveDialog(cb); 
            if (rval == JFileChooser.APPROVE_OPTION) { 
                saveFile = chooser.getSelectedFile(); 
                try { 
                    ImageIO.write(biFiltered, format, saveFile); 
                } catch (IOException ex) {  // error handling
                    System.out.println("Image could not be saved");
                }
            }
        }
    }

/*
 * Function to perform histogram equalisation on the image
 * this is done by finding the histogram of the image and then finding a cumulative histogram
 * the cumulative histogram is then used to find the new pixel values
 * the new pixel values are then used to create a new image
 */
    public BufferedImage histogramEqualisation(BufferedImage timg) {
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int[] histogram = new int[256]; 
        int[] cumulativeHistogram = new int[256]; 
    
        // here the histogram of the image is found
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                histogram[ImageArray[x][y][1]]++; 
            }
        }
    
        // the cumulative histogram is found here (just the sum of the histogram values until that point)
        cumulativeHistogram[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            cumulativeHistogram[i] = cumulativeHistogram[i - 1] + histogram[i];
        }
    
        // normalising the cumulative histogram to find the new pixel values
        int[][][] ResultArray = new int[width][height][4];
        
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < width; x++) {
                int oldPixel = ImageArray[x][y][1]; 
                // we use the cumulative histogram to find the new pixel values by normalising the cumulative Histogram
                //normalisation is done by dividing the cumulative histogram by the total number of pixels 
                int newPixel = (int) (((float) cumulativeHistogram[oldPixel] / (width * height)) * 255); 
                ResultArray[x][y][0] = ImageArray[x][y][0];
                ResultArray[x][y][1] = newPixel;
                ResultArray[x][y][2] = newPixel;
                ResultArray[x][y][3] = newPixel;
            }
        }
        
    
        return convertToBimage(ResultArray);
    }

    // function to perform thresholding on the image
    // thresholding is done by finding the optimal threshold value automatically
    // the optimal threshold value is found by finding the variance between the two classes
    // the class with the minimum variance is then selected as the optimal threshold value
    // the pixel values are then replaced by 0 or 255 based on the threshold value

    // This automatic thresholding technique was proposed by Otsu in 1979 known as Otsu's method

    public BufferedImage thresholding(BufferedImage timg) {
        int width = timg.getWidth(); 
        int height = timg.getHeight(); 
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ResultArray = new int[width][height][4]; 

        // finding the histogram of the image to find the optimal threshold value
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                histogram[ImageArray[x][y][1]]++;
            }
        }
        // Finding the optimal threshold value
        // done by finding the variance between the two classes
        int totalPixels = width * height;
        double sumTotal = 0;
        for (int i = 0; i < 256; i++) {
            sumTotal += i * histogram[i]; // sum of the pixel values multiplied by the histogram values to find the total sum of the pixel values
        }
        //finding the optimal threshold value
        double sumBG = 0;
        int weightBG = 0;
        double maxVariance = Double.MAX_VALUE; 
        int optimalThreshold = 0; 
        
        // in the loop below, the variance between the two classes is found
        for (int threshold = 0; threshold < 256; threshold++) { 
            weightBG += histogram[threshold]; 
            if (weightBG == 0) continue; 

            double weightFG = totalPixels - weightBG; 
            // finding the mean values of the two classes - background and foreground
            sumBG += (double) threshold * histogram[threshold];  
            double meanBG = sumBG / weightBG; 
            double meanFG = (sumTotal - sumBG) / weightFG; 
            
            double varianceBG = 0;
            double varianceFG = 0;
            // finding the variance between the two classes by finding the deviation of the pixel values from the mean values and then squaring them
            for (int i = 0; i < 256; i++) {
                double deviation = (double) i - meanBG; 
                varianceBG += deviation * deviation * histogram[i]; 

                deviation = (double) i - meanFG; 
                varianceFG += deviation * deviation * histogram[i]; 
            }
            
            varianceBG /= weightBG;
            varianceFG /= weightFG;
            // finding the within class variance, the sum of the variances of the two classes
            // the threshold value with the minimum within class variance is selected as the optimal threshold value
            double withinClassVariance = weightBG * varianceBG + weightFG * varianceFG;
            
            if (withinClassVariance < maxVariance) {
                maxVariance = withinClassVariance; // updating the maxVariance value if a new minimum is found
                optimalThreshold = threshold; // updating the optimal threshold value to whatever the current threshold value is
            }
        }

        // Then we replace the pixel values by 0 or 255 based on the threshold value
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = ImageArray[x][y][1];
                
                int newPixelValue = pixelValue > optimalThreshold ? 255 : 0;
                
                ResultArray[x][y][0] = ImageArray[x][y][0];
                ResultArray[x][y][1] = newPixelValue;
                ResultArray[x][y][2] = newPixelValue;
                ResultArray[x][y][3] = newPixelValue;
            }
        }

        return convertToBimage(ResultArray);
    }
    // running the program using the main function
    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo"); 
        f.addWindowListener(new WindowAdapter() { 

            
            public void windowClosing(WindowEvent e) {
                System.exit(0); 
            }
        });

        // creating the frame and adding the image processing class to the frame
        ImageProcessing de = new ImageProcessing();
        f.add("Center", de); 
        JComboBox<String> choices = new JComboBox<>(de.getDescriptions()); 
        choices.setActionCommand("SetFilter"); 
        choices.addActionListener(de); 
        JComboBox<String> formats = new JComboBox<>(de.getFormats()); 
        formats.setActionCommand("Formats"); 
        formats.addActionListener(de); 
        JPanel panel = new JPanel(); 
        panel.add(choices); 
        panel.add(new JLabel("Save As")); 
        panel.add(formats); 
        f.add("North", panel); 
        f.pack(); 
        f.setVisible(true); 
    }
}