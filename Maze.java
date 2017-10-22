// Assignment 10
// McLean Colin
// mcleancolin
// Bui Kristi
// kristibui

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//An iterator for IList
class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  public boolean hasNext() {
    return this.items.isCons();
  }

  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }

  public void remove() {
    throw new UnsupportedOperationException("Don't do this!");
  }
}

// to represent a general predicate interface
interface IPred<T, U> {

  // to apply a method
  boolean apply(T t, U u);
}

// represents a List
interface IList<T> extends Iterable<T> {

  // determines whether this list is a ConsList
  boolean isCons();

  // creates a ConsList of the given List
  ConsList<T> asCons();
}

// represents an empty list
class MtList<T> implements IList<T> {

  // determines whether this empty list is a ConsList
  public boolean isCons() {
    return false;
  }

  // creates a ConsList of the given List
  public ConsList<T> asCons() {
    return null;
  }

  // makes this IList iterable
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }
}

// represents a non empty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // determines whether this list is a ConsList
  public boolean isCons() {
    return true;
  }

  // returns this list as a ConsList
  public ConsList<T> asCons() {
    return this;
  }

  // makes this IList iterable
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }
}

// to represent a Vertex
class Vertex {
  int x;
  int y;
  IList<Edge> outEdges;

  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.outEdges = new MtList<Edge>();
  }

  Vertex(int x, int y, IList<Edge> outEdges) {
    this.x = x;
    this.y = y;
    this.outEdges = outEdges;
  }

  // determines if this Vertex has a path to the given Vertex
  boolean hasPathTo(Vertex b) {
    for (Edge e : this.outEdges) {
      if (e.to.equals(b) || e.to.hasPathTo(b)) {
        return true;
      }
    }
    return false;
  }

  // checks if this Vertex equals the given Vertex
  public boolean equals(Object b) {
    if (b instanceof Vertex) {
      Vertex other = (Vertex) b;
      return this.x == other.x && this.y == other.y;
    }
    else {
      return false;
    }
  }

  // overrides the hashCode function
  public int hashCode() {
    return this.x * 11 + this.y * 7;
  }

  // draws this Vertex
  public WorldImage drawVertex() {
    return new RectangleImage(MazeWorld.SCALE / 2, MazeWorld.SCALE / 2, "solid",
        new Color(191, 114, 114));
  }

  // draws the solution Vertices
  public WorldImage drawSolution() {
    return new RectangleImage(MazeWorld.SCALE / 2, MazeWorld.SCALE / 2, "solid",
        new Color(91, 153, 91));
  }
}

// to represent an Edge
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
  }

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  // checks if this Edge equals the given Edge
  public boolean equals(Object e) {
    if (e instanceof Edge) {
      Edge other = (Edge) e;
      return this.from.equals(other.from) && this.to.equals(other.to)
          && this.weight == other.weight;
    }
    else {
      return false;
    }
  }

  // overrides the hashcode method to check for equality
  public int hashCode() {
    return this.from.hashCode() + this.to.hashCode() + this.weight * 11;
  }

  // draws a Vertical Edge
  public WorldImage drawVerticalEdge() {
    return new RectangleImage(3, MazeWorld.SCALE, "solid", new Color(0, 0, 0));
  }

  // draws a Horizontal Edge
  public WorldImage drawHorizontalEdge() {
    return new RectangleImage(MazeWorld.SCALE, 3, "solid", new Color(0, 0, 0));
  }

  // draws a Vertical Edge
  public WorldImage drawVerticalBranch() {
    return new RectangleImage(3, MazeWorld.SCALE, "solid", new Color(91, 153, 91));
  }

  // draws a Horizontal Edge
  public WorldImage drawHorizontalBranch() {
    return new RectangleImage(MazeWorld.SCALE, 3, "solid", new Color(91, 153, 91));
  }
}

