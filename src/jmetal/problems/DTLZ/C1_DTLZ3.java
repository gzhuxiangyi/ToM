//  C1_DTLZ3.java
//
// Author:
//    Ke Li <keli.genius@gmail.com>
// 
// Reference:
//    Ke Li, Kalyanmoy Deb, Qingfu Zhang, Sam Kwong, 
//    "An Evolutionary Many-Objective Optimization Algorithm Based on Dominance and Decomposition"
//    IEEE Transactions on Evolutionary Computation, 19(5): 694-716, 2015.
// 
// Homepage:
//    http://www.cs.cityu.edu.hk/~51888309/
// 
// Copyright (c) 2015 Ke Li
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.problems.DTLZ;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem C1_DTLZ3
 */
public class C1_DTLZ3 extends Problem{
  
 /**
  * Creates a default C1_DTLZ3 problem (12 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C1_DTLZ3(String solutionType) throws ClassNotFoundException {
    this(solutionType, 12,3);
  } // C1_DTLZ3
    
  /**
  * Creates a new C1_DTLZ3 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C1_DTLZ3(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) throws ClassNotFoundException {
    numberOfVariables_   = numberOfVariables.intValue();
    numberOfObjectives_  = numberOfObjectives.intValue();
    numberOfConstraints_ = 0;
    problemName_         = "C1_DTLZ3";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    }
        
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
		Variable[] gen = solution.getDecisionVariables();

		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		int k = numberOfVariables_ - numberOfObjectives_ + 1;

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		double g = 0.0;
		for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
			g += (x[i] - 0.5) * (x[i] - 0.5)
					- Math.cos(20.0 * Math.PI * (x[i] - 0.5));

		g = 100.0 * (k + g);
		for (int i = 0; i < numberOfObjectives_; i++)
			f[i] = 1.0 + g;

		for (int i = 0; i < numberOfObjectives_; i++) {
			for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)
				f[i] *= Math.cos(x[j] * 0.5 * Math.PI);
			if (i != 0) {
				int aux = numberOfObjectives_ - (i + 1);
				f[i] *= Math.sin(x[aux] * 0.5 * Math.PI);
			} // if
		} // for


		
//		constraint = (sum - 16.0) * (sum - radius * radius);
		
//		if (constraint < 0.0)
//	    	solution.setOverallConstraintViolation(-constraint);
//	    else
//	    	solution.setOverallConstraintViolation(0.0);
		
		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate


	public void evaluateConstraints(Solution solution) throws JMException{
		// constraint evaluation
		double[] f = new double[numberOfObjectives_];
		for(int i = 0; i < numberOfObjectives_; i++){
			f[i] = solution.getObjective(i);
		}

		double constraint1, constraint2;
		double sum = 0.0;
		for (int i = 0; i < numberOfObjectives_; i++)
			sum += f[i] * f[i];
		constraint1 = (sum - 16.0) * (sum - 25);
		constraint2 = 64 - sum;

		if (constraint1 < 0.0)
			solution.setOverallConstraintViolation(constraint1 / 25.0);
		else if (constraint2 < 0.0)
			solution.setOverallConstraintViolation(constraint2 / 64.0);
		else
			solution.setOverallConstraintViolation(0.0);
	}
}

