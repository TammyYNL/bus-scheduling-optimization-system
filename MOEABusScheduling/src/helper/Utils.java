package helper;

import java.text.ParseException;
import java.util.Calendar;

public class Utils {
    public static int getDayOfWeek(Calendar c) {
        boolean isFirstDaySunday = (c.getFirstDayOfWeek() == Calendar.SUNDAY);
        int dow = c.get(Calendar.DAY_OF_WEEK);
        if(isFirstDaySunday) {
            dow = dow - 1;
            if(dow == 0) {
                dow = 7;
            }
        }
        return dow - 1;
    }
    
    public static int getIntervalIndex(Calendar time, int interval) throws ParseException {
    	int perHourSample = 60 / interval;
    	
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        
        int h = 0, m_lo = 0, m_up = interval;
        int index = 0;
        
        for(int i = 0; i < 24; i ++) {
            for(int j = 0; j < perHourSample; j ++) {
                if(hour == h && min >= m_lo && min < m_up) {
                    return index;
                }
                m_lo += interval;
                m_up += interval;
                if(m_lo == 60) {
                    m_lo = 0;
                    m_up = interval;
                    h += 1;
                }
                index ++;
            }
        }
        return -1;
    }

}
