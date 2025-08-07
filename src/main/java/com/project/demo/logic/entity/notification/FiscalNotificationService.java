package com.project.demo.logic.entity.notification;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FiscalNotificationService {

    @Autowired
    private FiscalCalendarRepository fiscalCalendarRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNotificationStatusRepository statusRepository;

    @Scheduled(cron = "0 * * * * ?")
    public void checkFiscalDeadlines() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        List<FiscalCalendar> dueEvents = fiscalCalendarRepository
                .findByTaxDeclarationDeadlineBetween(today.plusDays(1), threeDaysLater);


        for (FiscalCalendar event : dueEvents) {
            if (!notificationExistsForEvent(event)) {
                createNotificationForEvent(event);
            }
        }
    }

    private boolean notificationExistsForEvent(FiscalCalendar event) {
        return notificationRepository.existsByTypeAndCloseDate(
                "Informativa",
                event.getTaxDeclarationDeadline());
    }

    private void createNotificationForEvent(FiscalCalendar event) {
        Notification notification = new Notification();
        notification.setName("Recordatorio Fiscal: " + event.getName());

        long daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(),
                event.getTaxDeclarationDeadline());

        String message = String.format(
                "Recordatorio "+event.getType()+": %s. Fecha límite: %s (%d %s restantes)",
                event.getDescription(),
                event.getTaxDeclarationDeadline().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                daysLeft,
                daysLeft == 1 ? "día" : "días");

        notification.setDescription(message);
        notification.setType("Informativa");
        notification.setState("Activa");
        notification.setCloseDate(event.getTaxDeclarationDeadline());

        Notification savedNotification = notificationRepository.save(notification);
        sendNotificationToAllUsers(savedNotification);
    }

    private void sendNotificationToAllUsers(Notification notification) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserNotificationStatus status = new UserNotificationStatus();
            status.setUser(user);
            status.setNotification(notification);
            status.setRead(false);
            statusRepository.save(status);

            // Opcional: Enviar también por correo electrónico
            sendEmailNotification(user.getEmail(), notification);
        }
    }

    private void sendEmailNotification(String email, Notification notification) {
        // Implementación del servicio de correo electrónico
        // (Necesitarías configurar un servicio de email aparte)
    }

}
