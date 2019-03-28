/* Starter code for enumerating topological orders of a DAG
 * @author
 */

package ypp170130;

/**
 *     Team No: 39
 *     @author Pranita Hatte: prh170230
 *     @author Prit Thakkar: pvt170000
 *     @author Shivani Thakkar: sdt170030
 *     @author Yash Pradhan: ypp170130
 *
 *     Long Project LP4: PERT, Enumeration of topological orders
 *
 *     Implementing enumeration of all topological orders of a given directed graph.
 */

import rbk.Graph;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Timer;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.Factory;

import java.util.List;
import java.util.Scanner;

public class EnumerateTopological extends GraphAlgorithm<EnumerateTopological.EnumVertex> {
    boolean print;  // Set to true to print array in visit
    long count;      // Number of permutations or combinations visited
    Selector sel;

    public EnumerateTopological(Graph g) {
        super(g, new EnumVertex());
        print = false;
        count = 0;
        sel = new Selector();
    }

    static class EnumVertex implements Factory {
        private int inDegree; //Used by selector
        EnumVertex() { }
        public EnumVertex make(Vertex u) { return new EnumVertex();	}
    }


    class Selector extends Enumerate.Approver<Vertex> {

        /**
         * Selects u if indegree is zero
         * Also reduces indegree of vertex having incident edges from u by 1
         * @param u
         * @return
         */
        @Override
        public boolean select(Vertex u) {
            if(get(u).inDegree == 0){
                for(Edge e: g.incident(u)){
                    get(e.otherEnd(u)).inDegree--;
                }
                return true;
            }
            return false;
        }

        /**
         * unselects u, restores indegree
         * Increases indegree of vertex having incident edges from u by 1
         * @param u
         */
        @Override
        public void unselect(Vertex u) {
            for(Edge e: g.incident(u)){
                get(e.otherEnd(u)).inDegree++;
            }
        }

        /**
         * To visit a permutation
         * @param arr
         * @param k
         */
        @Override
        public void visit(Vertex[] arr, int k) {
            count++;
            if(print) {
                for(Vertex u: arr) {
                    System.out.print(u + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Computes the number of topological orders of g
     * @param flag used by visit to print permutations
     * @return number of topological orderings of g
     */
    public long enumerateTopological(boolean flag) {
        print = flag;

        initialize();
        Vertex[] arr = g.getVertexArray();
        Enumerate e = new Enumerate(arr, sel);

        e.permute(g.size());

        return count;
    }

    /**
     * Initializes indegree field of EnumVertex from original graph
     *
     */
    private void initialize(){
        for(Vertex u: g){
            get(u).inDegree = u.inDegree();
        }
    }

    //-------------------static methods----------------------

    public static long countTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(false);
    }

    public static long enumerateTopologicalOrders(Graph g) {
        EnumerateTopological et = new EnumerateTopological(g);
        return et.enumerateTopological(true);
    }

    public static void main(String[] args) {
        int VERBOSE = 0;
        if(args.length > 0) { VERBOSE = Integer.parseInt(args[0]); }
        Graph g = Graph.readDirectedGraph(new java.util.Scanner(System.in));
        Graph.Timer t = new Graph.Timer();
        long result;
        if(VERBOSE > 0) {
            result = enumerateTopologicalOrders(g);
        } else {
            result = countTopologicalOrders(g);
        }
        System.out.println("\n" + result + "\n" + t.end());
    }
}
