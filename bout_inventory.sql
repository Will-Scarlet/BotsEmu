-- Tabellenstruktur für Tabelle `bout_inventory`
--

CREATE TABLE IF NOT EXISTS `bout_inventory` (
  `name` varchar(25) NOT NULL,
  `item1` int(25) NOT NULL DEFAULT '0',
  `item2` int(25) NOT NULL DEFAULT '0',
  `item3` int(25) NOT NULL DEFAULT '0',
  `item4` int(25) NOT NULL DEFAULT '0',
  `item5` int(25) NOT NULL DEFAULT '0',
  `item6` int(25) NOT NULL DEFAULT '0',
  `item7` int(25) NOT NULL DEFAULT '0',
  `item8` int(25) NOT NULL DEFAULT '0',
  `item9` int(25) NOT NULL DEFAULT '0',
  `item10` int(25) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `bout_inventory`
--

INSERT INTO `bout_inventory` (`name`, `item1`, `item2`, `item3`, `item4`, `item5`, `item6`, `item7`, `item8`, `item9`, `item10`) VALUES
('Auron3', 8014203, 7417000, 0, 0, 0, 0, 0, 0, 0, 0),
