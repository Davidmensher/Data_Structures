/**
 *davidmensher
 *212779920
 *David Mensher
 */

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	
	IAVLNode root;
/**	
	Two constructors: one that constructs tree with one node 
	and one that construct empty tree 
*/
	public AVLTree(IAVLNode root) {
		this.root=root;
	}
	public AVLTree() {
		root=new AVLNode();
		root.setHeight(-1);
	}
	
	/**
	 * public static void rotate(IAVLNode x, IAVLNode y)
	 * rotate function - make rotation on the x-->y edge. 
	 * x, y are AVLNodes, x is the parent of y 
	 * if y==x.getRight() -> rotate left
	 * if y==x.getLeft() -> rotate right
	 * complexity : O(1)
	 */
	public static void rotate(IAVLNode x, IAVLNode y) {
		if (y.isRealNode() && y.getParent()==x) {
			if (y == x.getLeft()) { //rotate to right
				x.setLeft(y.getRight());
				y.getRight().setParent(x);
				y.setRight(x);
			}

			if (y == x.getRight()) { ////rotate to left
				x.setRight(y.getLeft());
				y.getLeft().setParent(x);
				y.setLeft(x);
			}
			x.update(); // update size,max,min
			y.update();
			IAVLNode z = x.getParent();
			x.setParent(y);
			if (z != null) {
				y.setParent(z); //update child
				if (z.getLeft() == x) { //update parents - Children
					z.setLeft(y);
				} else {
					z.setRight(y);
				}
			}
			if (z == null) {
				y.setParent(null); //This took like 30 minutes to find aaaa
			}
		}

	}
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
  public boolean empty() {
    return !root.isRealNode(); //obvious
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   * complexity: O(log(n))
   */
  public String search(int k)
  {
	IAVLNode x= search(k, this.root); //helper function
	if(x == null) { //extreme case
		return null;
	}
	
	return x.getValue();
  }
	/**
	 * private IAVLNode search(int k,IAVLNode node)
	 * recursive function
	 * returns the item with key k if it exists under node
	 * otherwise, returns null
	 * complexity: O(log(n))
	 */
	private static IAVLNode search(int k,IAVLNode node)
	{
		if (node.isRealNode()) { // sanity check
			if(k==node.getKey()) { //Easy check
				return node;
			}
			else if (k<node.getKey()) { // if Node is less
				return search(k,node.getLeft());
			}
			return search(k,node.getRight()); //if node is bigger
		}
		return null;
	}


	/**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k already exists in the tree.
   * complexity time: O(log(n))
   */
   public int insert(int k, String i) {
	   IAVLNode x = this.root;
	   int currK;
	   
	   while(x.isRealNode()) { //reach the node where we insert the value
		   currK = x.getKey();
		   if(currK < k) {
			   x = x.getRight();
		   }
		   else if(currK > k) {
			   x = x.getLeft();
		   }
		   else {
			   return -1;
		   }
	   }
	  // set the nodes' value and initialize to new unreal nodes as his sons
	   x.setKey(k);
	   x.setValue(i);
	   IAVLNode y = new AVLNode(x);
	   IAVLNode z = new AVLNode(x);
	   x.setLeft(y);
	   x.setRight(z);
	   y.setHeight(-1);
	   z.setHeight(-1);
	   z.setParent(x);
	   y.setParent(x);
	   x.setHeight(0);
	   int ret;
	   x.update(); // update x's max, and size
	   if (x!=root) {
		   ret= insertRebalance(x.getParent()); //rebalncing steps
		   root= recupdate(x); // keep update the remaining nodes until the root
		   return ret;
	   }
	   else {
		   return 0;
	   }
	   
   }
   /**
    * public static int insertRebalance(IAVLNode y)
    * (helper function)
    * makes the rebalncing steps as we taught in class,
    *  (according to the three insertion cases + one extreme case that could occur at Join func)
    * from y until the problem solved/ until the root
    * complexity: O(log(n))
    * 
    */
   public static int insertRebalance(IAVLNode y) {
   	   IAVLNode x = y.getRight();
	   IAVLNode b = y.getLeft();
	   //problem solved case- 1,1\1,2\2,1 node, any rebalnce not needed
	   if (y.getHeight()-x.getHeight()==1 && y.getHeight()-b.getHeight()==1 || y.getHeight()-x.getHeight()==1 && y.getHeight()-b.getHeight()==2 || y.getHeight()-x.getHeight()==2 && y.getHeight()-b.getHeight()==1 ) {
	   	recupdate(y);
	   	return 0;
	   }
	   //0,1\1,0 node- case number one 
	   if((y.getHeight() - b.getHeight() == 0 && y.getHeight() - x.getHeight() == 1) || (y.getHeight() - x.getHeight() == 0 && y.getHeight() -b.getHeight() == 1)) {
		   y.setHeight(y.getHeight() + 1);
		   y.update();
		   if (y.getParent() != null) {
		   	return 1 + insertRebalance(y.getParent());
		   }
		   else {
		   	return 1;
		   }
	   }
	   //dealing with the two other cases + the special join rotate. assume its rotate to left- and fix it if its rotate to right case
	   IAVLNode xCent=x.getLeft();
	   IAVLNode xExt=x.getRight();
	   if(y.getHeight() - y.getLeft().getHeight() == 0 && y.getHeight() - y.getRight().getHeight() == 2) { //right rotations cases
	   	x = y.getLeft();
	   	b = y.getRight();
	   	xCent=x.getRight();
	   	xExt=x.getLeft();
	   }

	   if(x.getHeight() - xCent.getHeight() == 2 && x.getHeight() - xExt.getHeight() == 1) {//single rotate
		rotate(y, x);
		y.setHeight(y.getHeight() - 1);
		recupdate(y);
		return 2;
	}

		   if(x.getHeight() - xCent.getHeight() == 1 && x.getHeight() - xExt.getHeight() == 2) {//double rotate
		rotate(x, xCent);
		rotate(y, xCent);
		xCent.setHeight(xCent.getHeight() + 1);
		x.setHeight(x.getHeight() - 1);
		y.setHeight(y.getHeight() - 1);
		recupdate(y);
		return 5;
	}
		   if (x.getHeight() - xCent.getHeight() == 1 && x.getHeight() - xExt.getHeight() == 1) {// special join rotate
		rotate(y,x);
		x.setHeight(x.getHeight()+1);
		recupdate(y);
		return 2;
	}
	   recupdate(y); //shouldn't get here
	   return 0;
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k was not found in the tree.
   * complexity: O(log(n))
   */
   public int delete(int k)
   {
	   //search the wnated node
	   IAVLNode x = search(k, this.getRoot());
	   if(x == null) {
		   return -1;
	   }
	   int ret;
	   //case if the node isn't  leaf or unary node. 
	   //we will remove the node and replace him with his successor.
	   if(x.getLeft().isRealNode() && x.getRight().isRealNode()) {
		  //dealing with all pointers of the replace
		   IAVLNode y = AVLTree.successor(x);
		  IAVLNode yl = y.getLeft();
		  IAVLNode yr = y.getRight();
		  IAVLNode yp = y.getParent();
		  IAVLNode xl = x.getLeft();
		  IAVLNode xr = x.getRight();
		  IAVLNode xp = x.getParent();
		  
		  if(y == yp.getLeft()) {
			  yp.setLeft(yr);
			  yr.setParent(yp);
			  
		  }
		  else if(y == yp.getRight()) {
			  yp.setRight(y.getRight());
			  yr.setParent(yp);
		  }
		  
		  y.setParent(xp);
		  y.setHeight(x.getHeight());
		  y.setLeft(xl);
		  y.setRight(xr);
		  
		  
		  if(x == xp.getLeft()) {
			  xp.setLeft(y);
		  }
		  else if(x == xp.getRight()) {
			  xp.setRight(y);
		  }
		  
		  xl.setParent(y);
		  xr.setParent(y);

		  x.setLeft(null);
		  x.setRight(null);
		  x.setParent(null);
		  //updating
		  y.update();
		  yp.update();
		  //rebalncing steps
		  ret= deleteRebalance(yp);
		  root= recupdate(yp);
		  return ret;
	   }
	   else { //case if the node isn leaf or unary node.
		   IAVLNode xl = x.getLeft();
		   IAVLNode xr = x.getRight();
		   IAVLNode xp = x.getParent();
		   //dealing with the pointers
		   if(xp!= null && x == xp.getLeft()) {
			   if(x.getLeft().isRealNode()) {
				   xp.setLeft(xl);
				   xl.setParent(xp);
			   }
			   else{
				   xp.setLeft(xr);
				   xr.setParent(xp);
			   }  
		   }
		   
		   if(xp!=null && x == xp.getRight()) {
			   if(x.getLeft().isRealNode()) {
				   xp.setRight(xl);
				   xl.setParent(xp);
			   }
			   else{
				   xp.setRight(xr);
				   xr.setParent(xp);
			   }  
		   }
		   
		   x.setLeft(null);
		   x.setRight(null);
		   x.setParent(null);
		   ret=0;
		   //making the rebalance steps if needed
		   if (xp!=null && xp.isRealNode()) {
			   xp.update();
			   ret = deleteRebalance(xp);
			   root = recupdate(xp);
		   }
		   else {
		   	if (xr.isRealNode()) {
				root = xr;
				xr.setParent(null);
			}
		   	else {
		   		root=xl;
		   		xl.setParent(null);
			}
		   }
		   return ret;

		 
	   }
	   
	  
   }
   
   //public static int deleteRebalance(IAVLNode y)
   //doing the deletion rebalncing steps as we taught in class until not needed
   public static int deleteRebalance(IAVLNode y) {
   	if (y==null) {
   		return 0;
	}
	   
	   IAVLNode ylow = y.getLeft();
	   IAVLNode yhigh = y.getRight();
	   if((y.getHeight() - ylow.getHeight() == 2) && (y.getHeight() - yhigh.getHeight() == 1) || (y.getHeight() - ylow.getHeight() == 1) && (y.getHeight() - yhigh.getHeight() == 2) || (y.getHeight() - ylow.getHeight() == 1) && (y.getHeight() - yhigh.getHeight() == 1)) {//problem solved case
		   y.update();
		   return 0;
	   }
	   //2,2 case 
	   if((y.getHeight() - ylow.getHeight() == 2) && (y.getHeight() - yhigh.getHeight() == 2)) {
		   y.setHeight(y.getHeight() - 1);
		   y.update();
		   return 1 + deleteRebalance(y.getParent());
	   }
	   //other cases, assume that rotate are to left and fix it if needed
	   IAVLNode yExt = yhigh.getRight();
	   IAVLNode yCent = yhigh.getLeft();

	   // rotate right case
	   if((y.getHeight() - ylow.getHeight() == 1) && (y.getHeight() - yhigh.getHeight() == 3)) {
	   	yhigh=ylow;
	   	ylow=y.getRight();
	   	yCent = yhigh.getRight();
	   	yExt = yhigh.getLeft();

	   }
	   //case 2 at the presentation - single rotate
	   if((yhigh.getHeight() - yExt.getHeight() == 1) && (yhigh.getHeight() - yCent.getHeight() == 1)) {
		   rotate(y, yhigh);
		   y.setHeight(y.getHeight() - 1);
		   yhigh.setHeight(yhigh.getHeight() + 1);
		   y.update();
		   yhigh.update();
		   return 3 + deleteRebalance(yhigh.getParent());
	   }
	   //case 3 at the presentation - single rotate
	   if((yhigh.getHeight() - yExt.getHeight() == 1) && (yhigh.getHeight() - yCent.getHeight() == 2)) {
		   rotate(y, yhigh);
		   y.setHeight(y.getHeight() - 2);
		   y.update();
		   yhigh.update();
		   return 3 + deleteRebalance(yhigh.getParent());
	   }
	   //case 4 at the presentation - double rotate
	   if((yhigh.getHeight() - yExt.getHeight() == 2) && (yhigh.getHeight() - yCent.getHeight() == 1)) {
		   rotate(yhigh, yCent);
		   rotate(y, yCent);
		   yCent.setHeight(yCent.getHeight() + 1);
		   yhigh.setHeight(yhigh.getHeight() - 1);
		   y.setHeight(y.getHeight() - 2);
		   yhigh.update();
		   y.update();
		   yCent.update();
		   return 6 + deleteRebalance(yCent.getParent());
	   }
	   recupdate(y); //shouldn't get here?
	   return 0;
	   
	   
   }

 //public static IAVLNode recupdate (IAVLNode x)
   //update all nodes from x until root with the update function
   //see the description of update func at AVLNode class
   //complexity: O(log(n))
   public static IAVLNode recupdate (IAVLNode x) {
   	while (x.getParent()!=null) {
		x.update();
		x=x.getParent();
	}
	   x.update();
	   return x;
   }
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    * complexity: O(1)
    */
   public String min()
   {
	   if(root.getValue() == null) {
		   return null;
	   }
	   
	   return root.getMin().getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    * complexity: O(1)
    */

   public String max()
   {
	   if(root.getValue() == null) {
		   return null;
	   }
	   return root.getMax().getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   * uses loadToArray helper func.
   * complexity:O(n)
   */
  public int[] keysToArray()
  {
	  int[] arr = new int[size()];
	  loadToArray(root,arr,0);
	  return arr;
  }

	/**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   * uses loadToArray helper func.
   * complexity:O(n)
   */
  public String[] infoToArray()
  {
        String[] arr = new String[size()];
	    loadToArray(root,arr,0);
        return arr;
  }
	/**
	 * public String[] loadToArray()
	 * recursive function
	 * Loads all info under node into an array,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 * Starts from index start.
	 * Returns size of loaded array.
	 * complexity: O(n)
	 */
	private static int loadToArray(IAVLNode node,String[] arr, int start)
	{
		if(node.isRealNode()){
			int i=loadToArray(node.getLeft(),arr,start);
			arr[i]=node.getValue();
			return loadToArray(node.getRight(),arr,i+1);
		}
		return start;
	}
	/**
	 * public Int[] loadToArray()
	 * recursive function
	 * Loads all keys under node into an array,
	 * or an empty array if the tree is empty.
	 * Starts from index start.
	 * Returns size of loaded array.
	 * complexity: O(n)
	 */
	private static int loadToArray(IAVLNode node,int[] arr, int start)
	{
		if(node.isRealNode()){
			int i=loadToArray(node.getLeft(),arr,start);
			arr[i]=node.getKey();
			return loadToArray(node.getRight(),arr,i+1);
		}
		return start;
	}



	/**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    * complexity: O(1)
    */
   public int size()
   {
	   return root.getSize();
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    * complexity: O(1)
    */
   public IAVLNode getRoot()
   {
	   return root;
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    * complexity : O(log(n))
    */   
   public AVLTree[] split(int x)
   {
   	IAVLNode key=search(x,root); //find the node of key x
   	//initialize smaller and bigger
   	IAVLNode small=key.getLeft();
   	IAVLNode big=key.getRight();
   	small.setParent(null);
   	big.setParent(null);
   	key=key.getParent();
   	//moving up until the root, and making the needed Join to smaller and bigger according to the two cases
   	while (key!=null) {
   		if (x>key.getKey()) {
   			AVLTree tmp=new AVLTree(small);
			AVLTree tmp2=new AVLTree(key.getLeft());
			tmp2.getRoot().setParent(null);
			tmp.join(new AVLNode(key.getKey(),key.getValue()),tmp2);
   			small=tmp.root;
			small.setParent(null);

		}
   		else  if (x<key.getKey()) {
			AVLTree tmp=new AVLTree(big);
			AVLTree tmp2=new AVLTree(key.getRight());
			tmp2.getRoot().setParent(null);
			tmp.join(new AVLNode(key.getKey(),key.getValue()),tmp2);
			big=tmp.root;
			big.setParent(null);

		}
		key=key.getParent();
	}
   	return new AVLTree[]{new AVLTree(small),new AVLTree(big)};

   }

   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    * O(log(n))
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	//checks who has the biggest key ' and calls helper func joinnodes.
   	IAVLNode a=getRoot(),b=t.getRoot();
	if (a.getKey()<x.getKey()) {
		a=t.getRoot();
		b=getRoot();
	}
	int i=joinnodes(x,a,b);
	while (root.getParent()!=null) {
		root=root.getParent();
	   }
	return i;
   }
	/**
	 * private static int joinnodes(IAVLNode x,IAVLNode a, IAVLNode b)
	 *
	 * joins the nodes a,x,b where a>x>b
	 * complexity: O(log(n))
	 * */

	private int joinnodes(IAVLNode x,IAVLNode a, IAVLNode b) {
		int cplx=1;
		if (a.getHeight()>b.getHeight()) {
			while (a.getHeight()>b.getHeight()) {
				a=a.getLeft();
				cplx++;
			} //We have equal ranks now
			IAVLNode c=a.getParent();
			if (c!=null && c!=x) {
				c.setLeft(x);
				x.setParent(c);
			}
			x.setHeight(b.getHeight()+1);
		}
		else {
			while (b.getHeight()>a.getHeight()) {
				b=b.getRight();
				cplx++;
			} //We have equal ranks now
			IAVLNode c=b.getParent();
			if (c!=null && c!=x) {
				c.setRight(x);
				x.setParent(c);
			}
			x.setHeight(a.getHeight()+1);
		}
		x.setLeft(b);
		x.setRight(a);
		b.setParent(x);
		a.setParent(x);
		x.update();//updating x
		//rebalancing
		if (x.getParent()!=null) {
			insertRebalance(x.getParent());
			return cplx;
		}
		return cplx;
	}

	/**
	 * private static IAVLNode succesor()
	 *
	 * Returns the item with the smallest key which bigger than t,
	 * or null if the t has the largest key
	 * complexity: O(log(n))
	 * */

	private static IAVLNode successor(IAVLNode t) {
		if (t.getRight()!=null) {
			return t.getRight().getMin();
		}
		IAVLNode y=t.getParent();
		while (y!=null && t==y.getRight()) {
			t=y;
			y=t.getParent();
		}
		return y.getRight().getMin();
	}
	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public IAVLNode getMin(); //returns minimum node
		public IAVLNode getMax(); //returns max node
		public int getSize(); //returns size
		public void setKey(int k);
		public void setValue(String s);
		public void update();
	}

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{
  	private int key;
  	private String value;
  	private int height;
  	private IAVLNode min;
  	private int size;
  	private IAVLNode max;
  	private IAVLNode left;
  	private IAVLNode right;
  	private IAVLNode parent;
  	
  	public AVLNode(int key,String value) {
  		this.key=key;
  		this.value=value;
	}
	   public AVLNode() {
	   }
	
  	public AVLNode(IAVLNode parent) {
  		this.parent=parent;
	}
  	
	public int getKey()
		{
			return key;
		}
	// every node keeps max,min,size fields. when it's needed we call update and 
	//udating the fields.
	//complexity: O(1) 
	public void update() {
		if (isRealNode()) {
			if (left.isRealNode()) {
				min = left.getMin();
			} 
			else {
				min = this;
			}
			if (right.isRealNode()) {
				max = right.getMax();
			}
			else {
				max = this;
			}
			size = right.getSize() + left.getSize()+1;

		}
	}
		//returns the max field of the node
	   public IAVLNode getMax() {
		   return this.max;
	   }
	 //returns the min field of the node
	   public IAVLNode getMin() {
		   return this.min;
	   }
	 //returns the size field of the node
	   public int getSize() {
		   return size;
	   }
	   
	 //returns the value field of the node
	   public String getValue()
		{
			return value;
		}
		public void setLeft(IAVLNode node)
		{
			left=node;
		}
		public IAVLNode getLeft()
		{
			return left;
		}
		public void setRight(IAVLNode node)
		{
			right=node;
		}
		public IAVLNode getRight()
		{
			return right;
		}
		public void setParent(IAVLNode node)
		{
			parent=node;
		}
		public IAVLNode getParent()
		{
			return parent;
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			return height > -1;
		}
    public void setHeight(int height)
    {
    	this.height=height;
    }
    public int getHeight()
    {
      return height;
    }
    public void setKey(int k) {
    	this.key = k ;
    }
    public void setValue(String s) {
    	this.value = s ;
    }
  }

}
  

