export interface WeatherData {
    city: string;
    country: string;
    temperatures: Record<string, number>;
}