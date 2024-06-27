package org.msolutions.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.json.JSONObject;

public class WeatherForecast {
    private static final String WEATHER_PROPERTIES = "weather.properties";
    private static final String TEMPERATURE = "temperature.value";
    private static final String WIND_SPD = "wind_spd.value";
    private static final String HUMIDITY = "humidity.value";
    private static final String DESCRIPTION = "description.value";
    private static final String LAST_UPDATE = "last_update.value";

    private static Properties properties;

    private static void saveInfo() {
        try {
            properties.store(new FileOutputStream(WEATHER_PROPERTIES), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void weatherManager() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(WEATHER_PROPERTIES));
        } catch (IOException e) {
            // Если файл настроек не найден, создадим его с настройками по умолчанию
            updateWeather();
        }

        LocalDateTime lastUpdate = LocalDateTime.parse(properties.getProperty(LAST_UPDATE));
        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(lastUpdate, currentTime);
        long timeDifference = duration.toMillis();

        if (timeDifference > 3600000) {
            updateWeather();

            // Создаем таймер
            Timer timer = new Timer();

            // Определяем задачу, которая будет выполняться каждый час
            TimerTask weatherUpdateTask = new TimerTask() {
                public void run() {
                    updateWeather();
                }
            };

            // Запускаем таймер, чтобы задача выполнялась каждый час
            timer.scheduleAtFixedRate(weatherUpdateTask, 0, 3600000);
        }
    }

    public static List<String> getWeather() {
        weatherManager();
        List<String> weatherData = new ArrayList<>();

        weatherData.add(properties.getProperty(DESCRIPTION));
        weatherData.add(properties.getProperty(TEMPERATURE));
        weatherData.add(properties.getProperty(WIND_SPD));
        weatherData.add(properties.getProperty(HUMIDITY));

        return weatherData;
    }

    private static void updateWeather() {
        try {
            String city = "Minsk";
            // Строим URL-запрос к WeatherBit API
            String apiKey = "911f22194b4545f1b719a5230cbfe794";
            String apiUrl = "https://api.weatherbit.io/v2.0/current?city=" + city + "&key=" + apiKey + "&lang=ru";
            URL url = new URL(apiUrl);

            // Отправляем запрос и получаем ответ
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем ответ и парсим JSON
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                JSONObject json = new JSONObject(response.toString());
                JSONObject data = json.getJSONArray("data").getJSONObject(0);

                JSONObject weatherObject = data.getJSONObject("weather");
                String weatherDescription = weatherObject.getString("description");

                properties.setProperty(DESCRIPTION, weatherDescription);

                properties.setProperty(TEMPERATURE, String.valueOf(Math.round(data.getDouble("temp") * 10.0) / 10.0));
                properties.setProperty(WIND_SPD, String.valueOf(Math.round(data.getDouble("wind_spd") * 10.0) / 10.0));
                properties.setProperty(HUMIDITY, String.valueOf(Math.round(data.getDouble("rh") * 10.0) / 10.0));

                properties.setProperty(LAST_UPDATE, String.valueOf(LocalDateTime.now()));

                saveInfo();

                System.out.println("Погода обновлена");
            } else {
                System.out.println("Ошибка при получении данных о погоде: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("Не удалось загрузить погоду");
            //e.printStackTrace();
        }
    }
}
