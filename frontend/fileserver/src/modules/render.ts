import {cityName, countryName, getChart, weatherInfo} from './constants';
import {WeatherData} from './types';
import * as echarts from "echarts";

export function renderWeatherInfo(data: WeatherData) {
    cityName.textContent = data.city;
    countryName.textContent = data.country;
    weatherInfo.style.display = 'block';
}

export function renderTemperatureChart(temperatures: Record<string, number>) {
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

    const transformedTemps: (number | undefined)[] = temps.map(temp => temp === null ? undefined : temp);

    var chart = getChart();

    const option: echarts.EChartOption = {
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
            data: transformedTemps,
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
    chart.resize();

    window.addEventListener('resize', () => chart?.resize());
}