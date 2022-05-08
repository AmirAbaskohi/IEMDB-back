CREATE TABLE IF NOT EXISTS `actor_movie` (
  `actorId` int NOT NULL,
  `movieId` int NOT NULL,
  PRIMARY KEY (`actorId`,`movieId`),
  KEY `movie_fk_idx` (`movieId`),
  CONSTRAINT `actor_fk` FOREIGN KEY (`actorId`) REFERENCES `actor` (`id`),
  CONSTRAINT `movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;