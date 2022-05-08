CREATE TABLE IF NOT EXISTS `actor` (
  `id` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `birthDate` datetime DEFAULT NULL,
  `nationality` varchar(45) DEFAULT NULL,
  `imageUrl` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;