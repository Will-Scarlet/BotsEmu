--
-- Tabellenstruktur für Tabelle `rooms`
--

CREATE TABLE IF NOT EXISTS `rooms` (
  `ip` varchar(20) NOT NULL,
  `port` int(5) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
