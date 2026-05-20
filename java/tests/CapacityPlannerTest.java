package planner.tests;

import planner.CapacityPlanner;
import planner.CsvIO;
import planner.DemandPoint;
import planner.Facility;
import planner.PlannerResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CapacityPlannerTest {
    public static void main(String[] args) {
        testAssignsNearestFeasibleFacility();
        testLeavesDemandUnservicedWhenCapacityIsInsufficient();
        testRespectsFutureCapacityAdjustment();
        testRespectsFacilityOpeningWindow();
        testLoadsLegacyShapeScenario();
        System.out.println("All network capacity planner tests passed.");
    }

    private static void testAssignsNearestFeasibleFacility() {
        List<Facility> facilities = new ArrayList<>();
        facilities.add(facility("F01", "North Hub", 41.00, -87.00, 50, 50, 1, 2025, 12, 2028, 1, 2027, 60, 1, 2027, 60));
        facilities.add(facility("F02", "South Hub", 33.50, -84.00, 50, 50, 1, 2025, 12, 2028, 1, 2027, 60, 1, 2027, 60));

        List<DemandPoint> demand = List.of(
            demand("family_b", "Metro Customer", "Springfield", "IL", "JANUARY", 1, 2026, 41.10, -87.10, 10)
        );

        PlannerResult result = new CapacityPlanner().solve(facilities, demand);
        assertEquals(1, result.serviced().size(), "nearest assignment should service one row");
        assertEquals("North Hub", result.serviced().get(0).assignedFacility, "should assign to nearest feasible hub");
        assertEquals(0, result.unserviced().size(), "no rows should be unserviced");
    }

    private static void testLeavesDemandUnservicedWhenCapacityIsInsufficient() {
        List<Facility> facilities = List.of(
            facility("F01", "Tiny Hub", 41.00, -87.00, 5, 5, 1, 2025, 12, 2028, 1, 2027, 5, 1, 2027, 5)
        );

        List<DemandPoint> demand = List.of(
            demand("family_b", "Large Customer", "Columbus", "OH", "FEBRUARY", 2, 2026, 39.96, -82.99, 9)
        );

        PlannerResult result = new CapacityPlanner().solve(new ArrayList<>(facilities), demand);
        assertEquals(0, result.serviced().size(), "oversized demand should not be serviced");
        assertEquals(1, result.unserviced().size(), "oversized demand should be reported as unserviced");
    }

    private static void testRespectsFutureCapacityAdjustment() {
        List<Facility> facilities = List.of(
            facility("F01", "Adaptive Hub", 41.00, -87.00, 5, 5, 1, 2025, 12, 2028, 3, 2026, 12, 3, 2026, 12)
        );

        List<DemandPoint> demand = List.of(
            demand("family_a", "February Customer", "Akron", "OH", "FEBRUARY", 2, 2026, 41.05, -87.05, 6),
            demand("family_a", "March Customer", "Dayton", "OH", "MARCH", 3, 2026, 41.06, -87.06, 6)
        );

        PlannerResult result = new CapacityPlanner().solve(new ArrayList<>(facilities), demand);
        assertEquals(1, result.serviced().size(), "only one record should be serviceable across the adjustment boundary");
        assertEquals("March Customer", result.serviced().get(0).customerName, "adjusted capacity should make the March row feasible");
        assertEquals(1, result.unserviced().size(), "February row should remain unserviced");
    }

    private static void testRespectsFacilityOpeningWindow() {
        List<Facility> facilities = List.of(
            facility("F01", "Late Opening Hub", 41.00, -87.00, 50, 50, 4, 2026, 12, 2028, 1, 2027, 60, 1, 2027, 60)
        );

        List<DemandPoint> demand = List.of(
            demand("family_b", "March Customer", "Madison", "WI", "MARCH", 3, 2026, 43.07, -89.40, 8),
            demand("family_b", "April Customer", "Madison", "WI", "APRIL", 4, 2026, 43.07, -89.40, 8)
        );

        PlannerResult result = new CapacityPlanner().solve(new ArrayList<>(facilities), demand);
        assertEquals(1, result.serviced().size(), "only the post-opening demand should be serviced");
        assertEquals("April Customer", result.serviced().get(0).customerName, "facility should open in April");
        assertEquals(1, result.unserviced().size(), "pre-opening demand should be unserviced");
    }

    private static void testLoadsLegacyShapeScenario() {
        try {
            Path root = Path.of("").toAbsolutePath();
            List<Facility> facilities = CsvIO.readFacilities(root.resolve("input").resolve("legacy_shape_facilities.csv"));
            List<DemandPoint> demand = CsvIO.readDemand(root.resolve("input").resolve("legacy_shape_demand.csv"));

            assertEquals(10, facilities.size(), "legacy-shape facility set should preserve the larger network footprint");
            assertEquals(12, demand.size(), "legacy-shape demand sample should cover a full year");
            assertTrue(!facilities.get(9).isActive(10, 2023), "late-opening hub should be inactive before its start month");
            assertTrue(facilities.get(9).isActive(12, 2023), "late-opening hub should activate on its start month");
        } catch (Exception e) {
            throw new AssertionError("legacy-shape scenario should load cleanly: " + e.getMessage(), e);
        }
    }

    private static Facility facility(
        String id,
        String name,
        double lat,
        double lon,
        double aCap,
        double bCap,
        int openMonth,
        int openYear,
        int closeMonth,
        int closeYear,
        int aAdjustMonth,
        int aAdjustYear,
        double aAdjustCap,
        int bAdjustMonth,
        int bAdjustYear,
        double bAdjustCap
    ) {
        return new Facility(id, name, lat, lon, aCap, bCap, openMonth, openYear, closeMonth, closeYear, aAdjustMonth, aAdjustYear, aAdjustCap, bAdjustMonth, bAdjustYear, bAdjustCap);
    }

    private static DemandPoint demand(
        String family,
        String customer,
        String city,
        String state,
        String monthName,
        int month,
        int year,
        double lat,
        double lon,
        double demandMsf
    ) {
        return new DemandPoint(family, customer, city, state, monthName, month, year, lat, lon, demandMsf);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Expected " + expected + " but got " + actual + ".");
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Expected '" + expected + "' but got '" + actual + "'.");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
