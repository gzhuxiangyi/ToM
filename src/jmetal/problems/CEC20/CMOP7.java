/**
 * CMOP in CEC2020 COMPETITION
 */
// CMOP7.java
package jmetal.problems.CEC20;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing CMOP7 problem  
 */
public class CMOP7 extends Problem {   
 /** 
  * Creates a default CMOP7 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP7(String solutionType) throws ClassNotFoundException {
    this(solutionType, 6, 2);//2 objectives and 6 decision variables
  } // CMOP7   
    
  /** 
  * Creates a CMOP7 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP7(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 7;
    problemName_        = "CMOP7";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];   
    
    lowerLimit_[0] = 0.0;
    upperLimit_[0] = 1.0;
    
    lowerLimit_[1] = 78;
    upperLimit_[1] = 102;
    
    lowerLimit_[2] = 33;
    upperLimit_[2] = 45;
    
    for (int var = 3; var < numberOfVariables; var++){
      lowerLimit_[var] = 27;
      upperLimit_[var] = 45;
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
    double g =  5.3578547 * x[3] * x[3] + 0.8356891*x[1] *x[5] + 37.293239*x[1] - 40792.141 + 30665.5386717834 + 1;
     
    f[0] = x[0];
    f[1] = g * (1 - Math.sqrt(f[0])/g);    
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    constraint[0] =   1 - f[0] * f[0] - f[1] * f[1];
    constraint[1] =  (85.334407 + 0.0056858 *x[2] * x[5] + 0.0006262 *x[1] * x[4] - 0.0022053 *x[3] * x[5])/92 - 1;
    constraint[2] = - 85.334407 - 0.0056858 *x[2] * x[5] - 0.0006262 *x[1] * x[4] + 0.0022053 *x[3] * x[5];
    constraint[3] = (80.51249 + 0.0071317  *x[2] * x[5] + 0.0029955 *x[1] * x[2] + 0.0021813 *x[3] * x[3])/110 - 1;
    constraint[4] = (- 80.51249 - 0.0071317  *x[2] * x[5] - 0.0029955 *x[1] * x[2] - 0.0021813 *x[3] * x[3])/90 + 1;
    constraint[5] = (9.300961 + 0.0047026  *x[3] * x[5] + 0.0012547 *x[1] * x[3] + 0.0019085 *x[3] * x[4])/25 - 1;
    constraint[6] = (- 9.300961 - 0.0047026  *x[3] * x[5] - 0.0012547 *x[1] * x[3] - 0.0019085 *x[3] * x[4])/20 + 1;        	  
    
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

} // CMOP7

