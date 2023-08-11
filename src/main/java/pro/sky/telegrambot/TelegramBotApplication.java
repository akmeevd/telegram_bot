package pro.sky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import pro.sky.telegrambot.exception.NotCorrectDateTimeException;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {

	public static void main(String[] args) throws NotCorrectDateTimeException {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

}
