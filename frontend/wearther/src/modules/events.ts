import {cityInput, searchBtn} from './constants';
import {handleSearch} from './app';

searchBtn.addEventListener('click', handleSearch);
cityInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleSearch();
});