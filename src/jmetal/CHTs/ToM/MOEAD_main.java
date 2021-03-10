/**
 * Created by liwenji  Email: wenji_li@126.com
 */
package jmetal.CHTs.ToM;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.FileUtils;

public class MOEAD_main {

    public static void main(String[] args) throws Exception {
        Problem problem;              // The problem to solve
        Algorithm algorithm;            // The algorithm to use
        Operator crossover;            // Crossover operator
        Operator mutation;             // Mutation operator
        Operator selection;            // Selection operator
        HashMap parameters;           // Operator parameters
/////////////////////////////////////////// parameter setting //////////////////////////////////
        //parameter setting
        int popSize = 300; //210 for m = 5 // 156 for m = 8 // 275  for m = 10
        int maxFES = 300000; //  4e5 for m = 5 // 5e5 for m = 8 // 6e5 for m = 10
        int objNo = -1; // -1 for problems with less than 4 objs

        int runtime = 20;
        int neighborSize = (int) Math.floor(0.1 * popSize);
        int updateNumber = 2;
        double deDelta = 0.9;
        double DeCrossRate = 1.0;
        double DeFactor = 0.5;

//        String AlgorithmName = "NSGAII_CDP";
        String AlgorithmName = "MOEAD_CDP";
//        String AlgorithmName = "CNSGAIII";
//        String AlgorithmName = "CMOEADD";

        String mainPath = System.getProperty("user.dir");
        String weightPath = mainPath + "/weight";

        Boolean isDisplay = false;

        // cMOEAD_DRA parameters
        double epsilon = 0.02;
        int lastGen = 20;

        // MOEAD_SR parameters
        double srFactor = 0.5;

        String resultFile = mainPath + "/" + AlgorithmName + ".db";

        FileUtils.deleteFile(resultFile);

//        String[] problemStrings = {"DASCMaOP1","DASCMaOP2","DASCMaOP3","DASCMaOP4","DASCMaOP5","DASCMaOP6","DASCMaOP7","DASCMaOP8","DASCMaOP9"};

        String[] problemStrings = {"DASCMOP1", "DASCMOP2", "DASCMOP3","DASCMOP4", "DASCMOP5", "DASCMOP6","DASCMOP7", "DASCMOP8", "DASCMOP9"};
//        double[][] dif_level = {{0.0, 1, 0.0}};

        double[][] dif_level = {{0.25, 0.0, 0.0}, {0.0, 0.25, 0.0}, {0.0, 0.0, 0.25}, {0.25, 0.25, 0.25},
                                {0.5, 0.0, 0.0},{0.0, 0.5, 0.0}, {0.0, 0.0, 0.5}, {0.5, 0.5, 0.5},
                                {0.75, 0.0, 0.0}, {0.0, 0.75, 0.0}, {0.0, 0.0, 0.75},{0.75, 0.75, 0.75},
                                {0.0, 1.0, 0.0}, {0.5, 1.0, 0.0}, {0.0, 1.0, 0.5},{0.5, 1.0, 0.5}};

//        double[][] dif_level = {{0.25, 0.0, 0.0}, {0.0, 0.25, 0.0}, {0.0, 0.0, 0.25}, {0.25, 0.25, 0.25},
//                {0.5, 0.0, 0.0},{0.0, 0.5, 0.0} , {0.0, 0.0, 0.5}, {0.5, 0.5, 0.5},
//                {0.75, 0.0, 0.0}, {0.0, 0.75, 0.0}, {0.0, 0.0, 0.75},{0.75, 0.75, 0.75}};



//////////////////////////////////////// End parameter setting //////////////////////////////////

        for (int i = 0; i < problemStrings.length; i++) {

            for (int k = 0; k < dif_level.length; k++) {

                Object[] params;

                if(objNo > 3){
                    params = new Object[] {"Real", objNo, dif_level[k]};

                }else {
                    params = new Object[]{"Real", dif_level[k]};
                }

//                Object[] params = {"Real", objNo, dif_level[k]};
//                Object[] params = {"Real", dif_level[k]};

                problem = (new ProblemFactory()).getProblem(problemStrings[i], params);
                //define algorithm
                Object[] algorithmParams = {problem};
                algorithm = (new Utils()).getAlgorithm(AlgorithmName, algorithmParams);
                //define pareto file path
                String paretoPath = mainPath + "/pf_data/" + problemStrings[i] + ".pf";

                // Algorithm parameters
                algorithm.setInputParameter("AlgorithmName", AlgorithmName);
                algorithm.setInputParameter("populationSize", popSize);
                algorithm.setInputParameter("maxEvaluations", maxFES);
                algorithm.setInputParameter("dataDirectory", weightPath);
                algorithm.setInputParameter("T", neighborSize);
                algorithm.setInputParameter("delta", deDelta);
                algorithm.setInputParameter("nr", updateNumber);
                algorithm.setInputParameter("isDisplay", isDisplay);
                algorithm.setInputParameter("paretoPath", paretoPath);
                algorithm.setInputParameter("epsilon", epsilon);
                algorithm.setInputParameter("lastGen", lastGen);
                algorithm.setInputParameter("srFactor", srFactor);

                algorithm.setInputParameter("normalize", true);

                // set constraint information
                String curProblem = problemStrings[i];
                String constraintInfo = mainPath + "/Constraint/" + curProblem + ".con";
                algorithm.setInputParameter("constraintInfo", constraintInfo);

                // Mutation operator
                parameters = new HashMap();
                parameters.put("probability", 1.0 / problem.getNumberOfVariables());
                parameters.put("distributionIndex", 20.0);
                mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
                algorithm.addOperator("mutation", mutation);


                //Crossover operator
                parameters = new HashMap();
                parameters.put("probability", 1.0);
                parameters.put("distributionIndex", 30.0); // 20 for less or equal than 3 objs, 30 for more than 3 objs
                crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover",parameters);

                // DE operator
//                parameters = new HashMap();
//                parameters.put("CR", DeCrossRate);
//                parameters.put("F", DeFactor);
//                crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

                algorithm.addOperator("crossover", crossover);


                parameters = null;
                //selection = SelectionFactory.getSelectionOperator("RandomSelection",parameters);

                // for constrainted multi-objective optimization problems
                selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

                // Selection Operator
//            parameters = null ;
//            selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;
                algorithm.addOperator("selection", selection);

                algorithm.setInputParameter("difcultyIndex", k);

                // each problem runs runtime times
                for (int j = 0; j < runtime; j++) {
                    algorithm.setInputParameter("runningTime", j);
                    // Execute the Algorithm
                    System.out.println("The " + j + " run of " + problemStrings[i]);
                    long initTime = System.currentTimeMillis();
                    algorithm.execute();
                    long estimatedTime = System.currentTimeMillis() - initTime;
                    // Result messages
                    System.out.println("Total execution time: " + estimatedTime + "ms");
                    System.out.println("Problem:  " + curProblem + "  running time:  " + j);
                }
            }
        }
    }

}
