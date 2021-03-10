//  C3_DTLZ4.java
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
 * Class representing problem C3_DTLZ4
 */
public class C3_DTLZ4 extends Problem{
   
  
 /**
  * Creates a default C3_DTLZ4 problem (12 variables and 3 objectives) 
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C3_DTLZ4(String solutionType) throws ClassNotFoundException {
    this(solutionType, 12, 3);
  }	// C3_DTLZ4
    
 /**
  * Creates a C3_DTLZ4 problem problem instance 
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C3_DTLZ4(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) throws ClassNotFoundException {
    numberOfVariables_  = numberOfVariables.intValue();
    numberOfObjectives_ = numberOfObjectives.intValue();
    numberOfConstraints_= 0;
    problemName_        = "C3_DTLZ4";
        
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
  } //DTLZ4         

  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */      
	public void evaluate(Solution solution) throws JMException {
		Variable[] gen = solution.getDecisionVariables();

		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		double alpha = 100.0;
		int k = numberOfVariables_ - numberOfObjectives_ + 1;

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		double g = 0.0;
		for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
			g += (x[i] - 0.5) * (x[i] - 0.5);

		for (int i = 0; i < numberOfObjectives_; i++)
			f[i] = 1.0 + g;

		for (int i = 0; i < numberOfObjectives_; i++) {
			for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)
				f[i] *= Math.cos(Math.pow(x[j], alpha)
						* (Math.PI / 2.0));
			if (i != 0) {
				int aux = numberOfObjectives_ - (i + 1);
				f[i] *= Math.sin(Math.pow(x[aux], alpha)
						* (Math.PI / 2.0));
			} // if
		} // for


		
		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate

	public void evaluateConstraints(Solution solution) throws JMException {

		double[] f = new double[numberOfObjectives_];
		for (int i = 0; i < numberOfObjectives_; i++) {
			f[i] = solution.getObjective(i);
		}

		// constraint handling
		double[] constraints = new double[numberOfObjectives_];

		for (int i = 0; i < numberOfObjectives_; i++) {
			double sum = 0.0;
			for (int j = 0; j < numberOfObjectives_; j++) {
				if (j == i)
					continue;
				else
					sum += f[j] * f[j];
			}
			constraints[i] = sum + f[i] * f[i] / 4.0 - 1.0;
		}
		double constraint = 0.0;
		for (int i = 0; i < numberOfObjectives_; i++) {
			if (constraints[i] < 0.0)
				constraint += (constraints[i]);
		}
		solution.setOverallConstraintViolation(constraint);
	}
}

