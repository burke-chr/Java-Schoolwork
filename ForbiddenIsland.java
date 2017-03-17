import java.util.ArrayList;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// function objects for drawing images on a scene
interface IDraw<T> {

  // Draws the given t on the given scene
  WorldScene draw(T t, WorldScene scene, int waterHeight);
}

// function object for drawing cells
class DrawCell implements IDraw<Cell> {

  // Draws the given cell on the given scene
  public WorldScene draw(Cell c, WorldScene scene, int waterHeight) {
    scene.placeImageXY(new RectangleImage(ForbiddenIslandWorld.CELL_SIZE, 
        ForbiddenIslandWorld.CELL_SIZE, OutlineMode.SOLID,
        c.getColor(waterHeight)), 
        (c.x * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2), 
        (c.y * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2));
    scene.placeImageXY(new TextImage(Integer.toString(c.height.intValue()), 7, Color.black),
        (c.x * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2), 
        (c.y * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2));
    return scene;

  }
}

// A generic list
interface IList<T> {

  // Draw this list of T onto the given scene, and return the scene when empty
  WorldScene addListToScene(WorldScene scene, IDraw<T> drawer, int waterHeight);
}

// A generic empty list
class MtList<T> implements IList<T> {

  // Draw this mtlist of T onto the given scene, and return the scene when empty
  public WorldScene addListToScene(WorldScene scene, IDraw<T> drawer, int waterHeight) {
    return scene;
  }
}

// A generic nonempty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // Constructor
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // Draw this conslist of T onto the given scene, and return the scene when empty
  public WorldScene addListToScene(WorldScene scene, IDraw<T> drawer, int waterHeight) {
    drawer.draw(this.first, scene, waterHeight);
    return this.rest.addListToScene(scene, drawer, waterHeight);
  }
}


// Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  Double height;
  // In logical coordinates, with the origin at the top-left corner of the screen
  // the x-coordinate
  int x;
  // the y-coordinate
  int y;
  // the four adjacent cells to this one
  // the left cell
  Cell left;
  // the top cell
  Cell top;
  // the right cell
  Cell right;
  // the bottom cell
  Cell bottom;
  // reports whether this cell is flooded or not
  boolean isFlooded;

  // Initial Constructor
  Cell(double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
    this.isFlooded = false;
  }

  // returns the color that this cell should be
  Color getColor(int waterHeight) {
    Color initColor = new Color(10, 255, 10);
    int heightAboveSeaLevel = this.height.intValue() - waterHeight;

    return new Color((initColor.getRed() + (heightAboveSeaLevel * 7)),
        (initColor.getGreen()),
        (initColor.getBlue() + (heightAboveSeaLevel * 7)));

  }
}

// represents a single cell of the game area, mainly an ocean cell
class OceanCell extends Cell {
  OceanCell(double height, int x, int y) {
    super(height, x, y);
  }

  // returns the color that this ocean cell should be
  Color getColor(int waterHeight) {
    return new Color(0, 0, 255);
  }
}

class ForbiddenIslandWorld extends World {
  // Defines the size of the world canvas, in cells, 
  // from 0 to the constant
  static final int ISLAND_SIZE = 64;
  static final int CELL_SIZE = 14;
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight;

  // Constructor
  ForbiddenIslandWorld(String mode) {
    this.waterHeight = 0;
    if (mode.equals("mountain")) {
      this.board = this.arraytoList(makeCellList(this.makeMtHeights()));
    }
    else if (mode.equals("random")) {
      this.board = this.arraytoList(makeCellList(this.makeRandomHeights()));
    }
    else if (mode.equals("terrain")) {
      this.board = this.arraytoList(makeCellList(this.makeTerrainHeights()));
    }
    else {
      throw new RuntimeException("Not a valid mode type");
    }
  }

