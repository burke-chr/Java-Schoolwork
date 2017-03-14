// Assignment 8 - Problem 2
// Rothweiler Justin 
// jrothweiler
// Valluru Mahitha
// mmv456

import tester.*;

// To represent a two directional list
class Deque<T> {
  Sentinel<T> header;

  // Initial constructor
  Deque() {
    this.header = new Sentinel<T>();
  }

  // Convenience Constructor, takes a specific sentinel
  Deque(Sentinel<T> header) {
    this.header = header;
  }

  /*TEMPLATE:
   * Fields:
   *  ... this.header ...     -- Sentinel<T>
   *  
   * Methods:
   *  ... this.size() ...                 -- int
   *  ... this.addAtHead(T) ...           -- void
   *  ... this.addAtTail(T) ...           -- void
   *  ... this.find(IPred<T>) ...         -- ANode<T>
   *  ... this.removeFromHead() ...       -- T
   *  ... this.removeFromTail() ...       -- T
   *  ... this.removeNode(ANode<T>) ...   -- void
   *  
   * Methods for Fields:
   *  ... this.header.next.sizeHelp() ...       -- int
   *  ... this.header.add() ...                 -- void
   *  ... this.header.next.findHelper() ...     -- ANode<T>
   *  ... this.header.next.remove() ...         -- T
   * 
   */

  // how many nodes are in this Deque<T>
  // method template: same as class template
  int size() {
    return this.header.next.sizeHelp();
  }

  // adds a node with the given T value at the head of the deque
  // method template: same as class template
  void addAtHead(T t) {
    this.header.add(t, 0);
  }

  // adds a node with the given T value at the tail of the deque
  // method template: same as class template
  void addAtTail(T t) {
    this.header.add(t, this.size());
  }

  // produces the first node that satisfies the given predicate
  /* FIELDS OF PARAMETERS - none
   * 
   * METHODS OF PARAMETERS - 
   * pred.apply(T) - boolean
   */
  ANode<T> find(IPred<T> pred) {
    return this.header.next.findHelper(pred);
  }

  // removes a node with the given T value at the head of the deque
  // method template: same as class template
  T removeFromHead() {
    if (this.size() == 0) {
      throw new RuntimeException("Cannot remove from an empty deque");
    }
    else {
      return this.header.next.remove(this.header.next);
    }
  }

  // removes a node with the given T value at the tail of the deque
  // method template: same as class template
  T removeFromTail() {
    if (this.size() == 0) {
      throw new RuntimeException("Cannot remove from an empty deque");
    }
    else {
      return this.header.next.remove(this.header.prev);
    }
  }

  // removes the given node from this deque
  /* FIELDS OF PARAMETERS - 
   * node.next - ANode<T>
   * node.prev - ANode<T>
   * 
   * METHODS OF PARAMETERS - 
   * node.sizeHelp() ...            -- int
   * node.add(T, int) ...           -- void
   * node.remove(ANode<T>) ...      -- T
   * node.findHelper(IPred<T>) ...  -- ANode<T>
   */
  void removeNode(ANode<T> node) {
    this.header.next.remove(node);
  }
}

// To represent various nodes of a deque
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;


  /*TEMPLATE:
   * Fields:
   *  ... this.next ...     -- ANode<T>
   *  ... this.prev ...     -- ANode<T>
   *  
   * Methods:
   *  ... this.sizeHelp() ...            -- int
   *  ... this.add(T, int) ...           -- void
   *  ... this.remove(ANode<T>) ...      -- T
   *  ... this.findHelper(IPred<T>) ...  -- ANode<T>
   *  
   * Methods for Fields:
   *  ... this.next.sizeHelp() ...       -- int
   *  ... this.next.add() ...                 -- void
   *   
   */

  // helper for size, adds one for each node until the sentinel
  // method template: same as class template
  int sizeHelp() {
    return 1 + this.next.sizeHelp();
  }

  // adds a node with the given t value at the given index
  // method template: same as class template
  void add(T t, int index) {
    if (index == 0) {
      this.next = new Node<T>(t, this.next, this);
    }
    else {
      this.next.add(t, index - 1);
    }
  }

  // removes the given node 
  abstract T remove(ANode<T> node);

  // helper method for find method
  /* FIELDS OF PARAMETERS - none
   * 
   * METHODS OF PARAMETERS - 
   * pred.apply(T) - boolean
   */
  abstract ANode<T> findHelper(IPred<T> pred);

}

