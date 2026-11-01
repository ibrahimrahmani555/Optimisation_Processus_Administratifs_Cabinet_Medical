package ma.cabinetmedical.dashboard;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static ma.cabinetmedical.dashboard.DashboardDtos.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    Statistics statistics() {
        return dashboardService.statistics();
    }

    @GetMapping("/cabinets-evolution")
    List<MonthlyValue> cabinetsEvolution(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return dashboardService.cabinetsEvolution(dateRange(from, to));
    }

    @GetMapping("/cabinets-status")
    List<StatusDistribution> cabinetStatusDistribution() {
        return dashboardService.cabinetStatusDistribution();
    }

    @GetMapping("/users-distribution")
    List<UserDistribution> usersDistribution() {
        return dashboardService.usersDistribution();
    }

    @GetMapping("/registrations-evolution")
    List<MonthlyValue> registrationsEvolution(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return dashboardService.registrationsEvolution(dateRange(from, to));
    }

    @GetMapping("/recent-activities")
    List<Activity> recentActivities(@RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return dashboardService.recentActivities(limit);
    }

    private DateRange dateRange(LocalDate from, LocalDate to) {
        YearMonth current = YearMonth.now();
        LocalDate start = from == null ? current.minusMonths(11).atDay(1) : from;
        LocalDate end = to == null ? current.atEndOfMonth() : to;
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
        return new DateRange(start, end);
    }
}
