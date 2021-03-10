//  NSGAII_SP.java
// Constraint Handling in Multiobjective Evolutionary Optimization
// Yonas Gebre Woldesenbet, Gary G. Yen, Fellow, IEEE, and Biruk G. Tessema

package jmetal.CHTs.ToM;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PureDominanceRanking;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;


public class NSGAII_SP extends Algorithm {
    /**
     * Constructor
     * @param problem Problem to solve
     */
    public NSGAII_SP(Problem problem) {
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

        System.out.println("NSGAII-SP maxEvaluations = " +  maxEvaluations_);		
		System.out.println("NSGAII-SP popsize = " +  populationSize_ );		
				
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
            TransferObjectives(union_);                 
            // Ranking the union
            PureDominanceRanking ranking = new PureDominanceRanking(union_);

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

            //reset objectives    
            resetObjectives(population_);
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
    
    
    /**
     * Transfer objectives
     * @param union
     */
    public void TransferObjectives(SolutionSet union) {
    	double [][] d = new double[union.size()][problem_.getNumberOfObjectives()];
    	double [][] p = new double[union.size()][problem_.getNumberOfObjectives()];
    	double [][] normalizedF = new double[union.size()][problem_.getNumberOfObjectives()];
    	
    	double rf = union.GetFeasible_Ratio();
    	
    	if (rf == 0) { // All are infeasible
    		
    		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) { // for each objective
    			
    			for (int k = 0; k < union.size();k++) {
    				d[k][i] = Math.abs(union.get(k).getOverallConstraintViolation());
    			}
    		}
    		
    	} else { // Some are feasible 
    		
    		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) { // for each objective
    			double fmin = Double.MAX_VALUE;
    			double fmax = - Double.MAX_VALUE;
    		
    			for (int k = 0; k  < union.size();k++)  {
    				double value = union.get(k).getObjective(i);    				
    				if (value < fmin) 
    					fmin = value;
    				
    				if (value > fmax) 
    					fmax = value;    				
    			} // for k
    			
    			for (int k = 0; k  < union.size();k++)  {
    				
    				normalizedF[k][i] = (union.get(k).getObjective(i) - fmin)/(fmax - fmin);    				
    				double CV = Math.abs(union.get(k).getOverallConstraintViolation());    				
    				d[k][i]  = Math.sqrt(normalizedF[k][i] * normalizedF[k][i] + CV * CV);
    			}
    			
    		} // for i
    	} // if 
    	
    	
    	for (int i = 0; i < problem_.getNumberOfObjectives(); i++) { // for each objective
    		double Xi,Yi;
    		
    		for (int k = 0; k  < union.size();k++)  {
    			
    			if (rf == 0) 
    				Xi = 0;
    			else
    				Xi = Math.abs(union.get(k).getOverallConstraintViolation());    	
    			
    			if (Math.abs(union.get(k).getOverallConstraintViolation()) == 0.0) 
    				Yi = 0;
    			else
    				Yi = normalizedF[k][i];
    			
    			p[k][i] = (1-rf) * Xi + rf * Yi;
    		} // for k
    	}
    	
    	for (int k = 0; k  < union.size();k++)  {
    		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
    			// Temporarily store the original objectives into NormalizedObjective
    			union.get(k).setNormalizedObjective(i, union.get(k).getObjective(i)); 
    			union.get(k).setObjective(i, d[k][i] + p[k][i]);
    		}
    	}
    } // TransferObjectives
    
    public void resetObjectives (SolutionSet pop) {
	    for (int k = 0; k < population_.size();k++) {
	    	for (int i = 0 ;i < problem_.getNumberOfObjectives();i++) {
	    		double originalObj = population_.get(k).getNormalizedObjective(i);
	    		population_.get(k).setObjective(i, originalObj);
	    	}
	    	
	    }
    }
    
} // NSGAII_SP
