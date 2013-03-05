-- MySQL dump 10.13  Distrib 5.5.29, for Win64 (x86)
--
-- Host: localhost    Database: matrices
-- ------------------------------------------------------
-- Server version	5.5.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `test_filename2taxon`
--

DROP TABLE IF EXISTS `test_filename2taxon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_filename2taxon` (
  `filename` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `hasdescription` tinyint(1) DEFAULT '0',
  `domain` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `kingdom` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `phylum` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subphylum` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `superdivision` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `division` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subdivision` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `superclass` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `class` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subclass` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `superorder` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `order` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `suborder` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `superfamily` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `family` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subfamily` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tribe` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subtribe` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `genus` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subgenus` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `section` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subsection` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `species` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subspecies` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `variety` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_filename2taxon`
--

LOCK TABLES `test_filename2taxon` WRITE;
/*!40000 ALTER TABLE `test_filename2taxon` DISABLE KEYS */;
INSERT INTO `test_filename2taxon` VALUES ('100.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','','',''),('101.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','benthamii','',''),('102.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','humifusa','',''),('103.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','lanuginosa','',''),('104.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','lanuginosa','','lanuginosa'),('105.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','lanuginosa','','saxosa'),('106.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','livermorensis','',''),('107.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','longipedunculata','',''),('108.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','ludens','',''),('109.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','paludicola','',''),('110.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','pseudofrigida','',''),('111.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','serpyllifolia','',''),('112.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','serpyllifolia','','serpyllifolia'),('113.xml',1,'','','','','','','','','','','','','','','','','','','arenaria','','','','serpyllifolia','','tenuior');
/*!40000 ALTER TABLE `test_filename2taxon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_singularplural`
--

DROP TABLE IF EXISTS `test_singularplural`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_singularplural` (
  `singular` varchar(200) NOT NULL DEFAULT '',
  `plural` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_singularplural`
--

LOCK TABLES `test_singularplural` WRITE;
/*!40000 ALTER TABLE `test_singularplural` DISABLE KEYS */;
INSERT INTO `test_singularplural` VALUES ('achene','achenes'),('acourtium','acourtia'),('ambrosiina','ambrosiinae'),('annual','annuals'),('anther','anthers'),('apex','apices'),('appendage','appendages'),('axil','axils'),('band','bands'),('base','bases'),('biennial','biennials'),('blade','blades'),('body','bodies'),('bract','bracts'),('bractlet','bractlets'),('branch','branches'),('bristle','bristles'),('bristly','bristles'),('calyculus','calyculi'),('caudex','caudices'),('chaptalium','chaptalia'),('combination','combinations'),('corolla','corollas'),('crown','crowns'),('cypsela','cypselae'),('description','descriptions'),('edge','edges'),('embryo','embryos'),('face','faces'),('filament','filaments'),('floret','florets'),('fruit','fruits'),('gochnatium','gochnatia'),('hair','hairs'),('head','heads'),('hecastoclei','hecastocleis'),('helianthea','heliantheae'),('herb','herbs'),('inflorescence','inflorescences'),('involucre','involucres'),('kind','kinds'),('leaf','leaves'),('leaf','leaves:'),('leibnitzium','leibnitzia'),('limb','limbs'),('line','lines'),('lobe','lobes'),('margin','margins'),('ovary','ovaries'),('palea','paleae'),('papilla','papillae'),('pappus','pappi'),('peduncle','peduncles'),('perennial','perennials'),('petal','petals'),('petiole','petioles'),('phyllary','phyllaries'),('plant','plants'),('receptacle','receptacles'),('rib','ribs'),('root','roots'),('scale','scales'),('seed','seeds'),('sepal','sepals'),('shrub','shrubs'),('socket','sockets'),('spine','spines'),('stamen','stamens'),('style','styles'),('subshrub','subshrubs'),('tendril','tendrils'),('time','times'),('tip','tips'),('tree','trees'),('trixi','trixis'),('tube','tubes'),('tuft','tufts'),('vine','vines');
/*!40000 ALTER TABLE `test_singularplural` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-03-04 18:09:58
