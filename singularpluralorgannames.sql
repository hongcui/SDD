-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema singularpluralorgannames
--

CREATE DATABASE IF NOT EXISTS singularpluralorgannames;
USE singularpluralorgannames;

--
-- Definition of table `singularplural_fnav19`
--

DROP TABLE IF EXISTS `singularplural_fnav19`;
CREATE TABLE `singularplural_fnav19` (
  `singular` varchar(200) NOT NULL default '',
  `plural` varchar(200) NOT NULL default ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `singularplural_fnav19`
--

/*!40000 ALTER TABLE `singularplural_fnav19` DISABLE KEYS */;
INSERT INTO `singularplural_fnav19` (`singular`,`plural`) VALUES 
 ('angle','angles'),
 ('annual','annuals'),
 ('anther','anthers'),
 ('apex','apices'),
 ('appendage','appendages'),
 ('arista','aristae'),
 ('arp','arps'),
 ('attachment','attachments'),
 ('auricle','auricles'),
 ('awn','awns'),
 ('ax','axes'),
 ('axil','axils'),
 ('axis','axes'),
 ('band','bands'),
 ('barb','barbs'),
 ('base','bases'),
 ('beak','beaks'),
 ('biennial','biennials'),
 ('blade','blades'),
 ('blotch','blotches'),
 ('body','bodies'),
 ('border','borders'),
 ('bracklet','bracklets'),
 ('bract','bracts'),
 ('bracteole','bracteoles'),
 ('bractlet','bractlets'),
 ('branch','branches'),
 ('bristle','bristles'),
 ('bud','buds'),
 ('bundle','bundles'),
 ('calyculus','calyculi'),
 ('carpopodium','carpopodia'),
 ('caudex','caudices'),
 ('cell','cells'),
 ('center','centers'),
 ('collar','collars'),
 ('color','colors'),
 ('combination','combinations'),
 ('cone','cones'),
 ('corolla','corollas'),
 ('corona','coronas'),
 ('crown','crowns'),
 ('cup','cups'),
 ('cyme','cymes'),
 ('cypsela','cypselae'),
 ('desert','deserts'),
 ('diam','diams'),
 ('direction','directions'),
 ('disc','discs'),
 ('division','divisions'),
 ('dot','dots'),
 ('edge','edges'),
 ('elaiosome','elaiosomes'),
 ('embryo','embryos'),
 ('enlargement','enlargements'),
 ('face','faces'),
 ('fan','fans'),
 ('filament','filaments'),
 ('floret','florets'),
 ('fork','forks'),
 ('frequency','frequencies'),
 ('fruit','fruits'),
 ('furrow','furrows'),
 ('gland','glands'),
 ('glomerule','glomerules'),
 ('groove','grooves'),
 ('hair','hairs'),
 ('head','heads'),
 ('herb','herbs'),
 ('horn','horns'),
 ('individual','individuals'),
 ('indument','induments'),
 ('inflorescence','inflorescences'),
 ('internode','internodes'),
 ('involucre','involucres'),
 ('keel','keels'),
 ('kind','kinds'),
 ('lamina','laminae'),
 ('lateral','laterals'),
 ('leaf','leaves'),
 ('ligule','ligules'),
 ('limb','limbs'),
 ('line','lines'),
 ('lip','lips'),
 ('lobe','lobes'),
 ('lobule','lobules'),
 ('margin','margins'),
 ('midnerf','midnerves'),
 ('midstem','midstems'),
 ('midstripe','midstripes'),
 ('midvein','midveins'),
 ('neck','necks'),
 ('nerf','nerves'),
 ('other','others'),
 ('ovary','ovaries'),
 ('palea','paleae'),
 ('papilla','papillae'),
 ('pappus','pappi'),
 ('part','parts'),
 ('patch','patches'),
 ('peduncle','peduncles'),
 ('perennial','perennials'),
 ('pericarp','pericarps'),
 ('periderm','periderms'),
 ('petal','petals'),
 ('petiole','petioles'),
 ('phyllary','phyllaries'),
 ('pistillate_zone','pistillate_zone'),
 ('pit','pits'),
 ('plane','planes'),
 ('plant','plants'),
 ('portion','portions'),
 ('receptacle','receptacles'),
 ('region','regions'),
 ('rhizome','rhizomes'),
 ('rib','ribs'),
 ('ridge','ridges'),
 ('rim','rims'),
 ('ring','rings'),
 ('root','roots'),
 ('rootstock','rootstocks'),
 ('rosette','rosettes'),
 ('sac','sacs'),
 ('scale','scales'),
 ('scar','scars'),
 ('seed','seeds'),
 ('segment','segments'),
 ('sepal','sepals'),
 ('seta','setae'),
 ('shoulder','shoulders'),
 ('shrub','shrubs'),
 ('side','sides'),
 ('sinuse','sinuses'),
 ('socket','sockets'),
 ('specimen','specimens'),
 ('speckle','speckles'),
 ('spine','spines'),
 ('spinule','spinules'),
 ('spot','spots'),
 ('sprout','sprouts'),
 ('stalk','stalks'),
 ('stamen','stamens'),
 ('staminode','staminodes'),
 ('stem','stems'),
 ('stereome','stereomes'),
 ('stolon','stolons'),
 ('stomate','stomates'),
 ('streak','streaks'),
 ('striation','striations'),
 ('stripe','stripes'),
 ('strips','stripes'),
 ('style','styles'),
 ('subshrub','subshrubs'),
 ('sulcus','sulci'),
 ('taproot','taproots'),
 ('tenuifolium','tenuifolia'),
 ('terminal','terminals'),
 ('thickening','thickenings'),
 ('throat','throats'),
 ('time','times'),
 ('tip','tips'),
 ('tissue','tissues'),
 ('tooth','teeth'),
 ('top','tops'),
 ('tree','trees'),
 ('trichome','trichomes'),
 ('tube','tubes'),
 ('tuber','tubers'),
 ('tubercle','tubercles'),
 ('tuft','tufts'),
 ('type','types'),
 ('unit','units'),
 ('vine','vines'),
 ('wall','walls'),
 ('whorl','whorls'),
 ('wing','wings'),
 ('zone','zones');
/*!40000 ALTER TABLE `singularplural_fnav19` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
