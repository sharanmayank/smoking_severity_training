task_name = "ov";
save_models = false;
save_results = false;

% Task 1, 2, 3
% stop_limit_arr = [0.2, 0.4, 0.6, 0.8, 1, 2, 3, 4, 5, 1000];

% Task 4
% stop_limit_arr = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 200];

% Overall
% stop_limit_arr = [180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 1000];

model_dir = strcat('models/lasso_lars_models/', task_name);

if save_models
    mkdir(model_dir);
end

task_names = ["task1", "task2", "task3", "task4", "ov"];
task_stop_limits = [2, 0.4, 0.4, 20, 220];
stop_lim_dict = containers.Map(task_names, task_stop_limits);
set_stop_limit = stop_lim_dict(task_name);

% Best Results
% Task 1 - 2       2.001       9.096       0.954       1.555       0.123
% Task 2 - 0.4         1.9       9.228       0.906       1.578       0.056
% Task 3 - 0.4        1.91       9.261       0.911       1.583       0.026
% Task 4 - 20        1.933        9.474        0.921         1.62        0.024
% Overall - 220         1.735         7.967         0.827         1.362         0.179 

Tbl = readtable(strcat('tr_data/', task_name, '_data.csv'));
table_shape = size(Tbl);
num_ex = table_shape(1);

% Extract data and normalize it

id_arr = table2array(Tbl(:,1));

x = table2array(Tbl(:,2:end-2));
x = (x - mean(x)) ./ std(x);

y = table2array(Tbl(:,end-1));

pred_arr = zeros(num_ex, 1);

stop_limit_arr = [set_stop_limit];

for stop_limit = stop_limit_arr
    for test_idx = 1:num_ex
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

        delta = 0;
        [a, b] = larsen(x_train, y_train, delta, stop_limit, [], true, false);

        weights = a(:, end);

        if save_models
            model_file = strcat(model_dir, '/leave_', ...
                num2str(test_idx), '_out.mat');
            save(model_file, 'weights');
        end

        pred = x_test * weights;
        diff = y_test - pred;

        pred_arr(test_idx) = pred;
    end

    % Compute metrics

    pred_arr(pred_arr < 0) = 0;
    diff_arr = pred_arr - y;

    mae = round(mean(abs(diff_arr)), 3);
    mse = round(mean(diff_arr .* diff_arr), 3);

    nmae = round(mean(abs(diff_arr)) ./ mean(abs(y - mean(y))), 3);
    nmse = round(mean(diff_arr .* diff_arr) ./ ...
        (mean((y - mean(y)) .* (y - mean(y)))), 3);

    R = corrcoef(y, pred_arr);
    r2 = round(R(1, 2).^2, 3);

    disp(['Limit ', num2str(stop_limit), ...
        ' MAE ', num2str(mae), ...
        ' MSE ', num2str(mse), ...
        ' NMAE ', num2str(nmae), ...
        ' NMSE ', num2str(nmse), ...
        ' R2 ', num2str(r2)]);

    disp(num2str([stop_limit, mae, mse, nmae, nmse, r2]));
end

if save_results
    mkdir(strcat('models/lasso_lars_models/', task_name));
    column_headings = {'ID', 'Actual FTND', 'Predicted FTND'};
    writecell([column_headings; [id_arr, num2cell(y), num2cell(pred_arr)]], ...
        strcat('models/lasso_lars_models/', task_name, '/fit.csv'))
end
