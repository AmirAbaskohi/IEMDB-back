CREATE TABLE IF NOT EXISTS `watchlist` (
  `movieId` int NOT NULL,
  `userEmail` varchar(45) NOT NULL,
  PRIMARY KEY (`movieId`,`userEmail`),
  KEY `watchlist_user_fk_idx` (`userEmail`),
  CONSTRAINT `watchlist_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE,
  CONSTRAINT `watchlist_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;