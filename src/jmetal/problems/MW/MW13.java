//  MW13.java
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
 * Class representing MW13 problem  
 */
public class MW13 extends Problem {   
 /** 
  * Creates a default MW13 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW13(String solutionType) throws ClassNotFoundException {
    this(solutionType, 15, 2);//2 objectives and 15 decision variables
  } // MW13   
    
  /** 
  * Creates a F1 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public MW13(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 2;
    problemName_        = "MW13";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.5;
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
     
    f[0] = g2 * x[0];
    f[1] = g2 * (5 - Math.exp(f[0]/g2) - 0.5 * Math.abs(Math.sin(3 * Math.PI * f[0]/g2)));    
    
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    double T1,T2,T3,T4;
    T1 = 5 - Math.exp(f[0]) - 0.5 * Math.sin(3 * Math.PI * f[0]) - f[1];
    T2 = 5 - (1 + f[0] + 0.5 * f[0] * f[0]) - 0.5 * Math.sin(3 * Math.PI * f[0]) - f[1];
    T3 = 5 - (1 + 0.7 * f[0]) - 0.5 * Math.sin(3 * Math.PI * f[0]) - f[1];
    T4 = 5 - (1 + 0.4 * f[0]) - 0.5 * Math.sin(3 * Math.PI * f[0]) - f[1];
    
    constraint[0] =  - T1 * T4;//
    constraint[1] =    T2 * T3;//
        		
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
	    return A * Math.pow(Math.sin(B*Math.PI*t), D);
  }
}

