//  NSGAII_SR.java
//Infeasible Elitists and Stochastic Ranking Selection in
//Constrained Evolutionary Multi-objective Optimization
// Huantong Geng, Min Zhang, Linfeng Huang, and Xufa Wang
package jmetal.CHTs.ToM;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.PureDominanceRanking;
import jmetal.util.Ranking;
import jmetal.util.comparators.MinimizedCrowdingDistanceComparator;


public class NSGAII_SR extends Algorithm {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public NSGAII_SR(Problem problem) {
        super (problem) ;
    } // NSGAII

    SolutionSet allPop;
    int populationSize_;
    int maxEvaluations_;
    int evaluations_;
    SolutionSet population_;
    SolutionSet offspringPopulation_;
    SolutionSet union_;
    SolutionSet Pr_;
    SolutionSet nondominated_;
    
    double Pf_ = 0.45;
    
    Operator mutationOperator_;
    Operator crossoverOperator_;
    Operator selectionOperator_;
    boolean isDisplay_;
    Distance distance_  = new Distance();
    
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


       

        //Read the parameters
        populationSize_ = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations_ = ((Integer) getInputParameter("maxEvaluations")).intValue();
//        methodName_      = this.getInputParameter("AlgorithmName").toString();

        System.out.println("NSGAII-SR maxEvaluations = " +  maxEvaluations_);		
		System.out.println("NSGAII-SR popsize = " +  populationSize_ );		
				
        isDisplay_ = (Boolean)this.getInputParameter("isDisplay");

        //Initialize the variables
        population_   = new SolutionSet(populationSize_);
        Pr_           = new SolutionSet(2 * populationSize_);
        nondominated_ =  new SolutionSet(2 * populationSize_);
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
            fitness(union_);
            // Ranking the union
            environmentalSelection();
           
        } // while

        SolutionSet finalSet = removeSame(population_);    
//        SqlUtils.InsertSolutionSet(problemName,population_);
        // Return the first non-dominated front
        Ranking ranking = new Ranking(finalSet);
        return ranking.getSubfront(0);
    } // execute
    
    /**
     * Compute fitness
     * @param union
     */
    public void fitness(SolutionSet union) {
    	 PureDominanceRanking ranking = new PureDominanceRanking(union);

    	 int index = 0;
    	 SolutionSet front = null;
    	 nondominated_.clear();
    	 Pr_.clear();
    	 
    	 boolean addedNondominated = false;
    	 int addedIndex = -1;    	
    	 
         while (index < ranking.getNumberOfSubfronts()) {
        	 front = ranking.getSubfront(index);
             //Assign crowding distance to individuals
        
             distance_.crowdingDistanceAssignmentMin(front, problem_.getNumberOfObjectives()); // A modified crowding distance
            
             for (int k = 0; k < front.size(); k++) {
                front.get(k).setFitness(index+front.get(k).getCrowdingDistance());                 
             } // for
           
             if (!addedNondominated && isfeasible(front.get(0))) {
            	 
            	 for (int k = 0; k < front.size(); k++) {
            		 nondominated_.add(front.get(k));
                 } // for
            	 
            	 addedNondominated = true;
            	 addedIndex = index;
             }
             
             if (index != addedIndex) {
	             for (int k = 0; k < front.size(); k++) {
	            	 Pr_.add(front.get(k));
	             } // for
             }
             
             index ++;
         } // while       
    }
    
    /**
     * 
     */
    public void environmentalSelection () {
    	     	
    	population_.clear();
    	
    	if (nondominated_.size() > populationSize_) {
    		
    		nondominated_.sort(new MinimizedCrowdingDistanceComparator());
            for (int k = 0; k < populationSize_; k++) {
              population_.add(nondominated_.get(k));
            } // for
            
    	} else {
    		
    		// Add all non-dominated feasible solutions
    		for (int k = 0; k < nondominated_.size(); k++) {
                population_.add(nondominated_.get(k));
            } // for
    		
    		int remain = populationSize_ - population_.size();
    		
    		// Stochastic Ranking Selection
    		int Nr = Pr_.size();
    		
    		for (int K=1; K <= Nr; K++) {
    			
    			int sflag = 0;
    			
    			for (int i = 0; i < Nr - 1;i++) {
    				double u = PseudoRandom.randDouble();
    				
    				if ( (isfeasible(Pr_.get(i)) && isfeasible(Pr_.get(i+1))) || (u < Pf_) ) {
    					
    					if (Pr_.get(i).getFitness() > Pr_.get(i+1).getFitness()) {
    						// swap
    						Solution temp = new Solution(Pr_.get(i));
    						Pr_.replace(i, Pr_.get(i+1));
    						Pr_.replace(i+1,temp);
    						sflag = 1;
    					}
    					
    				} else {
    					
    					if (Math.abs(Pr_.get(i).getOverallConstraintViolation())
    							> Math.abs(Pr_.get(i+1).getOverallConstraintViolation()) ) {
    						// swap
    						Solution temp = new Solution(Pr_.get(i));
    						Pr_.replace(i, Pr_.get(i+1));
    						Pr_.replace(i+1,temp);
    						sflag = 1;
    					}
    				} 
    			} // for i
    			
    			if (sflag == 0) break;
    		} // for K
    		
    		if (remain > 0) {
    			for (int i = 0; i < remain;i++) {
    				population_.add(Pr_.get(i));
    			}
    		}
    	} // if     	

    } // environmentalSelection
    
    
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

	// Judge a solution is feasible or not
	public boolean isfeasible(Solution sol) {
		if (sol.getOverallConstraintViolation() == 0.0 && sol.getNumberOfViolatedConstraint() == 0) //
			return true;
		else 
			return false;
	}
    
} // NSGAII_SR
