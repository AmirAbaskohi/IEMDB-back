package com.iemdb.sqls;

import java.util.HashMap;
import java.util.Map;

public class SqlQueries {
    public static Map<String, String> queries;

    public static Map<String, String> getQueries(){
        queries = new HashMap<>();
        queries.put("actor",
                "CREATE TABLE IF NOT EXISTS `actor` (\n" +
                "  `id` int NOT NULL,\n" +
                "  `name` varchar(45) DEFAULT NULL,\n" +
                "  `birthDate` datetime DEFAULT NULL,\n" +
                "  `nationality` varchar(45) DEFAULT NULL,\n" +
                "  `imageUrl` varchar(100) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("actor_movie",
                "CREATE TABLE IF NOT EXISTS `actor_movie` (\n" +
                        "  `actorId` int NOT NULL,\n" +
                        "  `movieId` int NOT NULL,\n" +
                        "  PRIMARY KEY (`actorId`,`movieId`),\n" +
                        "  KEY `movie_fk_idx` (`movieId`),\n" +
                        "  CONSTRAINT `actor_fk` FOREIGN KEY (`actorId`) REFERENCES `actor` (`id`),\n" +
                        "  CONSTRAINT `movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("comment",
                "CREATE TABLE IF NOT EXISTS `comment` (\n" +
                        "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                        "  `userEmail` varchar(45) DEFAULT NULL,\n" +
                        "  `text` varchar(200) DEFAULT NULL,\n" +
                        "  `movieId` int DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  KEY `movie_comment_fk_idx` (`movieId`),\n" +
                        "  CONSTRAINT `movie_comment_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("genre",
                "CREATE TABLE IF NOT EXISTS `genre` (\n" +
                        "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` varchar(50) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;");

        queries.put("genre_movie",
                "CREATE TABLE IF NOT EXISTS `genre_movie` (\n" +
                        "  `movieId` int NOT NULL,\n" +
                        "  `genreId` int NOT NULL,\n" +
                        "  PRIMARY KEY (`movieId`,`genreId`),\n" +
                        "  KEY `has_genre_genre_fk_idx` (`genreId`),\n" +
                        "  CONSTRAINT `genre_movie_genre_fk` FOREIGN KEY (`genreId`) REFERENCES `genre` (`id`),\n" +
                        "  CONSTRAINT `genre_movie_movie_Fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("movie",
                "CREATE TABLE IF NOT EXISTS `movie` (\n" +
                        "  `id` int NOT NULL,\n" +
                        "  `name` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,\n" +
                        "  `summary` varchar(2000) DEFAULT NULL,\n" +
                        "  `releaseDate` datetime DEFAULT NULL,\n" +
                        "  `director` varchar(100) DEFAULT NULL,\n" +
                        "  `imdbRate` double DEFAULT NULL,\n" +
                        "  `duration` int DEFAULT NULL,\n" +
                        "  `ageLimit` int DEFAULT NULL,\n" +
                        "  `rating` double DEFAULT NULL,\n" +
                        "  `imageUrl` varchar(200) DEFAULT NULL,\n" +
                        "  `coverImageUrl` varchar(200) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("rate",
                "CREATE TABLE IF NOT EXISTS `rate` (\n" +
                        "  `userEmail` varchar(50) NOT NULL,\n" +
                        "  `movieId` int NOT NULL,\n" +
                        "  `score` int DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`userEmail`,`movieId`),\n" +
                        "  KEY `rate_movie_fk_idx` (`movieId`),\n" +
                        "  CONSTRAINT `rate_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE,\n" +
                        "  CONSTRAINT `rate_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("user",
                "CREATE TABLE IF NOT EXISTS `user` (\n" +
                        "  `email` varchar(50) NOT NULL,\n" +
                        "  `password` varchar(128) DEFAULT NULL,\n" +
                        "  `name` varchar(45) DEFAULT NULL,\n" +
                        "  `nickName` varchar(45) DEFAULT NULL,\n" +
                        "  `birthDate` datetime DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`email`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("vote",
                "CREATE TABLE IF NOT EXISTS `vote` (\n" +
                        "  `commentId` int NOT NULL,\n" +
                        "  `userEmail` varchar(45) NOT NULL,\n" +
                        "  `vote` int DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`commentId`,`userEmail`),\n" +
                        "  KEY `vote_user_fk_idx` (`userEmail`),\n" +
                        "  CONSTRAINT `vote_comment_fk` FOREIGN KEY (`commentId`) REFERENCES `comment` (`id`),\n" +
                        "  CONSTRAINT `vote_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("watchlist",
                "CREATE TABLE IF NOT EXISTS `watchlist` (\n" +
                        "  `movieId` int NOT NULL,\n" +
                        "  `userEmail` varchar(45) NOT NULL,\n" +
                        "  PRIMARY KEY (`movieId`,`userEmail`),\n" +
                        "  KEY `watchlist_user_fk_idx` (`userEmail`),\n" +
                        "  CONSTRAINT `watchlist_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`) ON DELETE CASCADE,\n" +
                        "  CONSTRAINT `watchlist_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`) ON DELETE CASCADE\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("writer",
                "CREATE TABLE IF NOT EXISTS `writer` (\n" +
                        "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` varchar(45) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        queries.put("writer_movie",
                "CREATE TABLE IF NOT EXISTS `writer_movie` (\n" +
                        "  `writerId` int NOT NULL,\n" +
                        "  `movieId` int NOT NULL,\n" +
                        "  PRIMARY KEY (`writerId`,`movieId`),\n" +
                        "  KEY `write_movie_movie_fk_idx` (`movieId`),\n" +
                        "  CONSTRAINT `write-_movie_writer_fk` FOREIGN KEY (`writerId`) REFERENCES `writer` (`id`),\n" +
                        "  CONSTRAINT `write_movie_movie_fk` FOREIGN KEY (`movieId`) REFERENCES `movie` (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;");

        return queries;
    }
}
