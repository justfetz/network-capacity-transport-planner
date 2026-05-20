package planner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CapacityPlanner {
    public PlannerResult solve(List<Facility> facilities, List<DemandPoint> demandPoints) {
        List<AssignmentRecord> serviced = new ArrayList<>();
        List<AssignmentRecord> unserviced = new ArrayList<>();
        final int[] activeYear = {Integer.MIN_VALUE};
        final int[] activeMonth = {Integer.MIN_VALUE};

        demandPoints.stream()
            .sorted(Comparator.comparingInt((DemandPoint point) -> point.year)
                .thenComparingInt(point -> point.month))
            .forEach(point -> {
                if (point.year != activeYear[0] || point.month != activeMonth[0]) {
                    for (Facility facility : facilities) {
                        facility.resetForPeriod(point.month, point.year);
                    }
                    activeYear[0] = point.year;
                    activeMonth[0] = point.month;
                }

                Facility bestFacility = facilities.stream()
                    .filter(facility -> facility.isActive(point.month, point.year))
                    .filter(facility -> facility.hasCapacityFor(point.productFamily, point.demandSqFt))
                    .min(Comparator.comparingDouble(facility ->
                        DistanceUtils.miles(point.latitude, point.longitude, facility.latitude, facility.longitude)))
                    .orElse(null);

                if (bestFacility == null) {
                    unserviced.add(new AssignmentRecord(
                        "Unserviced",
                        point.productFamily,
                        point.customerName,
                        point.city,
                        point.state,
                        point.monthName,
                        point.year,
                        point.demandSqFt,
                        "",
                        0.0
                    ));
                    return;
                }

                double distance = DistanceUtils.miles(point.latitude, point.longitude, bestFacility.latitude, bestFacility.longitude);
                bestFacility.consume(point.productFamily, point.demandSqFt);
                serviced.add(new AssignmentRecord(
                    "Serviced",
                    point.productFamily,
                    point.customerName,
                    point.city,
                    point.state,
                    point.monthName,
                    point.year,
                    point.demandSqFt,
                    bestFacility.facilityName,
                    distance
                ));
            });

        return new PlannerResult(serviced, unserviced, facilities);
    }
}
