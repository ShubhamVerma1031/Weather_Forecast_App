package com.example.weatherreport

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.weatherreport.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Lucknow")
        SearchCity()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun SearchCity() {
      val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchWeatherData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }


    private fun fetchWeatherData(cityName: String?) {
     val retrofit = Retrofit.Builder()
         .addConverterFactory(GsonConverterFactory.create())
         .baseUrl("https://api.openweathermap.org/data/2.5/")
         .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData("$cityName","53ea3a8f35c566af5deec8d5c0ef8391","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()!!
                if (response.isSuccessful){
                    val temperature = responseBody.main.temp.toString()
                   val humidity = responseBody.main.humidity
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max
                    val weather = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val windSpeed= responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure

                  binding.temprature.text= "$temperature °C"
                    binding.humadity.text = "$humidity %"
                    binding.minTemp.text = "min temp : $minTemp °C"
                    binding.maxTemp.text = "max temp : $maxTemp °C"
                    binding.weather.text = weather
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.condition.text = weather
                    binding.seaLevel.text = "$seaLevel hpa"
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.date.text =date()
                        binding.location.text = "$cityName"
                    changeImagesAccordingToWeather(weather)



                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Enter Correct City Name", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun changeImagesAccordingToWeather(weathers: String) {
        when(weathers){
            "Haze","Partly Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud_bg)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_bg)
                binding.lottieAnimationView.setAnimation(R.raw.sunny_bg)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain)
                binding.lottieAnimationView.setAnimation(R.raw.reiny_bg)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow_bg)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.weather)
                binding.lottieAnimationView.setAnimation(R.raw.sunny_bg)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format((Date()))

    }
    private fun time(timestamp:Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp:Long): String{
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
}