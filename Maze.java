import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//A generic list
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

// Visitors for ILists
interface IListVisitor<T, R> {

  // Visits from the MtList class
  R visitMt(MtList<T> list);

  // Visits from the ConsList class
  R visitCons(ConsList<T> list);
}

// Visitor that determines if an edge is already in a list of edges
class EdgeInList implements IListVisitor<Edge, Boolean> {
  
  Edge edge;
  
  EdgeInList(Edge edge) {
    this.edge = edge;
  }
  
  // Visits from the MtList class, is this edge in the given MtList?
  public Boolean visitMt(MtList<Edge> list) {
    return false;
  }

  // Visits from the ConsList class, is this edge in the given ConsList?
  public Boolean visitCons(ConsList<Edge> list) {
    boolean isIn = false;
    Posn node1 = this.edge.node1;
    Posn node2 = this.edge.node2;
    for (Edge e : list) {
      if ((node1.equals(e.node1) && node2.equals(e.node2))
          || (node1.equals(e.node2) && node2.equals(e.node1))) {
        isIn = true;
      }
    }
    return isIn;
  }
}

// Visitor that calculates an edge list size
class ListSize implements IListVisitor<Edge, Integer> {
  
  // Visits from the MtList class, calcs the Mt list size
  public Integer visitMt(MtList<Edge> list) {
    return 0;
  }

  // Visits from the ConsList class, calcs the Conslist size
  public Integer visitCons(ConsList<Edge> list) {
    Integer acc = 0;
    for (Edge e : list) {
      acc += 1;
    }
    return acc;
  }
}


class Cell {
  
  Posn pos;
  Color color;
  Cell top;
  Cell left;
  Cell right;
  Cell bottom;
}

class Edge {
  
  Posn node1;
  int weight;
  Posn node2;
  
  // Constructor
  Edge(Posn node1, Posn node2) {
    this.node1 = node1;
    this.weight = MazeGeneratorWorld.randomizer.nextInt(10001);
    this.node2 = node2;
  }
}


class MazeGeneratorWorld extends World {
  
  // Horizontal number of cells
  int cellLength;
  // Vertical number of cells
  int cellHeight;
  // cell pixel width
  int cellSize;
  // minimum spanning tree
  IList<Edge> tree;
  static final Random randomizer = new Random();
  
  // Constructor
  MazeGeneratorWorld(int cellLength, int cellHeight) {
    this.cellLength = cellLength;
    this.cellHeight = cellHeight;
    this.cellSize = 100;
    this.tree = this.makeAllEdges();
  }
  
  // makes a list of every possible edge to be included in the minimum spanning tree.
  // randomizes their weights.
  IList<Edge> makeAllEdges() {
    IList<Edge> base = new MtList<Edge>();
    for (int i = 0; i <= this.cellLength; i = i + 1) {
      for (int j = 0; j <= this.cellHeight; j = j + 1) {
        if (i != this.cellLength) {
          base = new ConsList<Edge>(new Edge(new Posn(i, j), new Posn(i + 1, j)), base);
        }
        if (j != this.cellHeight) {
          base = new ConsList<Edge>(new Edge(new Posn(i, j), new Posn(i, j + 1)), base);
        }
      }
    }
    return base;
  }
  
  // Returns the scene of this maze
  public WorldScene makeScene() {
    return this.getEmptyScene();
  }
}

class ExamplesMaze {
  
  MazeGeneratorWorld world1 = new MazeGeneratorWorld(1, 1);
  Edge edge1 = new Edge(new Posn(0, 0), new Posn(0, 1));
  IList<Edge> mt = new MtList<Edge>();
  IList<Edge> elist1 = new ConsList<Edge>(this.edge1, this.mt);
  IListVisitor<Edge, Boolean> edgeInList = new EdgeInList(this.edge1);
  IListVisitor<Edge, Integer> listSize = new ListSize();
  
  // initialization method
  void init() {
    this.world1 = new MazeGeneratorWorld(1, 1);
    this.edge1 = new Edge(new Posn(0, 0), new Posn(0, 1));
    this.mt = new MtList<Edge>();
    this.elist1 = new ConsList<Edge>(this.edge1, this.mt);
    this.edgeInList = new EdgeInList(this.edge1);
    this.listSize = new ListSize();
  }
  
  // tests for finding an edge in a list of edges
  void testEdgeInList(Tester t) {
    init();
    t.checkExpect(this.mt.accept(this.edgeInList), false);
    t.checkExpect(this.elist1.accept(this.edgeInList), true);
    t.checkExpect(this.elist1.accept(new EdgeInList(new Edge(new Posn(10, 10), new Posn(10, 11)))), false);
    t.checkExpect(this.elist1.accept(new EdgeInList(new Edge(new Posn(0, 1), new Posn(0, 0)))), true);
  }
  
  // tests for initial graph creation
  void testInitGraph(Tester t) {
    init();
    t.checkExpect(new MazeGeneratorWorld(0, 0).makeAllEdges(), this.mt);
    IList<Edge> elist = this.world1.makeAllEdges();
    ConsList<Edge> conselist = (ConsList<Edge>)elist;
    t.checkExpect(conselist.first.node1, new Posn(1, 0));
    t.checkExpect(conselist.first.node2, new Posn(1, 1));
    t.checkExpect(conselist.accept(this.listSize), 4);
  }
  
  // tests for list size
  void testSize(Tester t) {
    init();
    t.checkExpect(this.mt.accept(this.listSize), 0);
    t.checkExpect(elist1.accept(this.listSize), 1);
    t.checkExpect(new ConsList<Edge>(this.edge1, this.elist1).accept(this.listSize), 2);
  }
  
  // Main method to run the world program
  public static void main(String[] argv) {
    
    // run the game
    MazeGeneratorWorld w = new MazeGeneratorWorld(1, 1);
    w.bigBang((w.cellLength + 1) * w.cellSize,
        (w.cellHeight + 1) * w.cellSize);
  }
}