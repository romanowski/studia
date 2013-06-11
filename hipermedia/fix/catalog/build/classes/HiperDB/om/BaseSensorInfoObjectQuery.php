<?php


/**
 * Base class that represents a query for the 'sensorInfoObject' table.
 *
 *
 *
 * @method SensorInfoObjectQuery orderByid($order = Criteria::ASC) Order by the id column
 * @method SensorInfoObjectQuery orderByname($order = Criteria::ASC) Order by the name column
 * @method SensorInfoObjectQuery orderByhref($order = Criteria::ASC) Order by the href column
 * @method SensorInfoObjectQuery orderBymeasure($order = Criteria::ASC) Order by the measure column
 * @method SensorInfoObjectQuery orderBydataType($order = Criteria::ASC) Order by the dataType column
 * @method SensorInfoObjectQuery orderByfrequency($order = Criteria::ASC) Order by the frequency column
 * @method SensorInfoObjectQuery orderByresource($order = Criteria::ASC) Order by the resource column
 * @method SensorInfoObjectQuery orderBytype($order = Criteria::ASC) Order by the type column
 *
 * @method SensorInfoObjectQuery groupByid() Group by the id column
 * @method SensorInfoObjectQuery groupByname() Group by the name column
 * @method SensorInfoObjectQuery groupByhref() Group by the href column
 * @method SensorInfoObjectQuery groupBymeasure() Group by the measure column
 * @method SensorInfoObjectQuery groupBydataType() Group by the dataType column
 * @method SensorInfoObjectQuery groupByfrequency() Group by the frequency column
 * @method SensorInfoObjectQuery groupByresource() Group by the resource column
 * @method SensorInfoObjectQuery groupBytype() Group by the type column
 *
 * @method SensorInfoObjectQuery leftJoin($relation) Adds a LEFT JOIN clause to the query
 * @method SensorInfoObjectQuery rightJoin($relation) Adds a RIGHT JOIN clause to the query
 * @method SensorInfoObjectQuery innerJoin($relation) Adds a INNER JOIN clause to the query
 *
 * @method SensorInfoObject findOne(PropelPDO $con = null) Return the first SensorInfoObject matching the query
 * @method SensorInfoObject findOneOrCreate(PropelPDO $con = null) Return the first SensorInfoObject matching the query, or a new SensorInfoObject object populated from the query conditions when no match is found
 *
 * @method SensorInfoObject findOneByname(string $name) Return the first SensorInfoObject filtered by the name column
 * @method SensorInfoObject findOneByhref(string $href) Return the first SensorInfoObject filtered by the href column
 * @method SensorInfoObject findOneBymeasure(string $measure) Return the first SensorInfoObject filtered by the measure column
 * @method SensorInfoObject findOneBydataType(string $dataType) Return the first SensorInfoObject filtered by the dataType column
 * @method SensorInfoObject findOneByfrequency(int $frequency) Return the first SensorInfoObject filtered by the frequency column
 * @method SensorInfoObject findOneByresource(string $resource) Return the first SensorInfoObject filtered by the resource column
 * @method SensorInfoObject findOneBytype(string $type) Return the first SensorInfoObject filtered by the type column
 *
 * @method array findByid(int $id) Return SensorInfoObject objects filtered by the id column
 * @method array findByname(string $name) Return SensorInfoObject objects filtered by the name column
 * @method array findByhref(string $href) Return SensorInfoObject objects filtered by the href column
 * @method array findBymeasure(string $measure) Return SensorInfoObject objects filtered by the measure column
 * @method array findBydataType(string $dataType) Return SensorInfoObject objects filtered by the dataType column
 * @method array findByfrequency(int $frequency) Return SensorInfoObject objects filtered by the frequency column
 * @method array findByresource(string $resource) Return SensorInfoObject objects filtered by the resource column
 * @method array findBytype(string $type) Return SensorInfoObject objects filtered by the type column
 *
 * @package    propel.generator.HiperDB.om
 */
