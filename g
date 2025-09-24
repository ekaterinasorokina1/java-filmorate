*   [33m8a34a6a[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32madd-recommendations[m[33m, [m[1;31morigin/develop[m[33m, [m[1;32mdevelop[m[33m)[m Merge pull request #9 from ekaterinasorokina1/add-reviews
[31m|[m[32m\[m  
[31m|[m * [33m1d004a8[m[33m ([m[1;31morigin/add-reviews[m[33m)[m Refactor: перенёс проверку filmId на null в SQL-запрос, фильтрация теперь опциональна
[31m|[m * [33m195a718[m Refactor: убрать лишнее обращение в базу в ReviewService.create
[31m|[m * [33me3d7d3d[m fix: Увеличение и уменьшение поля useful при созданий лайка и дизлайка на отзыв
[31m|[m * [33m1d112a7[m fix: Удаление лайка из таблицы при созданий дизлайка
[31m|[m * [33m0728227[m Добавить функционал отзывов
[31m|[m[31m/[m  
*   [33m6605279[m Merge pull request #8 from ekaterinasorokina1/add-most-populars
[33m|[m[34m\[m  
[33m|[m * [33m1fd9890[m[33m ([m[1;31morigin/add-most-populars[m[33m)[m Вывод самых популярных фильмов по жанру и годам. 2 SP
[33m|[m * [33m2a2b48f[m Вывод самых популярных фильмов по жанру и годам. 2 SP
[33m|[m[33m/[m  
*   [33m3da2481[m Merge pull request #7 from ekaterinasorokina1/add-common-films
[35m|[m[36m\[m  
[35m|[m * [33m0d64d84[m[33m ([m[1;31morigin/add-common-films[m[33m)[m Добавить сортировку по популярности для общих фильмов
[35m|[m *   [33m6305618[m Merge remote-tracking branch 'origin/develop' into add-common-films
[35m|[m [1;31m|[m[35m\[m  
[35m|[m [1;31m|[m[35m/[m  
[35m|[m[35m/[m[1;31m|[m   
* [1;31m|[m [33m2d4a96b[m refactoring(develop): dto распределены по пакетам. Сокращение запросов с Optional
[1;32m|[m * [33m1d27751[m Добавить функционал получения общих фильмов по лайкам
[1;32m|[m[1;32m/[m  
*   [33m9e68c7a[m[33m ([m[1;31morigin/main[m[33m, [m[1;31morigin/HEAD[m[33m, [m[1;32mmain[m[33m)[m Merge pull request #6 from ekaterinasorokina1/add-database
[1;33m|[m[1;34m\[m  
[1;33m|[m * [33m8a6890b[m[33m ([m[1;31morigin/add-database[m[33m)[m fix(add-database): удален лишний код и enum. в запросе на получение фильмов сразу получаем рейтинг
[1;33m|[m * [33m6d67ab9[m fix(add-database): стили код
[1;33m|[m * [33md3280e5[m fix(add-database): стили код
[1;33m|[m * [33m0347938[m fix(add-database): стили код
[1;33m|[m * [33m6db213d[m fix(add-database): стили код
[1;33m|[m * [33me1fd752[m fix(add-database): стили код
[1;33m|[m * [33m60cb276[m fix(add-database): стили код
[1;33m|[m * [33m103bf98[m fix(add-database): стили код
[1;33m|[m * [33ma7c5749[m feat(add-database): добавлены тесты
[1;33m|[m * [33m15cccf5[m feat(add-database): стили кода
[1;33m|[m * [33m32e399b[m feat(add-database): работа с БД
[1;33m|[m * [33m7274aee[m Update README.md
[1;33m|[m * [33m5e25d6c[m[33m ([m[1;31morigin/add-genre-rating[m[33m)[m fix(add-genre-rating): исправлены тесты
[1;33m|[m * [33m23a6e21[m fix(add-genre-rating): добавлены рейтинг и жанры для фильмов и статус для друзей
[1;33m|[m[1;33m/[m  
*   [33m6efa418[m Merge pull request #2 from ekaterinasorokina1/add-friends-likes
[1;35m|[m[1;36m\[m  
[1;35m|[m * [33meae8f1f[m[33m ([m[1;31morigin/add-friends-likes[m[33m)[m fix(add-friends-likes): добавлены модификаторы,default значение count, сортировка в stream
[1;35m|[m * [33m828e62d[m fix(add-friends-likes): fix модификаторов
[1;35m|[m * [33m0b55a40[m feat(add-friends-likes): добавлены новые эндпойнты по работе с друзьями и лайками
[1;35m|[m[1;35m/[m  
*   [33md5eb501[m Merge pull request #1 from ekaterinasorokina1/controllers-films-users
[31m|[m[32m\[m  
[31m|[m * [33ma0821e2[m[33m ([m[1;31morigin/controllers-films-users[m[33m)[m feat(controllers-films-users): кастомные аннотации валидируются с помощью spring validation
[31m|[m * [33m7b3f709[m feat(controllers-films-users): добавлены кастомные аннотации MinReleaseDate и NotEmptySpaces. Для поля description добавлена аннотация @Size(max = 200)
[31m|[m * [33m0b298ae[m feat(controllers-films-users): тесты переписала с SpringBootTest
[31m|[m * [33m91adc52[m feat(controllers-films-users): токены вынесены из тестовых классов
[31m|[m * [33m3ea1837[m feat(controllers-films-users): реализованы контроллеры FilmController и UserController с эндпоинты get, post, put
[31m|[m[31m/[m  
* [33ma710f11[m Initial commit
