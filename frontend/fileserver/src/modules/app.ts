import {fetchWeatherData} from './api';
import {renderTemperatureChart, renderWeatherInfo} from './render';
import {setLoading, showError} from './utils';
import {cityInput, errorMessage, weatherInfo} from './constants';

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