// To represent a dataless node of a deque
class Sentinel<T> extends ANode<T> {

  // Constructor
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  /*TEMPLATE:
   * Fields:
   *  -- Same as ANode<T> --
   *  
   * Methods:
   *  -- Same as ANode<T> --
   *  
   * Methods for Fields:
   *  -- Same as ANode<T> --
   * 
   */

  // overrides sizeHelp in ANode class to terminate
  // method template: same as class template
  int sizeHelp() {
    return 0;
  }

  // helper method for find method
  /* FIELDS OF PARAMETERS - none
   * 
   * METHODS OF PARAMETERS - 
   * pred.apply(T) - boolean
   */
  ANode<T> findHelper(IPred<T> pred) {
    return this;
  }

  // removes the given node 
  /* FIELDS OF PARAMETERS - 
   * node.next - ANode<T>
   * node.prev - ANode<T>
   * 
   * METHODS OF PARAMETERS - 
   * node.sizeHelp() ...            -- int
   * node.add(T, int) ...           -- void
   * node.remove(ANode<T>) ...      -- T
   * node.findHelper(IPred<T>) ...  -- ANode<T>
   */
  T remove(ANode<T> node) {
    return null;
  }
}

// To represent a data node of a deque
class Node<T> extends ANode<T> {
  T data;

  // Initial constructor
  Node(T data) {
    this.data = data;
    this.next = null;
    this.prev = null;
  }

  // Convenience constructor: assigns given ANodes to the next and prev
  Node(T data, ANode<T> next, ANode<T> prev) {
    this.data = data;
    if ((next == null) || (prev == null)) {
      throw new IllegalArgumentException("Cannot initialize null");
    }
    else {
      this.next = next;
      this.prev = prev;
      next.prev = this;
      prev.next = this;
    }
  }

  /*TEMPLATE:
   * Fields:
   *  -- Same as ANode<T> --
   *  ... this.data ...     -- T
   *  
   * Methods:
   *  -- Same as ANode<T> --
   *  
   * Methods for Fields:
   *  -- Same as ANode<T> --
   * 
   */

  // helper method for find method
  /* FIELDS OF PARAMETERS - none
   * 
   * METHODS OF PARAMETERS - 
   * pred.apply(T) - boolean
   */
  ANode<T> findHelper(IPred<T> pred) {
    if (pred.apply(this.data)) {
      return this;
    }
    else {
      return this.next.findHelper(pred);
    }
  }

  // removes the given node 
  /* FIELDS OF PARAMETERS - 
   * node.next - ANode<T>
   * node.prev - ANode<T>
   * 
   * METHODS OF PARAMETERS - 
   * node.sizeHelp() ...            -- int
   * node.add(T, int) ...           -- void
   * node.remove(ANode<T>) ...      -- T
   * node.findHelper(IPred<T>) ...  -- ANode<T>
   */
  T remove(ANode<T> node) {
    if (node == this) {
      this.prev.next = this.next;
      this.next.prev = this.prev;
      return this.data;
    }
    else {
      return this.next.remove(node);

    }
  }




}

// Represents a boolean-valued question over values of type T
interface IPred<T> {
  boolean apply(T t);
}

// predicate function object that determines if a string length is 4
class IsFour implements IPred<String> {
  
  /*TEMPLATE:
   * Fields:
   *  -- None --
   *  
   * Methods:
   *  ... this.apply(String) ...   -- boolean
   *  
   * Methods for Fields:
   *  ... this.s.length() ...      -- int
   * 
   */