  // Create an arraylist of arraylists of doubles
  // representing each cells height in mountain mode
  ArrayList<ArrayList<Double>> makeMtHeights() {
    ArrayList<ArrayList<Double>> acc = new ArrayList<ArrayList<Double>>();
    for (double i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Double> doubleacc = new ArrayList<Double>();
      for (double j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        doubleacc.add(this.calcManhattenDist(i, j));
      } 
      acc.add(doubleacc);
    }
    return acc;
  }

  // Create an arraylist of arraylists of doubles
  // representing each cells height in random mode
  ArrayList<ArrayList<Double>> makeRandomHeights() {
    ArrayList<ArrayList<Double>> acc = new ArrayList<ArrayList<Double>>();
    for (double i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Double> doubleacc = new ArrayList<Double>();
      for (double j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        if (this.calcManhattenDist(i, j) > 0) {
          doubleacc.add((new Random().nextInt(32) + 1) * 1.0);
        }
        else {
          doubleacc.add(0.0);
        }
      } 
      acc.add(doubleacc);
    }
    return acc;
  }

  // Create an arraylist of arraylist of doubles
  // representing each cell's height in terrain mode
  ArrayList<ArrayList<Double>> makeTerrainHeights() {
    ArrayList<ArrayList<Double>> array = new ArrayList<ArrayList<Double>>();
    for (double i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Double> doublearray = new ArrayList<Double>();
      for (double j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        doublearray.add(0.0);

      } 
      array.add(doublearray);
    }
    array.get(0).set(0, 0.0);
    array.get(0).set(ForbiddenIslandWorld.ISLAND_SIZE, 0.0);
    array.get(ForbiddenIslandWorld.ISLAND_SIZE).set(0, 0.0);
    array.get(ForbiddenIslandWorld.ISLAND_SIZE).set(ForbiddenIslandWorld.ISLAND_SIZE, 0.0);

    array.get(ForbiddenIslandWorld.ISLAND_SIZE / 2).set(ForbiddenIslandWorld.ISLAND_SIZE / 2, 
        (ForbiddenIslandWorld.ISLAND_SIZE / 2 * 1.0));
    array.get(ForbiddenIslandWorld.ISLAND_SIZE / 2).set(0, 1.0);
    array.get(ForbiddenIslandWorld.ISLAND_SIZE).set(ForbiddenIslandWorld.ISLAND_SIZE / 2, 1.0);
    array.get(0).set(ForbiddenIslandWorld.ISLAND_SIZE / 2, 1.0);
    array.get(ForbiddenIslandWorld.ISLAND_SIZE / 2).set(ForbiddenIslandWorld.ISLAND_SIZE, 1.0);

    makeTerrainHeightsHelp(array, new Posn(0, 0), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, 0),
        new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE / 2), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2), 
        8);
    makeTerrainHeightsHelp(array, new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, 0), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, 0),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        8);
    makeTerrainHeightsHelp(array, new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE / 2), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE),
        8);
    makeTerrainHeightsHelp(array, new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, 
        ForbiddenIslandWorld.ISLAND_SIZE / 2), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE), 
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE), 
        8);

    return array;
  }

  // EFFECT: Helper for makeTerrainHeights, updates at the given posns
  void makeTerrainHeightsHelp(ArrayList<ArrayList<Double>> array, 
      Posn tl, Posn tr, Posn bl, Posn br, int variance) {
    Posn t = new Posn((tl.x + tr.x) / 2, (tl.y + tr.y) / 2);
    Posn l = new Posn((tl.x + bl.x) / 2, (tl.y + bl.y) / 2);
    Posn r = new Posn((tr.x + br.x) / 2, (tr.y + br.y) / 2);
    Posn b = new Posn((bl.x + br.x) / 2, (bl.y + br.y) / 2);
    Posn m = new Posn(t.x, l.y);
    Double randnum = (new Random().nextInt(variance) - variance / 2) * 1.0;
    Double tlval = array.get(tl.y).get(tl.x);
    Double trval = array.get(tr.y).get(tr.x);
    Double blval = array.get(bl.y).get(bl.x);
    Double brval = array.get(br.y).get(br.x);
    Double tval = (((tlval + trval) / 2) + (new Random().nextInt(variance) - variance / 2) * 1.0);
    Double lval = ((tlval + blval) / 2) + (new Random().nextInt(variance) - variance / 2) * 1.0;
    Double rval = ((trval + brval) / 2) + (new Random().nextInt(variance) - variance / 2) * 1.0;
    Double bval = ((blval + brval) / 2) + (new Random().nextInt(variance) - variance / 2) * 1.0;
    Double mval = (tlval + trval + blval + brval) / 4 + 
        (new Random().nextInt(variance) - variance / 2) * 1.0;

    if (tr.x - tl.x == 1) {
      return ;
    }
    else {
      array.get(t.y).set(t.x, tval);
      array.get(l.y).set(l.x, lval);
      array.get(r.y).set(r.x, rval);
      array.get(b.y).set(b.x, bval);
      array.get(m.y).set(m.x, mval);

      makeTerrainHeightsHelp(array, tl, t, l, m, variance - 1);
      makeTerrainHeightsHelp(array, t, tr, m, r, variance - 1);
      makeTerrainHeightsHelp(array, l, m, bl, b, variance - 1);
      makeTerrainHeightsHelp(array, m, r, b, br, variance - 1);
    }

  }





  // calculates the manhatten distance of a cell at the given i, j coordinates
  Double calcManhattenDist(double i, double j) {
    return (ForbiddenIslandWorld.ISLAND_SIZE / 2) - 
        (Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2.0) - j)
        + Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2.0) - i));
  }

  // turns an ArrayList<ArrayList<Double>> into an ArrayList<ArrayList<Cell>> 
  // using the doubles as heights
  ArrayList<ArrayList<Cell>> makeCellList(ArrayList<ArrayList<Double>> heights) {
    ArrayList<ArrayList<Cell>> acc = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      ArrayList<Cell> cellacc = new ArrayList<Cell>();
      for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        if (heights.get(i).get(j) > 0) {
          cellacc.add(new Cell(heights.get(i).get(j), j, i));
        }
        else {
          cellacc.add(new OceanCell(heights.get(i).get(j), j, i));
        }
      } 
      acc.add(cellacc);
    }
    return acc;
  }

  // turns a double arraylist to an IList
  IList<Cell> arraytoList(ArrayList<ArrayList<Cell>> array) {
    return arraytoListHelp(array, new MtList<Cell>());
  }

  // helper for arraytolist with list accumulator
  IList<Cell> arraytoListHelp(ArrayList<ArrayList<Cell>> array, IList<Cell> list) {
    for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        list = new ConsList<Cell>(array.get(i).get(j), list);
      } 
    }
    return list;
  }

  // Draws the WorldScene of the world
  public WorldScene makeScene() {
    return this.board.addListToScene(
        getEmptyScene(), new DrawCell(), this.waterHeight);
  }
}

// examples and tests for the game
// All tests assume an island size constant of 2
class ExamplesForbiddenIsland {
  ForbiddenIslandWorld mountain = new ForbiddenIslandWorld("mountain");
  ForbiddenIslandWorld random = new ForbiddenIslandWorld("random");
  ForbiddenIslandWorld terrain = new ForbiddenIslandWorld("terrain");

  // tests for island height creation
  void testHeightCreation(Tester t) {
    t.checkExpect(this.terrain.board, null);
  }


  // Main method to run the world program
  public static void main(String[] argv) {

    // run the game
    ForbiddenIslandWorld w = new ForbiddenIslandWorld("terrain");
    w.bigBang((ForbiddenIslandWorld.ISLAND_SIZE + 1) * (ForbiddenIslandWorld.CELL_SIZE), 
        (ForbiddenIslandWorld.ISLAND_SIZE + 1) * (ForbiddenIslandWorld.CELL_SIZE), 0.1);
  }
}