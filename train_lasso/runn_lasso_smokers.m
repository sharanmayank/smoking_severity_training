task_name = "task2";

% Pick the max stop limit as per the task name

task_names = ["task1", "task2", "task3", "task4", "ov"];
task_stop_limits = [5, 4, 5, 213, 9];
stop_lim_dict = containers.Map(task_names, task_stop_limits);
max_stop_limit = stop_lim_dict(task_name);

% Uncomment this line to run the complete lasso path to get the max
% absolute sum of weights
% max_stop_limit = 1000;
min_stop_limit = 0;

% Create a list of linearly spaced 1000 points between the stop limit min
% and max

stop_limit_arr = linspace(min_stop_limit, max_stop_limit, 1000);

% Load the training data

Tbl = readtable(strcat('tr_data/', task_name, '_data.csv'));
table_shape = size(Tbl);

% Extract data

id_arr = table2array(Tbl(:,1));
x = table2array(Tbl(:,2:end-2));
y = table2array(Tbl(:,end-1));

% Limit the data to only the smokers

smoker_bool = y > 0;

x = x(smoker_bool, :);
y = y(smoker_bool);

% Perform z normalization on the input features and the output FTND

x = (x - mean(x)) ./ std(x);
y_m = mean(y);
y_s = std(y);
y = (y - y_m) ./ y_s;
low_lim = (0 - y_m) ./ y_s;

num_ex = length(y);

% Initialize arrays to store the plotting variables

pre_tr_error = zeros(1000, 1);
pre_te_error = zeros(1000, 1);
post_tr_error = zeros(1000, 1);
post_te_error = zeros(1000, 1);

% stop_limit_arr = [set_stop_limit];

for stop_limit_idx = linspace(1, 1000, 1000)
    
    stop_limit = stop_limit_arr(stop_limit_idx);
    pre_pred_arr = zeros(num_ex, 1);
    post_pred_arr = zeros(num_ex, 1);

    % Initialize the variables to sum the training error from each fold to
    % get the training error at the end
    pre_tr_nmse = 0;
    post_tr_nmse = 0;

    max_weight_sum = 0;

    for test_idx = 1:num_ex

        % Split the data into train and test using leave one out
        if test_idx == 1
            x_train = x(2:end, :);
            y_train = y(2:end);
            x_test = x(test_idx, :);
            y_test = y(test_idx);
        elseif test_idx == num_ex
            x_train = x(1:end-1, :);
            y_train = y(1:end-1);
            x_test = x(test_idx, :);
            y_test = y(test_idx);
        else
            x_train = cat(1, x(1:test_idx-1, :), x(test_idx+1:end, :));
            y_train = cat(1, y(1:test_idx-1), y(test_idx+1:end));
            x_test = x(test_idx, :);
            y_test = y(test_idx);
        end

        % Run the Larsen code. This is run every time but it is the same
        % run with the maximum stop limit so that the path stays the same
        [a, b] = larsen(x_train, y_train, 0, max_stop_limit, [], true, false);

        % Update the maximum sum of weights for this fold
        
        if sum(abs(a(:, end))) > max_weight_sum
            max_weight_sum = sum(abs(a(:, end)));
        end

        % Find the step just before the stop limit sum for this iteration
        idx_arr = find(sum(abs(a)) < stop_limit);

        % If all steps have higher sum the 1st step is the pre index
        if isempty(idx_arr)
            pre_idx = 1;
        else
            pre_idx = max(idx_arr);
        end

        % Set post index to the step after the pre index
        post_idx = pre_idx + 1;

        % If the pre index was the last step then the post index is also
        % set to the same
        weights_arr_size = size(a);
        if post_idx > weights_arr_size(2)
            post_idx = pre_idx;
        end

        % Extract pre and post weights from the path and predict on the
        % train set
        pre_weights = a(:, pre_idx);
        post_weights = a(:, post_idx);

        pre_tr_pred = x_train * pre_weights;
        pre_tr_pred(pre_tr_pred < low_lim) = low_lim;

        post_tr_pred = x_train * post_weights;
        post_tr_pred(post_tr_pred < low_lim) = low_lim;

        % Calculate NMSE error on the training set for pre and post step
        
        diff_arr = y_train - pre_tr_pred;
        y_diff_arr = y_train - mean(y_train);
        
        pre_tr_nmse = pre_tr_nmse + round(mean(diff_arr .* diff_arr) ./ ...
            (mean(y_diff_arr .* y_diff_arr)), 3);

        diff_arr = y_train - post_tr_pred;
        post_tr_nmse = post_tr_nmse + round(mean(diff_arr .* diff_arr) ./ ...
            (mean(y_diff_arr .* y_diff_arr)), 3);

        pre_pred_arr(test_idx) = x_test * pre_weights;
        post_pred_arr(test_idx) = x_test * post_weights;

    end

    % Divide the sum of training error by number of steps

    pre_tr_error(stop_limit_idx) = pre_tr_nmse / num_ex;
    post_tr_error(stop_limit_idx) = post_tr_nmse / num_ex;

    % Compute the test nmse error
    pre_pred_arr(pre_pred_arr < low_lim) = low_lim;
    post_pred_arr(post_pred_arr < low_lim) = low_lim;

    diff_arr = y - pre_pred_arr;
    y_diff_arr = y - mean(y);

    pre_te_error(stop_limit_idx) = round(mean(diff_arr .* diff_arr) ./ ...
            (mean(y_diff_arr .* y_diff_arr)), 3);
 
    diff_arr = y - post_pred_arr;
    
    post_te_error(stop_limit_idx) = round(mean(diff_arr .* diff_arr) ./ ...
            (mean(y_diff_arr .* y_diff_arr)), 3);

%     pred_arr(pred_arr < low_lim) = low_lim;
%     diff_arr = pred_arr - y;

%     mae = round(mean(abs(diff_arr)), 3);
%     mse = round(mean(diff_arr .* diff_arr), 3);
% 
%     nmae = round(mean(abs(diff_arr)) ./ mean(abs(y - mean(y))), 3);
%     nmse = round(mean(diff_arr .* diff_arr) ./ ...
%         (mean((y - mean(y)) .* (y - mean(y)))), 3);

%     r2 = 1 - nmse;

%     disp(['Limit ', num2str(stop_limit), ...
%         ' MAE ', num2str(mae), ...
%         ' MSE ', num2str(mse), ...
%         ' NMAE ', num2str(nmae), ...
%         ' NMSE ', num2str(nmse), ...
%         ' R2 ', num2str(r2)]);

%     disp(num2str([stop_limit, mae, mse, nmae, nmse, r2]));

    % For each stop limit print the pre step training error and the pre
    % step test error
    disp(num2str([stop_limit_idx, stop_limit, pre_tr_nmse / num_ex, pre_te_error(stop_limit_idx)]))
end

% Print the maximum sum of weights in the last step across folds used to
% set the config in line 6
disp(max_weight_sum);

% Create and save plot

plot(stop_limit_arr, pre_tr_error)
hold on
plot(stop_limit_arr, post_tr_error)
plot(stop_limit_arr, pre_te_error)
plot(stop_limit_arr, post_te_error)
legend({'Pre Step training NMSE', 'Post Step training NMSE', ...
    'Pre Step test NMSE', 'Post Step test NMSE'}, 'location', 'northeast')
title(task_name);
grid on

saveas(gcf, strcat(task_name, '_lasso.png'));
