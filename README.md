Установка NodeJS Windows:

```shell
winget install OpenJS.NodeJS.LTS
```

Установка NodeJS Linux:

```shell
apt install npm
```

Запуск бэкенда:

```
./gradlew run
```

Доступен по умолчанию по порту 8100. 

Реализация кэширования реализована с помощью CacheLoading из библиотеки Guava. Период срока актуальности данных - 15 минут, согласно условию ТЗ.
Поддержка кириллицы в запросе на внешний API. Нет сортировки по странам, т.к. если задать запрос "Москва", API open-meteo может передать данные из Таджикистана. Считаю погрешностью внешнего API. В ТЗ нет условия по явной достоверности отношений "город-страна"

Доступная URL: http://localhost:8100/weather?city=Октябрьский

**Frontend**: TypeScript, HTML, CSS.

**Backend**: Java.
*Libraries*: *Lombok, Jetbrains Annotations, Gson, Guava*