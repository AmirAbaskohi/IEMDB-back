CREATE TABLE IF NOT EXISTS `vote` (
  `commentId` int NOT NULL,
  `userEmail` varchar(45) NOT NULL,
  `vote` int DEFAULT NULL,
  PRIMARY KEY (`commentId`,`userEmail`),
  KEY `vote_user_fk_idx` (`userEmail`),
  CONSTRAINT `vote_comment_fk` FOREIGN KEY (`commentId`) REFERENCES `comment` (`id`),
  CONSTRAINT `vote_user_fk` FOREIGN KEY (`userEmail`) REFERENCES `user` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;