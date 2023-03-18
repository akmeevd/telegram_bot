package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.NotCorrectDateTimeException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Value("${telegram.bot.token}")
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskService notificationTaskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
        });
        for (Update update : updates) {
            if (update.message().text().equals("/start")) {
                SendMessage message = new SendMessage
                        (update.message().chat().id(),
                                "hello, enter datetime and task which you need to remind. Datetime example 01.01.2023 01:00 ");
                SendResponse sendResponse = telegramBot.execute(message);
            } else {
                Long notificationTaskRepositorySize = notificationTaskService.getCount();
                try {
                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.extractMessage(update.message().text());
                    notificationTask.setChatId(update.message().chat().id());
                    notificationTaskService.addNotificationTask(notificationTask);
                    if (!Objects.equals(notificationTaskRepositorySize, notificationTaskService.getCount())) {
                        SendMessage message = new SendMessage
                                (update.message().chat().id(),
                                        "it's ok!");
                        SendResponse sendResponse = telegramBot.execute(message);
                    }
                } catch (NotCorrectDateTimeException e) {
                    SendMessage message = new SendMessage
                            (update.message().chat().id(),
                                    e.getMessage());
                    SendResponse sendResponse = telegramBot.execute(message);
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
