-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: huellas
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `fingerprints`
--

DROP TABLE IF EXISTS `fingerprints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fingerprints` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL,
  `fmd` blob NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fingerprints`
--

LOCK TABLES `fingerprints` WRITE;
/*!40000 ALTER TABLE `fingerprints` DISABLE KEYS */;
INSERT INTO `fingerprints` VALUES (4,1,_binary 'FMR\0 20\0\0\0§\0\0êÙ\0\≈\0\≈\0\0\0VAA+@\„d@Ü+öcÅca\‰aÄıygaA(\0øl`ÄfŒæ`Ä£Iü_Äã\0\»^Äù\0sÄ]@úè]@w\À\Œ]Å8\‚\\ÄΩ+ã[AyXZAkZÄæ\œ\„ZÄ§wΩYÄ\ÿî\ÈYÄé¥\’Y@\Î3uXÄ\‰\0ÄpX@∫\0◊ÄXÄ\ﬂ\0bmWA7s\”WÅLf\‡WA\0FhVAjkVÄ\≈\0ö{UA+\0neU@l\nUÄ\… ˜TÄ\œ\ÊaT@S\0\“SA^ç\€SÅG\0\ÿ\ÎSÄdy≥R@ZØµRÄ\Œ_ùRÄä~ΩRÅ\À\0R@N´2Q@ë\0\ÏéP@d\0öéPÅ,\0_\·O@\„≥\ÓO@∏\0¬ÄNÄ\√´\·NÄı\÷ÚNÄ|•\≈NAm´\∆M@ÑL§M@\≈\0\œÛLAd\0†hJ@\÷KáJ@øQùJÅG\0\‚\ÎIA1∫1I@|x∞HÄ\“Ü\ÊGAJô\≈GAàaEAókBÄpxµAÄwÉ∑@@\»O\0?\0\0','2024-03-28 20:14:41');
/*!40000 ALTER TABLE `fingerprints` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-28 14:32:39
