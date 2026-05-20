package planner;

public class AssignmentRecord {
    public final String serviceStatus;
    public final String productFamily;
    public final String customerName;
    public final String city;
    public final String state;
    public final String monthName;
    public final int year;
    public final double demandSqFt;
    public final String assignedFacility;
    public final double distanceMiles;

    public AssignmentRecord(
        String serviceStatus,
        String productFamily,
        String customerName,
        String city,
        String state,
        String monthName,
        int year,
        double demandSqFt,
        String assignedFacility,
        double distanceMiles
    ) {
        this.serviceStatus = serviceStatus;
        this.productFamily = productFamily;
        this.customerName = customerName;
        this.city = city;
        this.state = state;
        this.monthName = monthName;
        this.year = year;
        this.demandSqFt = demandSqFt;
        this.assignedFacility = assignedFacility;
        this.distanceMiles = distanceMiles;
    }
}

