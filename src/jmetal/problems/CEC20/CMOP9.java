/**
 * CMOP in CEC2020 COMPETITION
 */
// CMOP9.java
package jmetal.problems.CEC20;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing CMOP9 problem  
 */
public class CMOP9 extends Problem {   
 /** 
  * Creates a default CMOP9 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP9(String solutionType) throws ClassNotFoundException {
    this(solutionType, 8, 2);//2 objectives and 8 decision variables
  } // CMOP9   
    
  /** 
  * Creates a CMOP9 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP9(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 9;
    problemName_        = "CMOP9";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];   
    
    lowerLimit_[0] = 0.0;
    upperLimit_[0] = 1.0;      
    
    lowerLimit_[1] = 0.0;
    upperLimit_[1] = 1000;      
    
    lowerLimit_[2] = 0.0;
    upperLimit_[2] = 40;
    
    lowerLimit_[3] = 0.0;
    upperLimit_[3] = 40;
    
    lowerLimit_[4] = 100;
    upperLimit_[4] = 300;
       
    lowerLimit_[5] = 6.3;
    upperLimit_[5] = 6.7;
    
    lowerLimit_[6] = 5.9;
    upperLimit_[6] = 6.4;
    
    lowerLimit_[7] = 4.5;
    upperLimit_[7] = 6.25;
    
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
    double g = x[1] - 192.724510070035;

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
    constraint[2] = (f[0] - 0.8) * (f[1] - 0.6);
    constraint[3] = - x[1] + 35 * Math.pow(x[2], 0.6) + 35 * Math.pow(x[3], 0.6) ;
    constraint[4] = Math.abs(-300 * x[3] + 7500 * x[5] - 7500 * x[6] - 25 * x[4] * x[5] + 25 * x[4] * x[6] + x[3] * x[4]) - 0.0001;
    constraint[5] = Math.abs(100 * x[2] + 155.365 * x[4] + 2500 * x[7] - x[2] * x[4] - 25 * x[4] * x[7] - 15536.5) - 0.0001;
    constraint[6] = Math.abs(- x[5] + Math.log( - x[4] + 900)) - 0.0001;
    constraint[7] = Math.abs(- x[6] + Math.log(x[4] + 300))  - 0.0001;
    constraint[8] = Math.abs(- x[7] + Math.log(-2 * x[4] + 700))  - 0.0001;     	  
    
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

} // CMOP9

