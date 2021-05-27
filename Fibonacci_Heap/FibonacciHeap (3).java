import java.util.ArrayList;
import java.util.Collections;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 * דוד מנשר
 * 212779920
 * davidmensher
 * עמית סנדלר
 * amitsandler
 * 
 */
public class FibonacciHeap
{
    public static int linkCounter = 0;
    public static int cutCounter = 0;
    public static int maxcut = 0;
    public HeapNode minNode;
    private int Marked;
    private int trees;
    public HeapNode firstNode;
    public int sizeField;

    public FibonacciHeap(){
        this.sizeField = 0;
    }

    private HeapNode getFirstNode(){
        return this.firstNode;
    }

    private HeapNode getLastNode(){
        return this.firstNode.getPrev();
    }

    private void setMinNode(HeapNode x){
        this.minNode = x;
    }

    private void setFirstNode(HeapNode firstNode) {
        this.firstNode = firstNode;
    }


    private void setSize(int s){
        this.sizeField = s;
    }

    /**
     * public boolean isEmpty()
     *
     * precondition: none
     *
     * The method returns true if and only if the heap
     * is empty.
     *
     */
    public boolean isEmpty()
    {
        if (this.firstNode == null){
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     *
     * Returns the new node created.
     */
    public HeapNode insert(int key)
    {
        HeapNode inNode = new HeapNode(key);

        if (this.isEmpty()){
            this.setMinNode(inNode);
            this.setFirstNode(inNode);
            firstNode.setPrev(inNode);
            firstNode.setNext(inNode);
            trees++;
        }
        else{
            insertAfter(inNode);
            this.setFirstNode(inNode);
        }
        this.setSize(this.size() + 1);
        return inNode;


    }

    public FibonacciHeap consolidate(){

        HeapNode[] arr = this.toBuckets();
        return fromBuckets(arr);

    }

    private HeapNode[] toBuckets(){

        double dSize = Math.log(this.size())/Math.log(1.62);
        int n = (int) Math.floor(dSize) + 1;

        HeapNode[] listOfNodes = new HeapNode[n];

        HeapNode x = this.getFirstNode();
        HeapNode y;
        HeapNode last=this.getLastNode();

        x.getPrev().setNext(null);
        x.setPrev(null);

        while(x != null){
            y = x;
            x = x.getNext();

            if(y != last){
                y.getNext().setPrev(null);
                y.setNext(null);
            }

            while (listOfNodes[y.getRank()] != null){
                trees-=1;
                y = link(y, listOfNodes[y.getRank()]);
                listOfNodes[y.getRank()-1] = null;
            }

            listOfNodes[y.getRank()] = y;
        }

        return listOfNodes;

    }

    private FibonacciHeap fromBuckets(HeapNode[] B){
        int n = B.length;
        FibonacciHeap x = new FibonacciHeap();
        HeapNode curr;

        for(int i = 0; i<n ; i++){
            if(B[i] != null){
                curr = B[i];
                if(x.getFirstNode() == null){
                    x.setMinNode(curr);
                    x.setFirstNode(curr);

                    curr.setPrev(curr);
                    curr.setNext(curr);

                }
                else{
                    x.insertAfter(curr);
                }
            }
        }

        return x;
    }

    private void insertAfter(HeapNode newNode){
        HeapNode last = this.getLastNode();
        HeapNode first = this.getFirstNode();

        last.setNext(newNode);
        newNode.setPrev(last);

        first.setPrev(newNode);
        newNode.setNext(first);
        if (newNode.getKey()<minNode.getKey()) {
            minNode=newNode;
        }


        trees+=1;
    }

    /**
     * public void deleteMin()
     *
     * Delete the node containing the minimum key.
     *
     */
    public void deleteMin()
    {
        if (this.isEmpty()){
            return;
        }
        this.setSize(this.size() - 1);
        trees-=1;
        HeapNode min = this.findMin();

        if(min.getChild() == null){
            if(this.getFirstNode() == min){
                if (firstNode.getNext()==min) {
                    this.setFirstNode(null);
                    this.setMinNode(null);
                    return;
                }
                this.setFirstNode(min.getNext());
            }

            min.getPrev().setNext(min.getNext());
            min.getNext().setPrev(min.getPrev());
        }
        else {
            HeapNode minFirstChild = min.getChild();
            HeapNode minLastChild = minFirstChild.getPrev();

            if (this.getFirstNode() == min) {
                this.setFirstNode(minFirstChild);
            }


            //making all min childrens parent field null
            HeapNode x = minFirstChild;

            for (int i = 1; i <= min.getRank(); i++) {
                x.setParent(null);
                x = x.getNext();
            }

            //put the childrens of min into the forest and remove min
            min.getPrev().setNext(minFirstChild);
            minFirstChild.setPrev(min.getPrev());

            min.getNext().setPrev(minLastChild);
            minLastChild.setNext(min.getNext());
            trees += min.getRank();
        }

        FibonacciHeap tmp = this.consolidate();

        this.setMinNode(tmp.findMin());
        this.setFirstNode(tmp.getFirstNode());

    }

    public static HeapNode link(HeapNode Node1, HeapNode Node2){
        linkCounter+=1;

        HeapNode returnedNode, linkedNode;

        if(Node1.getKey() < Node2.getKey()){
            returnedNode = Node1;
            linkedNode =Node2;
        }
        else{
            returnedNode = Node2;
            linkedNode = Node1;
        }

        if(returnedNode.getRank() == 0){
            returnedNode.setChild(linkedNode);
            linkedNode.setPrev(linkedNode);
        }

        //dealing with linkednode pointers
        HeapNode lastChild = returnedNode.getChild().getPrev();
        linkedNode.setNext(returnedNode.getChild());
        linkedNode.setPrev(lastChild);
        linkedNode.setParent(returnedNode);


        //dealing with the previous this.child and his last child pointers
        returnedNode.getChild().setPrev(linkedNode);
        lastChild.setNext(linkedNode);

        returnedNode.setChild(linkedNode);
        returnedNode.setRank(returnedNode.getRank() + 1);
        return returnedNode;
    }

    /**
     * public HeapNode findMin()
     *
     * Return the node of the heap whose key is minimal.
     *
     */
    public HeapNode findMin()
    {
        if (this.isEmpty()){
            return null;
        }
        else{
            return this.minNode;
        }
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Meld the heap with heap2
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        if(heap2 == null || heap2.isEmpty() ){
            return;
        }

        HeapNode heap1First = this.getFirstNode();
        HeapNode heap1Last = this.getLastNode();

        HeapNode heap2First = heap2.getFirstNode();
        HeapNode heap2Last = heap2.getLastNode();

        heap1First.prev = heap2Last;
        heap2Last.next = heap1First;

        heap1Last.next = heap2First;
        heap2First.prev = heap1Last;

        this.setSize(this.size() + heap2.size());

        if(this.findMin().getKey() > heap2.findMin().getKey()) {
            this.setMinNode(heap2.findMin());
        }


        trees+=heap2.trees;
        Marked+=heap2.Marked;
    }

    /**
     * public int size()
     *
     * Return the number of elements in the heap
     *
     */
    public int size()
    {
        return this.sizeField;
    }

    /**
     * public int[] countersRep()
     *
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     *
     */
    public int[] countersRep()
    {

        double dSize = Math.log(this.size())/Math.log(1.62);
        int n = (int) Math.floor(dSize) + 1;

        int[] listOfNodes = new int[n];

        HeapNode x = this.getFirstNode();
        HeapNode y=x;
        do {
            listOfNodes[x.getRank()]+=1;
            x = x.getNext();
        } while(x != y);

        return listOfNodes;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     *
     */
    public void delete(HeapNode x)
    {
        decreaseKey(x,2000000000);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.setKey(x.getKey()-delta);
        if(x.getKey()<findMin().getKey()) {
            minNode=x;
        }
        if (x.getParent()!=null && x.getKey()<x.getParent().getKey())
        {
            casccut(x,x.getParent());
        }
    }
    /**
     * private void casccut(HeapNode x,HeapNode y)
     *
     * The function  cuts X from Y, X needs to be son of Y
     */
    private void casccut(HeapNode x,HeapNode y) {
        int prevcut=cutCounter;
        cut(x,y);
        if (y.getParent()!=null) {
            if (y.isMarked()) {
                casccut(y,y.getParent());
            }
            else {
                y.setMark(true);
                Marked+=1;
            }
        }
        if (cutCounter-prevcut>maxcut) {
            maxcut=cutCounter-prevcut;
        }
    }
    /**
     * private static void cut(HeapNode x,HeapNode y)
     *
     * The function  cuts X from Y, X needs to be son of Y
     */
    private void cut(HeapNode x,HeapNode y) {
        cutCounter+=1;
        if(x.isMarked()) {
            Marked-=1;
        }
        x.setParent(null);
        x.setMark(false);
        y.setRank(y.getRank()-1);
        if(x.getNext()==x) {
            y.setChild(null);
        }
        else {
            y.setChild(x.getNext());
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }
        insertAfter(x);
        this.setFirstNode(x);

    }
    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        return trees+2*Marked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the run-time of the program.
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * in its root.
     */
    public static int totalLinks()
    {
        return linkCounter;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return cutCounter;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k minimal elements in a binomial tree H.
     * The function should run in O(k*deg(H)).
     * You are not allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        HeapNode tmp,tmp1;
        if(k==0) {
            return null;
        }
        int[] arr = new int[k];
        FibonacciHeap x=new FibonacciHeap();

        tmp=H.getFirstNode();
        tmp1=tmp;
        for (int i=0;i<k;i++) {
            if(tmp1!=null) {
                do {
                    x.insert(tmp1.getKey()).setkBack(tmp1);
                    tmp1 = tmp1.getNext();
                } while (tmp1 != tmp);
            }
            arr[i]=x.findMin().getKey();
            tmp=x.findMin().getkBack().getChild();
            x.deleteMin();
            tmp1=tmp;
        }
        return arr;
    }

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in
     * another file
     *
     */
    public static class HeapNode{

        public int key;
        public int rank;
        public boolean mark;
        public  HeapNode child;
        public  HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public HeapNode kBack;

        public HeapNode(int key)
        {
            this.key = key;
            this.rank = 0;
            this.mark = false;
        }

        public int getRank() {
            return rank;
        }

        public HeapNode getChild() {
            return child;
        }

        public HeapNode getkBack() {
            return kBack;
        }

        public void setkBack(HeapNode kBack) {
            this.kBack = kBack;
        }

        public boolean isMarked() {
            return mark;
        }

        public HeapNode getNext() {
            return next;
        }

        public HeapNode getParent() {
            return parent;
        }

        public HeapNode getPrev() {
            return prev;
        }

        public int getKey(){
            return this.key;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

    }

}