// to represent a Player
class Player {
  int x;
  int y;

  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // moves the player
  public Player movePlayer(String ke, ArrayList<ArrayList<Vertex>> arrayVertex,
      ArrayList<Edge> mazeList) {
    if (ke.equals("right") && this.moveRight(arrayVertex, mazeList)) {
      return new Player(this.x + 1, this.y);
    }
    else if (ke.equals("left") && this.moveLeft(arrayVertex, mazeList)) {
      return new Player(this.x - 1, this.y);
    }
    else if (ke.equals("up") && this.moveUp(arrayVertex, mazeList)) {
      return new Player(this.x, this.y - 1);
    }
    else if (ke.equals("down") && this.moveDown(arrayVertex, mazeList)) {
      return new Player(this.x, this.y + 1);
    }
    else {
      return this;
    }
  }

  // to see if the player can move right
  boolean moveRight(ArrayList<ArrayList<Vertex>> arrayVertex, ArrayList<Edge> mazeList) {
    return this.x + 1 < MazeWorld.MAZE_WIDTH
        && !(mazeList.contains(
            new Edge(arrayVertex.get(this.x).get(this.y), arrayVertex.get(this.x + 1).get(this.y))))
        && !(mazeList.contains(new Edge(arrayVertex.get(this.x + 1).get(this.y),
            arrayVertex.get(this.x).get(this.y))));
  }

  // to see if the player can move left
  boolean moveLeft(ArrayList<ArrayList<Vertex>> arrayVertex, ArrayList<Edge> mazeList) {
    return this.x - 1 >= 0
        && !(mazeList.contains(
            new Edge(arrayVertex.get(this.x).get(this.y), arrayVertex.get(this.x - 1).get(this.y))))
        && !(mazeList.contains(new Edge(arrayVertex.get(this.x - 1).get(this.y),
            arrayVertex.get(this.x).get(this.y))));
  }

  // to see if the player can move up
  boolean moveUp(ArrayList<ArrayList<Vertex>> arrayVertex, ArrayList<Edge> mazeList) {
    return this.y - 1 >= 0
        && !(mazeList.contains(
            new Edge(arrayVertex.get(this.x).get(this.y), 
                arrayVertex.get(this.x).get(this.y - 1))))
        && !(mazeList.contains(new Edge(arrayVertex.get(this.x).get(this.y - 1),
            arrayVertex.get(this.x).get(this.y))));
  }

  // to see if the player can move down
  boolean moveDown(ArrayList<ArrayList<Vertex>> arrayVertex, ArrayList<Edge> mazeList) {
    return this.y + 1 < MazeWorld.MAZE_HEIGHT
        && !(mazeList.contains(
            new Edge(arrayVertex.get(this.x).get(this.y), 
                arrayVertex.get(this.x).get(this.y + 1))))
        && !(mazeList.contains(new Edge(arrayVertex.get(this.x).get(this.y + 1),
            arrayVertex.get(this.x).get(this.y))));
  }

  // draws the player
  public WorldImage drawPlayer() {
    return new RectangleImage(MazeWorld.SCALE, MazeWorld.SCALE, "solid", new Color(190, 140, 114));
  }
}

// to represent the Forbidden Island world
class MazeWorld extends World {

  // Defines an int constant
  static final int MAZE_HEIGHT = 15;

  // Defines an int constant
  static final int MAZE_WIDTH = 20;

  // scale for the vertex size
  static final int SCALE = 60;

  // random nummber generator
  Random rand = new Random();

  // counter for knocking down the walls
  int c = 0;

  // sets the random number weight for the y edges
  int jNum = 100;

  // sets the random number weight for the x edges
  int iNum = 100;

  // represents the initial vertices
  ArrayList<ArrayList<Vertex>> arrayVertex = new ArrayList<ArrayList<Vertex>>(MAZE_WIDTH);

  // represents the vertices
  ArrayList<Vertex> allVertex = new ArrayList<Vertex>(MAZE_WIDTH * MAZE_HEIGHT);

  // to represent the tree
  HashMap<Vertex, Vertex> reps = new HashMap<Vertex, Vertex>();

  // to represent the edges in the tree
  ArrayList<Edge> edgesInTree = new ArrayList<Edge>();

  // to represent the edges sorted by weights
  ArrayList<Edge> workList = new ArrayList<Edge>();

  // to represent the edges that will be knocked down, and are sorted by weights
  ArrayList<Edge> workListKnock = new ArrayList<Edge>();

