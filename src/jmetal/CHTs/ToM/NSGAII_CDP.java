//  NSGAII.java
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

package jmetal.CHTs.ToM;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;



public class NSGAII_CDP extends Algorithm {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public NSGAII_CDP(Problem problem) {
        super (problem) ;
    } // NSGAII

    SolutionSet allPop;
    int populationSize_;
    int maxEvaluations_;
    int evaluations_;
    SolutionSet population_;
    SolutionSet offspringPopulation_;
    SolutionSet union_;
    Operator mutationOperator_;
    Operator crossoverOperator_;
    Operator selectionOperator_;
    boolean isDisplay_;

    String methodName_;

    /**
     * Runs the NSGA-II algorithm.
     * @return a <code>SolutionSet</code> that is a set of non dominated solutions
     * as a result of the algorithm execution
     * @throws JMException
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {

//        int runningTime  = ((Integer)this.getInputParameter("runningTime")).intValue() + 1;
//        int dif_index      = ((Integer)this.getInputParameter("difcultyIndex")).intValue() + 1; // start from 1


        Distance distance = new Distance();

        //Read the parameters
        populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();
//        methodName_      = this.getInputParameter("AlgorithmName").toString();

        System.out.println("NSGAII maxEvaluations = " +  maxEvaluations_);		
		System.out.println("NSGAII popsize = " +  populationSize_ );		
		
		
        isDisplay_ = (Boolean)this.getInputParameter("isDisplay");

        //Initialize the variables
        population_ = new SolutionSet(populationSize_);
        evaluations_ = 0;


        //Read the operators
        mutationOperator_  = operators_.get("mutation");
        crossoverOperator_ = operators_.get("crossover");
        selectionOperator_ = operators_.get("selection");

        // Create the initial solutionSet
        Solution newSolution;
        for (int i = 0; i < populationSize_; i++) {
            newSolution = new Solution(problem_);
            problem_.evaluate(newSolution);
            problem_.evaluateConstraints(newSolution);
            evaluations_++;
            population_.add(newSolution);
        } //for

        allPop  = population_;

        //creat database
//        String problemName = problem_.getName() +"_" + Integer.toString(dif_index) + "_" + Integer.toString(runningTime);
//        SqlUtils.CreateTable(problemName,methodName_);

        // Generations
        while (evaluations_ < maxEvaluations_) {

            // Create the offSpring solutionSet
            offspringPopulation_ = new SolutionSet(populationSize_);
            for (int i = 0; i < (populationSize_ / 2); i++) {
                //obtain parents
                Solution[] offSpring = new Solution[2];
                // Apply Crossover for Real codification
                if(crossoverOperator_.getClass().getSimpleName().equalsIgnoreCase("SBXCrossover")){
                    Solution[] parents = new Solution[2];
                    parents[0] = (Solution) selectionOperator_.execute(population_);
                    parents[1] = (Solution) selectionOperator_.execute(population_);
                    offSpring = ((Solution[])crossoverOperator_.execute(parents));
                }
                // Apply DE crossover
                else if(crossoverOperator_.getClass().getSimpleName().equalsIgnoreCase("DifferentialEvolutionCrossover")){
                    Solution[] parents = new Solution[3];
                    parents[0] = (Solution) selectionOperator_.execute(population_);
                    parents[1] = (Solution) selectionOperator_.execute(population_);
                    parents[2] = parents[0];
                    offSpring[0] = (Solution) crossoverOperator_.execute(new Object[]{parents[0], parents});
                    offSpring[1] = (Solution) crossoverOperator_.execute(new Object[]{parents[1], parents});
                }
                else {
                    System.out.println("unknown crossover" );

                }
                mutationOperator_.execute(offSpring[0]);
                mutationOperator_.execute(offSpring[1]);
                problem_.evaluate(offSpring[0]);
                problem_.evaluateConstraints(offSpring[0]);
                problem_.evaluate(offSpring[1]);
                problem_.evaluateConstraints(offSpring[1]);
                offspringPopulation_.add(offSpring[0]);
                offspringPopulation_.add(offSpring[1]);
                evaluations_ += 2;
            } // for

            // Create the solutionSet union of solutionSet and offSpring
            union_ = population_.union(offspringPopulation_);

            //ENS_SS_Ranking ranking = new ENS_SS_Ranking(union_);

            // Ranking the union
            Ranking ranking = new Ranking(union_);

            int remain = populationSize_;
            int index = 0;
            SolutionSet front = null;
            population_.clear();

            // Obtain the next front
            front = ranking.getSubfront(index);

            while ((remain > 0) && (remain >= front.size())) {
                //Assign crowding distance to individuals
                distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                //Add the individuals of this front
                for (int k = 0; k < front.size(); k++) {
                    population_.add(front.get(k));
                } // for

                //Decrement remain
                remain = remain - front.size();

                //Obtain the next front
                index++;
                if (remain > 0) {
                    front = ranking.getSubfront(index);
                } // if
            } // while

            // Remain is less than front(index).size, insert only the best one
            if (remain > 0) {  // front contains individuals to insert
                distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                front.sort(new CrowdingComparator());
                for (int k = 0; k < remain; k++) {
                    population_.add(front.get(k));
                } // for
            } // if

        } // while

        SolutionSet finalSet = removeSame(population_);    
//        SqlUtils.InsertSolutionSet(problemName,population_);
        // Return the first non-dominated front
        Ranking ranking = new Ranking(finalSet);
        return ranking.getSubfront(0);
    } // execute
    
    public SolutionSet removeSame(SolutionSet pop) {
  	  // At last remove identical solutions 
  	    SolutionSet finalSet = new SolutionSet(pop.size());        
  	    finalSet.add(pop.get(0));        
  	  
  	    for (int i = 1; i < pop.size(); i++) {      
  			      
  			Solution sol = pop.get(i);
  			boolean existEqual = false;
  			
  			for (int j = 0; j < finalSet.size();j++) {
  				if (equalSolution(sol, finalSet.get(j))) {
  					existEqual = true;
  					break;	    		
  				}
  			}
  			
  			if (existEqual == true) continue;
  		
  			finalSet.add(pop.get(i));	
  			
  		} // for	
  	    return finalSet;
    }
    
    public boolean equalSolution (Solution sol1, Solution sol2) {
  		
  		if (sol1.getNumberOfViolatedConstraint() !=sol2.getNumberOfViolatedConstraint()) { // Լ������
  			return false; // ���ز���
  		}
  		
  		for (int i = 0; i < sol1.getNumberOfObjectives();i++) {
  			if (sol1.getObjective(i) != sol2.getObjective(i))
  				return false;
  		}
  		
  		return true;
  	}
} // NSGA-II
