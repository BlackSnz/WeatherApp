package com.example.weatherapp.data.weather

enum class WeatherCondition(val code: Int, val group: String, val description: String, val icon: String) {
    // Thunderstorm
    THUNDERSTORM_WITH_LIGHT_RAIN(200, "Гроза", "Гроза с лёгким дождём", "11d"),
    THUNDERSTORM_WITH_RAIN(201, "Гроза", "Гроза с дождём", "11d"),
    THUNDERSTORM_WITH_HEAVY_RAIN(202, "Гроза", "Гроза с сильным дождём", "11d"),
    LIGHT_THUNDERSTORM(210, "Гроза", "Лёгкая гроза", "11d"),
    THUNDERSTORM(211, "Гроза", "Гроза", "11d"),
    HEAVY_THUNDERSTORM(212, "Гроза", "Сильная гроза", "11d"),
    RAGGED_THUNDERSTORM(221, "Гроза", "Местами грозы", "11d"),
    THUNDERSTORM_WITH_LIGHT_DRIZZLE(230, "Гроза", "Гроза с лёгкой моросью", "11d"),
    THUNDERSTORM_WITH_DRIZZLE(231, "Гроза", "Гроза с моросью", "11d"),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE(232, "Гроза", "Гроза с сильной моросью", "11d"),

    // Drizzle
    LIGHT_INTENSITY_DRIZZLE(300, "Морось", "Слабая морось", "09d"),
    DRIZZLE(301, "Морось", "Морось", "09d"),
    HEAVY_INTENSITY_DRIZZLE(302, "Морось", "Сильная морось", "09d"),
    LIGHT_INTENSITY_DRIZZLE_RAIN(310, "Морось", "Дождь со слабой моросью", "09d"),
    DRIZZLE_RAIN(311, "Морось", "Дождь с моросью", "09d"),
    HEAVY_INTENSITY_DRIZZLE_RAIN(312, "Морось", "Дождь с сильной моросью", "09d"),
    SHOWER_RAIN_AND_DRIZZLE(313, "Морось", "Ливень и морось", "09d"),
    HEAVY_SHOWER_RAIN_AND_DRIZZLE(314, "Морось", "Сильный ливень и морось", "09d"),
    SHOWER_DRIZZLE(321, "Морось", "Ливень с моросью", "09d"),

    // Rain
    LIGHT_RAIN(500, "Дождь", "Лёгкий дождь", "10d"),
    MODERATE_RAIN(501, "Дождь", "Умеренный дождь", "10d"),
    HEAVY_INTENSITY_RAIN(502, "Дождь", "Сильный дождь", "10d"),
    VERY_HEAVY_RAIN(503, "Дождь", "Очень сильный дождь", "10d"),
    EXTREME_RAIN(504, "Дождь", "Экстремально сильный дождь", "10d"),
    FREEZING_RAIN(511, "Дождь", "Ледяной дождь", "13d"),
    LIGHT_INTENSITY_SHOWER_RAIN(520, "Дождь", "Слабый ливень", "09d"),
    SHOWER_RAIN(521, "Дождь", "Ливень", "09d"),
    HEAVY_INTENSITY_SHOWER_RAIN(522, "Дождь", "Сильный ливень", "09d"),
    RAGGED_SHOWER_RAIN(531, "Дождь", "Местами ливень", "09d"),

    // Snow
    LIGHT_SNOW(600, "Снег", "Лёгкий снег", "13d"),
    SNOW(601, "Снег", "Снег", "13d"),
    HEAVY_SNOW(602, "Снег", "Сильный снег", "13d"),
    SLEET(611, "Снег", "Дождь со снегом", "13d"),
    LIGHT_SHOWER_SLEET(612, "Снег", "Лёгкий дождь со снегом", "13d"),
    SHOWER_SLEET(613, "Снег", "Дождь со снегом", "13d"),
    LIGHT_RAIN_AND_SNOW(615, "Снег", "Лёгкий дождь и снег", "13d"),
    RAIN_AND_SNOW(616, "Снег", "Дождь и снег", "13d"),
    LIGHT_SHOWER_SNOW(620, "Снег", "Лёгкий снежная метель", "13d"),
    SHOWER_SNOW(621, "Снег", "Снежный ливень", "13d"),
    HEAVY_SHOWER_SNOW(622, "Снег", "Сильная снежная метель", "13d"),

    // Atmosphere
    MIST(701, "Атмосфера", "Дымка", "50d"),
    SMOKE(711, "Атмосфера", "Дым", "50d"),
    HAZE(721, "Атмосфера", "Мгла", "50d"),
    DUST_WHIRLS(731, "Атмосфера", "Вихри песка", "50d"),
    FOG(741, "Атмосфера", "Туман", "50d"),
    SAND(751, "Атмосфера", "Песок", "50d"),
    DUST(761, "Атмосфера", "Пыль", "50d"),
    VOLCANIC_ASH(762, "Атмосфера", "Вулканический пепел", "50d"),
    SQUALLS(771, "Атмосфера", "Шквалы", "50d"),
    TORNADO(781, "Атмосфера", "Торнадо", "50d"),

    // Clear
    CLEAR_SKY_DAY(800, "Ясно", "Ясное небо", "01d"),
    CLEAR_SKY_NIGHT(800, "Ясно", "Ясное небо", "01n"),

    // Clouds
    FEW_CLOUDS_DAY(801, "Облака", "Малооблачно", "02d"),
    FEW_CLOUDS_NIGHT(801, "Облака", "Малооблачно", "02n"),
    SCATTERED_CLOUDS_DAY(802, "Облака", "Рассеянные облака", "03d"),
    SCATTERED_CLOUDS_NIGHT(802, "Облака", "Рассеянные облака", "03n"),
    BROKEN_CLOUDS_DAY(803, "Облака", "Разорванные облака", "04d"),
    BROKEN_CLOUDS_NIGHT(803, "Облака", "Разорванные облака", "04n"),
    OVERCAST_CLOUDS_DAY(804, "Облака", "Сплошная облачность", "04d"),
    OVERCAST_CLOUDS_NIGHT(804, "Облака", "Сплошная облачность", "04n");

    companion object {
        fun byCode(value: Int): WeatherCondition? {
            return entries.find { it.code == value }
        }
    }
}
