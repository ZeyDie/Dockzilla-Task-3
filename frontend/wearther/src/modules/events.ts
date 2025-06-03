import {cityInput, searchBtn} from './constants.js';
import {handleSearch} from './app.js';

searchBtn.addEventListener('click', handleSearch);
cityInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSearch();
});