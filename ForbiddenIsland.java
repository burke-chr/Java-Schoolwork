
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
    scene.placeImageXY(
        new RectangleImage(ForbiddenIslandWorld.CELL_SIZE, ForbiddenIslandWorld.CELL_SIZE,
            OutlineMode.SOLID, c.getColor(waterHeight)),
        (c.x * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2),
        (c.y * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2));
    return scene;
  }
}

// Visitors for ILists
interface IListVisitor<T, R> {

  // Visits from the MtList class
  R visitMt(MtList<T> list);

  // Visits from the ConsList class
  R visitCons(ConsList<T> list);
}

// Visitor for adding an IList<Cell>s items to a scene
class AddListToScene implements IListVisitor<Cell, WorldScene> {

  WorldScene scene;
  IDraw<Cell> drawer;
  int waterHeight;

  // Constructor
  AddListToScene(WorldScene scene, IDraw<Cell> drawer, int waterHeight) {
    this.scene = scene;
    this.drawer = drawer;
    this.waterHeight = waterHeight;
  }

  // Visits from the MtList class, adds each cell in the
  // given MtList<Cell> to this visitors scene, then returns it
  public WorldScene visitMt(MtList<Cell> list) {
    return this.scene;
  }

  // Visits from the ConsList class, adds each cell in the
  // given ConsList<Cell> to this visitors scene, then returns it
  public WorldScene visitCons(ConsList<Cell> list) {
    for (Cell c : list) {
      drawer.draw(c, scene, waterHeight);
    }
    return this.scene;
  }
}

// visitor for calculating the coastline cells of an IList<Cell>
class CalcCoastalCells implements IListVisitor<Cell, IList<Cell>> {

  int waterHeight;

  // Constructor
  CalcCoastalCells(int waterHeight) {
    this.waterHeight = waterHeight;
  }

  // Visits from the MtList class, calculates the given
  // Mtlist<Cell>s coastal cells
  public IList<Cell> visitMt(MtList<Cell> list) {
    return list;
  }

  // Visits from the ConsList class, calculates the given
  // Conslist<Cell>s coastal cells
  public IList<Cell> visitCons(ConsList<Cell> list) {
    IList<Cell> base = new MtList<Cell>();

    for (Cell c : list) {
      if ((c.height > this.waterHeight) && ((c.left.height <= 0 || c.left.isFlooded)
          || (c.right.height <= 0 || c.right.isFlooded) || (c.top.height <= 0 || c.top.isFlooded)
          || (c.bottom.height <= 0 || c.bottom.isFlooded))) {
        base = new ConsList<Cell>(c, base);
      }
    }
    return base;
  }
}

// visitor for finding a cell with the given coordinates
class FindCell implements IListVisitor<Cell, Cell> {
  int x;
  int y;

  // Constructor
  FindCell(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // Visits from the MtList class, finds the cell with the given
  // coordinates
  public Cell visitMt(MtList<Cell> list) {
    throw new RuntimeException("Did not find a cell");
  }

  // Visits from the ConsList class, finds the cell with the given
  // coordinates
  public Cell visitCons(ConsList<Cell> list) {
    for (Cell c : list) {
      if ((this.x == c.x) && (this.y == c.y)) {
        return c;
      }
    }
    throw new RuntimeException("Did not find a cell");
  }
}

// visitor for determining if a posn is unique to a list of posns
class UniquePosn implements IListVisitor<Posn, Boolean> {

  Posn pos;

  // Constructor
  UniquePosn(Posn pos) {
    this.pos = pos;
  }

  // Visits from the MtList class, determines if this posn is in the given
  // mtlist
  public Boolean visitMt(MtList<Posn> list) {
    return true;
  }

  // Visits from the consList class, determines if this posn is in the given
  // conslist
  public Boolean visitCons(ConsList<Posn> list) {
    for (Posn p : list) {
      if (this.pos.x == p.x && this.pos.y == p.y) {
        return false;
      }
    }
    return true;
  }
}

// Visitor for adding an IList<Cell>s items to a scene
class HighestPoint implements IListVisitor<Cell, Posn> {

  // Visits from the MtList class, finds the highest cell in the given mtlist
  public Posn visitMt(MtList<Cell> list) {
    throw new RuntimeException("Didn't find a cell");
  }

  // Visits from the ConsList class, finds the highest cell in the given
  // conslist
  public Posn visitCons(ConsList<Cell> list) {
    int currentx = 0;
    int currenty = 0;
    Double currentheight = 0.0;
    for (Cell c : list) {
      if (c.height >= currentheight) {
        currentx = c.x;
        currenty = c.y;
        currentheight = c.height;
      }
    }
    return new Posn(currentx, currenty);
  }
}

// A generic list
interface IList<T> extends Iterable<T> {

  // Is this Ilist a cons?
  boolean isCons();

  // returns this IList as a ConsList
  ConsList<T> asCons();

  // accept the given visitor
  <R> R accept(IListVisitor<T, R> v);
}

// A generic empty list
class MtList<T> implements IList<T> {

  // accept the given visitor
  public <R> R accept(IListVisitor<T, R> v) {
    return v.visitMt(this);
  }

  // returns the iterator of an IList
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // Is this empty list a cons?
  public boolean isCons() {
    return false;
  }

  // returns this MtList as a ConsList
  public ConsList<T> asCons() {
    throw new RuntimeException("Not a cons!");
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

  // accept the given visitor
  public <R> R accept(IListVisitor<T, R> v) {
    return v.visitCons(this);
  }

  // returns the iterator of an IList
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // Is this nonempty list a cons?
  public boolean isCons() {
    return true;
  }

  // returns this ConsList as a ConsList
  public ConsList<T> asCons() {
    return this;
  }
}

// An iterator for ILists
class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  // Constructor
  IListIterator(IList<T> items) {
    this.items = items;
  }

  // Does the IList have at least one item remaining?
  public boolean hasNext() {
    return this.items.isCons();
  }

  // returns the next item in the IList
  // EFFECT: updates the tracked IList so the next call of
  // next() returns the next item in line
  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }

  // unimplemented
  public void remove() {
    throw new UnsupportedOperationException("Don't do this!");
  }
}

// Represents a single square of the game area
class Cell {
  // represents absolute height of this cell, in feet
  Double height;
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
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