  // to represent the edges that compose the maze
  ArrayList<Edge> mazeList = new ArrayList<Edge>();

  // to represent the Vertices that have already been seen
  ArrayList<Vertex> alreadySeen = new ArrayList<Vertex>();

  // to represent the solution in a HashMap with the predecessors of each Vertex
  HashMap<Vertex, Vertex> predecessor = new HashMap<Vertex, Vertex>();

  // to represent the solution to the maze
  ArrayList<Vertex> solution = new ArrayList<Vertex>();

  // keeps track of the number of wrong moves
  int wrongMoves = 0;

  // checks to see whether the user wants to display the minimal spanning tree or not
  boolean mst = false;

  // checks to see whether the viewer wants to see the Vertices they have
  // visited
  boolean alreadySeenVert = true;

  // checks to see whether or not the viewer wants to see the walls being
  // knocked down
  boolean knockDown = true;

  // shows the search path
  ArrayList<Vertex> searchPath = new ArrayList<Vertex>();

  // to represent the Player
  Player p = new Player(0, 0);

  MazeWorld() {
    this.initArrayVertex();
    this.initArrayEdges();
    this.initWorkList();
    this.initReps();
    this.buildTree();
    this.initMazeList();
    this.initWLK();
  }

  // to initialize the vertices
  void initArrayVertex() {
    for (int i = 0; i < MAZE_WIDTH; i++) {
      ArrayList<Vertex> alv = new ArrayList<Vertex>(MAZE_HEIGHT);
      for (int j = 0; j < MAZE_HEIGHT; j++) {
        alv.add(j, new Vertex(i, j));
      }
      this.arrayVertex.add(i, alv);
    }

    for (int a = 0; a < MAZE_WIDTH; a++) {
      for (int b = 0; b < MAZE_HEIGHT; b++) {
        this.allVertex.add(this.arrayVertex.get(a).get(b));
      }
    }
  }

  // to initialize the vertices with Edges
  void initArrayEdges() {
    for (int i = 0; i < MAZE_WIDTH; i++) {
      for (int j = 0; j < MAZE_HEIGHT; j++) {
        this.createVertex(i, j);
      }
    }
    this.alreadySeen.add(this.arrayVertex.get(0).get(0));
  }

  // to initialize the vertices with Edges
  void createVertex(int i, int j) {
    Vertex v = this.arrayVertex.get(i).get(j);
    if (j != 0 && (j % 2 == 0)) {
      int randomNum = rand.nextInt(this.jNum);
      this.workList.add(new Edge(v, this.arrayVertex.get(i).get(j - 1), randomNum));
    }
    if (j != MAZE_HEIGHT - 1 && (j % 2 == 0)) {
      int randomNum = rand.nextInt(this.jNum);
      this.workList.add(new Edge(v, this.arrayVertex.get(i).get(j + 1), randomNum));
    }
    if (i != 0 && (i % 2 == 0)) {
      int randomNum = rand.nextInt(this.iNum);
      this.workList.add(new Edge(v, this.arrayVertex.get(i - 1).get(j), randomNum));
    }
    if (i != MAZE_WIDTH - 1 && (i % 2 == 0)) {
      int randomNum = rand.nextInt(this.iNum);
      this.workList.add(new Edge(v, this.arrayVertex.get(i + 1).get(j), randomNum));
    }
  }

  // sorts this workList
  void initWorkList() {
    Collections.sort(this.workList, new Comparator<Edge>() {
      public int compare(Edge e1, Edge e2) {
        return e1.weight - e2.weight;
      }
    });
  }

  // initializes the HashMap
  void initReps() {
    for (Vertex v : this.allVertex) {
      this.reps.put(v, v);
    }
  }

  // to initialize the graph
  void buildTree() {
    ArrayList<Edge> workListCopy = new ArrayList<Edge>(this.workList);
    while (this.edgesInTree.size() < this.allVertex.size() - 1) {
      Edge e = workListCopy.get(0);
      Vertex x = e.from;
      Vertex y = e.to;
      workListCopy.remove(0);
      if (!(this.find(x).equals(this.find(y)))) {
        this.edgesInTree.add(e);
        this.union(x, y);
        Vertex asx = this.arrayVertex.get(x.x).get(x.y);
        Vertex asy = this.arrayVertex.get(y.x).get(y.y);
        asx.outEdges = new ConsList<Edge>(new Edge(asx, asy, e.weight), asx.outEdges);
        asy.outEdges = new ConsList<Edge>(new Edge(asy, asx, e.weight), asy.outEdges);
      }
    }
  }

