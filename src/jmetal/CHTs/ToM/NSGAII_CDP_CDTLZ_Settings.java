//  NSGAII-CDP_Settings.java 

package jmetal.CHTs.ToM;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

/**
 * Settings class of algorithm MOEA/D
 */
public class NSGAII_CDP_CDTLZ_Settings extends Settings {
    public int popSize_ = 300;  	//210 for m = 5 // 156 for m = 8 // 275  for m = 10
    public int maxFES_ = 300000; 	//  4e5 for m = 5 // 5e5 for m = 8 // 6e5 for m = 10
    public int objNo_ = -1; 		// -1 for problems with less than 4 objs
    
    public double DeCrossRate_ = 1.0;
    public double DeFactor_ = 0.5;
    public Boolean isDisplay_ = false;
        
    public double mutationProbability_ ;
    public double crossoverProbability_     ;
    public double mutationDistributionIndex_  ;
    public double crossoverDistributionIndex_  ;
    
  /**
   * Constructor
   */
  public NSGAII_CDP_CDTLZ_Settings(String problem, Object[] problemParams) {
    super(problem);
    //-------------------------------------------
//  int pos = problem.indexOf("_");
//  String realProbName = problem.substring(0, pos);   
  //-------------------------------------------
  String realProbName = problem;
    try {
	    problem_ = (new ProblemFactory()).getProblem(realProbName, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      

    // Default experiments.settings
    /**
     * M=3, populationSize_ = 92; M=5, populationSize_ = 212(210); 
     * M=8, populationSize_ = 156; M=10, populationSize_ = 276(275); 
     * M=15, populationSize_ = 136(135);
     */
    
    if (problem_.getNumberOfObjectives() == 2) {
    	popSize_ = 80   ;	 
    	
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 1000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 250 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 750 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 750 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 1000 * popSize_ ;
    	}
    	
    } else if(problem_.getNumberOfObjectives() == 3){	
    	popSize_ = 92   ;  
 
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 1000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 250 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 750 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 750 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 1000 * popSize_ ;
    	}
    	
    }else if(problem_.getNumberOfObjectives() == 5){    	
    	popSize_ = 212   ;  
    	
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 600 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 1500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 350 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 1250 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 1250 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 1500 * popSize_ ;
    	}
    	
    }else if(problem_.getNumberOfObjectives() == 8){    	
    	popSize_ = 156   ;   
    	
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 800 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 2500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 2000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 2000 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 2500 * popSize_ ;
    	}
    	
    } else if(problem_.getNumberOfObjectives() == 10){    	    	
    	popSize_ = 276   ;  
    	
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 1000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 3500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 750 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 3000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 3000 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 3500 * popSize_ ;
    	}
    	
    } else if(problem_.getNumberOfObjectives() == 15){    	    	
    	popSize_ = 136   ;  
    	
    	if (problem_.getName().equalsIgnoreCase("C1DTLZ1")) {
    		maxFES_ = 1500 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C1DTLZ3")) {
    		maxFES_ = 5000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C2DTLZ2")) {
    		maxFES_ = 1000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ1")) {
			maxFES_ = 4000 * popSize_ ;
    	} else if (problem_.getName().equalsIgnoreCase("C3DTLZ4")) {
    		maxFES_ = 4000 * popSize_ ;
    	} else { // For DC*-DTLZ1 and DTLZ3
    		maxFES_ = 5000 * popSize_ ;
    	}
    }  else {
    	System.out.println("The number of objectives is undefined~~~");
    }
   
    // ----------------Parameters in genetic operators----------------------
    crossoverProbability_        = 0.9   ;
    mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
    crossoverDistributionIndex_  = 30.0  ;
    mutationDistributionIndex_   = 20.0  ;    
    // ----------------Parameters in genetic operators end-------------------
  } // NSGAII_CDP_Settings
  
  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new NSGAII_CDP(problem_);
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", popSize_);
    algorithm.setInputParameter("maxEvaluations", maxFES_);;
    algorithm.setInputParameter("isDisplay", isDisplay_);

    algorithm.setInputParameter("normalize", false);
      
    String constraintInfo =  "./Constraint/" + ".con";
    algorithm.setInputParameter("constraintInfo", constraintInfo);
    
    // Mutation operator
    parameters = new HashMap();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
    algorithm.addOperator("mutation", mutation);


    //Crossover operator
    parameters = new HashMap();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_); // 20 for less or equal than 3 objs, 30 for more than 3 objs
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    // DE operator
//    parameters = new HashMap();
//    parameters.put("CR", DeCrossRate);
//    parameters.put("F", DeFactor);
//    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    algorithm.addOperator("crossover", crossover);

    parameters = null;
    //selection = SelectionFactory.getSelectionOperator("RandomSelection",parameters);
    // for constrainted multi-objective optimization problems
//    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);
    // Selection Operator
	//parameters = null ;
	selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;
    algorithm.addOperator("selection", selection);

    return algorithm;
  } // configure

} // NSGAII_CDP_Settings