abstract class BaseSensorInfoObjectQuery extends ModelCriteria
{
    /**
     * Initializes internal state of BaseSensorInfoObjectQuery object.
     *
     * @param     string $dbName The dabase name
     * @param     string $modelName The phpName of a model, e.g. 'Book'
     * @param     string $modelAlias The alias for the model in this query, e.g. 'b'
     */
    public function __construct($dbName = 'HiperDB', $modelName = 'SensorInfoObject', $modelAlias = null)
    {
        parent::__construct($dbName, $modelName, $modelAlias);
    }

    /**
     * Returns a new SensorInfoObjectQuery object.
     *
     * @param     string $modelAlias The alias of a model in the query
     * @param     SensorInfoObjectQuery|Criteria $criteria Optional Criteria to build the query from
     *
     * @return SensorInfoObjectQuery
     */
    public static function create($modelAlias = null, $criteria = null)
    {
        if ($criteria instanceof SensorInfoObjectQuery) {
            return $criteria;
        }
        $query = new SensorInfoObjectQuery();
        if (null !== $modelAlias) {
            $query->setModelAlias($modelAlias);
        }
        if ($criteria instanceof Criteria) {
            $query->mergeWith($criteria);
        }

        return $query;
    }

    /**
     * Find object by primary key.
     * Propel uses the instance pool to skip the database if the object exists.
     * Go fast if the query is untouched.
     *
     * <code>
     * $obj  = $c->findPk(12, $con);
     * </code>
     *
     * @param mixed $key Primary key to use for the query
     * @param     PropelPDO $con an optional connection object
     *
     * @return   SensorInfoObject|SensorInfoObject[]|mixed the result, formatted by the current formatter
     */
    public function findPk($key, $con = null)
    {
        if ($key === null) {
            return null;
        }
        if ((null !== ($obj = SensorInfoObjectPeer::getInstanceFromPool((string) $key))) && !$this->formatter) {
            // the object is alredy in the instance pool
            return $obj;
        }
        if ($con === null) {
            $con = Propel::getConnection(SensorInfoObjectPeer::DATABASE_NAME, Propel::CONNECTION_READ);
        }
        $this->basePreSelect($con);
        if ($this->formatter || $this->modelAlias || $this->with || $this->select
         || $this->selectColumns || $this->asColumns || $this->selectModifiers
         || $this->map || $this->having || $this->joins) {
            return $this->findPkComplex($key, $con);
        } else {
            return $this->findPkSimple($key, $con);
        }
    }

    /**
     * Alias of findPk to use instance pooling
     *
     * @param     mixed $key Primary key to use for the query
     * @param     PropelPDO $con A connection object
     *
     * @return   SensorInfoObject A model object, or null if the key is not found
     * @throws   PropelException
     */
     public function findOneByid($key, $con = null)
     {
        return $this->findPk($key, $con);
     }

    /**
     * Find object by primary key using raw SQL to go fast.
     * Bypass doSelect() and the object formatter by using generated code.
     *
     * @param     mixed $key Primary key to use for the query
     * @param     PropelPDO $con A connection object
     *
     * @return   SensorInfoObject A model object, or null if the key is not found
     * @throws   PropelException
     */
    protected function findPkSimple($key, $con)
    {
        $sql = 'SELECT `ID`, `NAME`, `HREF`, `MEASURE`, `DATATYPE`, `FREQUENCY`, `RESOURCE`, `TYPE` FROM `sensorInfoObject` WHERE `ID` = :p0';
        try {
            $stmt = $con->prepare($sql);
            $stmt->bindValue(':p0', $key, PDO::PARAM_INT);
            $stmt->execute();
        } catch (Exception $e) {
            Propel::log($e->getMessage(), Propel::LOG_ERR);
            throw new PropelException(sprintf('Unable to execute SELECT statement [%s]', $sql), $e);
        }
        $obj = null;
        if ($row = $stmt->fetch(PDO::FETCH_NUM)) {
            $obj = new SensorInfoObject();
            $obj->hydrate($row);
            SensorInfoObjectPeer::addInstanceToPool($obj, (string) $key);
        }
        $stmt->closeCursor();

        return $obj;
    }

