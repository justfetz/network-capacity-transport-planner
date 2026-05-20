package planner;

public final class DistanceUtils {
    private DistanceUtils() {}

    public static double miles(double firstLatitude, double firstLongitude, double secondLatitude, double secondLongitude) {
        double x = 3959 * Math.acos(
            Math.sin(Math.toRadians(firstLatitude)) * Math.sin(Math.toRadians(secondLatitude)) +
            Math.cos(Math.toRadians(firstLatitude)) * Math.cos(Math.toRadians(secondLatitude)) *
            Math.cos(Math.toRadians(secondLongitude) - Math.toRadians(firstLongitude))
        );
        return x;
    }
}

