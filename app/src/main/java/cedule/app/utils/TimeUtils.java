package cedule.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static String replenishZero(int target, int n) {
        String targetInString = String.valueOf(target);

        if (n - targetInString.length() > 0) {
            return "0".repeat(n - targetInString.length()) + target;
        }
        return targetInString;
    }

    public static String toTimeNotation(int seconds) {
        int hrs = (int) (seconds / 3600.);
        int min = seconds/60 - hrs*60;
        int sec = seconds - hrs * 3600 - min * 60;

        return replenishZero(hrs, 2) + ":" +  replenishZero(min, 2) + ":" + replenishZero(sec, 2);
    }


    public static String toTimeString(long ms) {
        int hour = (int) TimeUnit.MILLISECONDS.toHours(ms);
        int min = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) - hour * 60);

        return replenishZero(hour, 2) + ":" + replenishZero(min, 2);
    }

    public static void setMidNight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static String toDateString(long ms) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(ms);


        return date.get(Calendar.DAY_OF_MONTH) + " " + new SimpleDateFormat("MMM", Locale.ENGLISH).format(ms) + " " + date.get(Calendar.YEAR);

    }
}
