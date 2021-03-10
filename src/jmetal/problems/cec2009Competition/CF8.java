//  CF8.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.cec2009Competition;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem CF8
 */
public class CF8 extends Problem {
	/**
	 * Creates an CF8 problem (10 variables and 2 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF8(String solutionType) throws ClassNotFoundException {
		this(solutionType, 10, 3);
	} // CF8

	/**
	 * Creates an CF8 problem instance
	 * 
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF8(String solutionType, Integer numberOfVariables,
			Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 1;
		problemName_ = "CF8";

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		for (int var = 0; var < numberOfVariables; var++) {
			lowerLimit_[var] = -4.0;
			upperLimit_[var] = 4.0;
		} // for
		lowerLimit_[0] = 0.0;
		upperLimit_[0] = 1.0;
		lowerLimit_[1] = 0.0;
		upperLimit_[1] = 1.0;

		if (solutionType.compareTo("BinaryReal") == 0)
			solutionType_ = new BinaryRealSolutionType(this);
		else if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType
					+ " invalid");
			System.exit(-1);
		}
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @throws JMException
	 */

	public void evaluate(Solution solution) throws JMException {

		double sum1, sum2,sum3, yj;
		int j, count1, count2, count3;
        sum1 = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
        int nx = numberOfVariables_;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		for (j = 3; j <= nx; j++) {
			
				yj = x[j-1] - 2.0*x[1]*Math.sin(2.0*Math.PI*x[0]+j*Math.PI/nx);
				if(j % 3 == 1) 
				{
					sum1  += yj*yj;
					count1++;
				} 
				else if(j % 3 == 2) 
				{
					sum2  += yj*yj;
					count2++;
				}
				else
				{
					sum3  += yj*yj;
					count3++;
				}
		}
		f[0] = Math.cos(0.5*Math.PI*x[0])*Math.cos(0.5*Math.PI*x[1]) + 2.0*sum1 / (double)count1;
		f[1] = Math.cos(0.5*Math.PI*x[0])*Math.sin(0.5*Math.PI*x[1]) + 2.0*sum2 / (double)count2;
		f[2] = Math.sin(0.5*Math.PI*x[0]) + 2.0*sum3 / (double)count3;

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		double N = 2.0, a = 4.0;
		double [] f = new double[3];
        f[0] = solution.getObjective(0);
		f[1] = solution.getObjective(1);
		f[2] = solution.getObjective(2);
		double constraint = (f[0]*f[0]+f[1]*f[1])/(1-f[2]*f[2]) 
				- a*Math.abs(Math.sin(N*Math.PI*((f[0]*f[0]-f[1]*f[1])/(1-f[2]*f[2])+1.0))) + 1.0;
		
		double total = 0.0;
		int number = 0;
		if (constraint < 0.0) {
			total += constraint;
			number++;
		}

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints

}
