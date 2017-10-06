-- Tabellenstruktur für Tabelle `bout_users`
--

CREATE TABLE IF NOT EXISTS `bout_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `password` varchar(40) NOT NULL,
  `coins` int(25) NOT NULL DEFAULT '0',
  `banned` int(1) NOT NULL DEFAULT '0',
  `online` int(1) NOT NULL DEFAULT '0',
  `current_ip` varchar(100) NOT NULL DEFAULT '',
  `logincount` mediumint(9) unsigned NOT NULL DEFAULT '0',
  `last_ip` varchar(100) NOT NULL DEFAULT '',
  `lastlogin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `email` varchar(40) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
--  Tabellenstruktur für Tabelle `bout_users`
--

INSERT INTO `bout_users` (`id`, `username`, `password`, `coins`, `banned`, `online`, `current_ip`, `logincount`, `last_ip`, `lastlogin`, `email`) VALUES
(1, 'test', 'test', 0, 0, 0, '0', 0, '0', '0000-00-00 00:00:00', 'a@a.de'),