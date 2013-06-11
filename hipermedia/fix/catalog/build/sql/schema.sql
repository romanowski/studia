
# This is a fix for InnoDB in MySQL >= 4.1.x
# It "suspends judgement" for fkey relationships until are tables are set.
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- monitorObjects
-- ---------------------------------------------------------------------

DROP TABLE IF EXISTS `monitorObjects`;

CREATE TABLE `monitorObjects`
(
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `href` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=MyISAM;

-- ---------------------------------------------------------------------
-- sensorInfoObject
-- ---------------------------------------------------------------------

DROP TABLE IF EXISTS `sensorInfoObject`;

CREATE TABLE `sensorInfoObject`
(
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `href` VARCHAR(255) NOT NULL,
    `measure` VARCHAR(255) NOT NULL,
    `dataType` VARCHAR(255) NOT NULL,
    `frequency` INTEGER NOT NULL,
    `resource` VARCHAR(255) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=MyISAM;

# This restores the fkey checks, after having unset them earlier
SET FOREIGN_KEY_CHECKS = 1;
