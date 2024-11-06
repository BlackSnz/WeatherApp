<h1 align="center" id="title">Simple Weather</h1>

<p id="description">The main idea behind this app is to keep the design simple using mostly monochrome colors and only including the necessary functions for displaying the main weather factors.</p>
<p></p>
<p>In app project used:</p> 
<ul>
  <li><a href="https://developer.android.com/topic/architecture#recommended-app-arch">VMMW model</a></li>
  <li><a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt</a></li>
  <li><a href="https://developer.android.com/training/data-storage/room">Room</a> for cache results from API for save API calls limit</li>
  <li><a href="https://github.com/square/retrofit">Retrofit</a></li>
  <li><a href="https://github.com/airbnb/lottie-android">Lottie</a> for a wether loading progress bar animation</li>
</ul>
<p></p>
<p>Source of weather data is <a href="https://openweathermap.org/api">OpenWeather API</a>. But the app architecture allows you to ease change source of data.</p>
<p>To get the correct application work, you need to insert your OpenWeather API key into the <b>network/Weather API Service</b>.</p>
<h2>Project Screenshots:</h2>
<img src="https://i.imgur.com/HIZ0thj.png" alt="project-screenshot" width="225" height="500/">
