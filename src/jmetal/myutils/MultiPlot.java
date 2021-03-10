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
public class MultiPlot {
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
	public MultiPlot(String trueParetoFrontPath,String[] approximateParetoFrontPath,
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
		      
//		      // 写入真实PF
//		      bw.write("trueFront = [ ");
//		      
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
		        	bw.write("plot(trueFront(:,1),trueFront(:,2),'ro','LineWidth',0.5,'MarkerSize',2)");
		           	bw.newLine();
		            bw.write("set(gca,'XTickMode','auto','YTickMode','auto');");
		            bw.newLine();		            
		            bw.write("set(gca,'xminortick','on');"); 
		            bw.newLine();
//		            bw.write("axis tight;axis equal;");
		        }
		        else if(objectives_==3){
//		        	bw.write("plot3(trueFront(:,1),trueFront(:,2),trueFront(:,3),'r.','LineWidth',1,'MarkerSize',2)");
//		        	bw.newLine();
		        	bw.write("%CreateParetoFrontPlant");bw.newLine();
		        	bw.write("%CreateParetoFrontSphere");bw.newLine();
		        	bw.write("%CreateParetoFrontInverseSphere");bw.newLine();
		        	bw.write("%CreateParetoFrontMixed");bw.newLine();
		        	bw.write("%CreateParetoFrontConvexSphere");bw.newLine();
		        	bw.write("%CreateParetoFrontMinusSphere");bw.newLine();
		        	bw.write("%CreateParetoFrontScaledSphere");bw.newLine();
		        	
//		        	bw.write("%% --------------------");bw.newLine();
//		        	bw.write("p = 10; 	[fia,theta]=meshgrid([linspace(0,pi/2,p+1),pi/2]);");bw.newLine();
//		        	bw.write("x=sin(theta).*cos(fia);     	y=sin(theta).*sin(fia);");bw.newLine();
//		        	bw.write("z=cos(theta);");bw.newLine();
//		        	bw.write("q = 0.8;");bw.newLine();
//		        	bw.write("x = x.^q;  y = y.^q; z = z.^q;");bw.newLine();
//		        
//		        	bw.write("h = mesh(x,y,z,'linewidth',1.2);");bw.newLine();
//					bw.write("set(h,'EdgeColor','r')");bw.newLine();
//					bw.write("axis([0 1.2 0 1.2 0 1.2]) ");bw.newLine();
//					bw.write("alpha(h,0);");bw.newLine();
//					bw.write("%% --------------------");bw.newLine();
		        	
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
					        
					        if(objectives_==2){
					        	
					        	String temp = "plot("+frontName +"(:,1),"+ frontName+"(:,2),";
					        	
					        	temp = temp + "'" + property_[frontIndex] + "'";
//					        	temp = temp + ",'LineWidth',1,'MarkerSize',4)"; // 
					        	
//					        	temp = temp + ",'LineWidth',1,'MarkerSize',8,'MarkerFaceColor',"
//					        			+ "[.7 .7 .7],'MarkerEdgeColor','k')"; // PF边界颜色

					        	temp = temp + ",'LineWidth',1,'MarkerSize',7,'MarkerFaceColor',"
					        			+ "[0.7 0.7 0.7],'MarkerEdgeColor','k')"; // PS换一种颜色
					        	
					        	bw.write(temp);
					        	
					        }else if(objectives_==3){
					        	// old 
					        	String temp = "plot3("+frontName+"(:,1),"+frontName+"(:,2),"+frontName+"(:,3),";
					        	temp = temp + "'" + property_[frontIndex]  + "'";
					        	
					        	// old code start
//					        	temp = temp + ",'LineWidth',1,'MarkerSize',6, 'MarkerFaceColor',";
//					        	temp = temp + "'" + property_[frontIndex].substring(0, 1)+"' )";
					        	
//					        	temp = temp + ",'LineWidth',1.5,'MarkerSize',6, 'MarkerEdgeColor',";
//					        	temp = temp + "'" + property_[frontIndex].substring(0, 1)+"');" ;
//					        	                 + ",'MarkerFaceColor'" + ",[1 1 1]);";
					        	
					        	// old code end
					        	if (frontIndex == 0) {
						        	temp = temp + ",'LineWidth',1,'MarkerSize',6,'MarkerFaceColor',"
						        			+ "[0.7 0.7 0.7],'MarkerEdgeColor','k')"; // 有填充色
					        	} else {					        	
						        	
						        	temp = temp + ",'LineWidth',1,'MarkerSize',6,'MarkerFaceColor','r','MarkerEdgeColor','k')"; // 无填充色
//						        	temp = temp + ",'LineWidth',1.5,'MarkerSize',6, 'MarkerEdgeColor',";
//						        	temp = temp + "'" + property_[frontIndex].substring(0, 1)+"');" ;
					        		
//					            	temp = temp + ",'LineWidth',1,'MarkerSize',6,'MarkerFaceColor',"
//						        			+ "[0.0 0.0 0.0],'MarkerEdgeColor','k')"; // 有填充色
						        	
					        	}
					        						        	
					        	bw.write(temp);
					        	bw.newLine();
					        	bw.write("grid on");
					        	bw.newLine();
					        	bw.write("view([135 20])"); // 135 45
					        	
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
//			        bw.newLine();
//			        bw.write(" xl = xlabel('\\it x \\rm_1','fontname','Times New Roman');");
//			        bw.newLine();
//			        bw.write("set(xl,'fontsize',25)");
//			        bw.newLine();
//			        bw.write(" yl = ylabel('\\it x \\rm_2','fontname','Times New Roman');");
//			        bw.newLine();
//			        bw.write("set(yl,'fontsize',25)");
//			        bw.newLine();
			     // ---------------------PS---------------------------------------------------
			        
			     // ---------------------PF---------------------------------------------------
		        bw.newLine();
		        bw.write(" xl = xlabel('\\it f\\rm_1','fontname','Times New Roman');");
		        bw.newLine();
		        bw.write("set(xl,'fontsize',20)");
		        bw.newLine();
		        bw.write(" yl = ylabel('\\it f\\rm_2','fontname','Times New Roman');");
		        bw.newLine();
		        bw.write("set(yl,'fontsize',20)");
		        bw.newLine();
		     // ---------------------PF---------------------------------------------------
			        
			        if(objectives_==3){
//			        	bw.write(" zl = zlabel('\\it x \\rm3','fontname','Times New Roman');"); //PS
			        	bw.write(" zl = zlabel('\\it f\\rm_3','fontname','Times New Roman');"); //PF
				        bw.newLine();
				        bw.write("set(zl,'fontsize',20)");
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
			        	bw.write(" tit = title(' " + algNames_[0]  + "');"); // 以算法为标题
			        }
			        
			        bw.newLine();
			        bw.write("set(tit,'fontsize',20)");
			        bw.newLine();
			        bw.write("set(gca,'fontsize',18)");
			        bw.newLine();
			        
//			        if(objectives_==2 || objectives_==3){
//			        	// 图例
//				        String lgd = "lgd = legend('PF',";
////			        	String lgd = "lgd = legend(";
//				        for(int algID=0;algID<algNames_.length-1;algID++) {
//				        	lgd = lgd + "'" + algNames_[algID] + "',";
//				        }
//				        lgd = lgd + "'" + algNames_[algNames_.length-1] + "');";
//				        
//				        bw.write(lgd);
//				        bw.newLine();
//				        
//				        
//				        if (objectives_==2) {
//				        	bw.write("set(lgd,'fontsize',15)");				        	
//				        }
//				        else 
//				        	bw.write("set(lgd,'fontsize',10,'Orientation','horizontal','Location', 'northeast')");				       
//			         }					        		       
			        
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
