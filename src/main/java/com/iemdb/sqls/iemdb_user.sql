CREATE TABLE IF NOT EXISTS `user` (
  `email` varchar(50) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `nickName` varchar(45) DEFAULT NULL,
  `birthDate` datetime DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;