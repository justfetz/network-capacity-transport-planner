package planner;

import java.util.List;

public record PlannerResult(
    List<AssignmentRecord> serviced,
    List<AssignmentRecord> unserviced,
    List<Facility> facilities
) {}

