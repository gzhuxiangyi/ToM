//  C2_DTLZ2.java
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
 * Class representing problem C2_DTLZ2
 */
public class C2_DTLZ2 extends Problem{
       
 /** 
  * Creates a default C2_DTLZ2 problem (12 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C2_DTLZ2(String solutionType) throws ClassNotFoundException {
    this(solutionType, 12, 3);
  } // C2_DTLZ2

 /**
  * Creates a new instance of C2_DTLZ2 
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C2_DTLZ2(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) throws ClassNotFoundException {
    numberOfVariables_  = numberOfVariables.intValue();
    numberOfObjectives_ = numberOfObjectives.intValue();
    numberOfConstraints_= 0;
    problemName_        = "C2_DTLZ2";
        
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables_; var++){
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
  } //DTLZ2
        
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
			g += (x[i] - 0.5) * (x[i] - 0.5);

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

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	}


	public void evaluateConstraints(Solution solution) throws JMException {

		double[] f = new double[numberOfObjectives_];
		for (int i = 0; i < numberOfObjectives_; i++) {
			f[i] = solution.getObjective(i);
		}

		// constraint evaluation

		double radius;
		if (numberOfObjectives_ == 3)
			radius = 0.4;
		else
			radius = 0.5;
		double sum = 0.0;
		for (int i = 0; i < numberOfObjectives_; i++) {
			if (i == 0)
				continue;
			else
				sum += f[i] * f[i];
		}
		double min = (f[0] - 1.0) * (f[0] - 1.0) + sum - radius * radius;
		for (int i = 1; i < numberOfObjectives_; i++) {
			sum = 0.0;
			for (int j = 0; j < numberOfObjectives_; j++) {
				if (j == i)
					continue;
				else
					sum += f[j] * f[j];
			}
			double cur = (f[i] - 1.0) * (f[i] - 1.0) + sum - radius * radius;
			if (cur < min)
				min = cur;
		}
		double part1 = min;

		sum = 0.0;
		for (int i = 0; i < numberOfObjectives_; i++) {
			sum += (f[i] - 1.0 / Math.sqrt(numberOfObjectives_)) * (f[i] - 1.0 / Math.sqrt(numberOfObjectives_));
		}
		double part2 = sum - radius * radius;

		if (part1 < part2)
			min = part1;
		else
			min = part2;

		double constraint = -min;
		if (constraint < 0.0)
			solution.setOverallConstraintViolation(constraint);
		else
			solution.setOverallConstraintViolation(0.0);
	}
} //evaluate
