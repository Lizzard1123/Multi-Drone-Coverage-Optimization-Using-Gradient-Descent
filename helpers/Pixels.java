package helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Pixels
{
  private BufferedImage image;
  private double[][] grid;
  private double totalValue;
  public int width;
  private String name;

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


  public void listInfo(){
    System.out.println("Height: " + image.getHeight() + ", Width: " + image.getWidth());
  }

  public void createGrid(int width){
    int height = width;
    int intervalX = image.getWidth() / width;
    int intervalY = image.getHeight() / width;
    double[][] grayscale = new double[height][width];

    for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
            int color = image.getRGB(i * intervalX, j * intervalY);
            int blue = color & 0xff;
            int green = (color & 0xff00) >> 8;
            int red = (color & 0xff0000) >> 16;
            double grey = (double) (blue + green + red) / (3 * 256);
            grayscale[j][i] = 1- grey;
            totalValue += 1 - grey;
        }
    }
    grid = grayscale;
  }

  public int getSize(){
    return image.getWidth();
  }

  public String getName(){
    return name;
  }

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

  private boolean inRadius(int x, int y, int radius){
    return Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(radius, 2);
  }

  private boolean validCoord(int x, int y){
    return x >= 0 && x < width && y >= 0 && y < width;
  }

  private void hashmapAdder(HashMap<Coords, Double> map, Coords pos, int x, int y){
    //bottom right
    int currentX = pos.getX() + x;
    int currentY = pos.getY() + y;
    if(validCoord(currentX, currentY)){
      map.put(new Coords(currentX, currentY), grid[currentX][currentY]);
    }
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

  public Coords[] randomPositions(int n){
    Coords[] points = new Coords[n];
    for(int i = 0; i < n; i++){
      points[i] = new Coords((int)Math.round((Math.random() * width)), (int)Math.round((Math.random() * width)));
    }
    return points;
  }

  public void printLocations(Coords[] locations){
    for(int i = 0 ; i < locations.length; i++){
      System.out.println("" + i + ") X: " + locations[i].getX() + ", Y: " + locations[i].getY());
    }
  }

}