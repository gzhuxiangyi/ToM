//  MW7.java
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
 * Class representing MW7 problem  
 */
public class MW7 extends Problem {   
 /** 
  * Creates a default MW7 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW7(String solutionType) throws ClassNotFoundException {
    this(solutionType, 15, 2);//2 objectives and 15 decision variables
  } // MW7   
    
  /** 
  * Creates a F1 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW7(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 2;
    problemName_        = "MW7";
        
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
    // Compute g3   
    double g3 = 1.0;
    
    int m = numberOfObjectives_;
    int n = numberOfVariables_;
    
    for(int i = m; i <= n; i++) {
    	g3 = g3 + 2 * Math.pow(x[i-1] +  Math.pow(x[i-2] - 0.5, 2) - 1, 2);
    }
     
    f[0] = g3 * x[0];
    f[1] = g3 * Math.sqrt(1 - (f[0]/g3) * (f[0]/g3));    
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    constraint[0] = Math.pow(1.2 + LA(0.4,4,1,16,Math.atan(f[1]/f[0])), 2) - f[0] * f[0] - f[1] * f[1];//
    
    constraint[1] = -(Math.pow(1.15 - LA(0.2,4,1,8,Math.atan(f[1]/f[0])), 2) - f[0] * f[0] - f[1] * f[1]);//
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
	    return A * Math.pow(Math.sin(B*t), D);
  }
}

