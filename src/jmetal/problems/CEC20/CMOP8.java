/**
 * CMOP in CEC2020 COMPETITION
 */
// CMOP8.java
package jmetal.problems.CEC20;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing CMOP8 problem  
 */
public class CMOP8 extends Problem {   
 /** 
  * Creates a default CMOP8 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP8(String solutionType) throws ClassNotFoundException {
    this(solutionType, 8, 2);//2 objectives and 8 decision variables
  } // CMOP8   
    
  /** 
  * Creates a CMOP8 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP8(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 6;
    problemName_        = "CMOP8";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];   
    
    lowerLimit_[0] = 0.0;
    upperLimit_[0] = 1.0;      
    
    for (int var = 1; var < numberOfVariables; var++){
      lowerLimit_[var] = -10.0;
      upperLimit_[var] =  10.0;
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
    
    for (int i = 0; i < numberOfVariables_; i++) {
        x[i] = gen[i].getValue(); // Get x_i's 
    }
    /**
     *  ----------------------Evaluate objectives---------------------------------------------     
     */
    // Compute g   
    double g = (x[1] - 10) * (x[1] - 10) + 5 * (x[2] - 12) * (x[2] - 12) + Math.pow(x[3], 4) + 3 * (x[4] - 11) * (x[4] - 11)
    		+ 10 * Math.pow(x[5], 6) + 7 * Math.pow(x[6], 2) + Math.pow(x[7], 4) - 4 * x[6] * x[7] - 10 * x[6] - 8 * x[7]
    		- 680.6300573745 + 1;

    f[0] = x[0];
    f[1] = g * (1 - Math.sqrt(f[0])/g);    
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    constraint[0] = 1 - f[0] - f[1];
    constraint[1] = 1 - f[0] - f[1] + Math.abs(Math.sin(10*Math.PI*(f[0] - f[1] + 1)));    
    constraint[2] = -127 + 2 * x[1] * x[1] + 3 * Math.pow(x[2], 4) + x[3] + 4 * x[4] * x[4] + 5 * x[5];
    constraint[3] = -282 + 7 * x[1] + 3 * x[2] + 10 * x[3] * x[3] + x[4] - x[5];
    constraint[4] = -196 + 23 * x[1] + x[2] * x[2] + 6 * x[6] * x[6] - 8 * x[7];
    constraint[5] = 4 * x[1] * x[1] + x[2] * x[2] - 3 * x[1] * x[2] + 2 * x[3] * x[3] + 5 * x[6] - 11 * x[7];       	  
    
    double CV = 0.0; // Overall Constraint Violation
    int number = 0; // Number Of Violated Constraint
    
    // Set constraints  		
    for (int i = 0; i < numberOfConstraints_;i++) {
    	solution.setConstraint(i, constraint[i]);
    	
    	if (constraint[i] > 0.0) { // Constraints Violated
    		number++;
    		CV = CV + constraint[i];
    	}
    } // For i

    solution.setNumberOfViolatedConstraint(number);  
    solution.setOverallConstraintViolation(CV);  
   /* ----------------------Evaluate constraints (end)--------------------------*/
     
  } // evaluate    

} // CMOP8