  // is the given string of length 4?
  // method template: same as class template
  public boolean apply(String s) { 
    return s.length() == 4;
  }
}

// predicate function object that determines if a string is "bcd"
class IsBCD implements IPred<String> {
  
  /*TEMPLATE:
   * Fields:
   *  -- None --
   *  
   * Methods:
   *  ... this.apply(String) ...   -- boolean
   *  
   * Methods for Fields:
   *  ... this.s.equals() ...      -- boolean
   * 
   */

  // is the given string "bcd"?
  // method template: same as class template
  public boolean apply(String s) {
    return s.equals("bcd");
  }
}

// various examples and tests of deques
class ExamplesDeque {
  Deque<String> deque1 = new Deque<String>();

  Deque<String> deque2 = new Deque<String>();
  Node<String> abc = new Node<String>("abc", this.deque2.header, this.deque2.header);
  Node<String> bcd = new Node<String>("bcd", this.deque2.header, this.abc);
  Node<String> cde = new Node<String>("cde", this.deque2.header, this.bcd);
  Node<String> def = new Node<String>("def", this.deque2.header, this.cde);

  Deque<String> deque3 = new Deque<String>();
  Node<String> dog = new Node<String>("dog", this.deque3.header, this.deque3.header);
  Node<String> cat = new Node<String>("cat", this.deque3.header, this.dog);
  Node<String> fish = new Node<String>("fish", this.deque3.header, this.cat);
  Node<String> horse = new Node<String>("horse", this.deque3.header, this.fish);

  IPred<String> IsFour = new IsFour();
  IPred<String> IsBCD = new IsBCD();

  // initializes deque data
  void initDeques() {
    this.deque1 = new Deque<String>();

    this.deque2 = new Deque<String>();
    this.abc = new Node<String>("abc", this.deque2.header, this.deque2.header);
    this.bcd = new Node<String>("bcd", this.deque2.header, this.abc);
    this.cde = new Node<String>("cde", this.deque2.header, this.bcd);
    this.def = new Node<String>("def", this.deque2.header, this.cde);

    this.deque3 = new Deque<String>();
    this.dog = new Node<String>("dog", this.deque3.header, this.deque3.header);
    this.cat = new Node<String>("cat", this.deque3.header, this.dog);
    this.fish = new Node<String>("fish", this.deque3.header, this.cat);
    this.horse = new Node<String>("horse", this.deque3.header, this.fish);
  }

  // tests for assignment accuracy
  void testAssignments(Tester t) {
    initDeques();
    t.checkExpect(this.deque1.header, new Sentinel<String>());

    t.checkExpect(this.deque2.header.next, this.abc);
    t.checkExpect(this.abc.next, this.bcd);
    t.checkExpect(this.bcd.next, this.cde);
    t.checkExpect(this.cde.next, this.def);
    t.checkExpect(this.def.next, this.deque2.header);
    t.checkExpect(this.abc.prev, this.deque2.header);
    t.checkExpect(this.bcd.prev, this.abc);
    t.checkExpect(this.cde.prev, this.bcd);
    t.checkExpect(this.def.prev, this.cde);
    t.checkExpect(this.deque2.header.prev, this.def);

    t.checkExpect(this.deque3.header.next, this.dog);
    t.checkExpect(this.dog.next, this.cat);
    t.checkExpect(this.cat.next, this.fish);
    t.checkExpect(this.fish.next, this.horse);
    t.checkExpect(this.horse.next, this.deque3.header);
    t.checkExpect(this.dog.prev, this.deque3.header);
    t.checkExpect(this.cat.prev, this.dog);
    t.checkExpect(this.fish.prev, this.cat);
    t.checkExpect(this.horse.prev, this.fish);
    t.checkExpect(this.deque3.header.prev, this.horse);
  }
  
