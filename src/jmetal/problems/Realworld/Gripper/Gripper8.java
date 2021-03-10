//  Gripper8.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.Realworld.Gripper;

import jmetal.CMOEAs.Utils;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem Gripper8
 */
public class Gripper8 extends Problem {
    /**
     * Creates an Gripper8 problem (30 variables and 2 objectives)
     *
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public Gripper8(String solutionType) throws ClassNotFoundException {
        this(solutionType, 7, 2);
    } // Gripper8

    /**
     * Creates an Gripper8 problem instance
     *
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     * @param solutionType       The solution type must "Real" or "BinaryReal".
     */
    public Gripper8(String solutionType, Integer numberOfVariables,
                    Integer numberOfObjectives) throws ClassNotFoundException {
        numberOfVariables_ = numberOfVariables.intValue();
        numberOfObjectives_ = numberOfObjectives.intValue();
        numberOfConstraints_ = 8;
        problemName_ = "Gripper8";

        lowerLimit_ = new double[]{10, 10, 100, 1e-6, 10, 100 + 1e-6, 1};
        upperLimit_ = new double[]{150, 150, 200, 50, 150, 300, 3.14};

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
     * @param solution The solution to evaluate
     * @throws JMException
     */

    public void evaluate(Solution solution) throws JMException {

        // repair solutions
        Utils.repairSolution(solution,this);

        Variable[] gen = solution.getDecisionVariables();
        double a = gen[0].getValue();
        double b = gen[1].getValue();
        double c = gen[2].getValue();
        double e = gen[3].getValue();
        double f = gen[4].getValue();
        double l = gen[5].getValue();
        double delta = gen[6].getValue();
        double[] z = new double[]{0, 100};

        if (a < 4 * b) {
            if (c < a + b) {
                f = 2 * e + 10;
            } else if (c > a + b) {
                f = e + 50;
            }
        }

        double[] g = new double[2];
        double[] beta = new double[2];
        double[] phi = new double[2];
        double[] y = new double[2]; // y[0] = y(x,0) y[1] = y(x,z_max);

        for (int i = 0; i < 2; i++) {
            g[i] = Math.sqrt((l - z[i]) * (l - z[i]) + e * e);
            phi[i] = Math.atan(e / (l - z[i]));
            beta[i] = Math.acos((b * b + g[i] * g[i] - a * a) / (2 * b * g[i])) - phi[i];
            y[i] = 2 * (e + f + c * Math.sin(beta[i] + delta));
        }

        double max_f = calcFmaxByGoldenSection(a, b, c, e, l);
        double min_f = Math.min(calc_F(a,b,c,e,l,0),calc_F(a,b,c,e,l,100));

        double f1 = - Math.abs((y[1] - y[0]) / (z[1] - z[0]));
        double f2 = a + b + c + e + l;

        solution.setObjective(0, f1);
        solution.setObjective(1, f2);
    } // evaluate

    /**
     * Evaluates the constraint overhead of a solution
     *
     * @param solution The solution
     * @throws JMException
     */
    public void evaluateConstraints(Solution solution) throws JMException {

        double y_min = 50, y_max = 100, y_g = 150;
        double[] z = new double[]{0, 100};

        Variable[] gen = solution.getDecisionVariables();
        double a = gen[0].getValue();
        double b = gen[1].getValue();
        double c = gen[2].getValue();
        double e = gen[3].getValue();
        double f = gen[4].getValue();
        double l = gen[5].getValue();
        double delta = gen[6].getValue();

        if (a < 4 * b) {
            if (c < a + b) {
                f = 2 * e + 10;
            } else if (c > a + b) {
                f = e + 50;
            }
        }

        double[] g = new double[2];
        double[] beta = new double[2];
        double[] phi = new double[2];
        double[] y = new double[2]; // y[0] = y(x,0) y[1] = y(x,z_max);

        for (int i = 0; i < 2; i++) {
            g[i] = Math.sqrt((l - z[i]) * (l - z[i]) + e * e);
            phi[i] = Math.atan(e / (l - z[i]));
            beta[i] = Math.acos((b * b + g[i] * g[i] - a * a) / (2 * b * g[i])) - phi[i];
            y[i] = 2 * (e + f + c * Math.sin(beta[i] + delta));
        }

        double max_f = calcFmaxByGoldenSection(a, b, c, e, l);

        int num_constraint = numberOfConstraints_;
        double[] cons = new double[num_constraint];
        cons[0] = y_min - y[1];
        cons[1] = y[1];
        cons[2] = y[0] - y_max;
        cons[3] = y_g - y[0] ;
        cons[4] = (a + b) * (a + b) - l * l - e * e ;
        cons[5] = (l - z[1]) * (l - z[1]) + (a - e) * (a - e) -  b * b ;
        cons[6] = l - z[1];
        cons[7] = max_f - 50;
        double total = 0.0;
        int number = 0;
        for (int i = 0; i < num_constraint; i++) {
            if (cons[i] < 0.0) {
                total += cons[i];
                number++;
                solution.setConstraint(0, cons[i]);
            }
        }
        solution.setOverallConstraintViolation(total);
        solution.setNumberOfViolatedConstraint(number);
    } // evaluateConstraints

    private double calc_F(double a, double b, double c, double e, double l, double z){
        double g = Math.sqrt((l - z) * (l - z) + e * e);
        double phi = Math.atan(e / (l - z));
        double beta = Math.acos((b * b + g * g - a * a) / (2 * b * g)) - phi;
        double alpha = Math.acos((a * a + g * g - b * b) / (2 * a * g)) + phi;
        return 100 * b * Math.sin(beta + alpha) / (2 * c * Math.cos(alpha));
    }


    private double calcFmaxByGoldenSection(double a, double b, double c, double e, double l){
        double z_max = 100;
        double z_min = 0;
        double max_z = goldSplit(z_min,z_max,1e-3,a,b,c,e,l);
        return calc_F(a,b,c,e,l,max_z);
    }

    private double goldSplit(double min, double max, double deviation, double a, double b, double c, double e, double l){

        if(Math.abs(max - min) < deviation){
            return 0.5 * (max + min);
        }
        double t1 = min + 0.382 * (max - min);
        double t2 = min + 0.618 * (max - min);
        double f1 = calc_F(a,b,c,e,l,t1);
        double f2 = calc_F(a,b,c,e,l,t2);
        if(f1 < f2){
            min = t1;
            return goldSplit(min,max,deviation,a,b,c,e,l);
        }else if(f1 > f2){
            max = t2;
            return goldSplit(min,max,deviation,a,b,c,e,l);
        }else {
            min = t1;
            max = t2;
            return goldSplit(min,max,deviation,a,b,c,e,l);
        }
    }

}
