package cedule.app.utils;

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

    public static String toTimeString(int seconds) {
        int hrs = (int) (seconds / 3600.);
        int min = seconds/60 - hrs*60;
        int sec = seconds - hrs * 3600 - min * 60;

        return hrs + " hrs " +  min + " mins " + sec + " sec ";
    }
}