  // returns the color that this cell should be, based on its height relative to
  // the water height
  // and its flooded status
  Color getColor(int waterHeight) {
    Double heightAboveSeaLevel = this.height - waterHeight;
    int intHeightAboveSeaLevel = heightAboveSeaLevel.intValue();

    if (heightAboveSeaLevel > 0) {
      Color initColor = new Color(0, 255, 0);
      if (initColor.getRed() + (intHeightAboveSeaLevel * 7) > 255) {
        return new Color(255, 255, 255);
      }
      else {
        return new Color((initColor.getRed() + (intHeightAboveSeaLevel * 7)),
            (initColor.getGreen()), (initColor.getBlue() + (intHeightAboveSeaLevel * 7)));
      }
    }

    else if (this.isFlooded) {
      Color initColor = new Color(0, 0, 255);
      if (initColor.getBlue() - (Math.abs(intHeightAboveSeaLevel) * 15) < 0) {
        return new Color(0, 0, 0);
      }
      else {
        return new Color(0, 0, initColor.getBlue() - (Math.abs(intHeightAboveSeaLevel) * 15));
      }
    }

    else {
      Color initColor = new Color(0, 255, 0);
      if (initColor.getRed() + (Math.abs(intHeightAboveSeaLevel) * 30) > 255) {
        return new Color(255, 0, 0);
      }
      else {
        return new Color(initColor.getRed() + (Math.abs(intHeightAboveSeaLevel) * 30),
            initColor.getGreen() - (Math.abs(intHeightAboveSeaLevel) * 30), initColor.getBlue());
      }
    }

  }

  // EFFECT: Updates this cells flooded status, if applicable, and if it does,
  // updates its neighbors
  void updateFlood(int waterHeight) {
    if (waterHeight >= this.height && !this.isFlooded) {
      this.isFlooded = true;
      this.left.updateFlood(waterHeight);
      this.right.updateFlood(waterHeight);
      this.top.updateFlood(waterHeight);
      this.bottom.updateFlood(waterHeight);
    }
    else {
      return;
    }
  }
}

// represents an Ocean cell
class OceanCell extends Cell {
  OceanCell(double height, int x, int y) {
    super(height, x, y);
  }

  // returns the color that this ocean cell should be
  Color getColor(int waterHeight) {
    return new Color(0, 0, 255);
  }

  // EFFECT: Updates this cells flooded status, if applicable, and if it does,
  // updates its neighbors
  void updateFlood(int waterHeight) {
    return;
  }
}

// represents a non-final helicopter target
class Target {
  Posn position;
  WorldImage image;

  // Constructor
  Target(Posn position, WorldImage image) {
    this.position = position;
    this.image = image;
  }
}

// Represents the final helicopter target
class HelicopterTarget extends Target {

  // Constructor
  HelicopterTarget(Posn position, WorldImage image) {
    super(position, image);
  }
}

// World for this game
class ForbiddenIslandWorld extends World {
  // Defines the size of the world canvas, in cells,
  // from 0 to the constant
  static final int ISLAND_SIZE = 64;
  // Defines the pixel size of each cell
  static final int CELL_SIZE = 14;
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight;
  int time;
  Posn playerpos;
  Target target1;
  Target target2;
  Target target3;
  HelicopterTarget heli;
  int timeleft;
  // Is the game paused? Can pause by pressing p
  boolean isPaused;

