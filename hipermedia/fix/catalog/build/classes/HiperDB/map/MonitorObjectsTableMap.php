<?php



/**
 * This class defines the structure of the 'monitorObjects' table.
 *
 *
 *
 * This map class is used by Propel to do runtime db structure discovery.
 * For example, the createSelectSql() method checks the type of a given column used in an
 * ORDER BY clause to know whether it needs to apply SQL to make the ORDER BY case-insensitive
 * (i.e. if it's a text column type).
 *
 * @package    propel.generator.HiperDB.map
 */
class MonitorObjectsTableMap extends TableMap
{

    /**
     * The (dot-path) name of this class
     */
    const CLASS_NAME = 'HiperDB.map.MonitorObjectsTableMap';

    /**
     * Initialize the table attributes, columns and validators
     * Relations are not initialized by this method since they are lazy loaded
     *
     * @return void
     * @throws PropelException
     */
    public function initialize()
    {
        // attributes
        $this->setName('monitorObjects');
        $this->setPhpName('MonitorObjects');
        $this->setClassname('MonitorObjects');
        $this->setPackage('HiperDB');
        $this->setUseIdGenerator(true);
        // columns
        $this->addPrimaryKey('ID', 'id', 'INTEGER', true, null, null);
        $this->addColumn('NAME', 'name', 'VARCHAR', true, 255, null);
        $this->addColumn('HREF', 'href', 'VARCHAR', true, 255, null);
        // validators
    } // initialize()

    /**
     * Build the RelationMap objects for this table relationships
     */
    public function buildRelations()
    {
    } // buildRelations()

} // MonitorObjectsTableMap
