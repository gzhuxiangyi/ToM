//  C-TaMOEAD_Settings.java 
// A Two-stage and Adaptive Decomposition-based Multi-objective Evolutionary Algorithm for Constrained and 
// Unconstrained Problems with Irregular Fronts


package jmetal.problems.CEC20;

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
public class CTsMOEAD_Settings extends Settings {
    public int popSize_ = 300;  	//210 for m = 5 // 156 for m = 8 // 275  for m = 10
    public int maxFES_ = 300000; 	//  4e5 for m = 5 // 5e5 for m = 8 // 6e5 for m = 10
    public int objNo_ = -1; 		// -1 for problems with less than 4 objs
    
    public int neighborSize_ = (int) Math.floor(0.1 * popSize_);
    public int updateNumber_ = 2;
    public double deDelta_ = 0.9;
    public double DeCrossRate_ = 0.9;
    public double DeFactor_ = 0.8;
    public String dataDirectory_;  //Weight path 
    public Boolean isDisplay_ = false;
    public double theta_      ;
    
    // cMOEAD_DRA parameters
    public double epsilon_ = 0.02;
    public int lastGen_ = 20;
    
    // MOEAD_SR parameters
    double srFactor_ = 0.5;
    
    public double mutationProbability_ ;
    public double crossoverProbability_     ;
    public double mutationDistributionIndex_  ;
    public double crossoverDistributionIndex_  ;
    
  /**
   * Constructor
   */
  public CTsMOEAD_Settings(String problem, Object[] problemParams) {
    super(problem);
    //-------------------------------------------
//    int pos = problem.indexOf("_");
//    String realProbName = problem.substring(0, pos);   
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
    	popSize_ = 100   ;    
    } else if(problem_.getNumberOfObjectives() == 3){	
    	popSize_ = 105   ; 
    }  else {
    	System.out.println("The number of objectives is undefined~~~");
    }
    
    maxFES_ = 100000 ;
    
    if (problem_.getName().equalsIgnoreCase("CMOP7") || problem_.getName().equalsIgnoreCase("CMOP8")
    		|| problem_.getName().equalsIgnoreCase("CMOP9") || problem_.getName().equalsIgnoreCase("CMOP10")) {
    	
    	maxFES_ = 200000 ;
    }
	
    mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
    crossoverProbability_        = 0.95   ;
    mutationDistributionIndex_   = 20.0  ;
    
    if (problem_.getNumberOfObjectives() <= 3) 
    	crossoverDistributionIndex_  = 20.0  ;
    else 
    	crossoverDistributionIndex_  = 30.0  ;
    
    neighborSize_ =  (int) Math.floor(0.1 * popSize_);    // 
    deDelta_ = 0.9;
        
    if (problem_.getNumberOfObjectives() >= 5) {
    	theta_  = 10.0;
    	updateNumber_ = 1; // For many-objective, 1 is suggested
    } else {
    	theta_  = 5.0;
    	updateNumber_ = 2; // 
    }
    
    dataDirectory_ =  "./weight" ;
  } // C-TaMOEAD_Settings
  
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
//    algorithm = new CTsMOEADR1(problem_);
//    algorithm = new CTaMOEAD(problem_);
//  algorithm = new CTsMOEADR1F1(problem_);
//  algorithm = new CTsMOEADR1F2(problem_);
//  algorithm = new CTsMOEADR1Release(problem_);
//  algorithm = new CTsMOEADR1ReleaseF2(problem_);
//    algorithm = new CTsMOEADR1ReleaseCDP(problem_);
    algorithm = new CTsMOEADR1ReleaseEps(problem_);
//    algorithm = new CTsMOEADR1ReleaseF2OneStage(problem_);
//    algorithm = new GDE3(problem_);
//    algorithm = new NSGAII_CDP(problem_);
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", popSize_);
    algorithm.setInputParameter("maxEvaluations", maxFES_);
    algorithm.setInputParameter("dataDirectory", dataDirectory_);
    algorithm.setInputParameter("T", neighborSize_);
    algorithm.setInputParameter("delta", deDelta_);
    algorithm.setInputParameter("nr", updateNumber_);
    algorithm.setInputParameter("isDisplay", isDisplay_);
    algorithm.setInputParameter("epsilon", epsilon_);
    algorithm.setInputParameter("lastGen", lastGen_);
    algorithm.setInputParameter("srFactor", srFactor_);
    algorithm.setInputParameter("theta",theta_);   
    
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
    algorithm.addOperator("crossover", crossover);
    
    // DE operator
    parameters = new HashMap();
    parameters.put("CR", DeCrossRate_);
    parameters.put("F", DeFactor_);
    //parameters.put("DE_VARIANT", "current-to-rand/1/bin");
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);
    algorithm.addOperator("crossover", crossover);

    parameters = null;
//    selection = SelectionFactory.getSelectionOperator("RandomSelection",parameters);
    // for constrainted multi-objective optimization problems
    selection = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", parameters);
    // Selection Operator
	//parameters = null ;
//	selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;
    algorithm.addOperator("selection", selection);

    return algorithm;
  } // configure

} // MOEAD_Settings
