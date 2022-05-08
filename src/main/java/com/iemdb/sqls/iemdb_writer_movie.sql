CREATE TABLE IF NOT EXISTS `writer_movie` (
  `writerId` int NOT NULL,
  `movieId` int NOT NULL,
  PRIMARY KEY (`writerId`,`movieId`),
  KEY `write_movie_movie_fk_idx` (`movieId`),
  CONSTRAINT `write-_movie_writer_fk` FOREIGN KEY (`writerId`) REFERENCES `writer` (`id`),
  CONSTRAINT `write_movie_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;