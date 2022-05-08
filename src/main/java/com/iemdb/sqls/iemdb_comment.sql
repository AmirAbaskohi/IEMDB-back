CREATE TABLE IF NOT EXISTS `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `useremail` varchar(45) DEFAULT NULL,
  `text` varchar(200) DEFAULT NULL,
  `movieId` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `movie_comment_fk_idx` (`movieId`),
  CONSTRAINT `movie_comment_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;