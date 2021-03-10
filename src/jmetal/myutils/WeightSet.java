package jmetal.myutils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jmetal.util.Configuration;

public class WeightSet implements Serializable {

	  /**
	   * Stores a list of <code>Weights</code> objects.
	   */
	  protected final List<Weights> weightsList_;

	  /** 
	   * Maximum size of the solution set 
	   */
	  private int capacity_ = Integer.MAX_VALUE;
	  
	  /**
	   * Constructor.
	   * Creates an unbounded solution set.
	   */
	  public WeightSet() {
		  weightsList_ = new ArrayList<Weights>();
	  } // 

	  /** 
	   * Creates a empty solutionSet with a maximum capacity.
	   * @param maximumSize Maximum size.
	   */
	  public WeightSet(int maximumSize){    
		weightsList_ = new ArrayList<Weights>();
	    capacity_      = maximumSize;
	  } // 


	  public boolean add(Weights weight) {
	    if (weightsList_.size() == capacity_) {
		      Configuration.logger_.severe("The population is full");
		      Configuration.logger_.severe("Capacity is : "+capacity_);
		      Configuration.logger_.severe("\t Size is: "+ this.size());
		      return false;
	    } // if

	    weightsList_.add(weight);
	    return true;
	  } // add
	  
	  public boolean add(int index, Weights weight) {
		  	weightsList_.add(index, weight) ;
		    return true ;
	  }
	  
	  public Weights get(int i) {
		    if (i >= weightsList_.size()) {
		      throw new IndexOutOfBoundsException("Index out of Bound "+i);
		    }
		    return weightsList_.get(i);
	  } // get
	  
	  public int getMaxSize(){
		    return capacity_ ;
	  } // getMaxSize
	  
	  public int size(){
		    return weightsList_.size();
	  } // size
	  
	  /** 
	   * Empties the WeightSet
	   */
	  public void clear(){
		  weightsList_.clear();
	  } // clear
	  
	  public void remove(int i){        
		    if (i > weightsList_.size()-1) {            
		      Configuration.logger_.severe("Size is: "+this.size());
		    } // if
		    weightsList_.remove(i);    
	  } // remove
	  
	  public Iterator<Weights> iterator(){
		    return weightsList_.iterator();
	  } // iterator   
	  
	  public WeightSet union(WeightSet weightSet) {       
		    //Check the correct size. In development
		    int newSize = this.size() + weightSet.size();
		    if (newSize < capacity_)
		      newSize = capacity_;

		    //Create a new population 
		    WeightSet union = new WeightSet(newSize);                
		    for (int i = 0; i < this.size(); i++) {      
		      union.add(this.get(i));
		    } // for

		    for (int i = this.size(); i < (this.size() + weightSet.size()); i++) {
		      union.add(weightSet.get(i-this.size()));
		    } // for

		    return union;        
	} // union        
	  
	  public void replace(int position, Weights weights) {
		    if (position > this.weightsList_.size()) {
		    	weightsList_.add(weights);
		    } // if 
		    weightsList_.remove(position);
		    weightsList_.add(position,weights);
	  } // replace
} // Class WeightSet
