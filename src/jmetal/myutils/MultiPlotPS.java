/**
 * This class is designed to plot pareto fronts for a given problem
 */
package jmetal.myutils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jmetal.core.Problem;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Configuration;

/**
 * @author Yi Xiang
 *
 */
public class MultiPlotPS {
	private String trueParetoFrontPath_ = "";//The true pareto front path
	private String[] approximateParetoFrontPath_ ;//The approximate pareto front path
	private String figurePath_ = "";//Path to store the figures
	private MetricsUtil meticsUtil_ = new MetricsUtil();
	private String problemName_ = "";
	private Problem  problem_;
	private String[] property_;
	private String[] algNames_;
	private int objectives_;
	/**
	 * 
	 */
	public MultiPlotPS(String trueParetoFrontPath,String[] approximateParetoFrontPath,
			String figurePath,String problemName,String[] algNames,String[] property,int objectives ) {
		trueParetoFrontPath_ = trueParetoFrontPath;
		approximateParetoFrontPath_ = approximateParetoFrontPath;
		figurePath_ = figurePath;
		problemName_ = problemName;
		algNames_ = algNames;
		property_ = property;
		objectives_ = objectives;
		// TODO Auto-generated constructor stub
	}
	
	public void plotFront(){
		double [][] trueFront = meticsUtil_.readFront(trueParetoFrontPath_);	
		int rowsTrueFront = trueFront.length;
		int colTrueFront = trueFront[0].length;
		String strToWrite ;	
		
		try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(figurePath_)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;
		      /**
		       * Plot figure of the true front
		       */
		      bw.write("clear ");
		      bw.newLine();
		      bw.write("clc ");
		      bw.newLine();
		      bw.write("figure ");
		      bw.newLine();
		      
		      // PS中不需要绘制front
//		      bw.write("trueFront = [ ");		      
//		      for(int i=0;i<rowsTrueFront;i++){
//		    	  strToWrite ="";
//		    	  for(int j=0;j<colTrueFront;j++){
//		    		  strToWrite = strToWrite + String.valueOf(trueFront[i][j])+" ";
//		    	  }
//		    	  bw.write(strToWrite);
//		    	  bw.newLine();
//		      }
//		        //if (this.vector[i].getFitness()<1.0) {
//		        bw.write("];");
//		        bw.newLine();
		      // PS中不需要绘制front  --- end
		      
		      
		        // old 
//		        bw.write("set(0,'units','centimeters')");
//		        bw.newLine();
//		        bw.write("position=[0 0 17 6.5];");
//		        bw.newLine();
		        
		        //new
//		        bw.write("set(gca,'Unit','pixels');");
//		        bw.newLine();
		        
		        bw.write("set(gcf,'Position',[500 200 350 350])"); // 设置图像显示位置
		        bw.newLine();
		        
		        bw.write("if get(gca,'Position') <= [inf inf 400 300]");
		        bw.newLine();
		        bw.write("    Size = [3 5 .8 25];");
		        bw.newLine();
		        bw.write("else");
		        bw.newLine();
		        bw.write("    Size = [6 3 2 25];");
		        bw.newLine();
		        bw.write("end");
		        bw.newLine();
		        
		        bw.write("set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',Size(4),'LineWidth',1.3);");
		        bw.newLine();
		        if(objectives_==2){
//		        	bw.write("plot(trueFront(:,1),trueFront(:,2),'bo','LineWidth',1,'MarkerSize',2)");
		           	bw.newLine();	        	
		        	//bw.write("CreateParetoFrontSpherePSD2();");
		        	
		           	bw.newLine();
		            bw.write("set(gca,'XTickMode','auto','YTickMode','auto');");
		            bw.newLine();		            
		            bw.write("set(gca,'xminortick','on');"); 
		            bw.newLine();
		            bw.write("axis tight;axis equal;");
		        }
		        else if(objectives_==3){
		        	//bw.write("CreateParetoFrontSpherePS();");
		        	bw.newLine();	  
		        	// 绘制出3维PS
		        	bw.write("[fia,theta]=meshgrid([linspace(0,pi/2,20),pi/2]);");bw.newLine();	  
					bw.write("x=sin(theta).*cos(fia);");bw.newLine();
					bw.write("y=sin(theta).*sin(fia);");bw.newLine();
					bw.write("z=cos(theta);");bw.newLine();					
					bw.write("handle_p = mesh(x,y,z);");bw.newLine();
					bw.write("set(handle_p,'EdgeColor','b');");bw.newLine();
					bw.write("alpha(handle_p,0);");bw.newLine();
		        	
		           	bw.newLine();
		           	
		        	bw.write("xlabel('\\itf\\rm_1'); ylabel('\\itf\\rm_2'); zlabel('\\itf\\rm_3');");
		        	bw.newLine();
		            bw.write("set(gca,'XTickMode','auto','YTickMode','auto','ZTickMode','auto','View',[80 20]);");
		            bw.newLine();		            
		            bw.write("axis tight;");
		             
		        }	
		        
		        bw.newLine();
		        bw.write("hold on");
		        bw.newLine();
		       
		        
		        for(int frontIndex=0;frontIndex<approximateParetoFrontPath_.length;frontIndex++) {
		        	
			        double [][] approximateFront = meticsUtil_.readFront(approximateParetoFrontPath_[frontIndex]);
					int rowsApproximateFront = approximateFront.length;
					int colApproximateFront = approximateFront[0].length;					        
				       
				      /**
				       * Plot figure of the Approximate front
				       */
					    String frontName = "approximateFront" + frontIndex;
				        bw.write(frontName +" = [ ");
					      
					      for(int i=0;i<rowsApproximateFront;i++){
					    	  strToWrite ="";
					    	  for(int j=0;j<colApproximateFront;j++){
					    		  strToWrite = strToWrite + String.valueOf(approximateFront[i][j])+" ";
					    	  }
					    	  bw.write(strToWrite);
					    	  bw.newLine();
					      }
					        //if (this.vector[i].getFitness()<1.0) {
					        bw.write("];");
					        bw.newLine();
					        bw.newLine();
					        // 已收敛和未收敛的解采用不同颜色绘制
					        
					        //--------------------------------- Matlab code begin-----------------------
//					        flag = sum(approximateFront0(:,1:3).^2,2) >=0.98 & sum(approximateFront0(:,1:3).^2,2) <=1.02;
//					        convergedPS = approximateFront0(flag,:);
//					        plot3(convergedPS(:,1),convergedPS(:,2),convergedPS(:,3),'ko','LineWidth',1,'MarkerSize',6,'MarkerfaceColor','k')
//					        hold on
//					        approximateFront0 = approximateFront0(~flag,:);
					        //--------------------------------- Matlab code end -----------------------
					        
					        bw.write("squre = sum(approximateFront0(:,1:" + objectives_ +").^2,2);"); bw.newLine(); 
					        bw.write("flag = squre >=0.98 & squre <=1.02;"); bw.newLine();
					        bw.write("convergedPS = approximateFront0(flag,:);"); bw.newLine();
					        					        
					        if(objectives_==2){
					        	
					        	bw.write("plot(convergedPS(:,1),convergedPS(:,2),'ko','LineWidth',1,'MarkerSize',6,'MarkerFaceColor', [1 1 1],'MarkerEdgeColor','k');");
					        	
					        	bw.newLine();
					        	bw.write("hold on ;"); bw.newLine();
					        	bw.write("approximateFront0 = approximateFront0(~flag,:);"); bw.newLine();
					        	bw.write("disp ('Number of converged solutions:')");bw.newLine();
					        	bw.write("disp (sum(flag))");bw.newLine();
					        	bw.newLine(); bw.newLine();	
					        	
					        	String temp = "plot("+frontName +"(:,1),"+ frontName+"(:,2),";
					        	
					        	temp = temp + "'" + property_[frontIndex] + "'";
//					        	temp = temp + ",'LineWidth',1,'MarkerSize',4)"; // 					        	

					        	temp = temp + ",'LineWidth',1,'MarkerSize',6,'MarkerFaceColor', [1 1 1],'MarkerEdgeColor','k')"; // PS换一种颜色
					        	
					        	bw.write(temp);
					        	
					        }else if(objectives_==3){
					        	
					        	bw.write("plot3(convergedPS(:,1),convergedPS(:,2),convergedPS(:,3),'ko','LineWidth',1,'MarkerSize',6,'MarkerfaceColor','k');");
					        	bw.newLine();
					        	bw.write("hold on ;"); bw.newLine();
					        	bw.write("approximateFront0 = approximateFront0(~flag,:);"); bw.newLine();
					        	bw.write("disp ('Number of converged solutions:')");bw.newLine();
					        	bw.write("disp (sum(flag))");bw.newLine();
					        	bw.newLine(); bw.newLine();		        	
					        	// old 
					        	String temp = "plot3("+frontName+"(:,1),"+frontName+"(:,2),"+frontName+"(:,3),";
					        	temp = temp + "'" + property_[frontIndex]  + "'";
					        	
					        	// old code start
//					        	temp = temp + ",'LineWidth',1.5,'MarkerSize',5, 'MarkerFaceColor',";
//					        	temp = temp + "'" + property_[frontIndex].substring(0, 1)+"' )";
					        	// old code end
					        	
					        	temp = temp + ",'LineWidth',1,'MarkerSize',6,'MarkerFaceColor',"
					        			+ "[1 1 1],'MarkerEdgeColor','r')"; // PS
					        						        	
					        	bw.write(temp);
					        	bw.newLine();
					        	bw.write("grid on");
					        	bw.newLine();
					        	bw.write("view([80 20])"); // 135 45
					        	
//					        	bw.write("varargin = {'ok','MarkerSize',Size(2),'Marker','o','Markerfacecolor',[.7 .7 .7],"
//					        			+ "'Markeredgecolor',[.4 .4 .4]};");
					        	
					        	//---------------------------------
//					        	bw.write("varargin = {'ok','MarkerSize',Size(2),'Marker','o','Markerfacecolor','k',"
//					        			+ "'Markeredgecolor','k'};");
//					        	bw.newLine();
//					        	String temp = "plot3("+frontName+"(:,1),"+frontName+"(:,2),"+frontName+"(:,3),varargin{:});";					        						        						        	
//					        	bw.write(temp);
					        	//---------------------------------
					        }
					bw.newLine();
		        }//for 
		        
		         // ---------------------PS---------------------------------------------------
			        bw.newLine();
			        bw.write(" xl = xlabel('\\it x \\rm_1','fontname','Times New Roman');");
			        bw.newLine();
			        bw.write("set(xl,'fontsize',25)");
			        bw.newLine();
			        bw.write(" yl = ylabel('\\it x \\rm_2','fontname','Times New Roman');");
			        bw.newLine();
			        bw.write("set(yl,'fontsize',25)");
			        bw.newLine();
			     // ---------------------PS---------------------------------------------------
			        			        
			        if(objectives_==3){
			        	bw.write(" zl = zlabel('\\it x \\rm_3','fontname','Times New Roman');"); //PS
				        bw.newLine();
				        bw.write("set(zl,'fontsize',25)");
				        bw.newLine();
			        }
			        
			        bw.write("grid off; box off");
			        bw.newLine();
//			        String title = "Pareto front of problem " + problemName_;		
			        if(problemName_.contains("mQAP"))	{
				    	String newproblemName_ = problemName_.substring(4);
				    	 bw.write(" tit = title('Pareto front of problem "+newproblemName_+"');");
			        }else {				    	
//			        	bw.write(" tit = title('Pareto front of problem "+problemName_+"');"); // 以问题为标题
			        	bw.write(" tit = title(' " + algNames_[0] + "');"); // 以算法为标题
			        }
			        
			        bw.newLine();
			        bw.write("set(tit,'fontsize',20)");
			        bw.newLine();
			        bw.write("set(gca,'fontsize',20)");
			        bw.newLine();
			        
			        if (objectives_==2 ) {
			           	// 绘制出PS
			        	bw.write("[fia,theta]=meshgrid([linspace(0,pi/2,20),pi/2]);");bw.newLine();	
			        	bw.write("x=sin(theta);");bw.newLine();
	        			bw.write("z=cos(theta);");bw.newLine();
						bw.write("handle_p = plot(x,z,'b','linewidth',1.5);");bw.newLine();
			        }
			        
			        if(objectives_==2 || objectives_==3){
			        	// 图例
				        String lgd = "lgd = legend('PS',";
				        
				        // 以算法为图例
//				        for(int algID=0;algID<algNames_.length-1;algID++) {
//				        	lgd = lgd + "'" + algNames_[algID] + "',";
//				        }
//				        lgd = lgd + "'" + algNames_[algNames_.length-1] + "');";
				     // 以算法为图例
				        
				        lgd = lgd + "'C','UC');";				        		
				        bw.write(lgd);
				        bw.newLine();				        
				        
				        if (objectives_==2) {		  
				        	bw.write("set(lgd,'fontsize',13,'box','off');");			
				        }
				        else 
				        	bw.write("set(lgd,'fontsize',13,'box','off','Location', 'EastOutside')");				       
			         }	
			        
			        
			        bw.newLine(); bw.write("figure");   bw.newLine();
			        bw.write("plot(approximateFront0','linewidth',1.1,'color','k')"); bw.newLine();
			        bw.write("line([0 size(approximateFront0,2)],[0.2 0.2],'color','r',"
			        		+ "'linewidth',2.5,'linestyle','--') "); bw.newLine();
			        bw.write(" tit = title(' " + algNames_[0] + "');"); bw.newLine(); // 以算法为标题
			        bw.write("set(tit,'fontsize',18)");bw.newLine(); 
			        bw.write(" xl = xlabel('Variable No.');");bw.newLine(); 
			        bw.write("set(xl,'fontsize',18)");bw.newLine(); 
			        bw.write("yl = ylabel('Variable Value');");bw.newLine(); 
			        bw.write("set(yl,'fontsize',20)");bw.newLine(); 
			        bw.write("set(gca,'XLim',[1  size(approximateFront0,2)])");bw.newLine(); 
			        bw.write("set(gca,'XTick',1:1:size(approximateFront0,2))");bw.newLine(); 
			        bw.write("set(gca,'YTick',0:0.2:1)");bw.newLine(); 
			        bw.write(" set(gca,'fontsize',20)");bw.newLine(); 
			        
		      /* Close the file */
		      bw.close();
		    }catch (IOException e) {
		      Configuration.logger_.severe("Error acceding to the file");
		      e.printStackTrace();
		    }//catch
	}//plotFront

	public String getTrueParetoFrontPath_() {
		return trueParetoFrontPath_;
	}

	public void setTrueParetoFrontPath_(String trueParetoFrontPath_) {
		this.trueParetoFrontPath_ = trueParetoFrontPath_;
	}

	public String[] getApproximateParetoFrontPath_() {
		return approximateParetoFrontPath_;
	}

	public void setApproximateParetoFrontPath_(String[] approximateParetoFrontPath_) {
		this.approximateParetoFrontPath_ = approximateParetoFrontPath_;
	}

	public String getFigurePath_() {
		return figurePath_;
	}

	public void setFigurePath_(String figurePath_) {
		this.figurePath_ = figurePath_;
	}

	public MetricsUtil getMeticsUtil_() {
		return meticsUtil_;
	}

	public void setMeticsUtil_(MetricsUtil meticsUtil_) {
		this.meticsUtil_ = meticsUtil_;
	}

	public String getProblemName_() {
		return problemName_;
	}

	public void setProblemName_(String problemName_) {
		this.problemName_ = problemName_;
	}

	public Problem getProblem_() {
		return problem_;
	}

	public void setProblem_(Problem problem_) {
		this.problem_ = problem_;
	}

	public String[] getProperty() {
		return property_;
	}

	public void setProperty(String[] property) {
		this.property_ = property;
	}
	

}
