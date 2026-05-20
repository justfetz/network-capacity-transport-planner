package planner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Path root = Path.of("").toAbsolutePath();
        Path inputDir = root.resolve("input");
        Path outputDir = root.resolve("output");
        Files.createDirectories(outputDir);

        Path facilitiesPath = args.length >= 1
            ? resolvePath(root, args[0])
            : inputDir.resolve("sample_facilities.csv");
        Path demandPath = args.length >= 2
            ? resolvePath(root, args[1])
            : inputDir.resolve("sample_demand.csv");

        List<Facility> facilities = CsvIO.readFacilities(facilitiesPath);
        List<DemandPoint> demandPoints = CsvIO.readDemand(demandPath);
        CapacityPlanner planner = new CapacityPlanner();
        PlannerResult result = planner.solve(facilities, demandPoints);

        CsvIO.writeAssignments(outputDir.resolve("serviced_assignments.csv"), result.serviced());
        CsvIO.writeAssignments(outputDir.resolve("unserviced_demand.csv"), result.unserviced());
        CsvIO.writeCapacitySnapshot(outputDir.resolve("facility_capacity_snapshot.csv"), result.facilities());

        System.out.println("Network capacity planning run complete.");
        System.out.println("Serviced demand records: " + result.serviced().size());
        System.out.println("Unserviced demand records: " + result.unserviced().size());
    }

    private static Path resolvePath(Path root, String arg) {
        Path candidate = Path.of(arg);
        return candidate.isAbsolute() ? candidate : root.resolve(candidate);
    }
}
