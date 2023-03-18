package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.NotCorrectDateTimeException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    //Ищет уведомления в базе данных, чья дата и время совпадают с текущим
    public List<NotificationTask> getNotificationTasksByDateTime() {
        return notificationTaskRepository.findAll()
                .stream()
                .filter(notificationTask -> notificationTask
                        .getDateTime().equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .collect(Collectors.toList());
    }

    // Раз в минуту отправляет уведомления, если пришло время
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationTask() {
        TelegramBot telegramBot = new TelegramBot
                ("6031020322:AAHsmmxDxz507mBvqA-1KvCXn1EXfyFrKZk");
        for (NotificationTask notificationTask : getNotificationTasksByDateTime()) {
            if (notificationTask != null) {
                SendMessage message = new SendMessage
                        (notificationTask.getChatId(), notificationTask.getMessage());
                SendResponse sendResponse = telegramBot.execute(message);
            }
        }
    }

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
        TelegramBot bot = new TelegramBot
                ("6031020322:AAHsmmxDxz507mBvqA-1KvCXn1EXfyFrKZk");
        for (Update update : updates) {
            if (update.message().text().equals("/start")) {
                SendMessage message = new SendMessage
                        (update.message().chat().id(),
                                "hello, enter datetime and task which you need to remind. Datetime example 01.01.2023 01:00 ");
                SendResponse sendResponse = bot.execute(message);
            } else {
                Long notificationTaskRepositorySize = notificationTaskRepository.count();
                try {
                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.extractMessage(update.message().text());
                    notificationTask.setChatId(update.message().chat().id());
                    notificationTaskRepository.save(notificationTask);
                    if (notificationTaskRepositorySize != notificationTaskRepository.count()) {
                        SendMessage message = new SendMessage
                                (update.message().chat().id(),
                                        "it's ok!");
                        SendResponse sendResponse = bot.execute(message);
                    }
                } catch (NotCorrectDateTimeException e) {
                    SendMessage message = new SendMessage
                            (update.message().chat().id(),
                                    e.getMessage());
                    SendResponse sendResponse = bot.execute(message);
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
