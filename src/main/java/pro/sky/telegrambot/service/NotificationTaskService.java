package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    //Ищет уведомления в базе данных, чья дата и время совпадают с текущим
    public List<NotificationTask> getNotificationTasksByDateTime() {
        return notificationTaskRepository.getNotificationTasksByDateTime(LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES));
    }

    public void addNotificationTask(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }

    public Long getCount() {
        return notificationTaskRepository.count();
    }
}