    /**
     * Find object by primary key.
     *
     * @param     mixed $key Primary key to use for the query
     * @param     PropelPDO $con A connection object
     *
     * @return SensorInfoObject|SensorInfoObject[]|mixed the result, formatted by the current formatter
     */
    protected function findPkComplex($key, $con)
    {
        // As the query uses a PK condition, no limit(1) is necessary.
        $criteria = $this->isKeepQuery() ? clone $this : $this;
        $stmt = $criteria
            ->filterByPrimaryKey($key)
            ->doSelect($con);

        return $criteria->getFormatter()->init($criteria)->formatOne($stmt);
    }

    /**
     * Find objects by primary key
     * <code>
     * $objs = $c->findPks(array(12, 56, 832), $con);
     * </code>
     * @param     array $keys Primary keys to use for the query
     * @param     PropelPDO $con an optional connection object
     *
     * @return PropelObjectCollection|SensorInfoObject[]|mixed the list of results, formatted by the current formatter
     */
    public function findPks($keys, $con = null)
    {
        if ($con === null) {
            $con = Propel::getConnection($this->getDbName(), Propel::CONNECTION_READ);
        }
        $this->basePreSelect($con);
        $criteria = $this->isKeepQuery() ? clone $this : $this;
        $stmt = $criteria
            ->filterByPrimaryKeys($keys)
            ->doSelect($con);

        return $criteria->getFormatter()->init($criteria)->format($stmt);
    }