  // Testing the exception code
  void testException(Tester t) {
    initDeques();
    t.checkException(
        new RuntimeException("Cannot remove from an empty deque"),
        this.deque1, "removeFromHead");
    t.checkException(
        new RuntimeException("Cannot remove from an empty deque"),
        this.deque1, "removeFromTail");
  }

  // tests for size method
  void testSize(Tester t) {
    initDeques();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 4);
  }

  // tests methods for adding items to deques
  void testAdd(Tester t) {
    initDeques();
    this.deque2.addAtHead("aaa");
    this.deque2.addAtTail("zzz");
    this.deque2.header.add("ccc", 3);

    this.deque3.addAtHead("elephant");
    this.deque3.addAtTail("emu");
    this.deque3.header.add("aardvark", 2);

    t.checkExpect(this.deque2.header.next, new Node<String>("aaa", this.abc, this.deque2.header));
    t.checkExpect(this.deque2.header.prev, new Node<String>("zzz", this.deque2.header, this.def));
    t.checkExpect(this.abc.prev, new Node<String>("aaa", this.abc, this.deque2.header));
    t.checkExpect(this.def.next, new Node<String>("zzz", this.deque2.header, this.def));
    t.checkExpect(this.bcd.next, new Node<String>("ccc", this.cde, this.bcd));

    t.checkExpect(this.deque3.header.next,
        new Node<String>("elephant", this.dog, this.deque3.header));
    t.checkExpect(this.deque3.header.prev, new Node<String>("emu", this.deque3.header, this.horse));
    t.checkExpect(this.dog.prev, new Node<String>("elephant", this.dog, this.deque3.header));
    t.checkExpect(this.horse.next, new Node<String>("emu", this.deque3.header, this.horse));
    t.checkExpect(this.dog.next, new Node<String>("aardvark", this.cat, this.dog));
  }

  // tests methods for removing items from deques
  void testRemove(Tester t) {
    initDeques();
    Deque<String> initdeque1 = this.deque1;
    this.deque1.removeNode(this.bcd);
    this.deque2.removeFromHead();
    this.deque2.removeFromTail();
    this.deque3.header.next.remove(this.cat);

    t.checkExpect(this.deque2.header.next, this.bcd);
    t.checkExpect(this.deque2.header.prev, this.cde);
    t.checkExpect(this.bcd.prev, this.deque2.header);
    t.checkExpect(this.cde.next, this.deque2.header);
    t.checkExpect(this.dog.next, this.fish);
    t.checkExpect(this.fish.prev, this.dog);
    t.checkExpect(initdeque1, this.deque1);
  }

  // tests for apply methods in IPreds
  void testApply(Tester t) {
    initDeques();
    t.checkExpect(this.IsBCD.apply("abc"), false);
    t.checkExpect(this.IsBCD.apply("bcd"), true);
    t.checkExpect(this.IsBCD.apply("cde"), false);
    t.checkExpect(this.IsBCD.apply("def"), false);

    t.checkExpect(this.IsFour.apply("dog"), false);
    t.checkExpect(this.IsFour.apply("cat"), false);
    t.checkExpect(this.IsFour.apply("fish"), true);
    t.checkExpect(this.IsFour.apply("horse"), false);
  }

  // tests for the find method
  void testFind(Tester t) {
    initDeques();
    t.checkExpect(this.deque1.find(IsBCD), this.deque1.header);
    t.checkExpect(this.deque2.find(IsBCD), this.bcd);
    t.checkExpect(this.deque3.find(IsFour), this.fish);
    t.checkExpect(this.deque2.find(IsFour), this.deque2.header);
  }

  // tests for the removeNode method
  void testRemoveNode(Tester t) {
    initDeques();
    this.deque2.removeNode(this.def);
    this.deque3.removeNode(this.fish);
    t.checkExpect(this.deque2.header.prev, this.cde);
    t.checkExpect(this.cde.next, this.deque2.header);
    t.checkExpect(this.horse.prev, this.cat);
    t.checkExpect(this.cat.next, this.horse);
  }
}