package sk.fri.uniza;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sk.fri.uniza.api.WeatherStationService;
import sk.fri.uniza.model.WeatherData;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class IotNode {
    private final Retrofit retrofit;
    private final WeatherStationService weatherStationService;


    public IotNode() {

        retrofit = new Retrofit.Builder()
                // Url adresa kde je umietnená WeatherStation služba
                .baseUrl("http://localhost:9000/")
                // Na konvertovanie JSON objektu na java POJO použijeme
                // Jackson knižnicu
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        // Vytvorenie inštancie komunikačného rozhrania
        weatherStationService = retrofit.create(WeatherStationService.class);

    }

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    public Optional<Double> getAverageTemperature(String station, String from, String to) {
        Call<List<WeatherData>> historyWeather =
                weatherStationService.getHistoryWeather(station,from,to);

        try {
            Response<List<WeatherData>> response = historyWeather.execute();

            if (response.isSuccessful()) { // Dotaz na server bol neúspešný
                //Získanie údajov vo forme Zoznam lokacií
                List<WeatherData> body = response.body();
                System.out.println(body);

                return Optional.of(body.stream().mapToDouble(weatherData -> weatherData.getAirTemperature().doubleValue()).sum()/body.size());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
