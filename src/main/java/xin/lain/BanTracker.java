package xin.lain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanTracker {

    public static int staffTotal = 0;
    public static int watchdogTotal = 0;

    public static int previousStaffTotal = 0;
    public static int previousWatchdogTotal = 0;

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(BanTracker::apiTask, 0, 3, TimeUnit.SECONDS);
    }

    public static void apiTask() {
        try {
            String apiUrl = "https://api.plancke.io/hypixel/v1/punishmentStats";
            URL url = new URL(apiUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36";
            conn.setRequestProperty("User-Agent", userAgent);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject record = jsonObject.getJSONObject("record");
            staffTotal = record.getInt("staff_total");
            watchdogTotal = record.getInt("watchdog_total");

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (staffTotal > previousStaffTotal) {
                System.out.println("[" + currentTime.format(formatter) + "] 新增 staff ban 玩家: " + (staffTotal - previousStaffTotal));
            }
            if (watchdogTotal > previousWatchdogTotal) {
                System.out.println("[" + currentTime.format(formatter) + "] 新增 watchdog ban 玩家: " + (watchdogTotal - previousWatchdogTotal));
            }

            previousStaffTotal = staffTotal;
            previousWatchdogTotal = watchdogTotal;

            conn.disconnect();
        } catch (IOException e) {
            System.out.println("[程序出现错误] " + e);
        }
    }
}
