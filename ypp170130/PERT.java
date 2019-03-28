/* Driver code for PERT algorithm (LP4)
 * @author
 */

// change package to your netid
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
 *     PERT: Project Evaluation and Review Technique
 */

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;

import java.util.*;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {

    private int criticalVertexCount;  //stores count

    public static class PERTVertex implements Factory {

        // ec : earliest completion time
        // lc: latest completion time
        // slack: max delay that can be added to this vertex, without delaying project duration
        // duration: time required to complete this task
        private int ec, lc, slack, duration;

        // critical: true if this task has slack 0, i.e. cannot be delayed
        private boolean critical;

        public PERTVertex(Vertex u) {
            this.duration = 0;
            this.ec = 0;
            this.lc = Integer.MAX_VALUE;
            this.slack = 0;
            this.critical = false;
        }
        public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    public PERT(Graph g) {
        super(g, new PERTVertex(null));
        initializePertGraph(g);
    }

    /**
     * Adds edges (1,u) and (u,n) for u in 2..n-1
     * @param g
     */
    private void initializePertGraph(Graph g) {
        Vertex s = g.getVertex(1);
        Vertex t = g.getVertex(g.size());
        int m = g.edgeSize();
        for(int i= 2; i<g.size(); i++) {
            g.addEdge(s, g.getVertex(i), 1, ++m);
            g.addEdge(g.getVertex(i), t, 1, ++m);
        }
    }

    /**
     * sets duration of PERTVertex u to d.
     * @param u
     * @param d
     */
    public void setDuration(Vertex u, int d) {
        get(u).duration = d;
    }

    /**
     * non-static method called after calling the constructor
     * @return false if graph is not a DAG, true otherwise
     */
    public boolean pert() {

        LinkedList<Vertex> finishList = (LinkedList<Vertex>) DFS.topologicalOrder1(g);
        // finishList contains vertices in topological order

        if(finishList != null){

            //init ec to 0
            initializeEC();

            PERTVertex u;
            PERTVertex v;
            Vertex v1;

            //LI: u.ec has been calculated
            for(Vertex u1: finishList){
                u = get(u1);
                for(Edge e: g.incident(u1)){
                    v1 = e.otherEnd(u1);
                    v = get(v1);

                    if(u.ec + v.duration > v.ec){
                        v.ec = u.ec + v.duration;
                    }
                }
            }

            Iterator di = finishList.descendingIterator();

            // init lc of all vertex to masxTime: t.ec
            initializeLC(get(g.getVertex(g.size())).ec);

            if(di.hasNext()){
                u = get((Vertex) di.next());
                u.slack = 0;
                u.critical = true;
                criticalVertexCount++;
            }

            //LI: Successor of u are done
            while(di.hasNext()){
                Vertex u1 = (Vertex) di.next();
                u = get(u1);
                for(Edge e: g.incident(u1)){
                    v = get(e.otherEnd(u1));

                    if(v.lc - v.duration < u.lc){
                        u.lc = v.lc - v.duration;
                    }
                }
                u.slack = u.lc - u.ec;
                if(u.slack == 0){
                    u.critical = true;
                    criticalVertexCount++;
                }
            }
            return true;
        }

        //not a DAG
        return false;
    }

    /**
     * helper method
     */
    private void initializeEC() {
        for(Vertex u: g){
            get(u).ec = 0;
        }
    }

    /**
     * helper method
     * @param maxTime
     */
    private void initializeLC(int maxTime){
        for(Vertex u : g){
            get(u).lc = maxTime;
        }
    }

    /**
     *
     * @param u
     * @return earliest completion time of u
     */
    public int ec(Vertex u) {
        return get(u).ec;
    }

    /**
     *
     * @param u
     * @return latest completion time of u
     */
    public int lc(Vertex u) {
        return get(u).lc;
    }

    /**
     *
     * @param u
     * @return slack of u
     */
    public int slack(Vertex u) {
        return get(u).slack;
    }

    /**
     * Minimum duration to complete the project
     * @return length of critical path
     */
    public int criticalPath() {
        return get(g.getVertex(g.size())).lc;
    }

    /**
     *
     * @param u
     * @return true if vertex u on a critical path, false otherwise
     */
    public boolean  critical(Vertex u) {
        return get(u).critical;
    }

    /**
     *
     * @return Number of critical nodes in graph
     */
    public int numCritical() {
        return criticalVertexCount;
    }

    /**
     * static method
     * @param g
     * @param duration
     * @return null if g is not a DAG, instance of PERT otherwise
     */
    public static PERT pert(Graph g, int[] duration) {
        PERT p = new PERT(g);
        int i = 0;
        for(Vertex u: g) {
            p.setDuration(u, duration[i++]);
        }

        if(p.pert()){
            return p;
        }
        return null;
    }
}