//  MW8.java
/**
 * MW problems
 */
package jmetal.problems.MW;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing MW8 problem  
 */
public class MW8 extends Problem {   
 /** 
  * Creates a default MW8 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW8(String solutionType) throws ClassNotFoundException {
    this(solutionType, 10, 3);//3 objectives and 10 decision variables
  } // MW8   
    
  /** 
  * Creates a F1 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW8(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 1;
    problemName_        = "MW8";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    } //for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }            
  }            
 
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */    
  public void evaluate(Solution solution) throws JMException {
    Variable[] gen  = solution.getDecisionVariables();
                
    double [] x = new double[numberOfVariables_];
    double [] f = new double[numberOfObjectives_];
    double [] constraint = new double[numberOfConstraints_]; 
    
    for (int i = 0; i < numberOfVariables_; i++)
        x[i] = gen[i].getValue(); // Get x_i's 
    
    /**
     *  ----------------------Evaluate objectives---------------------------------------------     
     */
    // Compute g2   
    double g2 = 1.0;
    
    int m = numberOfObjectives_;
    int n = numberOfVariables_;
    
    for(int i = m; i <= n; i++) {
    	double z = 1 -  Math.exp(-10 * Math.pow(x[i-1] - (double)((i-1)/(double)n),2));    	
    	g2 = g2 + (1.5 + (0.1/n) * (z * z) - 1.5 * Math.cos(2*Math.PI*z));
    }
    
    double  sumProd = 1.0;
    
    for (int i = m - 1; i >= 0; i--){
          if (i != 0) {
	          f[i]  =  sumProd * Math.sin(0.5 * Math.PI * x[(m - 1) - i]);  
	          sumProd *= Math.cos(0.5 * Math.PI * x[(m - 1) - i]);    
          } else {
        	  f[i]  =  sumProd;  
          }
        	  
    }//for 
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, g2 * f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    
    double sumF2 = 0.0;
    for (int i = 0; i < m; i++){
    	sumF2 = sumF2 +  f[i] * f[i];
    }
    
    double l = Math.asin(f[m-1]/Math.sqrt(sumF2));
       
    		    		
    constraint[0] = Math.pow(1.25 - LA(0.5,6,1,2,l), 2) - sumF2;//
    // Other constraints, if necessary
        		
    double CV = 0.0; // Overall Constraint Violation
    int number = 0; // Number Of Violated Constraint
    
    // Set constraints  		
    for (int i = 0; i < numberOfConstraints_;i++) {
    	solution.setConstraint(i, constraint[i]);
    	
    	if (constraint[i] < 0.0) { // Constraints Violated
    		number++;
    		CV = CV + constraint[i];
    	}
    } // For i
    
    solution.setNumberOfViolatedConstraint(number);  
    solution.setOverallConstraintViolation(CV);  
   /* ----------------------Evaluate constraints (end)--------------------------*/
     
  } // evaluate   
  
  public double  LA(double A, double  B, double C, double D, double thera) {
	    double t = Math.pow(thera, C);
	    return A * Math.pow(Math.sin(B * t), D);
  }
}

