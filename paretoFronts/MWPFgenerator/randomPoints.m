m = 3; % the number of objectives 
N = 100000; % the number of points
rand_points = rand(N,m);

%% Generate randomly
rand_points(:,1) = 1 - rand_points(:,1).^(1/(m-1));
for j = 2:m-1
    rand_points(:,j) = (1 - sum(rand_points(:,1:j-1),2)) .* (1 - rand_points(:,j).^(1/(m-j))); %
end
rand_points(:,m) = 1 - sum(rand_points(:,1:m-1),2);

%% Generate not randomly
%rand_points = rand_points./repmat(sum(rand_points,2),1,m);
% rand_points = rand_points./repmat(sqrt(sum(rand_points.^2,2)),1,m);
% rand_points = rand_points./repmat(sum(rand_points,2),1,m);
%% Plot
plot3(rand_points(:,1),rand_points(:,2),rand_points(:,3),'r.')
sum(rand_points(:,1:m-1),2)
