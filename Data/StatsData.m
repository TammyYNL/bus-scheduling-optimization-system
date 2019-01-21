function [t_full] = StatsData(inData)
global TotalStopId;
TotalStopId = 22;
global Interval;
Interval = 30;

[t_full,t1,t2,t3,t4,t5,t6,t7,a1,a2,a3,a4,a5,a6,a7,b1,b2,b3,b4,b5,b6,b7] = StatsAllData(inData);
%save data
xlswrite('E:\Data\Export\OutData.xlsx',inData,'OutData','A2');

xlswrite('E:\Data\Export\Travel.xls',t_full,'full','B2');
xlswrite('E:\Data\Export\Travel.xls',t1,'1','B2');
xlswrite('E:\Data\Export\Travel.xls',t2,'2','B2');
xlswrite('E:\Data\Export\Travel.xls',t3,'3','B2');
xlswrite('E:\Data\Export\Travel.xls',t4,'4','B2');
xlswrite('E:\Data\Export\Travel.xls',t5,'5','B2');
xlswrite('E:\Data\Export\Travel.xls',t6,'6','B2');
xlswrite('E:\Data\Export\Travel.xls',t7,'7','B2');

xlswrite('E:\Data\Export\Alighting.xls',a1,'1','B2');
xlswrite('E:\Data\Export\Alighting.xls',a2,'2','B2');
xlswrite('E:\Data\Export\Alighting.xls',a3,'3','B2');
xlswrite('E:\Data\Export\Alighting.xls',a4,'4','B2');
xlswrite('E:\Data\Export\Alighting.xls',a5,'5','B2');
xlswrite('E:\Data\Export\Alighting.xls',a6,'6','B2');
xlswrite('E:\Data\Export\Alighting.xls',a7,'7','B2');

xlswrite('E:\Data\Export\Boarding.xls',b1,'1','B2');
xlswrite('E:\Data\Export\Boarding.xls',b2,'2','B2');
xlswrite('E:\Data\Export\Boarding.xls',b3,'3','B2');
xlswrite('E:\Data\Export\Boarding.xls',b4,'4','B2');
xlswrite('E:\Data\Export\Boarding.xls',b5,'5','B2');
xlswrite('E:\Data\Export\Boarding.xls',b6,'6','B2');
xlswrite('E:\Data\Export\Boarding.xls',b7,'7','B2');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function[t_full,t1,t2,t3,t4,t5,t6,t7,a1,a2,a3,a4,a5,a6,a7,b1,b2,b3,b4,b5,b6,b7] = StatsAllData(inData)
        [row_len,~] = size(inData);
        %sort according to day of week, from Monday to Sunday
        inData = sortrows(inData,10);
        %get data for each day of week
        dataId = 1;
        for num = 1:row_len-1
            x1 = inData(num,10);
            x2 = inData(num+1,10);
            if(x2>x1)
                oneDayData = inData(dataId:num,:);
                [travelFull,travel,alight,board] = StatsOneDayData(oneDayData);
                switch(x1)
                    case 1
                        t_full(1,:) = travelFull;
                        t1 = travel;
                        a1 = alight;
                        b1 = board;
                    case 2
                        t_full(2,:) = travelFull;
                        t2 = travel;
                        a2 = alight;
                        b2 = board;
                    case 3
                        t_full(3,:) = travelFull;
                        t3 = travel;
                        a3 = alight;
                        b3 = board;
                    case 4
                        t_full(4,:) = travelFull;
                        t4 = travel;
                        a4 = alight;
                        b4 = board;
                    case 5
                        t_full(5,:) = travelFull;
                        t5 = travel;
                        a5 = alight;
                        b5 = board;
                    case 6
                        t_full(6,:) = travelFull;
                        t6 = travel;
                        a6 = alight;
                        b6 = board;
                    case 7
                        t_full(7,:) = travelFull;
                        t7 = travel;
                        a7 = alight;
                        b7 = board;
                end
                dataId = num + 1;
            end
        end
        oneDayData = inData(dataId:row_len,:);%get the last day of week
        [travelFull,travel,alight,board] = StatsOneDayData(oneDayData);
        switch(x1)
            case 1
                t_full(1,:) = travelFull;
                t1 = travel;
                a1 = alight;
                b1 = board;
            case 2
                t_full(2,:) = travelFull;
                t2 = travel;
                a2 = alight;
                b2 = board;
            case 3
                t_full(3,:) = travelFull;
                t3 = travel;
                a3 = alight;
                b3 = board;
            case 4
                t_full(4,:) = travelFull;
                t4 = travel;
                a4 = alight;
                b4 = board;
            case 5
                t_full(5,:) = travelFull;
                t5 = travel;
                a5 = alight;
                b5 = board;
            case 6
                t_full(6,:) = travelFull;
                t6 = travel;
                a6 = alight;
                b6 = board;
            case 7
                t_full(7,:) = travelFull;
                t7 = travel;
                a7 = alight;
                b7 = board;
        end
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function [travelFull,travel,alight,board] = StatsOneDayData(oneDayData)
        [row_len,~] = size(oneDayData);
        %sort according to stopID
        oneDayData = sortrows(oneDayData,2);
        count = ceil(24*60/Interval);
        travelFull = zeros(1,count);
        travel =  zeros(1,count);
        alight = zeros(1,count);
        board =  zeros(1,count);
        dataId = 1;
        for num = 1:row_len-1
            x1 = oneDayData(num,2);
            x2 = oneDayData(num+1,2);
            if(x2>x1)
                oneStopData = oneDayData(dataId:num,:);%get data for one stop
                [travel(x1,:),alight(x1,:),board(x1,:)] = StatsOneIntervalData(oneStopData);
                if(x1 == TotalStopId)%the data stored in the final stop is full travel time 
                    travelFull = travel(x1,:);
                end
                dataId = num + 1;
            end
        end
        oneStopData = oneDayData(dataId:row_len,:);%get data for the last stop
        [travel(x1,:),alight(x1,:),board(x1,:)] = StatsOneIntervalData(oneStopData);
        if(x1 == TotalStopId)
            travelFull = travel(x1,:);
        end
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function [travel,alight,board] = StatsOneIntervalData(oneStopData)
        [row_len,~] = size(oneStopData);
        %sort according to the index of the interval
        oneStopData = sortrows(oneStopData,9);
        count = ceil(24*60/Interval);
        travel =  zeros(1,count);
        alight = zeros(1,count);
        board =  zeros(1,count);
        dataId = 1;
        for num = 1:row_len-1
            x1 = oneStopData(num,9);
            x2 = oneStopData(num+1,9);
            if(x2>x1)
                oneIntervalData = oneStopData(dataId:num,:);%get data for one interval
                travel(1,x1) = median(oneIntervalData(:,7));
                alight(1,x1) = median(oneIntervalData(:,8));
                board(1,x1)   = median(oneIntervalData(:,12));
                dataId = num + 1;
            end
        end
        oneIntervalData = oneStopData(dataId:row_len,:);%get data for the last interval
        travel(1,x1) = median(oneIntervalData(:,7));
        alight(1,x1) = median(oneIntervalData(:,8));
        board(1,x1)   = median(oneIntervalData(:,12));
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
end

