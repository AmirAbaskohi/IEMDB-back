CREATE TABLE IF NOT EXISTS `genre_movie` (
  `movieId` int NOT NULL,
  `genreId` int NOT NULL,
  PRIMARY KEY (`movieId`,`genreId`),
  KEY `has_genre_genre_fk_idx` (`genreId`),
  CONSTRAINT `genre_movie_genre_fk` FOREIGN KEY (`genreId`) REFERENCES `genre` (`id`),
  CONSTRAINT `genre_movie_movie_Fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;