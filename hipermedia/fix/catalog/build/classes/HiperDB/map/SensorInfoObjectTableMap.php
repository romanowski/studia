<?php



/**
 * This class defines the structure of the 'sensorInfoObject' table.
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
class SensorInfoObjectTableMap extends TableMap
{

    /**
     * The (dot-path) name of this class
     */
    const CLASS_NAME = 'HiperDB.map.SensorInfoObjectTableMap';

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
        $this->setName('sensorInfoObject');
        $this->setPhpName('SensorInfoObject');
        $this->setClassname('SensorInfoObject');
        $this->setPackage('HiperDB');
        $this->setUseIdGenerator(true);
        // columns
        $this->addPrimaryKey('ID', 'id', 'INTEGER', true, null, null);
        $this->addColumn('NAME', 'name', 'VARCHAR', true, 255, null);
        $this->addColumn('HREF', 'href', 'VARCHAR', true, 255, null);
        $this->addColumn('MEASURE', 'measure', 'VARCHAR', true, 255, null);
        $this->addColumn('DATATYPE', 'dataType', 'VARCHAR', true, 255, null);
        $this->addColumn('FREQUENCY', 'frequency', 'INTEGER', true, null, null);
        $this->addColumn('RESOURCE', 'resource', 'VARCHAR', true, 255, null);
        $this->addColumn('TYPE', 'type', 'VARCHAR', true, 255, null);
        // validators
    } // initialize()

    /**
     * Build the RelationMap objects for this table relationships
     */
    public function buildRelations()
    {
    } // buildRelations()

} // SensorInfoObjectTableMap