  // assigns the representative of a to the representative of b
  void union(Vertex a, Vertex b) {
    this.reps.put(this.find(a), this.find(b));
  }

  // finds the representative of the given Vertex
  Vertex find(Vertex a) {
    if (this.reps.get(a).equals(a)) {
      return a;
    }
    else {
      return this.find(this.reps.get(a));
    }
  }

  // to construct the List of Edges in the Maze
  void initMazeList() {
    for (Edge e : this.workList) {
      if (!(this.edgesInTree.contains(e))) {
        this.mazeList.add(new Edge(e.from, e.to));
      }
    }
  }

  // checks to see if there is a path between these two vertex
  boolean depthFirstSearch(Vertex from, Vertex to) {
    ArrayList<Vertex> worklist = new ArrayList<Vertex>();
    worklist.add(0, from);
    while (!worklist.isEmpty()) {
      Vertex next = worklist.remove(0);
      if (next.equals(to)) {
        this.searchPath.add(next);
        return true;
      }
      else if (this.searchPath.contains(next)) {
        // already saw this vertex
      }
      else {
        for (Edge e : next.outEdges) {
          worklist.add(0, e.to);
          if (this.predecessor.containsKey(e.to)) {
            wrongMoves++;
          }
          if (!(this.predecessor.containsKey(e.to))) {
            this.predecessor.put(e.to, next);
          }
        }
        this.searchPath.add(next);
      }
    }
    return false;
  }

  // checks to see if there is a path between these two vertex
  boolean breadthFirstSearch(Vertex from, Vertex to) {
    ArrayList<Vertex> worklist = new ArrayList<Vertex>();
    worklist.add(0, from);
    while (!worklist.isEmpty()) {
      Vertex next = worklist.remove(0);
      if (next.equals(to)) {
        this.searchPath.add(next);
        return true;
      }
      else if (this.searchPath.contains(next)) {
        // already saw this vertex
      }
      else {
        for (Edge e : next.outEdges) {
          worklist.add(e.to);
          if (this.predecessor.containsKey(e.to)) {
            wrongMoves++;
          }
          if (!(this.predecessor.containsKey(e.to))) {
            this.predecessor.put(e.to, next);
          }
        }
        this.searchPath.add(next);
      }
    }
    return false;
  }

  // initializes the workList to knock down the walls
  void initWLK() {
    this.workListKnock = new ArrayList<Edge>(this.workList);
  }

  // initializes the solution array
  void initSolution() {
    Vertex curr = this.alreadySeen.get(0);
    while (!(curr.equals(this.arrayVertex.get(0).get(0)))) {
      this.solution.add(0, curr);
      curr = this.predecessor.get(curr);
    }
  }

