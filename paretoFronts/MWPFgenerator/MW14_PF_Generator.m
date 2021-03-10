clear
clc
m = 10;
N = 100000;

interval     = [0,0.731000,1.331000,1.500000];
median       = (interval(2)-interval(1))/(interval(4)-interval(3)+interval(2)-interval(1));
X            = ReplicatePoint(N,m -1);
X(X<=median) = X(X<=median)*(interval(2)-interval(1))/median+interval(1);
X(X>median)  = (X(X>median)-median)*(interval(4)-interval(3))/(1-median)+interval(3);
f            = [X,1/(m-1)*sum(6 - exp(X) - 1.5*sin(1.1*pi*X.^2),2)];
            
%% Write vectors into files
fPath = strcat('../MW14.M',num2str(m));
fPath = strcat(fPath,'.pf');

% open the file with write permission
fid = fopen(fPath, 'w');

format = '';
for i=1:m
    format = strcat(format,'%12.8f ');
end 
 format = strcat(format,'\r\n');
size(f)
fprintf(fid, format, f');
fclose(fid);

%% Plot
if(m == 3)         
    plot3(f(:,1),f(:,2),f(:,3),'r.')
    %sum(vector(:,1:m),2);
    
%     plot(vector','linewidth',1.1,'color','b')    
%     tit = title('True PF of MW4');
%     set(tit,'fontsize',20)
%      xl = xlabel('Objective No.');
%     set(xl,'fontsize',20)
%      yl = ylabel('Objective Value');
%     set(yl,'fontsize',20)
%     set(gca,'XLim',[1 3])
%     set(gca,'XTick',1:1:3)
%     set(gca,'fontsize',20)
else
%     set(gcf,'Position',[500 200 300 350])
%     if get(gca,'Position') <= [inf inf 400 300]
%     Size = [3 5 .8 25];
%     else
%     Size = [6 3 2 25];
%     end
% 
%     set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',2,'LineWidth',1.3);
%     set(gca,'XTickMode','auto','YTickMode','auto');
%     %set(gca,'xminortick','on');
% 
%     plot(f','linewidth',1.1,'color','b')    
%     tit = title('MW14');
%     set(tit,'fontsize',20)
%      xl = xlabel('Objective No.');
%     set(xl,'fontsize',20)
%      yl = ylabel('Objective Value');
%     set(yl,'fontsize',20)
%     set(gca,'XLim',[1 m])
%     set(gca,'XTick',1:1:m)
%     set(gca,'fontsize',20)
end