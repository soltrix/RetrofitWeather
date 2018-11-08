package ru.geekbrains.retrofit;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.geekbrains.retrofit.interfaces.OpenWeather;
import ru.geekbrains.retrofit.model.WeatherRequest;

public class MainActivity extends AppCompatActivity {

    private OpenWeather openWeather;
    private TextView textTemp;              // Температура
    private TextInputEditText editCity;
    private TextInputEditText editApiKey;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRetrofit();
        initGui();
        initPreferences();
        initEvents();
     }

    private void initPreferences() {
        sharedPref = getPreferences(MODE_PRIVATE);
        loadPreferences();    // загрузить настройки
    }

    // проинициализировать пользовательские элементы
    private void initGui() {
        textTemp = findViewById(R.id.textTemp);
        editApiKey = findViewById(R.id.editApiKey);
        editCity = findViewById(R.id.editCity);
    }

    // Здесь создадим обработку нажатия на кнопку
    private void initEvents() {
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();    // сохранить настройки
                requestRetrofit(editCity.getText().toString(), editApiKey.getText().toString());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savePreferences();
    }

    // сохранить настройки
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("apiKey", editApiKey.getText().toString());
        editor.apply();
    }

    // загрузить настройки
    private void loadPreferences() {
        String loadedApiKey = sharedPref.getString("apiKey", "29fb8d6056fe0f462c0731feda71d342");
        editApiKey.setText(loadedApiKey);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/") // Базовая часть адреса
                // Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Создаем объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequest> call,
                                           @NonNull Response<WeatherRequest> response) {
                        if (response.body() != null)
                            textTemp.setText(Float.toString(response.body().getMain().getTemp()));
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequest> call, @NonNull Throwable t) {
                        textTemp.setText("Error");
                    }
                });
    }
}
