CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `userAgent` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `devicesession` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deviceId` bigint NOT NULL,
  `created` datetime NOT NULL,
  `token` varchar(45) NOT NULL,
  `remote` varchar(45) DEFAULT NULL,
  `lastSignIn` datetime DEFAULT NULL,
  `language` varchar(45) DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `accountId` bigint DEFAULT NULL,
  `user` varchar(200) DEFAULT NULL,
  `lastSecurityCheck` datetime DEFAULT NULL,
  `remember` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_UNIQUE` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `visited` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sessionId` bigint NOT NULL,
  `created` datetime NOT NULL,
  `pathAndQuery` varchar(2048) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=627 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
