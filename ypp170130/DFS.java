package ypp170130;

/**
 *     DFS from Short Project 10
 *     Team No: 39
 *     @author Pranita Hatte: prh170230
 *     @author Prit Thakkar: pvt170000
 *     @author Shivani Thakkar: sdt170030
 *     @author Yash Pradhan: ypp170130
 */

import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;
import rbk.Graph;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {

    private static LinkedList<Vertex> finishList;
    private static boolean isCyclic;
    private static int topnum;
    private static int numConnectedComponents;

    public static class DFSVertex implements Factory {
        int cno;
        boolean seen;
        Vertex parent;
        boolean isInRecursionStack; //used for detecting cycles
        int top; //stores the topological order number
        int inDegree; // used by topologilcalOrder2 to compute the ordering

        public DFSVertex(Vertex u) {
            seen = false;
            parent = null;
            isInRecursionStack = false;
            inDegree = 0;
        }
        public DFSVertex make(Vertex u) { return new DFSVertex(u); }
    }

    public DFS(Graph g) {
        super(g, new DFSVertex(null));
    }

    //getter method to obtain number of connected components
    public int getNumConnectedComponents(){
        return numConnectedComponents;
    }

    /**
     * helper method used by dfs to visit all reachable nodes from vertex u
     * @param u
     */
    private void dfsVisit(Vertex u){
        get(u).seen = true;
        get(u).isInRecursionStack = true;
        get(u).cno = numConnectedComponents;

        for(Edge e: g.incident(u)){
            Vertex v = e.otherEnd(u);

            if(!isCyclic){
                if(get(v).isInRecursionStack){
                    isCyclic = true;
                }
            }

            if(!get(v).seen){
                get(v).parent = u;
                dfsVisit(v);
            }
        }

        get(u).isInRecursionStack = false;
        get(u).top = topnum--;
        finishList.addFirst(u);
    }

    /**
     * helper method for initialization
     */
    private void initialize(){
        finishList = new LinkedList<>();
        isCyclic = false;
        topnum = g.size();
        numConnectedComponents = 0;

        for(Vertex u: g){
            get(u).seen = false;
            get(u).parent = null;
            get(u).isInRecursionStack = false;
        }
    }

    /**
     * helper dfs(), invoked by other member functions
     * would call dfs(iterable) by passing g as parameter
     */
    private void dfs(){
        dfs(g);
    }

    /**
     * helper dfs(iterable), invoked by other functions
     * an iterable is provided as argument
     */
    private void dfs(Iterable<Vertex> iterable){

        initialize();

        for(Vertex u: iterable){
            if(!get(u).seen){
                ++numConnectedComponents;
                dfsVisit(u);
            }
        }
    }

    public static DFS depthFirstSearch(Graph g) {
        DFS d = new DFS(g);
        d.dfs();
        return d;
    }

    // Member function to find topological

    /**
     * Checks that graph is DAG, returns topological ordering of vertices, null otherwise
     * @return topological ordering of vertices if g is DAG, null otherwise
     */
    public List<Vertex> topologicalOrder1() {

        if(!g.isDirected()) {
            return null;
        }

        this.dfs();

        if(this.isCyclic) {
            return null;
        }

        return this.finishList;
    }

    // After running the topological ordering algorithm, the topological order no of each vertex can be queried
    public int topnum(Vertex u) {
        return get(u).top;
    }

    // Find the number of connected components of the graph g by running dfs.
    // Enter the component number of each vertex u in u.cno.
    // Note that the graph g is available as a class field via GraphAlgorithm.
    public int connectedComponents() {
        if(g.isDirected()){
            return -1;
        }
        dfs();
        return numConnectedComponents;
    }

    // After running the connected components algorithm, the component no of each vertex can be queried.
    public int cno(Vertex u) {
        return get(u).cno;
    }

    // Find topological order of a DAG using DFS. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder1(Graph g) {
        DFS d = new DFS(g);
        return d.topologicalOrder1();
    }

    private List<Vertex> topologicalOrder2() {

        if(!g.isDirected()) {
            return null;
        }

        PriorityQueue<Vertex> readyQueue = new PriorityQueue<Vertex>(g.size(), (x, y) -> get(x).inDegree - get(y).inDegree);
        LinkedList<Vertex> topologicalOrderList = new LinkedList<>();

        for(Vertex u: g){
            get(u).inDegree = u.inDegree();
            // if indegree is 0, it means the task can be processed right away
            if(get(u).inDegree == 0){
                readyQueue.add(u);
            }
        }

        topnum = 0;

        while(readyQueue.size() > 0){
            Vertex u = readyQueue.remove();
            get(u).top = ++topnum;
            topologicalOrderList.add(u);

            // decrease the inDegree of neighbouring vertices, as this task has been processed
            // not actually updating inDegree of Vertex of graph, just of DFSVertex

            for(Edge e: g.incident(u)){
                Vertex v = e.otherEnd(u);
                get(v).inDegree--;

                if(get(v).inDegree == 0){
                    readyQueue.add(v);
                }
            }
        }

        if(topologicalOrderList.size() == g.size()) {
            return topologicalOrderList;
        }

        return null;
    }

    // Find topological oder of a DAG using the second algorithm. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder2(Graph g) {
        DFS dfs = new DFS(g);
        return dfs.topologicalOrder2();
    }


    /**
     * Algorithm for finding the Strongly Connected Components
     * @param g Directed Graph to find Strongly Connected Components
     * @return an instance of DFS
     * The number of Strongly Connected Components can be obtained
     * using getNumConnectedComponents() method
     * and component number of each vertex can be obtained by cno(u) method
     */
    public static DFS stronglyConnectedComponents(Graph g) {

        //Strongly Connected Components is defined for Directed Graphs
        if(!g.isDirected()) {
            return null;
        }

        DFS d = new DFS(g);

        //performing first dfs and obtaining the finishlist
        //the finish list is decreasing order of finish times
        d.dfs();
        List<Vertex> list = d.finishList;

        //reversing the graph
        g.reverseGraph();

        //performing second dfs
        d.dfs(list);

        //restoring graph to original state
        g.reverseGraph();

        return d;
    }

    public static void main(String[] args) throws Exception {

        String string = "11 17   1 11 1   2 3 1   2 7 1   3 10 1   4 1 1   4 9 1   5 4 1   5 7 1   5 8 1   6 3 1   7 8 1   8 2 1   9 11 1   10 6 1   11 3 1   11 4 1   11 6 1 0";

        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);

        // Read graph from input
        Graph g = Graph.readDirectedGraph(in);
        g.printGraph(false);

        DFS d = stronglyConnectedComponents(g);

        System.out.println("\nNumber of Strongly Connected Components: " + d.getNumConnectedComponents() + "\nu\tcno");
        for (Vertex u : g) {
            System.out.println(u + "\t" + d.cno(u));
        }
    }
}