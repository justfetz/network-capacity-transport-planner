package planner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvIO {
    private CsvIO() {}

    public static List<Facility> readFacilities(Path path) throws IOException {
        List<Facility> facilities = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            String[] row = lines.get(i).split(",");
            facilities.add(new Facility(
                row[0],
                row[1],
                Double.parseDouble(row[2]),
                Double.parseDouble(row[3]),
                Double.parseDouble(row[4]),
                Double.parseDouble(row[5]),
                Integer.parseInt(row[6]),
                Integer.parseInt(row[7]),
                Integer.parseInt(row[8]),
                Integer.parseInt(row[9]),
                Integer.parseInt(row[10]),
                Integer.parseInt(row[11]),
                Double.parseDouble(row[12]),
                Integer.parseInt(row[13]),
                Integer.parseInt(row[14]),
                Double.parseDouble(row[15])
            ));
        }
        return facilities;
    }

    public static List<DemandPoint> readDemand(Path path) throws IOException {
        List<DemandPoint> points = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }
            String[] row = lines.get(i).split(",");
            String monthName = row[4].trim();
            points.add(new DemandPoint(
                row[0].trim(),
                row[1].trim(),
                row[2].trim(),
                row[3].trim(),
                monthName,
                monthNumber(monthName),
                Integer.parseInt(row[5].trim()),
                Double.parseDouble(row[6].trim()),
                Double.parseDouble(row[7].trim()),
                Double.parseDouble(row[8].trim())
            ));
        }
        return points;
    }

    public static void writeAssignments(Path path, List<AssignmentRecord> records) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("service_status,product_family,customer_name,city,state,month,year,demand_sqft,assigned_facility,distance_miles");
        for (AssignmentRecord record : records) {
            lines.add(String.join(",",
                record.serviceStatus,
                record.productFamily,
                sanitize(record.customerName),
                sanitize(record.city),
                sanitize(record.state),
                record.monthName,
                Integer.toString(record.year),
                Long.toString(Math.round(record.demandSqFt)),
                sanitize(record.assignedFacility),
                String.format("%.2f", record.distanceMiles)
            ));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void writeCapacitySnapshot(Path path, List<Facility> facilities) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("facility_id,facility_name,remaining_family_a_sqft,remaining_family_b_sqft");
        for (Facility facility : facilities) {
            lines.add(String.join(",",
                facility.facilityId,
                sanitize(facility.facilityName),
                Long.toString(Math.round(facility.remainingFamilyASqFt())),
                Long.toString(Math.round(facility.remainingFamilyBSqFt()))
            ));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static String sanitize(String value) {
        return value.replace(",", " ");
    }

    private static int monthNumber(String monthName) {
        return switch (monthName.toUpperCase()) {
            case "JANUARY" -> 1;
            case "FEBRUARY" -> 2;
            case "MARCH" -> 3;
            case "APRIL" -> 4;
            case "MAY" -> 5;
            case "JUNE" -> 6;
            case "JULY" -> 7;
            case "AUGUST" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER" -> 12;
            default -> throw new IllegalArgumentException("Unknown month: " + monthName);
        };
    }
}
