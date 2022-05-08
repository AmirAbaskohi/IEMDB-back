CREATE TABLE IF NOT EXISTS `rate` (
  `userEmail` varchar(50) NOT NULL,
  `movieId` int NOT NULL,
  `score` int DEFAULT NULL,
  PRIMARY KEY (`userEmail`,`movieId`),
  KEY `rate_movie_fk_idx` (`movieId`),
  CONSTRAINT `rate_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE,
  CONSTRAINT `rate_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;