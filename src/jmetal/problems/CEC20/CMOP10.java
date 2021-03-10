/**
 * CMOP in CEC2020 COMPETITION
 */
// CMOP10.java
package jmetal.problems.CEC20;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing CMOP10 problem  
 */
public class CMOP10 extends Problem {   
 /** 
  * Creates a default CMOP10 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP10(String solutionType) throws ClassNotFoundException {
    this(solutionType, 10, 3);//2 objectives and 8 decision variables
  } // CMOP10   
    
  /** 
  * Creates a CMOP10 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP10(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 7;
    problemName_        = "CMOP10";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];   
    
    lowerLimit_[0] = 0.0;
    upperLimit_[0] = 1.0;      
    
    lowerLimit_[1] = 0.0;
    upperLimit_[1] = 1.0;      
    
    lowerLimit_[2] = 500;
    upperLimit_[2] = 1000;
    
    lowerLimit_[3] = 1000;
    upperLimit_[3] = 2000;
    
    lowerLimit_[4] = 5000;
    upperLimit_[4] = 6000;
       
    for (int var = 5; var < numberOfVariables; var++){
        lowerLimit_[var] =  100;
        upperLimit_[var] =  500;
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
    double g = x[2] + x[3] + x[4] - 7048.2480205286;

    f[0] = g * x[0] * x[1];
    f[1] = g * x[0] * (1 - x[1]);    
    f[2] = g * (1 - x[0]);    
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    constraint[0] = (0.4 - f[2]) *(f[2] - 0.6);    
    constraint[1] = -1 + 0.0025*(x[5] + x[7]);
    constraint[2] = -1 + 0.0025*(x[6] + x[8] - x[5]);
    constraint[3] = -1 + 0.01*(x[9] - x[6]);
    constraint[4] = (-x[2] * x[7] + 833.33252*x[5] + 100*x[2])/83333.333 - 1;
    constraint[5] = -x[3] * x[8] + 1250*x[6] + x[3] * x[5] - 1250 * x[5];
    constraint[6] = (-x[4] * x[9] + x[4] * x[6] - 2500 * x[6])/1250000 + 1 ;
 	      
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

} // CMOP10

