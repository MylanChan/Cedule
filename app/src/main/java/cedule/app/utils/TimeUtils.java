package cedule.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static String replenishZero(int target) {
        String targetInString = String.valueOf(target);

        if (2 - targetInString.length() > 0) {
            return "0".repeat(2 - targetInString.length()) + target;
        }
        return targetInString;
    }

    public static String toTimeNotation(int seconds) {
        int hrs = (int) (seconds / 3600.);
        int min = seconds/60 - hrs*60;
        int sec = seconds - hrs * 3600 - min * 60;

        return replenishZero(hrs) + ":" +  replenishZero(min) + ":" + replenishZero(sec);
    }


    public static String toTimeString(long ms) {
        int hour = (int) TimeUnit.MILLISECONDS.toHours(ms);
        int min = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) - hour * 60);

        return replenishZero(hour) + ":" + replenishZero(min);
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

        // For example: 1 -> Feb
        String monthInEng = new SimpleDateFormat("MMM", Locale.ENGLISH).format(ms);

        return date.get(Calendar.DAY_OF_MONTH) + " " + monthInEng + " " + date.get(Calendar.YEAR);
    }
}
