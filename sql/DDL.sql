CREATE database deisIMDB;
GO

USE deisIMDB;
GO

DROP TABLE IF EXISTS movies;
GO
CREATE TABLE movies(
     movieID int NOT NULL
    ,movieName VARCHAR(150) NOT NULL
    ,movieDuration DECIMAL(5,2) DEFAULT 0 NULL
    ,movieBudget DECIMAL(15,2) DEFAULT 0 NULL
    ,movieReleaseDate VARCHAR(12) DEFAULT NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.movies in /work/movies.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1 -m 1 -e /work/bcp_errors.log


SELECT * FROM movies;

DROP TABLE IF EXISTS movie_votes;
GO
CREATE TABLE movie_votes(
     movieId int NOT NULL
    ,movieRating DECIMAL(3,1) DEFAULT 0 NULL
    ,movieRatingCount int DEFAULT 0 NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.movie_votes in /work/movie_votes.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1

SELECT * FROM movie_votes;

DROP TABLE IF EXISTS genres;
GO
CREATE TABLE genres(
     genreID int NOT NULL
    ,genreName varchar(50) NOT NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.genres in /work/genres.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1

SELECT * FROM genres;

DROP TABLE IF EXISTS actors;
GO
CREATE TABLE actors(
     actorID int NOT NULL
    ,actorName varchar(100) NOT NULL
    ,actorGender char(1) DEFAULT NULL
    ,movieID int NOT NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.actors in /work/actors.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1

SELECT * FROM actors;

DROP TABLE IF EXISTS directors;
GO

CREATE TABLE directors(
     directorID int NOT NULL
    ,directorName varchar(100) NOT NULL
    ,movieID int NOT NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.directors in /work/directors.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1

SELECT * FROM directors;

DROP TABLE IF EXISTS genres_movies;
GO

CREATE TABLE genres_movies(
     genreID int NOT NULL
    ,movieID int NOT NULL
);
GO
-- Import
-- docker run --rm -v ${PWD}:/work --entrypoint /opt/mssql-tools18/bin/bcp mcr.microsoft.com/mssql/server:2022-latest deisIMDB.dbo.genres_movies in /work/genres_movies.csv -S host.docker.internal,1433 -U sa -P "YourStrong!Passw0rd" -u -c -t "," -r "\n" -F 1 -m 1 -e /work/bcp_errors.log

SELECT * FROM genres_movies;