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
 *     Implementing enumeration of permutations.
 */

import java.util.Comparator;

public class Enumerate<T> {
    T[] arr;
    int k;
    int count;
    Approver<T> app;

    //-----------------Constructors-------------------

    public Enumerate(T[] arr, int k, Approver<T> app) {
        this.arr = arr;
        this.k = k;
        this.count = 0;
        this.app = app;
    }

    public Enumerate(T[] arr, Approver<T> app) {
        this(arr, arr.length, app);
    }

    public Enumerate(T[] arr, int k) {
        this(arr, k, new Approver<T>());
    }

    public Enumerate(T[] arr) {
        this(arr, arr.length, new Approver<T>());
    }

    //-------------Methods of Enumerate class: To do-----------------

    /**
     * n = arr.length, choose k things, d elements arr[0..d-1] done
     * c more elements are needed from arr[d..n-1].  d = k-c.
     * @param c
     */
    public void permute(int c) {
        int d;

        if(c == 0){
            visit(arr);
        }
        else{
            d = k-c;
            for(int i = d; i<arr.length;i++){
                // before performing swap and permute(c-1)
                // obtains permission from approver
                if(app.select(arr[i])) {
                    swap(d, i);
                    permute(c - 1);             // Permutations having A[ i ] as the next element
                    swap(d, i);
                    app.unselect(arr[i]);
                }
            }
        }
    }

    /**
     * SP11 Optional
     * choose c more items from A[i..n-1]
     * chosen items arr[0 ... k-c-1]
     * @param i
     * @param c
     */
    public void combine(int i, int c) {
        int d;
        if(c==0){
            visit(arr);
        }
        else{
            d = k - c;

            //choose arr[i]
            swap(d, i);
            combine(i+1, c-1);
            swap(d, i);     //restore

            //skip arr[i] only if enough elements left
            if(arr.length - i > c){
                combine(i+1, c);
            }
        }
    }

    /**
     * SP 11 Optional
     * A faster algorithm for generating all n! permutations,
     * using just one swap for generating each permutation from the previous one.
     *
     * g elements to go i.e. arr[0 ... g-1]
     * arr[g ... n-1] are done(frozen)
     * @param g
     */
    public void heap(int g) {
        if(g==1){
            visit(arr);
        }
        else{
            for(int i = 0; i<=g-2; i++){
                heap(g-1);
                if(g%2==0){
                    swap(i, g-1);
                }
                else{
                    swap(0, g-1);
                }
            }
            heap(g-1);
        }
    }

    /**
     * SP 11 Optional
     * This algorithm generates permutations in lexicographic order,
     * and is useful when the input elements are not distinct.
     *
     * Input Sorted Array: A[0] <= A[1] <= ... <= A[n-1]
     * @param c comparator
     *
     * UPDATE: Removed isDescending; j can detect same
     */
    public void algorithmL(Comparator<T> c) {
        int j, k;
        visit(arr);

        j = findJ(c);
        while(j!=-1){
            k = findK(c, j);
            swap(j, k);
            reverse(j+1, arr.length-1);     // now A[j+1 ... n-1] is in ascending order
            visit(arr);
            j = findJ(c);
        }
    }

    /**
     * helper method
     * @param c
     * @return true is array arr is descending, false otherwise
     *
    private boolean isDescending(Comparator<T> c){
        for(int i = 0; i<arr.length-1; i++){
            if(c.compare(arr[i], arr[i+1]) < 0){
                return false;
            }
        }
        return true;
    }
    */

    /**
     * Finds max index j such that A[ j ] < A[j + 1]
     * @param c
     * @return max index j
     */
    private int findJ(Comparator<T> c){
        int j = arr.length - 2;

        while (j>=0 && c.compare(arr[j], arr[j+1])>=0){
            j--;
        }

        return j;
    }

    /**
     * Finds max index k such that A[ j ] < A[ k ]
     * @param c
     * @param j
     * @return max index k
     */
    private int findK(Comparator<T> c, int j){
        int k = arr.length - 1;

        while (k>j && c.compare(arr[j], arr[k])>=0){
            k--;
        }

        return k;
    }

    public void visit(T[] array) {
        count++;
        app.visit(array, k);
    }

    //----------------------Nested class: Approver-----------------------


    // Class to decide whether to extend a permutation with a selected item
    // Extend this class in algorithms that need to enumerate permutations with precedence constraints
    public static class Approver<T> {
        /* Extend permutation by item? */
        public boolean select(T item) { return true; }

        /* Backtrack selected item */
        public void unselect(T item) { }

        /* Visit a permutation or combination */
        public void visit(T[] array, int k) {
            for (int i = 0; i < k; i++) {
                System.out.print(array[i] + " ");
            }
            System.out.println();
        }
    }

    //-----------------------Utilities-----------------------------

    void swap(int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    void reverse(int low, int high) {
        while(low < high) {
            swap(low, high);
            low++;
            high--;
        }
    }

    //--------------------Static methods----------------------------

    // Enumerate permutations of k items out of n = arr.length
    public static<T> Enumerate<T> permute(T[] arr, int k) {
        Enumerate<T> e = new Enumerate<>(arr, k);
        e.permute(k);
        return e;
    }

    // Enumerate combinations of k items out of n = arr.length
    public static<T> Enumerate<T> combine(T[] arr, int k) {
        Enumerate<T> e = new Enumerate<>(arr, k);
        e.combine(0, k);
        return e;
    }

    // Enumerate permutations of n = arr.length item, using Heap's algorithm
    public static<T> Enumerate<T> heap(T[] arr) {
        Enumerate<T> e = new Enumerate<>(arr, arr.length);
        e.heap(arr.length);
        return e;
    }

    // Enumerate permutations of items in array, using Knuth's algorithm L
    public static<T> Enumerate<T> algorithmL(T[] arr, Comparator<T> c) {
        Enumerate<T> e = new Enumerate<>(arr, arr.length);
        e.algorithmL(c);
        return e;
    }

    public static void main(String args[]) {
        int n = 4;
        int k = 4;

        if(args.length > 0) { n = Integer.parseInt(args[0]);  k = n; }
        if(args.length > 1) { k = Integer.parseInt(args[1]); }

        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i+1;
        }

        Enumerate<Integer> e;

        System.out.println("Permutations: " + n + " " + k);
        e = permute(arr, k);
        System.out.println("Count: " + e.count + "\n_________________________");

    }
}
