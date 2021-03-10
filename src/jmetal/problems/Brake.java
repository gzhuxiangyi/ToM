//  ReducerDesign.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem ReducerDesign
 */
public class Brake extends Problem {
	/**
	 * Creates an ReducerDesign problem (30 variables and 2 objectives)
	 *
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public Brake(String solutionType) throws ClassNotFoundException {
		this(solutionType, 4, 2);
	} // ReducerDesign

	/**
	 * Creates an ReducerDesign problem instance
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public Brake(String solutionType, Integer numberOfVariables,
                 Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 5;
		problemName_ = "BrakeDesign";

		lowerLimit_ = new double[]{55,75,1000,2};
		upperLimit_ = new double[]{80,110,3000,20};

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

		double x1,x2,x3,x4;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		x1 = x[0]; x2 = x[1]; x3 = x[2]; x4 = Math.round(x[3]);

		f[0] = 4.9 * 1e-5 * (x2 * x2 - x1 * x1)*(x4 - 1);
		f[1] = 9.82 * 1e6 * (x2 * x2 - x1 * x1) / (x3 * x4 * (Math.pow(x2,3) - Math.pow(x1,3)));

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);

		double[] constraint = new double[numberOfConstraints_];
		constraint[0] = (x2 - x1) - 20;
		constraint[1] = 30 - 2.5 * (x4 + 1);
		constraint[2] = 0.4 - x3 / (3.14 * (x2 * x2 - x1 * x1));
		constraint[3] = 1 - (2.22 * 1e-3 * x3 * (Math.pow(x2,3) - Math.pow(x1,3))) / Math.pow(x2 * x2 - x1 * x1,2);
		constraint[4] = (2.66 * 1e-2 * x3 * x4 * (Math.pow(x2,3) - Math.pow(x1,3))) / (x2 * x2 - x1 * x1) - 900;

		double total = 0.0;
		int number = 0;
		for(int i = 0 ; i < numberOfConstraints_; i++){
			if (constraint[i] < 0.0){
				total+=constraint[i] ;
				number++;
				solution.setConstraint(i,constraint[i]);
			}
		}

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);

	} // evaluate

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		
//		double constraint;
//		double x_1 = solution.getDecisionVariables()[0].getValue();
//		constraint = Math.sin(20 * Math.PI * x_1) - 0.5;
//
////        double y_1 = 10 * (x_1 - 0.5);
////
////        constraint = 2.5 - (-0.9 * y_1 * y_1 + Math.pow((5 * Math.pow(Math.abs(y_1),0.001) / Math.pow(5,0.001) ),2));
//
//		double total = 0.0;
//		int number = 0;
//		if (constraint < 0.0) {
//			total += constraint;
//			number++;
//			solution.setConstraint(0,constraint);
//		}
//		solution.setOverallConstraintViolation(total);
//		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints

}
