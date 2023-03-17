package pro.sky.telegrambot.model;

import pro.sky.telegrambot.exception.NotCorrectDateTimeException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.*;


@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String message;
    private LocalDateTime dateTime;

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    //Извлекает дату, время и сообщение из строки, введенной в телеграмботе
    public void extractMessage(String string) throws NotCorrectDateTimeException {
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\0-9a-zа-яА-ЯA-Z]+)");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            this.message = matcher.group().substring(17);
            this.dateTime = LocalDateTime.parse(matcher.group().substring(0, 16), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } else {
            throw new NotCorrectDateTimeException("date and time entered incorrectly");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, message, dateTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "chatId=" + chatId +
                ", message='" + message + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
