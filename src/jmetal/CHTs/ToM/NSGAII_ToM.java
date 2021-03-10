/* NSGAII_ToM.java
 * 
 * Author:  Yi Xiang <xiangyi@scut.edu.cn> or <gzhuxiang_yi@163.com>  
 * 
 * Data: 10/03/2021
 * Copyright (c) 2021 Yi Xiang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package jmetal.CHTs.ToM;
import java.util.Comparator;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.myutils.RecordMetricDuringRun;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.PureDominanceRanking;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.EqualSolutions;


public class NSGAII_ToM extends Algorithm {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public NSGAII_ToM(Problem problem) {
        super (problem) ;
    } // NSGAII

    SolutionSet allPop;
    int populationSize_;
    int maxEvaluations_;
    int evaluations_;
    SolutionSet population_;
    SolutionSet offspringPopulation_;
    SolutionSet union_;
	private SolutionSet feasiblePopulation_;  // feasible population
	
	public double alpha_ = 10.0/100;
	
    Operator mutationOperator_;
    Operator crossoverOperator_;
    Operator selectionOperator_;
    boolean isDisplay_;

    private boolean stage2_ = false; // 
    
    Distance distance_ = new Distance();
    
    String methodName_;
    
	RecordMetricDuringRun rmdr; // our class 
    boolean rmdrFlag = true;    

    /**
     * Runs the NSGA-II algorithm.
     * @return a <code>SolutionSet</code> that is a set of non dominated solutions
     * as a result of the algorithm execution
     * @throws JMException
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {

//        int runningTime  = ((Integer)this.getInputParameter("runningTime")).intValue() + 1;
//        int dif_index      = ((Integer)this.getInputParameter("difcultyIndex")).intValue() + 1; // start from 1
       
		alpha_ = ((Double) this.getInputParameter("alpha")).doubleValue();			
		
		if (rmdrFlag== true) {
//			rmdr = new RecordMetricDuringRun("CMOEAStudyCDTLZParameter",problem_.getName(), 
//						"NSGAIIToMAlpha" + (alpha_),problem_.getNumberOfObjectives()); // For CMOEAStudyCDTLZ 
			
			rmdr = new RecordMetricDuringRun("CMOEAStudyMW",problem_.getName(), 
					"NSGAIIToMLargeEVs",problem_.getNumberOfObjectives()); // For CMOEAStudyMW 
			
//			rmdr = new RecordMetricDuringRun("CMOEAStudyDASParameter",problem_.getFullProblemName(), 
//					"NSGAIIToMAlpha" + (alpha_),problem_.getNumberOfObjectives()); // For CMOEAStudyDAS 
		}
		
    	long elapsed = 0, start = System.currentTimeMillis(); // start
    	
        //Read the parameters
        populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();
//        methodName_      = this.getInputParameter("AlgorithmName").toString();
		int maxGen = maxEvaluations_/populationSize_;
		alpha_ = alpha_/100;
		
        System.out.println("NSGAII-ToM maxEvaluations = " +  maxEvaluations_);		
		System.out.println("NSGAII-ToM popsize = " +  populationSize_ );		
		System.out.println("NSGAII-ToM  alpha_ = " +  alpha_ );	
		
        isDisplay_ = (Boolean)this.getInputParameter("isDisplay");

        //Initialize the variables
        population_ = new SolutionSet(populationSize_);
        
        //Initialize feasible solutions
		feasiblePopulation_ = new SolutionSet(populationSize_);		
		
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
            
            if (isfeasible(newSolution)) {				
				feasiblePopulation_.add(newSolution);
			}
            
        } //for

        //creat database
//        String problemName = problem_.getName() +"_" + Integer.toString(dif_index) + "_" + Integer.toString(runningTime);
//        SqlUtils.CreateTable(problemName,methodName_);

        int infeasibleGen = 0;
        
        // Generations
        while (evaluations_ < maxEvaluations_) {
			
			if (this.population_.GetFeasible_Ratio() == 0) {
				infeasibleGen ++;
			}
			else {
				infeasibleGen = 0;
			}
			
        	offspringPopulation_ =  createOffspring (); // Generate offspring population 

            // Create the solutionSet union of solutionSet and offSpring
            union_ = population_.union(offspringPopulation_);
            
            environmentalSelection(union_);            
            maintainFeasibleSolutions(population_);
            
			if ((!stage2_  && (evaluations_ >= 0.5 * this.maxEvaluations_ )) ||  (!stage2_ && infeasibleGen >= (alpha_ * maxGen))) {
				
				if (infeasibleGen >= (alpha_ * maxGen))
					System.out.println("-----------------Terminated in advance--------------------");			
                   	
				int interSize = intersectionSize(population_,feasiblePopulation_);
				
//				int interSize = countFeasible(population_);
				
				System.out.println("feasibleSize = " + feasiblePopulation_.size());
				System.out.println("" + "interSize = " + interSize);
								
				int problemType = 0;						
				
				if (interSize >= 0.9 * populationSize_) {
					problemType = 1;
					System.out.println("Type I");
				} else if (interSize == 0) {
					problemType = 3;
					System.out.println("Type III");
				} else {
					problemType = 2;
					System.out.println("Type II");
				}
							
				if (problemType == 2 ) {
					reconstructPopulation();
				} else if (problemType == 3 ) {
					reconstructPopulation();
				} // 
									
				System.out.println("---Stage 2---");
				stage2_ = true;			
				
				//---------------Record type ------------------	
				if (rmdrFlag== true) { 	
					rmdr.writeMetric("Type", problemType);	
				} // if 
				
            }  //if 
            
        } // while

        elapsed = System.currentTimeMillis() - start;		
        
        System.out.println("RunTimeMS: " + elapsed);
        System.out.println("evaluations: " + evaluations_);
        
        reconstructPopulation();
        Ranking ranking = new Ranking(population_);
        System.out.println("Final size = " + ranking.getSubfront(0).size());
        return ranking.getSubfront(0);   
        
    } // execute
    
    
   	 public int intersectionSize(SolutionSet population,SolutionSet feasiblePopulation) {
		 int size = 0;		 
		 Comparator   equals  = new EqualSolutions();
		 		 
		 for (int i = 0; i < population.size(); i++) {
			 		 
			 boolean equal = false;
			 
			 for (int j = 0; j < feasiblePopulation.size();j++) {
				 
			 	 if (equals.compare(population.get(i),feasiblePopulation.get(j)) == 0) {
			 		equal = true; break;
				 }
					 
			 }
			
			 if (equal)  size++;
		 }
		 
		 return size;
	 }
    
   	public int countFeasible(SolutionSet population) {
		 int number = 0;		
		 
		 for (int i = 0; i < population.size(); i++) {
			 if (isfeasible(population.get(i))) 
				 number++;
		 }
		 
		 return number;
	 }
   	
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
    
    
    public SolutionSet createOffspring () throws JMException {
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
        
        return offspringPopulation_;
    }
    
    
    public void environmentalSelection(SolutionSet union) {
    	if (stage2_) {
    		population_  =  constrainedDominaceSelection(union);
    	} else {
    		population_  = pureDominaceSelection(union); // Use only objectives in Stage1 
    	}
    }
    
    /**
     * Comparing based on first constraints and then objectives
     * @param union
     */
    public SolutionSet constrainedDominaceSelection(SolutionSet union) {
    	
    	SolutionSet population = new SolutionSet(populationSize_);
        // Ranking the union
        Ranking ranking = new Ranking(union);        
        
        int remain = populationSize_;
        int index = 0;
        SolutionSet front = null;
        population.clear();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
            //Assign crowding distance to individuals
            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            //Add the individuals of this front
            for (int k = 0; k < front.size(); k++) {
                population.add(front.get(k));
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
            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            front.sort(new CrowdingComparator());
            for (int k = 0; k < remain; k++) {
                population.add(front.get(k));
            } // for
        } // if
        
        return population;
    }
    
 public SolutionSet pureDominaceSelection(SolutionSet union) {
	    SolutionSet population = new SolutionSet(populationSize_);
        // Ranking the union
	    PureDominanceRanking ranking = new PureDominanceRanking(union);      
        
        int remain = populationSize_;
        int index = 0;
        SolutionSet front = null;
        population.clear();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
            //Assign crowding distance to individuals
            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            //Add the individuals of this front
            for (int k = 0; k < front.size(); k++) {
                population.add(front.get(k));
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
            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            front.sort(new CrowdingComparator());
            for (int k = 0; k < remain; k++) {
                population.add(front.get(k));
            } // for
        } // if
        
        return population;
    }
 
    public void maintainFeasibleSolutions(SolutionSet union) {
	   SolutionSet feasibleSolutions = new SolutionSet(union.size()); // Store feasible solutions in union
	   
	   // Find feasible solutions
	   for(int i = 0; i < union.size();i++) {
		   Solution s = union.get(i);
		   
		   if (isfeasible(s)) {
			   feasibleSolutions.add(s);
		   }
	   } //

	   if (feasibleSolutions.size() == 0) {// Have no feasible solutions 
		   return;
	   } else {
		   if (feasibleSolutions.size() + feasiblePopulation_.size() <= populationSize_) {
			   feasiblePopulation_ = feasiblePopulation_.union(feasibleSolutions);	
		   } else {			   
			  
			   PureDominanceRanking ranking = new PureDominanceRanking(feasiblePopulation_);     
			   
			   feasiblePopulation_.clear();
			   
			   // Add first members in feasibleSolutions 
			   for (int k = 0; k < feasibleSolutions.size(); k++) {
				   feasiblePopulation_.add(feasibleSolutions.get(k));
	           } // for
			   
			    int remain = populationSize_ - feasibleSolutions.size();
		        int index = 0;
		        SolutionSet front = null;		    

		        // Obtain the next front
		        front = ranking.getSubfront(index);

		        while ((remain > 0) && (remain >= front.size())) {
		            //Assign crowding distance to individuals
		            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
		            //Add the individuals of this front
		            for (int k = 0; k < front.size(); k++) {
		            	feasiblePopulation_.add(front.get(k));
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
		            distance_.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
		            front.sort(new CrowdingComparator());
		            for (int k = 0; k < remain; k++) {
		            	feasiblePopulation_.add(front.get(k));
		            } // for
		        } // if		        	        
			   
//			   feasiblePopulation_ = pureDominaceSelection(feasiblePopulation_.union(feasibleSolutions));
		   }
	   }
	   
    }
 
	// Judge a solution is feasible or not
	public boolean isfeasible(Solution sol) {
		if (sol.getOverallConstraintViolation() == 0.0 && sol.getNumberOfViolatedConstraint() == 0) //
			return true;
		else 
			return false;
	}
	
	
	public void reconstructPopulation() {		
		
		if (feasiblePopulation_.size() == 0) return;
		
		population_.sort(new CrowdingComparator());
		feasiblePopulation_.sort(new CrowdingComparator());
		 
		int j = 0;
		//-----------------------------------------------------------------
		for (int i = populationSize_ - 1; i >= 0;i--) {
			
			if (!isfeasible(population_.get(i)) && j < feasiblePopulation_.size()) {
				population_.replace(i, feasiblePopulation_.get(j));	
				j++;
			}
		} // for i
		
	} // reconstructPopulation
	
} // NSGA-II