  // Constructor
  ForbiddenIslandWorld(String mode) {
    this.waterHeight = 0;
    this.time = 0;
    if (mode.equals("mountain")) {
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeMtHeights())));
      this.heli = new HelicopterTarget(this.board.accept(new HighestPoint()),
          new FromFileImage("helicopter.png"));
    }
    else if (mode.equals("random")) {
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeRandomHeights())));
      this.heli = new HelicopterTarget(this.board.accept(new HighestPoint()),
          new FromFileImage("helicopter.png"));
    }
    else if (mode.equals("terrain")) {
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeTerrainHeights())));
      this.heli = new HelicopterTarget(this.board.accept(new HighestPoint()),
          new FromFileImage("helicopter.png"));
    }
    else {
      throw new IllegalArgumentException("Not a valid mode type");
    }
    this.playerpos = this
        .createWorkingPosn(new ConsList<Posn>(this.heli.position, new MtList<Posn>()));
    this.target1 = new Target(
        this.createWorkingPosn(new ConsList<Posn>(this.playerpos,
            new ConsList<Posn>(this.heli.position, new MtList<Posn>()))),
        new FromFileImage("Part1.png"));
    this.target2 = new Target(
        this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
            new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
        new FromFileImage("Part2.png"));
    this.target3 = new Target(
        this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
            new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
        new FromFileImage("Part3.png"));
    Posn highestposn = this.board.accept(new HighestPoint());
    this.timeleft = this.board.accept(
        new FindCell(highestposn.x, highestposn.y)).height.intValue();
    this.isPaused = false;
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
          doubleacc.add((new Random().nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 2) + 1) * 1.0);
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
  // EFFECT: initializes the corners to height 0, the edge centers to height 1,
  // the center to the max height, and then recurs on the four quadrants
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

    makeTerrainHeightsHelp(array, new Posn(0, 0), new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, 0),
        new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2), 8);
    makeTerrainHeightsHelp(array, new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, 0),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, 0),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE / 2), 8);
    makeTerrainHeightsHelp(array, new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(0, ForbiddenIslandWorld.ISLAND_SIZE),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE), 8);
    makeTerrainHeightsHelp(array,
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE / 2),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE),
        new Posn(ForbiddenIslandWorld.ISLAND_SIZE, ForbiddenIslandWorld.ISLAND_SIZE), 8);

    return array;
  }

  // EFFECT: Helper for makeTerrainHeights, updates at the given posns
  void makeTerrainHeightsHelp(ArrayList<ArrayList<Double>> array, Posn tl, Posn tr, Posn bl,
      Posn br, int variance) {
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
    Double tval = (((tlval + trval) / 2) + randnum);
    Double lval = ((tlval + blval) / 2) + randnum;
    Double rval = ((trval + brval) / 2) + randnum;
    Double bval = ((blval + brval) / 2) + randnum;
    Double mval = (tlval + trval + blval + brval) / 4 + randnum;

    if (tr.x - tl.x == 1) {
      return;
    }
    else {
      if (array.get(t.y).get(t.x) == 0) {
        array.get(t.y).set(t.x, tval);
      }
      if (array.get(l.y).get(l.x) == 0) {
        array.get(l.y).set(l.x, lval);
      }
      if (array.get(r.y).get(r.x) == 0) {
        array.get(r.y).set(r.x, rval);
      }
      if (array.get(b.y).get(b.x) == 0) {
        array.get(b.y).set(b.x, bval);
      }
      if (array.get(m.y).get(m.x) == 0) {
        array.get(m.y).set(m.x, mval);
      }

      makeTerrainHeightsHelp(array, tl, t, l, m, variance - 1);
      makeTerrainHeightsHelp(array, t, tr, m, r, variance - 1);
      makeTerrainHeightsHelp(array, l, m, bl, b, variance - 1);
      makeTerrainHeightsHelp(array, m, r, b, br, variance - 1);
    }

  }

  // calculates the manhatten distance of a cell at the given i, j coordinates
  // subtracted from the max height for the height of the cell
  Double calcManhattenDist(double i, double j) {
    return (ForbiddenIslandWorld.ISLAND_SIZE / 2)
        - (Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2.0) - j)
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

  // Links each cell in the given double array list of cells to its neighbors
  ArrayList<ArrayList<Cell>> linkCells(ArrayList<ArrayList<Cell>> array) {
    for (int i = 0; i <= ForbiddenIslandWorld.ISLAND_SIZE; i = i + 1) {
      for (int j = 0; j <= ForbiddenIslandWorld.ISLAND_SIZE; j = j + 1) {
        Cell cell = array.get(i).get(j);
        if (cell.x == 0) {
          cell.left = cell;
        }
        else {
          cell.left = array.get(i).get(j - 1);
        }
        if (cell.y == 0) {
          cell.top = cell;
        }
        else {
          cell.top = array.get(i - 1).get(j);
        }
        if (cell.x == ForbiddenIslandWorld.ISLAND_SIZE) {
          cell.right = cell;
        }
        else {
          cell.right = array.get(i).get(j + 1);
        }
        if (cell.y == ForbiddenIslandWorld.ISLAND_SIZE) {
          cell.bottom = cell;
        }
        else {
          cell.bottom = array.get(i + 1).get(j);
        }
      }
    }
    return array;
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
  
  // creates a starting posn for an object to be positioned on.
  Posn createWorkingPosn(IList<Posn> posnlist) {
    boolean keepGoing = true;
    while (keepGoing) {
      Posn pos = new Posn(new Random().nextInt(ForbiddenIslandWorld.ISLAND_SIZE) + 1,
          new Random().nextInt(ForbiddenIslandWorld.ISLAND_SIZE) + 1);
      if ((this.board.accept(new FindCell(pos.x, pos.y)).height >= ForbiddenIslandWorld.ISLAND_SIZE
          / 4) && (!this.board.accept(new FindCell(pos.x, pos.y)).isFlooded)
          && posnlist.accept(new UniquePosn(pos))) {
        return pos;
      }
    }
    throw new RuntimeException("Didn't find a place");
  }

  // EFFECT: on tick handler, advances the time each tick and floods every 10
  public void onTick() {

    if (this.isPaused) {
      return;
    }

    if (this.time % 10 == 0) {
      IList<Cell> coastCells = this.board.accept(new CalcCoastalCells(this.waterHeight));
      this.waterHeight = this.waterHeight + 1;
      this.timeleft = this.timeleft - 1;
      for (Cell c : coastCells) {
        c.updateFlood(this.waterHeight);
      }
    }
    this.time = this.time + 1;
  }

  // EFFECT: key event handler, processes key presses
  public void onKeyEvent(String key) {

    if (key.equals("p")) {
      this.isPaused = !this.isPaused;
      return;
    }
    if (this.isPaused) {
      return;
    }
    else if (key.equals("up") && this.validSpot(new Posn(this.playerpos.x, this.playerpos.y - 1))) {
      this.playerpos.y = this.playerpos.y - 1;
    }
    else if (key.equals("down")
        && this.validSpot(new Posn(this.playerpos.x, this.playerpos.y + 1))) {
      this.playerpos.y = this.playerpos.y + 1;
    }
    else if (key.equals("left")
        && this.validSpot(new Posn(this.playerpos.x - 1, this.playerpos.y))) {
      this.playerpos.x = this.playerpos.x - 1;
    }
    else if (key.equals("right")
        && this.validSpot(new Posn(this.playerpos.x + 1, this.playerpos.y))) {
      this.playerpos.x = this.playerpos.x + 1;
    }
    if (this.target1 != null && this.playerpos.x == this.target1.position.x
        && this.playerpos.y == this.target1.position.y) {
      this.target1 = null;
    }
    if (this.target2 != null && this.playerpos.x == this.target2.position.x
        && this.playerpos.y == this.target2.position.y) {
      this.target2 = null;
    }
    if (this.target3 != null && this.playerpos.x == this.target3.position.x
        && this.playerpos.y == this.target3.position.y) {
      this.target3 = null;
    }
    if (key.equals("m")) {
      this.waterHeight = 0;
      this.time = 0;
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeMtHeights())));
      this.heli = new HelicopterTarget(
          new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
          new FromFileImage("helicopter.png"));
      this.playerpos = this
          .createWorkingPosn(new ConsList<Posn>(this.heli.position, new MtList<Posn>()));
      this.target1 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.playerpos,
              new ConsList<Posn>(this.heli.position, new MtList<Posn>()))),
          new FromFileImage("Part1.png"));
      this.target2 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part2.png"));
      this.target3 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part3.png"));
      Posn highestposn = this.board.accept(new HighestPoint());
      this.timeleft = this.board.accept(
          new FindCell(highestposn.x, highestposn.y)).height.intValue();
    }
    if (key.equals("t")) {
      this.waterHeight = 0;
      this.time = 0;
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeTerrainHeights())));
      this.heli = new HelicopterTarget(
          new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2, ForbiddenIslandWorld.ISLAND_SIZE / 2),
          new FromFileImage("helicopter.png"));
      this.playerpos = this
          .createWorkingPosn(new ConsList<Posn>(this.heli.position, new MtList<Posn>()));
      this.target1 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.playerpos,
              new ConsList<Posn>(this.heli.position, new MtList<Posn>()))),
          new FromFileImage("Part1.png"));
      this.target2 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part2.png"));
      this.target3 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part3.png"));
      Posn highestposn = this.board.accept(new HighestPoint());
      this.timeleft = this.board.accept(
          new FindCell(highestposn.x, highestposn.y)).height.intValue();
    }

    if (key.equals("r")) {
      this.waterHeight = 0;
      this.time = 0;
      this.board = this.arraytoList(this.linkCells(makeCellList(this.makeRandomHeights())));
      this.heli = new HelicopterTarget(this.board.accept(new HighestPoint()),
          new FromFileImage("helicopter.png"));
      this.playerpos = this
          .createWorkingPosn(new ConsList<Posn>(this.heli.position, new MtList<Posn>()));
      this.target1 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.playerpos,
              new ConsList<Posn>(this.heli.position, new MtList<Posn>()))),
          new FromFileImage("Part1.png"));
      this.target2 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part2.png"));
      this.target3 = new Target(
          this.createWorkingPosn(new ConsList<Posn>(this.heli.position,
              new ConsList<Posn>(this.playerpos, new MtList<Posn>()))),
          new FromFileImage("Part3.png"));
      Posn highestposn = this.board.accept(new HighestPoint());
      this.timeleft = this.board.accept(
          new FindCell(highestposn.x, highestposn.y)).height.intValue();
    }
    return;
  }

  // is this posn a valid spot for the player to stand on?
  boolean validSpot(Posn pos) {
    Cell c = this.board.accept(new FindCell(pos.x, pos.y));
    return (!c.isFlooded && c.height > 0);
  }

  // Draws the WorldScene of the world
  public WorldScene makeScene() {
    WorldScene base = this.getEmptyScene();
    WorldImage timeLeftText = new TextImage("Time Left:", 20, Color.RED);
    Double timeLeftTextWidth = timeLeftText.getWidth();
    Double timeLeftTextHeight = timeLeftText.getHeight();
    WorldImage pausedText = new TextImage("Paused", 30, Color.RED);
    Double pausedTextHeight = pausedText.getHeight();
    this.board.accept(new AddListToScene(base, new DrawCell(), this.waterHeight));
    base.placeImageXY(new TextImage("Time Left:", 20, Color.RED),
        10 + timeLeftTextWidth.intValue() / 2, 10 + timeLeftTextHeight.intValue() / 2);
    base.placeImageXY(new TextImage(Integer.toString(this.timeleft), 20, Color.RED),
        10 + timeLeftTextWidth.intValue() / 2, 10 + timeLeftTextHeight.intValue() / 2 + 20);
    if (this.isPaused) {
      base.placeImageXY(pausedText,
          (ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE) / 2,
          (ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE)
              - pausedTextHeight.intValue() / 2);
    }
    base.placeImageXY(new ScaleImage(this.heli.image, 0.5),
        (this.heli.position.x * ForbiddenIslandWorld.CELL_SIZE)
            + (ForbiddenIslandWorld.CELL_SIZE / 2),
        (this.heli.position.y * ForbiddenIslandWorld.CELL_SIZE)
            + (ForbiddenIslandWorld.CELL_SIZE / 2));
    if (this.target1 != null) {
      base.placeImageXY(new ScaleImage(this.target1.image, 0.06),
          (this.target1.position.x * ForbiddenIslandWorld.CELL_SIZE)
              + (ForbiddenIslandWorld.CELL_SIZE / 2),
          (this.target1.position.y * ForbiddenIslandWorld.CELL_SIZE)
              + (ForbiddenIslandWorld.CELL_SIZE / 2));
    }
    if (this.target2 != null) {
      base.placeImageXY(new ScaleImage(this.target2.image, 0.08),
          this.target2.position.x * ForbiddenIslandWorld.CELL_SIZE
              + (ForbiddenIslandWorld.CELL_SIZE / 2),
          this.target2.position.y * ForbiddenIslandWorld.CELL_SIZE
              + (ForbiddenIslandWorld.CELL_SIZE / 2));
    }
    if (this.target3 != null) {
      base.placeImageXY(new ScaleImage(this.target3.image, 0.07),
          this.target3.position.x * ForbiddenIslandWorld.CELL_SIZE
              + (ForbiddenIslandWorld.CELL_SIZE / 2),
          this.target3.position.y * ForbiddenIslandWorld.CELL_SIZE
              + (ForbiddenIslandWorld.CELL_SIZE / 2));
    }
    base.placeImageXY(new ScaleImage(new FromFileImage("pilot-icon.png"), 0.7),
        (this.playerpos.x * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2),
        (this.playerpos.y * ForbiddenIslandWorld.CELL_SIZE) + (ForbiddenIslandWorld.CELL_SIZE / 2));
    return base;
  }

  // Calculates when the world should end and ends the game properly
  public WorldEnd worldEnds() {
    if (!this.validSpot(this.playerpos)
        || (this.target1 != null && !this.validSpot(this.target1.position))
        || (this.target2 != null && !this.validSpot(this.target2.position))
        || (this.target3 != null && !this.validSpot(this.target3.position))) {
      return new WorldEnd(true, this.lastScene("You lose! :("));
    }
    else if (this.target1 == null && this.target2 == null && this.target3 == null
        && this.playerpos.x == this.heli.position.x && this.playerpos.y == this.heli.position.y) {
      return new WorldEnd(true, this.lastScene("You win! :)"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Produces the final scene for the game, when a win/lose condition has been
  // met
  public WorldScene lastScene(String s) {
    WorldScene base = this.makeScene();
    base.placeImageXY(new TextImage(s, 50, Color.red),
        (ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.CELL_SIZE) / 2, 40);
    return base;
  }
}

// examples and tests for the game
// *All tests assume an island size constant of 4, and a cell size of 14*
class ExamplesForbiddenIsland {
  ForbiddenIslandWorld mountain = new ForbiddenIslandWorld("mountain");
  ForbiddenIslandWorld random = new ForbiddenIslandWorld("random");
  ForbiddenIslandWorld terrain = new ForbiddenIslandWorld("terrain");
  IDraw<Cell> drawCell = new DrawCell();
  Cell cell1 = new Cell(1.0, 0, 0);
  Cell oCell1 = new OceanCell(0.0, 0, 0);
  IList<Cell> mt = new MtList<Cell>();
  IList<Cell> list1 = new ConsList<Cell>(this.cell1,
      new ConsList<Cell>(this.oCell1, new MtList<Cell>()));
  IListIterator<Cell> cellIter = new IListIterator<Cell>(this.mt);
  IListIterator<Cell> cellIter2 = new IListIterator<Cell>(this.list1);
  IListVisitor<Cell, IList<Cell>> calcCoast = new CalcCoastalCells(0);
  IListVisitor<Cell, Cell> find = new FindCell(2, 2);
  IListVisitor<Posn, Boolean> unique = new UniquePosn(new Posn(1, 2));

  // initializes example data conditions
  void init() {
    this.mountain = new ForbiddenIslandWorld("mountain");
    this.random = new ForbiddenIslandWorld("random");
    this.terrain = new ForbiddenIslandWorld("terrain");
    this.drawCell = new DrawCell();
    this.cell1 = new Cell(1.0, 0, 0);
    this.oCell1 = new OceanCell(0.0, 0, 0);
    this.mt = new MtList<Cell>();
    this.list1 = new ConsList<Cell>(this.cell1,
        new ConsList<Cell>(this.oCell1, new MtList<Cell>()));
    this.cellIter = new IListIterator<Cell>(this.mt);
    this.cellIter2 = new IListIterator<Cell>(this.list1);
    this.calcCoast = new CalcCoastalCells(0);
    this.find = new FindCell(2, 2);
    this.unique = new UniquePosn(new Posn(1, 2));
  }

  // test for constructor exceptions
  void testConstructorExceptions(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("Not a valid mode type"),
        "ForbiddenIslandWorld", "hi");
  }

  // tests for isCons and asCons methods
  void testListMethods(Tester t) {
    init();
    t.checkExpect(this.mt.isCons(), false);
    t.checkExpect(this.list1.isCons(), true);
    t.checkException(new RuntimeException("Not a cons!"), this.mt, "asCons");
    t.checkExpect(this.list1.asCons(), this.list1);
  }
  
  // tests for calculating coastal cells
  void testCoastalCells(Tester t) {
    init();
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkExpect(this.mt.accept(this.calcCoast), this.mt);
    t.checkExpect(this.mountain.board.accept(this.calcCoast), 
        new ConsList<Cell>(linkedArray.get(1).get(2), 
            new ConsList<Cell>(linkedArray.get(2).get(1), 
                new ConsList<Cell>(linkedArray.get(2).get(3),
                    new ConsList<Cell>(linkedArray.get(3).get(2), this.mt)))));
    
  }
  
  // tests for unique posn visitor
  void testUniquePosn(Tester t) {
    init();
    t.checkExpect(new MtList<Posn>().accept(new UniquePosn(new Posn(1, 2))), true);
    t.checkExpect(new ConsList<Posn>(new Posn(2, 2), new MtList<Posn>())
        .accept(new UniquePosn(new Posn(1, 2))), true);
    t.checkExpect(new ConsList<Posn>(new Posn(1, 2), new MtList<Posn>())
        .accept(new UniquePosn(new Posn(1, 2))), false);
  }
  
  // tests for IList iterator methods
  void testIterMethods(Tester t) {
    init();
    t.checkExpect(this.cellIter.hasNext(), false);
    t.checkExpect(this.cellIter2.hasNext(), true);
    t.checkException(new RuntimeException("Not a cons!"), this.cellIter, "next");
    t.checkExpect(this.cellIter2.next(), this.cell1);
    // After the above call of next advances the list...
    t.checkExpect(this.cellIter2.items, new ConsList<Cell>(this.oCell1, new MtList<Cell>()));
  }
  
  // tests for island rendering
  void testDraw(Tester t) {
    init();
    WorldScene scene = this.mountain.getEmptyScene();
    scene.placeImageXY(new RectangleImage(14, 14, OutlineMode.SOLID, new Color(7, 255, 7)), 7, 7);
    WorldScene scene2 = this.random.getEmptyScene();
    scene2.placeImageXY(new RectangleImage(14, 14, OutlineMode.SOLID, new Color(0, 0, 255)), 7, 7);
    WorldScene scene3 = this.terrain.getEmptyScene();
    scene3.placeImageXY(new RectangleImage(14, 14, OutlineMode.SOLID, new Color(7, 255, 7)), 7,
        7);
    scene3.placeImageXY(new RectangleImage(14, 14, OutlineMode.SOLID, new Color(0, 0, 255)), 7, 7);

    t.checkExpect(this.drawCell.draw(this.cell1, this.mountain.getEmptyScene(), 0), scene);
    t.checkExpect(this.drawCell.draw(this.oCell1, this.random.getEmptyScene(), 0), scene2);
    t.checkExpect(
        this.mt.accept(new AddListToScene(this.terrain.getEmptyScene(), this.drawCell, 0)),
        this.terrain.getEmptyScene());
    t.checkExpect(
        this.list1.accept(new AddListToScene(this.terrain.getEmptyScene(), this.drawCell, 0)),
        scene3);
  }

  
  // tests for getColor method
  void testGetColor(Tester t) {
    init();
    t.checkExpect(this.oCell1.getColor(0), new Color(0, 0, 255));
    t.checkExpect(this.oCell1.getColor(50), new Color(0, 0, 255));
    t.checkExpect(this.cell1.getColor(0), new Color(7, 255, 7));
    t.checkExpect(this.cell1.getColor(1), new Color(0, 255, 0));
  }

  // tests for the three makeHeights method
  void testMakeHeights(Tester t) {
    init();
    t.checkExpect(this.mountain.makeMtHeights(),
        new ArrayList<ArrayList<Double>>(
            Arrays.asList(new ArrayList<Double>(Arrays.asList(-2.0, -1.0, 0.0, -1.0, -2.0)),
                new ArrayList<Double>(Arrays.asList(-1.0, 0.0, 1.0, 0.0, -1.0)),
                new ArrayList<Double>(Arrays.asList(0.0, 1.0, 2.0, 1.0, 0.0)),
                new ArrayList<Double>(Arrays.asList(-1.0, 0.0, 1.0, 0.0, -1.0)),
                new ArrayList<Double>(Arrays.asList(-2.0, -1.0, 0.0, -1.0, -2.0)))));
    ArrayList<ArrayList<Double>> randarray = this.random.makeRandomHeights();
    t.checkExpect(randarray.get(0).get(0), 0.0);
    t.checkExpect(randarray.get(3).get(1), 0.0);
    t.checkNumRange(randarray.get(2).get(2), 1, 2, true, true);
    t.checkNumRange(randarray.get(1).get(2), 1, 2, true, true);
    ArrayList<ArrayList<Double>> terrainarray = this.terrain.makeTerrainHeights();
    t.checkExpect(terrainarray.get(0).get(0), 0.0);
    t.checkExpect(terrainarray.get(2).get(0), 1.0);
    t.checkExpect(terrainarray.get(2).get(2), 2.0);
    t.checkNumRange(terrainarray.get(2).get(3), -5, 5, true, true);
    t.checkNumRange(terrainarray.get(3).get(1), -5, 5, true, true);
  }

  // tests for the calcManhattenDist method
  void testCalcManhattenDist(Tester t) {
    init();
    t.checkExpect(this.mountain.calcManhattenDist(2.0, 2.0), 2.0);
    t.checkExpect(this.mountain.calcManhattenDist(4.0, 4.0), -2.0);
    t.checkExpect(this.mountain.calcManhattenDist(1.0, 1.0), 0.0);
  }
  
  // tests for the three makeTerrainHeightsHelp method
  void testMakeTerrainHeightsHelp(Tester t) {
    init();
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
    this.terrain.makeTerrainHeightsHelp(array,
        new Posn(0, 0), new Posn(4, 0), new Posn(0, 4), new Posn(4, 4), 8);
    t.checkExpect(array.get(0).get(0), 0.0);
    t.checkExpect(array.get(2).get(0), 1.0);
    t.checkExpect(array.get(2).get(2), 2.0);
    t.checkNumRange(array.get(2).get(3), -5, 5, true, true);
    t.checkNumRange(array.get(3).get(1), -5, 5, true, true);
  }
  
  // tests for the makeCellList method
  void testMakeCellList(Tester t) {
    init();
    ArrayList<ArrayList<Double>> initarray = new ArrayList<ArrayList<Double>>(
        Arrays.asList(new ArrayList<Double>(Arrays.asList(-2.0, -1.0, 0.0, -1.0, -2.0)),
            new ArrayList<Double>(Arrays.asList(-1.0, 0.0, 1.0, 0.0, -1.0)),
            new ArrayList<Double>(Arrays.asList(0.0, 1.0, 2.0, 1.0, 0.0)),
            new ArrayList<Double>(Arrays.asList(-1.0, 0.0, 1.0, 0.0, -1.0)),
            new ArrayList<Double>(Arrays.asList(-2.0, -1.0, 0.0, -1.0, -2.0))));
    t.checkExpect(this.mountain.makeCellList(initarray), 
        new ArrayList<ArrayList<Cell>>(
            Arrays.asList(new ArrayList<Cell>(Arrays.asList(new OceanCell(-2.0, 0, 0), 
                                                              new OceanCell(-1.0, 1, 0), 
                                                              new OceanCell(0.0, 2, 0), 
                                                              new OceanCell(-1.0, 3, 0), 
                                                              new OceanCell(-2.0, 4, 0))),
                new ArrayList<Cell>(Arrays.asList(new OceanCell(-1.0, 0, 1), 
                    new OceanCell(0.0, 1, 1), 
                    new Cell(1.0, 2, 1), 
                    new OceanCell(0.0, 3, 1), 
                    new OceanCell(-1.0, 4, 1))),
                new ArrayList<Cell>(Arrays.asList(new OceanCell(0.0, 0, 2), 
                    new Cell(1.0, 1, 2), 
                    new Cell(2.0, 2, 2), 
                    new Cell(1.0, 3, 2), 
                    new OceanCell(0.0, 4, 2))),
                new ArrayList<Cell>(Arrays.asList(new OceanCell(-1.0, 0, 3), 
                    new OceanCell(0.0, 1, 3), 
                    new Cell(1.0, 2, 3), 
                    new OceanCell(0.0, 3, 3), 
                    new OceanCell(-1.0, 4, 3))),
                new ArrayList<Cell>(Arrays.asList(new OceanCell(-2.0, 0, 4), 
                    new OceanCell(-1.0, 1, 4), 
                    new OceanCell(0.0, 2, 4), 
                    new OceanCell(-1.0, 3, 4), 
                    new OceanCell(-2.0, 4, 4))))));
  }
  
  // tests for linkCells method
  void testLinkCells(Tester t) {
    init();
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkFail(linkedArray.get(2).get(1).top, null);
    t.checkFail(linkedArray.get(0).get(0).left, null);
    t.checkFail(linkedArray.get(1).get(4).bottom, null);
    t.checkFail(linkedArray.get(2).get(3).right, null);
    t.checkExpect(linkedArray.get(2).get(1).top.bottom, linkedArray.get(2).get(1));
    t.checkExpect(linkedArray.get(0).get(0).left, linkedArray.get(0).get(0));
  }
  
  // tests for arrayToList method
  void testArrayToList(Tester t) {
    init();
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkExpect(this.mountain.arraytoList(linkedArray), 
        new ConsList<Cell>(linkedArray.get(4).get(4), 
            new ConsList<Cell>(linkedArray.get(4).get(3),
                new ConsList<Cell>(linkedArray.get(4).get(2),
                    new ConsList<Cell>(linkedArray.get(4).get(1),
                        new ConsList<Cell>(linkedArray.get(4).get(0),
                            new ConsList<Cell>(linkedArray.get(3).get(4),
                                new ConsList<Cell>(linkedArray.get(3).get(3),
                                    new ConsList<Cell>(linkedArray.get(3).get(2),
                                        new ConsList<Cell>(linkedArray.get(3).get(1),
                                            new ConsList<Cell>(linkedArray.get(3).get(0),
            new ConsList<Cell>(linkedArray.get(2).get(4),
                new ConsList<Cell>(linkedArray.get(2).get(3),
                    new ConsList<Cell>(linkedArray.get(2).get(2),
                        new ConsList<Cell>(linkedArray.get(2).get(1),
                            new ConsList<Cell>(linkedArray.get(2).get(0),
                                new ConsList<Cell>(linkedArray.get(1).get(4),
                                    new ConsList<Cell>(linkedArray.get(1).get(3),
                                        new ConsList<Cell>(linkedArray.get(1).get(2),
                                            new ConsList<Cell>(linkedArray.get(1).get(1),
                                                new ConsList<Cell>(linkedArray.get(1).get(0),
            new ConsList<Cell>(linkedArray.get(0).get(4),
                new ConsList<Cell>(linkedArray.get(0).get(3),
                    new ConsList<Cell>(linkedArray.get(0).get(2),
                        new ConsList<Cell>(linkedArray.get(0).get(1),
                            new ConsList<Cell>(linkedArray.get(0).get(0), 
                                new MtList<Cell>()))))))))))))))))))))))))));
  }
  
  // tests for arrayToListHelp method
  void testArrayToListHelp(Tester t) {
    init();
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkExpect(this.mountain.arraytoListHelp(linkedArray, new MtList<Cell>()), 
        new ConsList<Cell>(linkedArray.get(4).get(4), 
            new ConsList<Cell>(linkedArray.get(4).get(3),
                new ConsList<Cell>(linkedArray.get(4).get(2),
                    new ConsList<Cell>(linkedArray.get(4).get(1),
                        new ConsList<Cell>(linkedArray.get(4).get(0),
                            new ConsList<Cell>(linkedArray.get(3).get(4),
                                new ConsList<Cell>(linkedArray.get(3).get(3),
                                    new ConsList<Cell>(linkedArray.get(3).get(2),
                                        new ConsList<Cell>(linkedArray.get(3).get(1),
                                            new ConsList<Cell>(linkedArray.get(3).get(0),
            new ConsList<Cell>(linkedArray.get(2).get(4),
                new ConsList<Cell>(linkedArray.get(2).get(3),
                    new ConsList<Cell>(linkedArray.get(2).get(2),
                        new ConsList<Cell>(linkedArray.get(2).get(1),
                            new ConsList<Cell>(linkedArray.get(2).get(0),
                                new ConsList<Cell>(linkedArray.get(1).get(4),
                                    new ConsList<Cell>(linkedArray.get(1).get(3),
                                        new ConsList<Cell>(linkedArray.get(1).get(2),
                                            new ConsList<Cell>(linkedArray.get(1).get(1),
                                                new ConsList<Cell>(linkedArray.get(1).get(0),
            new ConsList<Cell>(linkedArray.get(0).get(4),
                new ConsList<Cell>(linkedArray.get(0).get(3),
                    new ConsList<Cell>(linkedArray.get(0).get(2),
                        new ConsList<Cell>(linkedArray.get(0).get(1),
                            new ConsList<Cell>(linkedArray.get(0).get(0), 
                                new MtList<Cell>()))))))))))))))))))))))))));
    t.checkExpect(this.mountain.arraytoListHelp(linkedArray, 
        new ConsList<Cell>(this.cell1, new MtList<Cell>())), 
        new ConsList<Cell>(linkedArray.get(4).get(4), 
            new ConsList<Cell>(linkedArray.get(4).get(3),
                new ConsList<Cell>(linkedArray.get(4).get(2),
                    new ConsList<Cell>(linkedArray.get(4).get(1),
                        new ConsList<Cell>(linkedArray.get(4).get(0),
                            new ConsList<Cell>(linkedArray.get(3).get(4),
                                new ConsList<Cell>(linkedArray.get(3).get(3),
                                    new ConsList<Cell>(linkedArray.get(3).get(2),
                                        new ConsList<Cell>(linkedArray.get(3).get(1),
                                            new ConsList<Cell>(linkedArray.get(3).get(0),
            new ConsList<Cell>(linkedArray.get(2).get(4),
                new ConsList<Cell>(linkedArray.get(2).get(3),
                    new ConsList<Cell>(linkedArray.get(2).get(2),
                        new ConsList<Cell>(linkedArray.get(2).get(1),
                            new ConsList<Cell>(linkedArray.get(2).get(0),
                                new ConsList<Cell>(linkedArray.get(1).get(4),
                                    new ConsList<Cell>(linkedArray.get(1).get(3),
                                        new ConsList<Cell>(linkedArray.get(1).get(2),
                                            new ConsList<Cell>(linkedArray.get(1).get(1),
                                                new ConsList<Cell>(linkedArray.get(1).get(0),
            new ConsList<Cell>(linkedArray.get(0).get(4),
                new ConsList<Cell>(linkedArray.get(0).get(3),
                    new ConsList<Cell>(linkedArray.get(0).get(2),
                        new ConsList<Cell>(linkedArray.get(0).get(1),
                            new ConsList<Cell>(linkedArray.get(0).get(0), 
                                new ConsList<Cell>(this.cell1, 
                                    new MtList<Cell>())))))))))))))))))))))))))));
  }
 
  // tests of makeScene method
  void testMakeScene(Tester t) {
    init();
    this.mountain.playerpos = new Posn(0, 0);
    this.mountain.heli.position = new Posn(0, 0);
    this.mountain.target1.position = new Posn(0, 0);
    this.mountain.target2.position = new Posn(0, 0);
    this.mountain.target3.position = new Posn(0, 0);
    WorldScene scene = this.mountain.getEmptyScene();
    this.mountain.board.accept(new AddListToScene(scene, this.drawCell, 0));
    scene.placeImageXY(new TextImage("Time Left:", 20, Color.RED), 54, 22);
    scene.placeImageXY(new TextImage("2", 20, Color.RED), 54, 42);
    scene.placeImageXY(new ScaleImage(new FromFileImage("helicopter.png"), 0.5), 7, 7);
    scene.placeImageXY(new ScaleImage(new FromFileImage("part1.png"), 0.06), 7, 7);
    scene.placeImageXY(new ScaleImage(new FromFileImage("part2.png"), 0.08), 7, 7);
    scene.placeImageXY(new ScaleImage(new FromFileImage("part3.png"), 0.07), 7, 7);
    t.checkExpect(this.mountain.makeScene(), scene);
  }
  
  // tests for lastScene method
  void testLastScene(Tester t) {
    init();
    WorldScene base = this.mountain.getEmptyScene();
    base = this.mountain.makeScene();
    base.placeImageXY(new TextImage("test", 50, Color.red), 
        28, 40);
    t.checkExpect(this.mountain.lastScene("test"), base);
  }
  
  // tests for WorldEnd method
  void testWorldEnds(Tester t) {
    init();
    this.mountain.playerpos = new Posn(1, 2);
    t.checkExpect(this.mountain.worldEnds(), new WorldEnd(false, this.mountain.makeScene()));
    this.mountain.onTick();
    t.checkExpect(this.mountain.worldEnds(), 
        new WorldEnd(true, this.mountain.lastScene("You lose! :(")));
    init();
    this.mountain.playerpos = new Posn(2, 2);
    this.mountain.target1 = null;
    this.mountain.target2 = null;
    this.mountain.target3 = null;
    t.checkExpect(this.mountain.worldEnds(), 
        new WorldEnd(true, this.mountain.lastScene("You win! :)")));
  }
  
  
  // tests for finding cells based on coordinates
  void testFind(Tester t) {
    init();
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkExpect(this.mountain.board.accept(this.find), linkedArray.get(2).get(2));
    t.checkException(new RuntimeException("Did not find a cell"), this.mt, "accept", this.find);
  }
  
  // tests for flooding mechanic
  void testFlooding(Tester t) {
    init();
    // sets the mountain to a water height of 1
    ArrayList<ArrayList<Cell>> unlinkedArray =
        this.mountain.makeCellList(this.mountain.makeMtHeights());
    ArrayList<ArrayList<Cell>> linkedArray = this.mountain.linkCells(unlinkedArray);
    t.checkExpect(this.mountain.waterHeight, 0);
    t.checkExpect(this.mountain.board.accept(this.calcCoast), 
        new ConsList<Cell>(linkedArray.get(1).get(2), 
            new ConsList<Cell>(linkedArray.get(2).get(1), 
                new ConsList<Cell>(linkedArray.get(2).get(3),
                    new ConsList<Cell>(linkedArray.get(3).get(2), this.mt)))));
    t.checkExpect(this.mountain.board.accept(new FindCell(1, 2)).isFlooded, false);
    this.mountain.onTick();
    t.checkExpect(this.mountain.waterHeight, 1);
    t.checkExpect(this.mountain.board.accept(new CalcCoastalCells(this.mountain.waterHeight)),
        new ConsList<Cell>(this.mountain.board.accept(this.find), new MtList<Cell>()));
    t.checkExpect(this.mountain.board.accept(new FindCell(1, 2)).isFlooded, true);
  }
  
  // tests for updateFlood method
  void testUpdateFlood(Tester t) {
    init();
    t.checkExpect(this.mountain.board.accept(this.find).isFlooded, false);
    t.checkExpect(this.mountain.board.accept(this.find).left.isFlooded, false);
    this.mountain.board.accept(this.find).updateFlood(1);
    t.checkExpect(this.mountain.board.accept(this.find).isFlooded, false);
    this.mountain.board.accept(this.find).updateFlood(2);
    t.checkExpect(this.mountain.board.accept(this.find).isFlooded, true);
    t.checkExpect(this.mountain.board.accept(this.find).left.isFlooded, true);
  }
  
  // tests for createWorkingPosn method
  void testCreateWorkingPosn(Tester t) {
    init();
    t.checkOneOf(this.mountain.createWorkingPosn(new MtList<Posn>()), new Posn(1, 2), 
        new Posn(2, 2), new Posn(3, 2), new Posn(2, 3), new Posn(2, 1));
  } 
  
  // tests for valid player spot method
  void testValidSpot(Tester t) {
    init();
    t.checkExpect(this.mountain.validSpot(new Posn(0, 0)), false);
    t.checkExpect(this.mountain.validSpot(new Posn(2, 2)), true);
  }
  
  // tests for key events
  void testOnKeyEvent(Tester t) {
    init();
    this.mountain.playerpos = new Posn(2, 2);
    this.mountain.onKeyEvent("right");
    t.checkExpect(this.mountain.playerpos, new Posn(3, 2));
    this.mountain.onKeyEvent("right");
    t.checkExpect(this.mountain.playerpos, new Posn(3, 2));
    this.mountain.onKeyEvent("left");
    t.checkExpect(this.mountain.playerpos, new Posn(2, 2));
    this.mountain.onKeyEvent("up");
    t.checkExpect(this.mountain.playerpos, new Posn(2, 1));
    this.mountain.onKeyEvent("down");
    t.checkExpect(this.mountain.playerpos, new Posn(2, 2));
  }
  
  // tests for visitor for finding highest cell
  void testHighestPoint(Tester t) {
    init();
    t.checkException(new RuntimeException("Didn't find a cell"), this.mt, "accept",
        new HighestPoint());
    t.checkExpect(this.mountain.board.accept(new HighestPoint()).x, 2);
    t.checkExpect(this.mountain.board.accept(new HighestPoint()).y, 2);
    this.random.board = new ConsList<Cell>(new Cell(5.0, 0, 0), 
        new ConsList<Cell>(new Cell(5.0, 1, 0), this.mt));
    t.checkExpect(this.random.board.accept(new HighestPoint()), new Posn(1, 0));
  }
  
  // tests for collecting parts
  void testCollecting(Tester t) {
    init();
    this.mountain.playerpos = new Posn(2, 2);
    this.mountain.target1.position = new Posn(3, 2);
    this.mountain.onKeyEvent("right");
    t.checkExpect(this.mountain.target1, null);
  }
  
  // tests for resetting the game
  void testReset(Tester t) {
    init();
    this.mountain.playerpos = new Posn(2, 2);
    this.mountain.target1.position = new Posn(2, 2);
    this.mountain.target2.position = new Posn(2, 2);
    this.mountain.target3.position = new Posn(2, 2);
    this.mountain.onTick();
    t.checkExpect(this.mountain.time, 1);
    t.checkExpect(this.mountain.waterHeight, 1);
    t.checkExpect(this.mountain.board.accept(new FindCell(1, 2)).isFlooded, true);
    this.mountain.onKeyEvent("m");
    t.checkExpect(this.mountain.time, 0);
    t.checkExpect(this.mountain.waterHeight, 0);
    t.checkExpect(this.mountain.board.accept(new FindCell(1, 2)).isFlooded, false);
    this.mountain.onTick();
    t.checkExpect(this.mountain.time, 1);
    t.checkExpect(this.mountain.waterHeight, 1);
    this.mountain.onKeyEvent("t");
    t.checkExpect(this.mountain.time, 0);
    t.checkExpect(this.mountain.waterHeight, 0);
    this.mountain.onTick();
    t.checkExpect(this.mountain.time, 1);
    t.checkExpect(this.mountain.waterHeight, 1);
    this.mountain.onKeyEvent("r");
    t.checkExpect(this.mountain.time, 0);
    t.checkExpect(this.mountain.waterHeight, 0);
    
  }
  
  // tests of time left
  void testTimeLeft(Tester t) {
    init();
    t.checkExpect(this.mountain.timeleft, 2);
    this.mountain.onTick();
    t.checkExpect(this.mountain.timeleft, 1);
  }
  
  // tests for pause feature
  void testPause(Tester t) {
    init();
    this.mountain.playerpos = new Posn(2, 2);
    t.checkExpect(this.mountain.timeleft, 2);
    t.checkExpect(this.mountain.waterHeight, 0);
    t.checkExpect(this.mountain.time, 0);
    this.mountain.onKeyEvent("p");
    this.mountain.onKeyEvent("left");
    this.mountain.onTick();
    t.checkExpect(this.mountain.timeleft, 2);
    t.checkExpect(this.mountain.waterHeight, 0);
    t.checkExpect(this.mountain.time, 0);
    t.checkExpect(this.mountain.playerpos, new Posn(2, 2));
    
  }
  
  // Main method to run the world program
  public static void main(String[] argv) {

    // run the game
    ForbiddenIslandWorld w = new ForbiddenIslandWorld("mountain");
    w.bigBang((ForbiddenIslandWorld.ISLAND_SIZE + 1) * (ForbiddenIslandWorld.CELL_SIZE),
        (ForbiddenIslandWorld.ISLAND_SIZE + 1) * (ForbiddenIslandWorld.CELL_SIZE), .1);
  }
}