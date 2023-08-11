package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;

@Service
public class NotificationTaskScheduler {

    @Value("${telegram.bot.token}")
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskService notificationTaskService;

    // Раз в минуту отправляет уведомление, если пришло время
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationTask() {
        for (NotificationTask notificationTask : notificationTaskService.getNotificationTasksByDateTime()) {
            if (notificationTask != null) {
                SendMessage message = new SendMessage
                        (notificationTask.getChatId(), notificationTask.getMessage());
                SendResponse sendResponse = telegramBot.execute(message);
            }
        }
    }
}
