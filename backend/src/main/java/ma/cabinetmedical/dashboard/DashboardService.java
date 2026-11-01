package ma.cabinetmedical.dashboard;

import ma.cabinetmedical.audit.AuditLog;
import ma.cabinetmedical.audit.AuditLogRepository;
import ma.cabinetmedical.cabinet.Cabinet;
import ma.cabinetmedical.cabinet.CabinetRepository;
import ma.cabinetmedical.patient.Patient;
import ma.cabinetmedical.patient.PatientRepository;
import ma.cabinetmedical.user.Role;
import ma.cabinetmedical.user.User;
import ma.cabinetmedical.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ma.cabinetmedical.dashboard.DashboardDtos.*;

@Service
public class DashboardService {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CabinetRepository cabinetRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardService(CabinetRepository cabinetRepository, UserRepository userRepository,
                            PatientRepository patientRepository, AuditLogRepository auditLogRepository) {
        this.cabinetRepository = cabinetRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public Statistics statistics() {
        return new Statistics(
                cabinetRepository.count(),
                cabinetRepository.countByActiveTrue(),
                cabinetRepository.countByActiveFalse(),
                userRepository.countByRole(Role.MEDECIN),
                userRepository.countByRole(Role.SECRETAIRE),
                patientRepository.countByArchivedFalse()
        );
    }

    @Transactional(readOnly = true)
    public List<MonthlyValue> cabinetsEvolution(DateRange range) {
        List<Cabinet> cabinets = cabinetRepository.findByCreatedAtBetween(toInstant(range.from()), toInstant(range.to().plusDays(1)));
        return monthlyValues(range, cabinets, Cabinet::getCreatedAt);
    }

    @Transactional(readOnly = true)
    public List<StatusDistribution> cabinetStatusDistribution() {
        return List.of(
                new StatusDistribution("ACTIVE", cabinetRepository.countByActiveTrue()),
                new StatusDistribution("INACTIVE", cabinetRepository.countByActiveFalse())
        );
    }

    @Transactional(readOnly = true)
    public List<UserDistribution> usersDistribution() {
        return List.of(
                new UserDistribution(Role.ADMIN.name(), userRepository.countByRole(Role.ADMIN)),
                new UserDistribution(Role.MEDECIN.name(), userRepository.countByRole(Role.MEDECIN)),
                new UserDistribution(Role.SECRETAIRE.name(), userRepository.countByRole(Role.SECRETAIRE))
        );
    }

    @Transactional(readOnly = true)
    public List<MonthlyValue> registrationsEvolution(DateRange range) {
        List<User> users = userRepository.findByCreatedAtBetween(toInstant(range.from()), toInstant(range.to().plusDays(1)));
        List<Patient> patients = patientRepository.findByCreatedAtBetween(toInstant(range.from()), toInstant(range.to().plusDays(1)));
        Map<YearMonth, Long> counts = new java.util.HashMap<>();
        users.forEach(user -> counts.merge(YearMonth.from(user.getCreatedAt().atZone(ZoneOffset.UTC)), 1L, Long::sum));
        patients.forEach(patient -> counts.merge(YearMonth.from(patient.getCreatedAt().atZone(ZoneOffset.UTC)), 1L, Long::sum));
        return months(range).stream()
                .map(month -> new MonthlyValue(month.format(MONTH_FORMAT), counts.getOrDefault(month, 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Activity> recentActivities(int limit) {
        return auditLogRepository.findRecent(PageRequest.of(0, limit)).stream()
                .map(this::toActivity)
                .toList();
    }

    private Activity toActivity(AuditLog log) {
        return new Activity(log.getId().toString(), log.getAction().name(), log.getEntityType(),
                log.getDescription(), log.getActorName(), log.getOccurredAt());
    }

    private <T> List<MonthlyValue> monthlyValues(DateRange range, List<T> entities,
                                                   Function<T, Instant> createdAt) {
        Map<YearMonth, Long> counts = entities.stream()
                .collect(Collectors.groupingBy(entity -> YearMonth.from(createdAt.apply(entity).atZone(ZoneOffset.UTC)),
                        Collectors.counting()));
        return months(range).stream()
                .map(month -> new MonthlyValue(month.format(MONTH_FORMAT), counts.getOrDefault(month, 0L)))
                .toList();
    }

    private List<YearMonth> months(DateRange range) {
        List<YearMonth> result = new ArrayList<>();
        YearMonth current = YearMonth.from(range.from());
        YearMonth end = YearMonth.from(range.to());
        while (!current.isAfter(end)) {
            result.add(current);
            current = current.plusMonths(1);
        }
        return result;
    }

    private Instant toInstant(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
