import {fetchWeatherData} from './api.js';
import {renderTemperatureChart, renderWeatherInfo} from './render.js';
import {setLoading, showError} from './utils.js';
import {cityInput, errorMessage, weatherInfo} from './constants.js';

export async function handleSearch() {
    const city = cityInput.value.trim();

    if (!city) {
        showError('Пожалуйста, введите город!');
        return;
    }

    try {
        setLoading(true);
        errorMessage.textContent = '';

        const weatherData = await fetchWeatherData(city);
        renderWeatherInfo(weatherData);
        renderTemperatureChart(weatherData.temperatures);
    } catch (error) {
        showError(error instanceof Error ? error.message : 'Не удалось получить данные');
        weatherInfo.style.display = 'none';
    } finally {
        setLoading(false);
    }
}