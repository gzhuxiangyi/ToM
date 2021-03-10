//  CMOP1.java
/**
 * MW problems
 */
package jmetal.problems.CEC20;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing CMOP1 problem  
 */
public class CMOP1 extends Problem {   
 /** 
  * Creates a default CMOP1 problem (15 variables and 2 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP1(String solutionType) throws ClassNotFoundException {
    this(solutionType, 25, 2);//2 objectives and 15 decision variables
  } // CMOP1   
    
  /** 
  * Creates a F1 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public CMOP1(String solutionType,  Integer numberOfVariables,  Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 3;
    problemName_        = "CMOP1";
        
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
    
    for (int i = 0; i < numberOfVariables_; i++) {
        x[i] = gen[i].getValue(); // Get x_i's 
//    	x[i]  = 0.5;
    }
    /**
     *  ----------------------Evaluate objectives---------------------------------------------     
     */
    // Compute g1   
    double g1 = 1.0;
    
    int m = numberOfObjectives_;
    int n = numberOfVariables_;
    
    for(int i = m; i <= n; i++) {
    	g1 = g1 + (1 - Math.exp(-10 * Math.pow(Math.pow(x[i-1],n-m) - 0.5 - (double)((i-1)/(2.0*n)), 2)));
    }
     
    f[0] = g1 * x[0];
    f[1] = g1 * Math.sqrt(1 - (f[0]/g1) * (f[0]/g1));    
//    System.out.println(" f[1] = " +  f[1]);
    // ----------------------Set objectives-----------------------
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i, f[i]);     	  
    }
    
    /**
     *  ----------------------Evaluate constraints---------------------------------------------     
     */
    double L1 = Math.atan(f[1]/f[0]);
    double L2 = 0.5 * Math.PI - 2 * Math.abs(Math.atan(f[1]/f[0]) - 0.25 * Math.PI);

     
    constraint[0] =  - (Math.pow(1.7 - LA(0.2,2,1,1,L1), 2) - f[0] * f[0] - f[1] * f[1]);//
    constraint[1] =   (Math.pow(1 + LA(0.5,6,3,1,L2), 2) - f[0] * f[0] - f[1] * f[1]);//
    constraint[2] =   (Math.pow(1 - LA(0.45,6,3,1,L2), 2) - f[0] * f[0] - f[1] * f[1]);//
   
//    System.out.println(" LA(0.2,2,1,1,L1) c = " +  0.2 * Math.pow(Math.sin(2 *Math.PI * Math.pow(L1, 1.0)),1)) ; //
    
//    System.out.println(" LA(0.2,2,1,1,L1) = " + LA(0.2,2,1,1,L1));
    
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
//    System.out.println("cv = " + CV);
    
    solution.setNumberOfViolatedConstraint(number);  
    solution.setOverallConstraintViolation(CV);  
   /* ----------------------Evaluate constraints (end)--------------------------*/
     
  } // evaluate   
  
  public double  LA(double A, double  B, double C, double D, double thera) {
	    double t = Math.pow(thera, C);
	    return A * Math.pow(Math.sin(B*t), D);
  }
}

