package planner;

import java.time.YearMonth;

public class Facility {
    public final String facilityId;
    public final String facilityName;
    public final double latitude;
    public final double longitude;
    public final int openMonth;
    public final int openYear;
    public final int closeMonth;
    public final int closeYear;

    private double familyACapacitySqFt;
    private double familyBCapacitySqFt;
    private final double baseFamilyACapacitySqFt;
    private final double baseFamilyBCapacitySqFt;

    private final int familyAAdjustMonth;
    private final int familyAAdjustYear;
    private final double familyAAdjustSqFt;
    private final int familyBAdjustMonth;
    private final int familyBAdjustYear;
    private final double familyBAdjustSqFt;

    public Facility(
        String facilityId,
        String facilityName,
        double latitude,
        double longitude,
        double familyACapacityMsf,
        double familyBCapacityMsf,
        int openMonth,
        int openYear,
        int closeMonth,
        int closeYear,
        int familyAAdjustMonth,
        int familyAAdjustYear,
        double familyAAdjustMsf,
        int familyBAdjustMonth,
        int familyBAdjustYear,
        double familyBAdjustMsf
    ) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openMonth = openMonth;
        this.openYear = openYear;
        this.closeMonth = closeMonth;
        this.closeYear = closeYear;
        this.baseFamilyACapacitySqFt = familyACapacityMsf * 1_000_000.0;
        this.baseFamilyBCapacitySqFt = familyBCapacityMsf * 1_000_000.0;
        this.familyACapacitySqFt = baseFamilyACapacitySqFt;
        this.familyBCapacitySqFt = baseFamilyBCapacitySqFt;
        this.familyAAdjustMonth = familyAAdjustMonth;
        this.familyAAdjustYear = familyAAdjustYear;
        this.familyAAdjustSqFt = familyAAdjustMsf * 1_000_000.0;
        this.familyBAdjustMonth = familyBAdjustMonth;
        this.familyBAdjustYear = familyBAdjustYear;
        this.familyBAdjustSqFt = familyBAdjustMsf * 1_000_000.0;
    }

    public void resetForPeriod(int month, int year) {
        familyACapacitySqFt = shouldUseAdjustedCapacity(month, year, familyAAdjustMonth, familyAAdjustYear)
            ? familyAAdjustSqFt
            : baseFamilyACapacitySqFt;
        familyBCapacitySqFt = shouldUseAdjustedCapacity(month, year, familyBAdjustMonth, familyBAdjustYear)
            ? familyBAdjustSqFt
            : baseFamilyBCapacitySqFt;
    }

    public boolean isActive(int month, int year) {
        YearMonth demandPeriod = YearMonth.of(year, month);
        YearMonth openPeriod = YearMonth.of(openYear, openMonth);
        YearMonth closePeriod = YearMonth.of(closeYear, closeMonth);
        return (!demandPeriod.isBefore(openPeriod)) && (!demandPeriod.isAfter(closePeriod));
    }

    public boolean hasCapacityFor(String productFamily, double demandSqFt) {
        if ("family_a".equalsIgnoreCase(productFamily)) {
            return familyACapacitySqFt - demandSqFt >= 0;
        }
        return familyBCapacitySqFt - demandSqFt >= 0;
    }

    public void consume(String productFamily, double demandSqFt) {
        if ("family_a".equalsIgnoreCase(productFamily)) {
            familyACapacitySqFt -= demandSqFt;
        } else {
            familyBCapacitySqFt -= demandSqFt;
        }
    }

    public double remainingFamilyASqFt() {
        return familyACapacitySqFt;
    }

    public double remainingFamilyBSqFt() {
        return familyBCapacitySqFt;
    }

    private boolean shouldUseAdjustedCapacity(int month, int year, int adjustMonth, int adjustYear) {
        if (adjustMonth < 1 || adjustMonth > 12) {
            return false;
        }
        YearMonth activePeriod = YearMonth.of(year, month);
        YearMonth adjustmentPeriod = YearMonth.of(adjustYear, adjustMonth);
        return !activePeriod.isBefore(adjustmentPeriod);
    }
}
