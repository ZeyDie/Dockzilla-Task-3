import {errorMessage, searchBtn} from './constants.js';

export function setLoading(isLoading: boolean) {
    searchBtn.disabled = isLoading;
    searchBtn.textContent = isLoading ? 'Загрузка...' : 'Поиск';
    errorMessage.textContent = '';
}

export function showError(message: string) {
    errorMessage.textContent = message;
}