import {WeatherData} from './types';

export async function fetchWeatherData(city: string): Promise<WeatherData> {
    const url = `http://localhost:10000/weather?city=${encodeURIComponent(city)}`;
    const response = await fetch(url);

    if (!response.ok) {
        throw new Error(await response.text());
    }
    return response.json();
}