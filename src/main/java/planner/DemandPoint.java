package planner;

public class DemandPoint {
    public final String productFamily;
    public final String customerName;
    public final String city;
    public final String state;
    public final String monthName;
    public final int month;
    public final int year;
    public final double latitude;
    public final double longitude;
    public final double demandSqFt;

    public DemandPoint(
        String productFamily,
        String customerName,
        String city,
        String state,
        String monthName,
        int month,
        int year,
        double latitude,
        double longitude,
        double demandMsf
    ) {
        this.productFamily = productFamily;
        this.customerName = customerName;
        this.city = city;
        this.state = state;
        this.monthName = monthName;
        this.month = month;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
        this.demandSqFt = demandMsf * 1_000_000.0;
    }
}

