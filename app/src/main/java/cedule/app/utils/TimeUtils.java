package cedule.app.utils;

import java.util.Calendar;

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


    public static String toTimeString(int ms) {

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(ms);

        return time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE);
    }

    public static String toDateString(long ms) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(ms);

        return date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.YEAR);

    }
}
