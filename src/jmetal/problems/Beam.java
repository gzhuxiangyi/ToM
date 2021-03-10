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
public class Beam extends Problem {
	/**
	 * Creates an ReducerDesign problem (30 variables and 2 objectives)
	 *
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public Beam(String solutionType) throws ClassNotFoundException {
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
	public Beam(String solutionType, Integer numberOfVariables,
                Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 4;
		problemName_ = "BeamDesign";

		lowerLimit_ = new double[]{0.125,0.1,0.1,0.125};
		upperLimit_ = new double[]{5.0,10,10,5.0};

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

		double h,l,t,b;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		h = x[0]; l = x[1]; t = x[2]; b = x[3];

		f[0] = 1.10471 * h * h * l + 0.04811 * t * b * (14.0 + t);
		f[1] = 2.1952 / (t * t * t * b);

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);

		double[] constraint = new double[numberOfConstraints_];

		double tao_1 = 6000 / (Math.sqrt(2) * h * l);
		double tao_2 = (6000 * (14.0 + 0.5 * l) * Math.sqrt(0.25 * (l * l + (h + t) * (h + t)))) / (2 * (0.707 * h * l * (l * l / 12 + 0.25 * (l * l + (h + t) * (h + t)))));
		double theta = 504000 / (t * t * b);
		double Pc    = 64764.022 * (1 - 0.0282346 * t) * t * b * b * b;

		constraint[0] = 13600 - Math.sqrt(tao_1 * tao_1 + tao_2 * tao_2 + l * tao_1 * tao_2 / (Math.sqrt(0.25 * (l * l + (h + t) * (h + t)))));
		constraint[1] = 30000 - theta;
		constraint[2] = b - h;
		constraint[3] = Pc - 6000;


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
