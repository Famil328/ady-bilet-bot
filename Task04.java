import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Task04 {

    // 1. Dəyişənlər:
    private static final String BOT_TOKEN = "7123456789:AAEfghIJKlmNoPQRsTUVwxyZ1234567";
    private static final String CHAT_ID = "5180189099"; // @userinfobot-un verdiyi ID təyin olundu
    private static final String ADY_API_URL = "https://ady.az";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
        System.out.println("ADY Bilet İzləyicisi İşə Düşdü...");

        // Botun işlədiyini anında yoxlamaq üçün test mesajı göndərir:
        sendTelegramNotification("Test mesajı: Bot uğurla qoşuldu!");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Hər 30 saniyədən bir avtomatik yoxlayır
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkTicketsAndNotify();
            } catch (Exception e) {
                System.err.println("Xəta baş verdi: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    private static void checkTicketsAndNotify() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ADY_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        // Biletin olmasını yoxlayır
        boolean biletVar = responseBody.contains("\"available_seats\":") && !responseBody.contains("\"available_seats\":0");

        if (biletVar) {
            String message = "🚨 DİQQƏT! Biletlər satışa çıxdı! Dərhal ADY tətbiqinə daxil olun.";
            sendTelegramNotification(message);
            System.out.println("Bilet tapıldı və Telegram-a bildiriş göndərildi!");
        } else {
            System.out.println("Bilet yoxdur... Yoxlanıldı: " + LocalTime.now());
        }
    }

    private static void sendTelegramNotification(String text) {
        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    BOT_TOKEN, CHAT_ID, URLEncoder.encode(text, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Telegram mesajı xətası: " + e.getMessage());
        }
    }
}