  // produces the image of the world
  public WorldScene makeScene() {
    javalib.impworld.WorldScene s = this.getEmptyScene();
    if (knockDown) {
      for (Edge e : this.workListKnock) {
        if (e.to.x == e.from.x) {
          s.placeImageXY(e.drawHorizontalEdge(),
              (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
              (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
        }
        else if (e.to.y == e.from.y) {
          s.placeImageXY(e.drawVerticalEdge(),
              (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
              (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
        }
      }
    }
    if (!knockDown) {
      for (Edge e : this.workList) {
        if (!(this.edgesInTree.contains(e))) {
          if (e.to.x == e.from.x) {
            s.placeImageXY(e.drawHorizontalEdge(),
                (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
                (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
          }
          else if (e.to.y == e.from.y) {
            s.placeImageXY(e.drawVerticalEdge(),
                (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
                (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
          }
        }
      }
    }
    if (mst) {
      for (Edge e : this.workList) {
        if (this.edgesInTree.contains(e)) {
          if (e.to.x == e.from.x) {
            s.placeImageXY(e.drawVerticalBranch(),
                (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
                (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
          }
          else if (e.to.y == e.from.y) {
            s.placeImageXY(e.drawHorizontalBranch(),
                (int) (((e.to.x + e.from.x) / 2.0) * SCALE) + SCALE / 2,
                (int) (((e.to.y + e.from.y) / 2.0) * SCALE) + SCALE / 2);
          }
        }
      }
    }
    if (alreadySeenVert) {
      for (Vertex v : this.alreadySeen) {
        s.placeImageXY(v.drawVertex(), (v.x * SCALE) + SCALE / 2, (v.y * SCALE) + SCALE / 2);
      }
    }
    if (this.wrongMoves != 0) {
      s.placeImageXY(new TextImage("Wrong moves: " + this.wrongMoves + "", SCALE, 
          new Color(76, 127, 112)),
          SCALE * 5,(MAZE_HEIGHT - 2) * SCALE);
    }    
    s.placeImageXY(new RectangleImage(SCALE, SCALE, "solid", new Color(76, 127, 112)),
        ((MAZE_WIDTH - 1) * SCALE) + SCALE / 2, ((MAZE_HEIGHT - 1) * SCALE) + SCALE / 2);
    s.placeImageXY(this.p.drawPlayer(), (this.p.x * SCALE) + SCALE / 2,
        (this.p.y * SCALE) + SCALE / 2);
    return s;
  }

  // resets the game, displays the minimal spanning tree
  public void onKeyEvent(String ke) {
    if (ke.equals("n")) {
      this.arrayVertex = new ArrayList<ArrayList<Vertex>>(MAZE_WIDTH);
      this.allVertex = new ArrayList<Vertex>(MAZE_WIDTH * MAZE_HEIGHT);
      this.reps = new HashMap<Vertex, Vertex>();
      this.edgesInTree = new ArrayList<Edge>();
      this.workList = new ArrayList<Edge>();
      this.mazeList = new ArrayList<Edge>();
      this.p = new Player(0, 0);
      this.alreadySeen = new ArrayList<Vertex>();
      this.searchPath = new ArrayList<Vertex>();
      this.initArrayVertex();
      this.initArrayEdges();
      this.initWorkList();
      this.initReps();
      this.buildTree();
      this.initMazeList();
      this.initWLK();
      this.c = 0;
      this.wrongMoves = 0;
    }
    else if (ke.equals("t")) {
      this.mst = !mst;
    }
    else if (ke.equals("d")) {
      this.depthFirstSearch(this.arrayVertex.get(0).get(0),
          this.arrayVertex.get(MAZE_WIDTH - 1).get(MAZE_HEIGHT - 1));
    }
    else if (ke.equals("b")) {
      this.breadthFirstSearch(this.arrayVertex.get(0).get(0),
          this.arrayVertex.get(MAZE_WIDTH - 1).get(MAZE_HEIGHT - 1));
    }
    else if (ke.equals("h")) {
      this.iNum = 20;
      this.jNum = 100;
    }
    else if (ke.equals("v")) {
      this.iNum = 100;
      this.jNum = 20;
    }
    else if (ke.equals("c")) {
      this.iNum = 100;
      this.jNum = 100;
    }
    else if (ke.equals("p")) {
      this.alreadySeenVert = !alreadySeenVert;
    }
    else if (ke.equals("k")) {
      this.knockDown = !knockDown;
    }
    else {
      Player p1 = new Player(this.p.x, this.p.y);
      this.p = this.p.movePlayer(ke, this.arrayVertex, this.mazeList);
      this.alreadySeen.add(0, this.arrayVertex.get(this.p.x).get(this.p.y));
      if (this.predecessor.containsKey(this.arrayVertex.get(this.p.x).get(this.p.y))) {
        wrongMoves++;
      }
      if (!(this.predecessor.containsKey(this.arrayVertex.get(this.p.x).get(this.p.y)))) {
        this.predecessor.put(this.arrayVertex.get(this.p.x).get(this.p.y),
            this.arrayVertex.get(p1.x).get(p1.y));
      }
    }
  }

  // adds one to the search path
  public void onTick() {
    if (!this.searchPath.isEmpty()) {
      this.alreadySeen.add(0, this.searchPath.remove(0));
    }
    if (this.knockDown && this.edgesInTree.size() > c) {
      this.workListKnock.remove(this.edgesInTree.get(c));
      c++;
    }
  }

  // displays end game message
  public javalib.impworld.WorldScene makeFinalScene(String end, WorldScene s) {
    this.initSolution();

    // display the solution path
    for (Vertex v : this.solution) {
      s.placeImageXY(v.drawSolution(), (v.x * SCALE) + SCALE / 2, (v.y * SCALE) + SCALE / 2);
    }
    Vertex start = this.arrayVertex.get(0).get(0);
    s.placeImageXY(start.drawSolution(), (start.x * SCALE) + SCALE / 2,
        (start.y * SCALE) + SCALE / 2);
    s.placeImageXY(new TextImage(end, 60, new Color(0, 0, 0)), (MAZE_WIDTH * SCALE) / 2,
        (MAZE_HEIGHT * SCALE) / 2);
    return s;
  }

  // to end the game, with different scenarios that can determine the game
  // ending
  public WorldEnd worldEnds() {
    // if the player is in the bottom right corner: player WINS
    if (this.p.x == MAZE_WIDTH - 1 && this.p.y == MAZE_HEIGHT - 1) {
      return new WorldEnd(true, this.makeFinalScene("You solved the maze!", this.makeScene()));
    }
    else if (this.alreadySeen.get(0)
        .equals(this.arrayVertex.get(MAZE_WIDTH - 1).get(MAZE_HEIGHT - 1))) {
      return new WorldEnd(true, this.makeFinalScene("You solved the maze!", this.makeScene()));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

// to represent the Maze World examples
class ExamplesMaze {

  // Maze World Example
  MazeWorld world = new MazeWorld();
  MazeWorld world2 = new MazeWorld();
  MazeWorld world3 = new MazeWorld();

  // Examples of Players
  Player player1 = new Player(5, 4);
  Player player2 = new Player(0, 0);

  // Examples of Vertex
  Vertex v1 = new Vertex(5,4);
  Vertex v2 = new Vertex(0, 0);
  Vertex v3 = new Vertex(10, 8);
  Vertex v4 = new Vertex(9, 5);
  Vertex v5 = new Vertex(3, 3);
  Vertex v6 = new Vertex(2, 2);
  Vertex v7 = new Vertex(2, 3);

  // Examples of edges
  Edge e1 = new Edge(this.v1, this.v2, 5);
  Edge e2 = new Edge(this.v3, this.v4, 10);
  Edge e3 = new Edge(this.v4, this.v5, 16);
  Edge e4 = new Edge(this.v1, this.v2, 7);
  Edge e5 = new Edge(this.v2, this.v3, 9);

  // Examples of ArrayList<Edge>
  ArrayList<Edge> edgeList1 = new ArrayList<Edge>();
  ArrayList<Edge> edgeList2 = new ArrayList<Edge>();

  // Examples of ArrayList<Vertex>
  ArrayList<Vertex> vertexList1 = new ArrayList<Vertex>();

  // initial vertex test conditions
  void initTestConditions() {
    this.v1.outEdges = new ConsList<Edge>(this.e1, new ConsList<Edge>(this.e2,
        new MtList<Edge>()));
    this.v2.outEdges = new ConsList<Edge>(this.e2, new MtList<Edge>());
    this.v3.outEdges = new ConsList<Edge>(this.e1, new MtList<Edge>());
    this.v4.outEdges = new ConsList<Edge>(this.e3, new MtList<Edge>());
    this.v5.outEdges = new ConsList<Edge>(this.e3, new ConsList<Edge>(this.e2,
        new ConsList<Edge>(this.e1, new MtList<Edge>())));
    this.v6.outEdges = new ConsList<Edge>(this.e4, new MtList<Edge>());
    this.v7.outEdges = new MtList<Edge>();
  }

  // initial test conditions maze world 2
  void initTestConditionsTwo() {
    HashMap<Vertex, Vertex> hash = new HashMap<Vertex, Vertex>();
    hash.put(this.v2, this.v2);
    hash.put(this.v3, this.v3);
    hash.put(this.v4, this.v4);
    this.world2.reps = hash;
  }

  // initial test conditions maze world 3
  void initTestConditionsThree() {
    this.initTestConditions();
    this.vertexList1.add(this.v2);
    this.vertexList1.add(this.v4);
    this.world3.searchPath = this.vertexList1;
  }

  // to test method hasPathTo
  void testHasPathTo(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.v1.hasPathTo(this.v2), true);
    t.checkExpect(this.v3.hasPathTo(this.v5), true);
    t.checkExpect(this.v7.hasPathTo(this.v6), false);
  }

  // to test method equals
  void testEquals(Tester t) {
    t.checkExpect(this.v1.equals(this.v2), false);
    t.checkExpect(this.v2.equals(this.v2), true);
    t.checkExpect(this.e1.equals(this.e2), false);
    t.checkExpect(this.e3.equals(this.e3), true);
  }

  // to test method hashCode
  void testHashCode(Tester t) {
    t.checkExpect(this.e1.hashCode(), 138);
    t.checkExpect(this.e2.hashCode(), 410);
  }

  // to test method drawVertex
  void testDrawVertex(Tester t) {
    t.checkExpect(this.v1.drawVertex(), new RectangleImage(MazeWorld.SCALE / 2,
        MazeWorld.SCALE / 2, "solid", new Color(191, 114, 114)));
    t.checkExpect(this.v2.drawVertex(),  new RectangleImage(MazeWorld.SCALE / 2,
        MazeWorld.SCALE / 2, "solid", new Color(191, 114, 114)));
  }

  // to test method drawSolution
  void testDrawSolution(Tester t) {
    t.checkExpect(this.v1.drawSolution(), new RectangleImage(MazeWorld.SCALE / 2,
        MazeWorld.SCALE / 2, "solid", new Color(91, 153, 91)));
    t.checkExpect(this.v2.drawSolution(), new RectangleImage(MazeWorld.SCALE / 2,
        MazeWorld.SCALE / 2, "solid", new Color(91, 153, 91)));
  }

  // to test method drawVerticalEdge
  void testDrawVerticalEdge(Tester t) {
    t.checkExpect(this.e1.drawVerticalEdge(), new RectangleImage(3, MazeWorld.SCALE, 
        "solid", new Color(0, 0, 0)));
    t.checkExpect(this.e2.drawVerticalEdge(), new RectangleImage(3, MazeWorld.SCALE,
        "solid", new Color(0, 0, 0)));
  }

  // to test method drawHorizontalEdge
  void testDrawHorizontalEdge(Tester t) {
    t.checkExpect(this.e2.drawHorizontalEdge(), new RectangleImage(MazeWorld.SCALE, 3,
        "solid", new Color(0, 0, 0)));
    t.checkExpect(this.e3.drawHorizontalEdge(), new RectangleImage(MazeWorld.SCALE, 3,
        "solid", new Color(0, 0, 0)));
  }

  // to test method drawVerticalBranch
  void testDrawVerticalBranch(Tester t) {
    t.checkExpect(this.e2.drawVerticalBranch(), new RectangleImage(3, MazeWorld.SCALE,
        "solid", new Color(91, 153, 91)));
    t.checkExpect(this.e3.drawVerticalBranch(), new RectangleImage(3, MazeWorld.SCALE,
        "solid", new Color(91, 153, 91)));
  }

  // to test method drawHorizontalBranch
  void testDrawHorizontalBranch(Tester t) {
    t.checkExpect(this.e2.drawHorizontalBranch(), new RectangleImage(MazeWorld.SCALE, 3,
        "solid", new Color(91, 153, 91)));
    t.checkExpect(this.e3.drawHorizontalBranch(), new RectangleImage(MazeWorld.SCALE, 3,
        "solid", new Color(91, 153, 91)));
  }

  // to test method drawPlayer
  void testDrawPlayer(Tester t) {
    t.checkExpect(this.player1.drawPlayer(), new RectangleImage(MazeWorld.SCALE,
        MazeWorld.SCALE, "solid", new Color(190, 140, 114)));
    t.checkExpect(this.player2.drawPlayer(), new RectangleImage(MazeWorld.SCALE,
        MazeWorld.SCALE, "solid", new Color(190, 140, 114)));
  }

  // to test method initArrayVertex
  // tests that the initial vertices are initialized with the right x and y values
  void testInitArrayVertex(Tester t) {
    for (ArrayList<Vertex> aL : this.world.arrayVertex) {
      for (Vertex v : aL) {
        t.checkNumRange(v.x, 0, MazeWorld.MAZE_WIDTH);
        t.checkNumRange(v.y, 0, MazeWorld.MAZE_HEIGHT);
      }
    }
  }

  // to test method createVertex
  // tests that the edges are initialized with the right random weight
  void testCreateVertex(Tester t) {
    for (Edge e : this.world.workList) {
      t.checkNumRange(e.weight, 0, 100);
    }
  }

  // to test method initWorkList
  // tests that the edge weights are in an increasing order
  void testInitWorkList(Tester t) {
    ArrayList<Edge> arr = this.world.workList;
    for (int i = 1; i < arr.size(); i++) {
      t.checkNumRange(arr.get(i).weight, arr.get(i - 1).weight - 1, arr.get(i).weight + 1);
    }  
  }

  // to test method initReps
  // tests that the HashMap has been properly initialized with the vertices of the game
  void testInitReps(Tester t) {
    for (Vertex v : this.world.allVertex) {
      t.checkExpect(this.world.reps.containsKey(v), true);
    }
  }

  // to test method buildTree
  // tests that the size of the minimum spanning tree
  // is one less than the number of nodes
  void testBuildTree(Tester t) {
    t.checkExpect(this.world.edgesInTree.size(), this.world.allVertex.size() - 1);
  }

  // to test method union
  void testUnion(Tester t) {
    this.initTestConditionsTwo();

    // initial state before invoking union method
    t.checkExpect(this.world2.reps.get(this.v2), this.v2);

    this.world2.union(this.v2, this.v3);

    // state after invoking union method
    t.checkExpect(this.world2.reps.get(this.v2), this.v3);

  }

  // to test method find
  void testFind(Tester t) {
    this.initTestConditionsTwo();

    t.checkExpect(this.world2.find(this.v2), this.v2);
    t.checkExpect(this.world2.find(this.v3), this.v3);
    t.checkExpect(this.world2.find(this.v4), this.v4);
  }

  // to test method initMazeList
  // tests that the initialized maze list does not contain any of the
  // edges in the tree
  void testInitMazeList(Tester t) {
    ArrayList<Edge> mazeList = this.world.mazeList;
    ArrayList<Edge> edgesInTree = this.world.edgesInTree;

    for (Edge e : mazeList) {
      for (Edge w : edgesInTree) {
        t.checkExpect(e.equals(w), false);
      }
    }
  }

  // to test method depthFirstSearch
  void testDepthFirstSearch(Tester t) {
    this.initTestConditionsThree();
    t.checkExpect(this.world3.depthFirstSearch(this.v3, this.v2), true);
    t.checkExpect(this.world3.depthFirstSearch(this.v2, this.v3), false);
    t.checkExpect(this.world3.depthFirstSearch(this.v2, this.v1), false);
  }

  // to test method breadthFirstSearch
  void testBreadthFirstSearch(Tester t) {
    this.initTestConditionsThree();
    t.checkExpect(this.world3.breadthFirstSearch(this.v1, this.v2), true);
    t.checkExpect(this.world3.breadthFirstSearch(this.v2, this.v3), false);
    t.checkExpect(this.world3.breadthFirstSearch(this.v2, this.v1), false);
  }

  // to test method initWLK
  // checks that the initial knock workList is the same size as the workList
  void testInitWLK(Tester t) {
    t.checkExpect(this.world.workListKnock.size(), this.world.workList.size());
  }

  // to run the game
  public static void main(String[] argv) {

    // run the game
    //    MazeWorld w = new MazeWorld();
    //    w.bigBang((MazeWorld.MAZE_WIDTH) * MazeWorld.SCALE, 
    //        ((MazeWorld.MAZE_HEIGHT) * MazeWorld.SCALE), .001);
  }
}