    /**
     * Filter the query by primary key
     *
     * @param     mixed $key Primary key to use for the query
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByPrimaryKey($key)
    {

        return $this->addUsingAlias(SensorInfoObjectPeer::ID, $key, Criteria::EQUAL);
    }

    /**
     * Filter the query by a list of primary keys
     *
     * @param     array $keys The list of primary key to use for the query
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByPrimaryKeys($keys)
    {

        return $this->addUsingAlias(SensorInfoObjectPeer::ID, $keys, Criteria::IN);
    }

    /**
     * Filter the query on the id column
     *
     * Example usage:
     * <code>
     * $query->filterByid(1234); // WHERE id = 1234
     * $query->filterByid(array(12, 34)); // WHERE id IN (12, 34)
     * $query->filterByid(array('min' => 12)); // WHERE id > 12
     * </code>
     *
     * @param     mixed $id The value to use as filter.
     *              Use scalar values for equality.
     *              Use array values for in_array() equivalent.
     *              Use associative array('min' => $minValue, 'max' => $maxValue) for intervals.
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByid($id = null, $comparison = null)
    {
        if (is_array($id) && null === $comparison) {
            $comparison = Criteria::IN;
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::ID, $id, $comparison);
    }

    /**
     * Filter the query on the name column
     *
     * Example usage:
     * <code>
     * $query->filterByname('fooValue');   // WHERE name = 'fooValue'
     * $query->filterByname('%fooValue%'); // WHERE name LIKE '%fooValue%'
     * </code>
     *
     * @param     string $name The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByname($name = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($name)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $name)) {
                $name = str_replace('*', '%', $name);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::NAME, $name, $comparison);
    }

    /**
     * Filter the query on the href column
     *
     * Example usage:
     * <code>
     * $query->filterByhref('fooValue');   // WHERE href = 'fooValue'
     * $query->filterByhref('%fooValue%'); // WHERE href LIKE '%fooValue%'
     * </code>
     *
     * @param     string $href The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByhref($href = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($href)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $href)) {
                $href = str_replace('*', '%', $href);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::HREF, $href, $comparison);
    }

    /**
     * Filter the query on the measure column
     *
     * Example usage:
     * <code>
     * $query->filterBymeasure('fooValue');   // WHERE measure = 'fooValue'
     * $query->filterBymeasure('%fooValue%'); // WHERE measure LIKE '%fooValue%'
     * </code>
     *
     * @param     string $measure The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterBymeasure($measure = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($measure)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $measure)) {
                $measure = str_replace('*', '%', $measure);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::MEASURE, $measure, $comparison);
    }

    /**
     * Filter the query on the dataType column
     *
     * Example usage:
     * <code>
     * $query->filterBydataType('fooValue');   // WHERE dataType = 'fooValue'
     * $query->filterBydataType('%fooValue%'); // WHERE dataType LIKE '%fooValue%'
     * </code>
     *
     * @param     string $dataType The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterBydataType($dataType = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($dataType)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $dataType)) {
                $dataType = str_replace('*', '%', $dataType);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::DATATYPE, $dataType, $comparison);
    }

    /**
     * Filter the query on the frequency column
     *
     * Example usage:
     * <code>
     * $query->filterByfrequency(1234); // WHERE frequency = 1234
     * $query->filterByfrequency(array(12, 34)); // WHERE frequency IN (12, 34)
     * $query->filterByfrequency(array('min' => 12)); // WHERE frequency > 12
     * </code>
     *
     * @param     mixed $frequency The value to use as filter.
     *              Use scalar values for equality.
     *              Use array values for in_array() equivalent.
     *              Use associative array('min' => $minValue, 'max' => $maxValue) for intervals.
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByfrequency($frequency = null, $comparison = null)
    {
        if (is_array($frequency)) {
            $useMinMax = false;
            if (isset($frequency['min'])) {
                $this->addUsingAlias(SensorInfoObjectPeer::FREQUENCY, $frequency['min'], Criteria::GREATER_EQUAL);
                $useMinMax = true;
            }
            if (isset($frequency['max'])) {
                $this->addUsingAlias(SensorInfoObjectPeer::FREQUENCY, $frequency['max'], Criteria::LESS_EQUAL);
                $useMinMax = true;
            }
            if ($useMinMax) {
                return $this;
            }
            if (null === $comparison) {
                $comparison = Criteria::IN;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::FREQUENCY, $frequency, $comparison);
    }

    /**
     * Filter the query on the resource column
     *
     * Example usage:
     * <code>
     * $query->filterByresource('fooValue');   // WHERE resource = 'fooValue'
     * $query->filterByresource('%fooValue%'); // WHERE resource LIKE '%fooValue%'
     * </code>
     *
     * @param     string $resource The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterByresource($resource = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($resource)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $resource)) {
                $resource = str_replace('*', '%', $resource);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::RESOURCE, $resource, $comparison);
    }

    /**
     * Filter the query on the type column
     *
     * Example usage:
     * <code>
     * $query->filterBytype('fooValue');   // WHERE type = 'fooValue'
     * $query->filterBytype('%fooValue%'); // WHERE type LIKE '%fooValue%'
     * </code>
     *
     * @param     string $type The value to use as filter.
     *              Accepts wildcards (* and % trigger a LIKE)
     * @param     string $comparison Operator to use for the column comparison, defaults to Criteria::EQUAL
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function filterBytype($type = null, $comparison = null)
    {
        if (null === $comparison) {
            if (is_array($type)) {
                $comparison = Criteria::IN;
            } elseif (preg_match('/[\%\*]/', $type)) {
                $type = str_replace('*', '%', $type);
                $comparison = Criteria::LIKE;
            }
        }

        return $this->addUsingAlias(SensorInfoObjectPeer::TYPE, $type, $comparison);
    }

    /**
     * Exclude object from result
     *
     * @param   SensorInfoObject $sensorInfoObject Object to remove from the list of results
     *
     * @return SensorInfoObjectQuery The current query, for fluid interface
     */
    public function prune($sensorInfoObject = null)
    {
        if ($sensorInfoObject) {
            $this->addUsingAlias(SensorInfoObjectPeer::ID, $sensorInfoObject->getid(), Criteria::NOT_EQUAL);
        }

        return $this;
    }

}
