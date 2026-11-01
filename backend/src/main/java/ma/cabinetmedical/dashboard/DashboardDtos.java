package ma.cabinetmedical.dashboard;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class DashboardDtos {
    private DashboardDtos() {
    }

    public record Statistics(long totalCabinets, long activeCabinets, long inactiveCabinets,
                             long doctors, long secretaries, long patients) {}

    public record MonthlyValue(String month, long value) {}

    public record StatusDistribution(String status, long value) {}

    public record UserDistribution(String role, long value) {}

    public record Activity(String id, String action, String entityType, String description,
                           String actorName, Instant occurredAt) {}

    public record DateRange(LocalDate from, LocalDate to) {}

    public record DashboardData(Statistics statistics, List<MonthlyValue> cabinetsEvolution,
                                List<StatusDistribution> cabinetStatusDistribution,
                                List<UserDistribution> usersDistribution,
                                List<MonthlyValue> registrationsEvolution,
                                List<Activity> recentActivities) {}
}
