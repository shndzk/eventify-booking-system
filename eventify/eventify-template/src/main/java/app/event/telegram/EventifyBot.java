package app.event.telegram;

import app.event.repository.TelegramLinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.annotation.PostConstruct;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class EventifyBot extends TelegramLongPollingBot {

    private final TelegramLinkRepository linkRepo;
    private final String botToken;
    private final String botName;

    public EventifyBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.name}") String botName,
                       TelegramLinkRepository linkRepo) {
        super(botToken);
        this.botToken = botToken;
        this.botName = botName;
        this.linkRepo = linkRepo;
    }

    @PostConstruct
    public void registerBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String code = text.replace("/start ", "").trim();

            var linkOpt = linkRepo.findByLinkCode(code);
            if (linkOpt.isPresent()) {
                var link = linkOpt.get();
                link.setChatId(chatId);
                link.setLinked(true);
                linkRepo.save(link);
                sendText(chatId, "✅ Аккаунт привязан! Теперь уведомления будут приходить сюда.");
            } else if (text.startsWith("/start")) {
                sendText(chatId, "❌ Код не найден. Получите его в приложении.");
            }
        }
    }

    private void sendText(Long chatId, String text) {
        try {
            execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() { return botName; }

    @Override
    public String getBotToken() { return botToken; }
}
