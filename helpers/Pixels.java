package helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
/*
 * Turns an image into a weighted probability matrix represented as a 2D array
 */
public class Pixels
{
  private BufferedImage image;
  //represent image as a 2D array of pixel values (grey scale)
  private double[][] grid;
  // total combined weight of the image (sum of probability matrix)
  private double totalValue;
  // input image is square, only store one of the lengths
  public int width;
  // name of image file
  private String name;
  // used to add noise to the previous result of the frame. 
  private double noiseWeight = .75;

  public Pixels(int width, String name)
  {
    try
    {
      // the line that reads the image file
      this.name = name;
      image = ImageIO.read(new File(name));

      // work with the image here ...
      this.width = width;
      createGrid(width);
    }
    catch (IOException e)
    {
      System.out.println("Error opening image");
    }
  }

  //prints the natural image details
  public void listInfo(){
    System.out.println("Height: " + image.getHeight() + ", Width: " + image.getWidth());
  }

  //creates a square matrix from input image
  public void createGrid(int width){
    //square image remember
    int height = width;
    //create intervals to change the resolution of the grid when coming from the image
    int intervalX = image.getWidth() / width;
    int intervalY = image.getHeight() / height;
    double[][] grayscale = new double[height][width];
    //iterate through the image to fill up the smaller gray scale matrix
    for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
            //get pixel from the loop count
            int color = image.getRGB(i * intervalX, j * intervalY);
            //fancy bit manipulation to get the individual color values from the pixel data
            int blue = color & 0xff;
            int green = (color & 0xff00) >> 8;
            int red = (color & 0xff0000) >> 16;
            //grey scale formula
            double grey = (double) (blue + green + red) / (3 * 256);
            //invert the grey to make gradient descent turn into gradient ascent by making the darker spots where the drones should go 
            //(its reversed somewhere else i bet)
            grayscale[j][i] = 1 - grey;
            totalValue += 1 - grey;
        }
    }
    //set the global variable to the grey scale matrix produced by this function
    grid = grayscale;
  }

  //returns the size of the natural image
  public int getSize(){
    return image.getWidth();
  }

  //return the name of the image (file)
  public String getName(){
    return name;
  }

  //prints to the terminal a python interpretable array that can be copied from the terminal and put into the ImageDisplay.py file
  public void printImage(){
      System.out.println("[");
      for(int i = 0; i < grid.length; i++){
          System.out.print("[");
          for(int j = 0; j < grid[i].length; j++){
              System.out.print(grid[i][j]);
              if(j != grid[i].length - 1){
                  System.out.print(",");
              }
          }
          System.out.print("]");
          if(i != grid.length - 1){
            System.out.print(",");
          }
          System.out.println();
      }
      System.out.print("]");
  }

  //pythagoras theorem
  private boolean inRadius(int x, int y, int radius){
    return Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(radius, 2);
  }

  //checks bounds of image
  private boolean validCoord(int x, int y){
    return x >= 0 && x < width && y >= 0 && y < width;
  }

  // think of a circle inscribed fully inside of a square, this function gets called for each coordinate of a drone in its first quadrant square
  // the circle is the drone's radius and the square is 2*radius.
  // I tried to implement an optimization here that would add the coordinates mirrored across the X and Y axis of the circle to get all 4 quadrants without recomputing
  // pythagoras' theorem for each point, But I ran into issues with it not counting that coordinate if the first one is invalid because it is out of bounds
  // so currently it is not implemented, but this whole thing works still by brute force

  //adds all coordinates into a hashmap that are covered by at least one drone. Hashmap ensures no double counted spots
  private void hashmapAdder(HashMap<Coords, Double> map, Coords pos, int x, int y){
    //bottom right
    int currentX = pos.getX() + x;
    int currentY = pos.getY() + y;
    if(validCoord(currentX, currentY)){
      map.put(new Coords(currentX, currentY), grid[currentX][currentY]);
    }
    //center spot, only check once
    if(x == 0 && y == 0){
      return;
    }
    //bottom left
    currentX = pos.getX() - x;
    currentY = pos.getY() + y;
    if(validCoord(currentX, currentY)){
      map.put(new Coords(currentX, currentY), grid[currentX][currentY]);
    }
    //top right
    currentX = pos.getX() + x;
    currentY = pos.getY() - y;
    if(validCoord(currentX, currentY)){
      map.put(new Coords(currentX, currentY), grid[currentX][currentY]);
    }
    //top left
    currentX = pos.getX() - x;
    currentY = pos.getY() - y;
    if(validCoord(currentX, currentY)){
      map.put(new Coords(currentX, currentY), grid[currentX][currentY]);
    }
  }

  // calculates the % covered of the weighted probability matrix given the positions of the drones
  public double coverage(Coords[] positions){
    HashMap<Coords, Double> covered = new HashMap<>();
    // go through each drone
    for(Coords pos : positions){
      //add quad to hashmap
      //System.out.println(pos);
      for(int i = 0; i < pos.radius; i++){
        for(int j = 0; j < pos.radius; j++){
          // is in bounds
          if(!inRadius(i, j, pos.radius)){
            continue;
          }
          // add to hashmap
          //System.out.println("Adding: X: " + i + ", Y: " + j);
          hashmapAdder(covered, pos, i, j);
          //System.out.println("Map size: " + covered.size());
        }
      }
    }
    //add up hashmap
    double droneCovered = 0;
    for(double val : covered.values()){
      droneCovered += val;
      //System.out.println("Value covered: " + val);
    }
    return (droneCovered / totalValue)  * 100;
  }

  //given a number of drones, create a 2*n matrix of random drone locations (using Coords datatype which is a row in this matrix)
  public Coords[] randomPositions(int n){
    Coords[] points = new Coords[n];
    for(int i = 0; i < n; i++){
      points[i] = new Coords((int)Math.round((Math.random() * width)), (int)Math.round((Math.random() * width)));
    }
    return points;
  }

  //given a the previous drone position solutions, add noise to the coordinates and return positions
  public Coords[] warmStart(Coords[] prev){
    Coords[] points = new Coords[prev.length];
    for(int i = 0; i < prev.length; i++){
      double averageX = (((Math.random() * width) * noiseWeight) + (prev[i].getX() * (1 - noiseWeight)));
      double averageY = (((Math.random() * width) * noiseWeight) + (prev[i].getY() * (1 - noiseWeight)));
      int newX = (int)Math.round(averageX);
      int newY = (int)Math.round(averageY);
      points[i] = new Coords(newX, newY);
    }
    return points;
  }

  //prints the locations of the input 2*n matrix
  public void printLocations(Coords[] locations){
    for(int i = 0 ; i < locations.length; i++){
      System.out.println("" + i + ") X: " + locations[i].getX() + ", Y: " + locations[i].getY());
    }
  }

}