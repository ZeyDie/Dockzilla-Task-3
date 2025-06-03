import {ECharts} from "echarts";

export const cityInput = document.getElementById('city-input') as HTMLInputElement;
export const searchBtn = document.getElementById('search-btn') as HTMLButtonElement;
export const errorMessage = document.getElementById('error-message') as HTMLDivElement;
export const weatherInfo = document.getElementById('weather-info') as HTMLDivElement;
export const cityName = document.getElementById('city-name') as HTMLHeadingElement;
export const countryName = document.getElementById('country-name') as HTMLHeadingElement;
export const chartContainer = document.getElementById('temperature-chart') as HTMLDivElement;

const chartInstance: ECharts = echarts.init(chartContainer);

export const getChart = () => chartInstance;