function [busCount,teamCount,dayCount,outData,FullTime] = ProcessData(rawData)
%make records completed, from stop 1 to stop 22
global TotalStopId;
TotalStopId = 22;
global Interval;
Interval = 30;

[busCount,teamCount,preOutData] = PreProcessData(rawData);
[dayCount,outData] = GetHeadway(preOutData);
[FullTime] = StatsData(outData);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function [busCount,teamCount,outData] = PreProcessData(rawData)
        %busCount the number of buses
        %teamCount the number of teams, one team is data for one trip
        %outData data cleaned
        [row_len,~] = size(rawData);
        rawData = sortrows(rawData,1);%sort according to busID in ascending order
        busCount = 0;
        dataId = 1;
        for num = 1:row_len-1
            b1 = rawData(num,1);
            b2 = rawData(num+1,1);
            if(b2>b1)
                rawData(dataId:num,1:6) = sortrows(rawData(dataId:num,1:6),6);%sort according to departureTime
                dataId = num + 1;
                busCount = busCount + 1;
            end
        end
        rawData(dataId:row_len,1:6) = sortrows(rawData(dataId:row_len,1:6),6) ;%sort the last bus
        busCount = busCount + 1;
        %process data after sorting
        outData = 1:10;
        outData = outData';
        teamCount = 0;
        dataId = 1;
        for num = 1:row_len-1
            x1 = rawData(num,2);
            x2 = rawData(num+1,2);
            if (x2 < x1)
                oneTeamData = rawData(dataId:num,1:6);%if stopID decreases, means the end of one trip
                outData(teamCount*TotalStopId+1:(teamCount+1)*TotalStopId,1:12) = PreProcessTeamData(oneTeamData);
                teamCount = teamCount+1;
                dataId = num+1;
            end
        end
        oneTeamData = rawData(dataId:row_len,1:6);%get data for the last trip
        outData(teamCount*TotalStopId+1:(teamCount+1)*TotalStopId,1:12) = PreProcessTeamData(oneTeamData);
        teamCount = teamCount+1;
        %add dataID to the last column, for later processing
        did = 1:teamCount*TotalStopId;
        did = did';
        outData = [outData,did];
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Process one team data
    function outData = PreProcessTeamData(oneTeamData)
        [row_len,~] = size(oneTeamData);
        outData = 1:TotalStopId;
        outData = outData';
        t2m = 0.001389;%t2m is 2 minutes, but the unit is day, t2m*24*60=2;
        stopId = 1;
        for num = 1:row_len
            x1 = oneTeamData(num,2);
            if(x1 == stopId)%then the data is correct, directly use
                outData(stopId,1:6) = oneTeamData(num,1:6);
                stopId = stopId +1;
            elseif(x1 < stopId)%repetition of stops
                outData(stopId-1,3) = outData(stopId-1,3) + oneTeamData(num,3);%add boarding for each records together
                outData(stopId-1,4) = outData(stopId-1,4) + oneTeamData(num,4);%add alighting together
            elseif(x1 > stopId)%loss of stops
                tim = oneTeamData(num,6);
                len = x1 - stopId;%the number of stops lost
                for i=1:len
                    outData(i+stopId-1,1) = oneTeamData(num,1);%busID
                    outData(i+stopId-1,2) = i+stopId-1;%stopID
                    outData(i+stopId-1,6) = tim - t2m*(len+1-i);%assume the travel time from one stop to the next stop is 2 minutes
                end
                outData(stopId+len,1:6) = oneTeamData(num,1:6);
                stopId = stopId + len + 1;
            end
        end
        %add data for the last few stops
        if(stopId <= TotalStopId)
            tim = oneTeamData(row_len,6);
            len = TotalStopId - stopId + 1;
            for i=1:len
                outData(i+stopId-1,1) = oneTeamData(row_len,1);
                outData(i+stopId-1,2) = i+stopId-1;
                outData(i+stopId-1,6) = tim + t2m*i;
            end
        end
        %alighting for the first stop should be 0
        outData(1,4) = 0;
        %load for the first stop should be 0
        outData(1,5) = 0;
        loadNum =0;
        for num=1:(TotalStopId-1)
            boardNum = outData(num,3);
            alightNum = outData(num,4);
            tempLoadNum = loadNum + boardNum - alightNum;
            %load will not be negative
            if(tempLoadNum >0)
                loadNum = tempLoadNum;
            else
                %recalculate alighting, which was noise
                outData(num,4) = loadNum + boardNum;
                loadNum = 0;
            end
            outData(num+1,5) = loadNum;
        end
        %boarding for the final stop should be 0
        outData(TotalStopId,3) = 0;
        %alighting = load for the final stop
        outData(TotalStopId,4) = outData(TotalStopId,5);
        %calculate travel time from this stop to the next stop, the full travel time stored in the data for the last stop
        for num=1:TotalStopId
            if(num == TotalStopId)
                tim1 = outData(1,6);
                tim2 = outData(TotalStopId,6);
            else
                tim1 = outData(num,6);
                tim2 = outData(num+1,6);
            end
            tempTim = tim2 - tim1;
            %travel time will not be negative
            if(tempTim>0)
                travelTim = tempTim*60*24;
            else
                travelTim = 1;
            end
            outData(num,7) = travelTim;
        end
        %calculate alighting ratio
        for num=1:TotalStopId
            alightNum = outData(num,4);
            loadNum = outData(num,5);
            if(alightNum==0)||(loadNum == 0)
                outData(num,8) = 0;
            else
                outData(num,8) = alightNum / loadNum;
            end
        end
        %2016/3/7 is Monday, the date value of it is 42436, which server as base
        BaseDate = 42436;
        for num=1:TotalStopId
            dateTime = outData(num,6);
            date = floor(dateTime);%the integral part is date
            time = dateTime - date;%the fractional part is time
            %calculate the index of the interval, every 30 minutes is an interval, and 0:00-0:29 is the first interval
            outData(num,9) = floor(time*24*60/Interval)+1;
            outData(num,10) = mod((date-BaseDate),7)+1;%1-7 is Monday-Sunday
            outData(num,11) = 0;
            outData(num,12) = 0;
        end
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function [dayCount,outData] = GetHeadway(inData)
        [row_len,~] = size(inData); 
        inData = sortrows(inData,6);%sort according to departureTime
        outData = 1:10;
        outData = outData';
        dayCount = 0;
        dataId = 1;
        for num = 1:row_len-1
            x1 = floor(inData(num,6));%get day of week
            x2 = floor(inData(num+1,6));
            if (x2 > x1)
                oneDayData = inData(dataId:num,1:13);%get data for one day
                outData(dataId:num,1:13) = GetOneDayHeadway(oneDayData); %calculate headway
                dataId = num + 1;
                dayCount = dayCount + 1;
            end
        end
        %get data for the last day
        oneDayData = inData(dataId:row_len,1:13);
        outData(dataId:row_len,1:13) = GetOneDayHeadway(oneDayData);
        dayCount = dayCount + 1;
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function outData = GetOneDayHeadway(oneDayData)
        [row_len,~] = size(oneDayData);
        oneDayData = sortrows(oneDayData,2);%sort according to stopID
        dataId = 1;
        for num = 1:row_len-1
            x1 = oneDayData(num,2);
            x2 = oneDayData(num+1,2);
            if (x2 > x1)
                oneStopData = oneDayData(dataId:num,1:13);%get data for one stop 
                outData(dataId:num,1:13) = GetOneStopHeadway(oneStopData);
                dataId = num + 1;
            end
        end
        oneStopData = oneDayData(dataId:row_len,1:13);% get data for the last stop
        outData(dataId:row_len,1:13) = GetOneStopHeadway(oneStopData);
    end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function outData = GetOneStopHeadway(oneStopData)
        [row_len,~] = size(oneStopData); 
        oneStopData = sortrows(oneStopData,6);%sort according to departureTime
        outData = oneStopData;
        %set the headway of the first trip for the day 5 minutes (the first run actually does not have headway)
        outData(1,11) = 5;
        board = oneStopData(1,3);
        outData(1,12) = board/outData(1,11);
        for num = 2:row_len
            tim1 = oneStopData(num-1,6);
            tim2 = oneStopData(num,6);
            tim = (tim2 - tim1)*24*60;
            outData(num,11) = tim;
            board = oneStopData(num,3);
            if(tim ~=0)
                outData(num,12) = board/tim;
            end
        end
    end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
end

