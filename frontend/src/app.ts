declare const echarts: {
    init(
        dom: HTMLElement,
        theme?: string,
        opts?: { width?: number; height?: number }
    ): {
        setOption(option: any): void;
        dispose(): void;
        resize(): void;
    };
};

interface WeatherData {
    city: string;
    country: string;
    temperatures: Record<string, number>;
}

// DOM elements
const cityInput = document.getElementById('city-input') as HTMLInputElement;
const searchBtn = document.getElementById('search-btn') as HTMLButtonElement;
const errorMessage = document.getElementById('error-message') as HTMLDivElement;
const weatherInfo = document.getElementById('weather-info') as HTMLDivElement;
const cityName = document.getElementById('city-name') as HTMLHeadingElement;
const countryName = document.getElementById('country-name') as HTMLHeadingElement;
const chartContainer = document.getElementById('temperature-chart') as HTMLDivElement;

let chart: any = null;

// Event listeners
searchBtn.addEventListener('click', handleSearch);
cityInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSearch();
});

async function handleSearch() {
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

async function fetchWeatherData(city: string): Promise<WeatherData> {
    const url = `http://localhost:8080/weather?city=${encodeURIComponent(city)}`;
    const response = await fetch(url);

    if (!response.ok) {
        throw new Error(await response.text());
    }
    return response.json();
}

function renderWeatherInfo(data: WeatherData) {
    cityName.textContent = data.city;
    countryName.textContent = data.country;
    weatherInfo.style.display = 'block';
}

function renderTemperatureChart(temperatures: Record<string, number>) {
    const now = new Date();
    const currentHour = now.getHours();

    const {hours, temps} = Array.from({length: 24}, (_, i) => {
        const hourValue = (currentHour + i) % 24;
        const hour = `${hourValue.toString().padStart(2, '0')}:00`;

        const temp = Object.entries(temperatures).find(([time]) => {
            const date = new Date(time);
            const targetFullDate = new Date(now.getTime() + i * 60 * 60 * 1000);
            return date.getHours() === hourValue &&
                date.getDate() === targetFullDate.getDate() &&
                date.getMonth() === targetFullDate.getMonth() &&
                date.getFullYear() === targetFullDate.getFullYear();
        })?.[1] ?? null;

        return {hour, temp};
    }).reduce((acc, {hour, temp}) => ({
        hours: [...acc.hours, hour],
        temps: [...acc.temps, temp]
    }), {hours: [] as string[], temps: [] as (number | null)[]});

    if (chart) {
        chart.dispose();
    }

    chart = echarts.init(chartContainer);

    const option = {
        title: {
            text: `Прогноз температуры на ближайшие 24 часа (начиная с ${hours[0]})`,
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            formatter: (params: any) => {
                const value = params[0].value;
                return value !== null
                    ? `${params[0].axisValue}: ${value}°C`
                    : `${params[0].axisValue}: Нет данных`;
            }
        },
        xAxis: {
            type: 'category',
            data: hours,
            axisLabel: {
                rotate: 45,
                interval: 0
            }
        },
        yAxis: {
            type: 'value',
            name: 'Температура (°C)',
            axisLine: {show: true}
        },
        series: [{
            type: 'line',
            name: 'Температура',
            connectNulls: true,
            data: temps,
            itemStyle: {
                color: '#5470C6'
            },
            lineStyle: {
                width: 3
            },
            symbolSize: 8
        }],
        grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            containLabel: true
        }
    };

    chart.setOption(option);
    window.addEventListener('resize', () => chart?.resize());
}

function setLoading(isLoading: boolean) {
    searchBtn.disabled = isLoading;
    searchBtn.textContent = isLoading ? 'Загрузка...' : 'Поиск';
    errorMessage.textContent = '';
}

function showError(message: string) {
    errorMessage.textContent = message;
}