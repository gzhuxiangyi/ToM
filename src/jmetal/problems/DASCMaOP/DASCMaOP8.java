//  DASCMaOP8.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package jmetal.problems.DASCMaOP;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.problems.WFG.Shapes;
import jmetal.problems.WFG.Transformations;
import jmetal.problems.WFG.WFG;
import jmetal.util.JMException;

/**
 * Creates a default DASCMaOP8 problem with
 * 2 position-related parameters,
 * 4 distance-related parameters,
 * and 2 objectives
 */
public class DASCMaOP8 extends WFG{
    ConstraintSetting constraintFunction;
    double[] difficultyTriple;
    String solutionType;

    /**
     * Creates a default DASCMaOP8 with
     * 2 position-related parameters,
     * 4 distance-related parameters,
     * and 2 objectives
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public DASCMaOP8(String solutionType, double[] difficultyFactor) throws ClassNotFoundException {
        this(solutionType, 10, 20, 5, difficultyFactor) ;
    } // DASCMaOP8

    public DASCMaOP8(String solutionType, int objNo, double[] difficultyFactors) throws ClassNotFoundException {
        this(solutionType, 10, 20, objNo, difficultyFactors) ;
    } // DASCMaOP8

    /**
     * Creates a DASCMaOP8 problem instance
     * @param k Number of position parameters
     * @param l Number of distance parameters
     * @param M Number of objective functions
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public DASCMaOP8(String solutionType, Integer k, Integer l, Integer M, double[] difficultyFactor) throws ClassNotFoundException {
        super(solutionType, k,l,M);
        this.solutionType = solutionType;
        problemName_ = "DASCMaOP8";

        S_ = new int[M_];
        for (int i = 0; i < M_; i++)
            S_[i] = 2 * (i+1);

        A_ = new int[M_-1];
        for (int i = 0; i < M_-1; i++)
            A_[i] = 1;

        difficultyTriple = difficultyFactor;
    } // DASCMaOP8

    /**
     * Evaluates a solution
     * @param z The solution to evaluate
     * @return double [] with the evaluation results
     */
    public float [] evaluate(float [] z){
        float [] y;

        y = normalise(z);
        y = t1(y,k_);
        y = t2(y,k_);
        y = t3(y,k_,M_);

        float [] result = new float[M_];
        float [] x = calculate_x(y);
        for (int m = 1; m <= M_ ; m++) {
            result [m-1] = D_*x[M_-1] + S_[m-1] * (new Shapes()).concave(x,m);
        }

        return result;
    } // evaluate

    public float[] evaluateX(float[] z) {
        float [] y;

        y = normalise(z);
        y = t1(y,k_);
        y = t2(y,k_);
        y = t3(y,k_,M_);
        float [] x = calculate_x(y);
        return x;

    }


    /**
     * DASCMaOP8 t1 transformation
     */
    public float [] t1(float [] z, int k){
        float [] result = new float[z.length];
        float [] w      = new float[z.length];

        for (int i = 0; i < w.length; i++) {
            w[i] = (float)1.0;
        }

        System.arraycopy(z, 0, result, 0, k);

        for (int i = k; i < z.length; i++){
            int head = 0;
            int tail = i - 1;
            float [] subZ = subVector(z,head,tail);
            float [] subW = subVector(w,head,tail);
            float aux = (new Transformations()).r_sum(subZ,subW);

            result[i] = (new Transformations()).b_param(z[i],aux,(float)0.98/(float)49.98,(float)0.02,50);
        }

        return result;
    } // t1

    /**
     * DASCMaOP8 t2 transformation
     */
    public float [] t2(float [] z, int k){
        float [] result = new float[z.length];

        System.arraycopy(z, 0, result, 0, k);

        for (int i = k; i < z.length; i++) {
            result[i] = (new Transformations()).s_linear(z[i],(float)0.35);
        }

        return result;
    } // t2

    /**
     * DASCMaOP8 t3 transformation
     */
    public float [] t3(float [] z, int k, int M){
        float [] result = new float[M];
        float [] w      = new float[z.length];

        for (int i = 0; i < z.length; i++) {
            w[i] = (float)1.0;
        }

        for (int i = 1; i <= M-1; i++){
            int head = (i - 1)*k/(M-1) + 1;
            int tail = i * k / (M - 1);
            float [] subZ = subVector(z,head-1,tail-1);
            float [] subW = subVector(w,head-1,tail-1);

            result[i-1] = (new Transformations()).r_sum(subZ,subW);
        }

        int head = k + 1;
        int tail = z.length;
        float [] subZ = subVector(z,head-1,tail-1);
        float [] subW = subVector(w,head-1,tail-1);
        result[M-1] = (new Transformations()).r_sum(subZ,subW);

        return result;
    } // t3

    /**
     * Evaluates a solution
     * @param solution The solution to evaluate
     * @throws JMException
     */
    public final void evaluate(Solution solution) throws JMException {
        float [] variables = new float[getNumberOfVariables()];
        Variable[] dv = solution.getDecisionVariables();

        for (int i = 0; i < getNumberOfVariables(); i++) {
            variables[i] = (float)dv[i].getValue();
        }
        float[] f = evaluate(variables);

        for (int i = 0; i < f.length; i++) {
            solution.setObjective(i, f[i]);
        }


        float[] x = evaluateX(variables);
        constraintFunction = new ConstraintSetting(solutionType, solution.getNumberOfObjectives(), x.length, S_, difficultyTriple);
        double[] con = constraintFunction.evaluate(x, f);

        // set the constraint values
        double total = 0.0;
        int number = 0;
        for (int i = 0; i <= 2 * numberOfObjectives_; i++) {
            if (con[i] < 0.0) {
                total = total + con[i];
                number++;
            }
        }
        solution.setOverallConstraintViolation(total);
        solution.setNumberOfViolatedConstraint(number);
    } // evaluate
} // DASCMaOP8


