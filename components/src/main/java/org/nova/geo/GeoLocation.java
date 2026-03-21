package org.nova.geo;

import java.time.ZoneId;

import org.nova.localization.CountryCode;

public record GeoLocation(LatitudeLongitude position,CountryCode countryCode,ZoneId zoneId)
{
}

//CREATE TABLE `accountsession` (
//        `id` bigint NOT NULL AUTO_INCREMENT,
//        `created` datetime NOT NULL,
//        `devicesessionid` bigint NOT NULL,
//        `accountid` bigint NOT NULL,
//        `keepSignedIn` bit(1) NOT NULL,
//        `lastSecurityCheck` datetime DEFAULT NULL,
//        PRIMARY KEY (`id`)
//      ) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



//CREATE TABLE `devicesession` (
//        `id` bigint NOT NULL AUTO_INCREMENT,
//        `deviceId` bigint NOT NULL,
//        `created` datetime NOT NULL,
//        `token` varchar(45) NOT NULL,
//        `remote` varchar(45) DEFAULT NULL,
//        `lastSignIn` datetime DEFAULT NULL,
//        `language` varchar(45) DEFAULT NULL,
//        `longitude` float DEFAULT NULL,
//        `latitude` float DEFAULT NULL,
//        `country` varchar(45) DEFAULT NULL,
//        PRIMARY KEY (`id`),
//        UNIQUE KEY `token_UNIQUE` (`token`)
//      ) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



//CREATE TABLE `visited` (
//        `id` bigint NOT NULL AUTO_INCREMENT,
//        `sessionId` bigint NOT NULL,
//        `created` datetime NOT NULL,
//        `pathAndQuery` varchar(2048) NOT NULL,
//        `fromId` bigint DEFAULT NULL,
//        `accountsessionid` bigint DEFAULT NULL,
//        PRIMARY KEY (`id`)
//      ) ENGINE=InnoDB AUTO_INCREMENT=2238 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

