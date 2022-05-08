CREATE TABLE IF NOT EXISTS `movie` (
  `id` int NOT NULL,
  `name` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `summary` varchar(2000) DEFAULT NULL,
  `releaseDate` datetime DEFAULT NULL,
  `director` varchar(100) DEFAULT NULL,
  `imdbRate` double DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `ageLimit` int DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `imageUrl` varchar(200) DEFAULT NULL,
  `coverImageUrl` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;