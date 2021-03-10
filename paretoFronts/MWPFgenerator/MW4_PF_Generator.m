clear
clc
m = 5;
N = 10000;
vector = UniformPoint(N,m);

%% Write vectors into files
% fPath = strcat('./MW4.M',num2str(m));
% fPath = strcat(fPath,'.dat');
% 
% % open the file with write permission
% fid = fopen(fPath, 'w');
% 
% format = '';
% for i=1:m
%     format = strcat(format,'%12.8f ');
% end 
%  format = strcat(format,'\r\n');
% size(vector)
% fprintf(fid, format, vector');
% fclose(fid);

%% Plot
if(m == 3)         
    plot3(vector(:,1),vector(:,2),vector(:,3),'r.')
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
    set(gcf,'Position',[500 200 300 350])
    if get(gca,'Position') <= [inf inf 400 300]
    Size = [3 5 .8 25];
    else
    Size = [6 3 2 25];
    end

    set(gca,'NextPlot','add','Box','on','Fontname','Times New Roman','FontSize',2,'LineWidth',1.3);
    set(gca,'XTickMode','auto','YTickMode','auto');
    %set(gca,'xminortick','on');

    plot(vector','linewidth',1.1,'color','b')    
    tit = title('True');
    set(tit,'fontsize',20)
     xl = xlabel('Objective No.');
    set(xl,'fontsize',20)
     yl = ylabel('Objective Value');
    set(yl,'fontsize',20)
    set(gca,'XLim',[1 m])
    set(gca,'XTick',1:1:m)
    set(gca,'fontsize',20)
end
