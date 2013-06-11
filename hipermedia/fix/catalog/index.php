<?php

ini_set('display_errors', '1');	

// Include the main Propel script
require_once 'vendor/propel/runtime/lib/Propel.php';

// Initialize Propel with the runtime configuration
Propel::init("build/conf/HiperDB-conf.php");

// Add the generated 'classes' directory to the include path
set_include_path("build/classes" . PATH_SEPARATOR . get_include_path());


echo 'aa';
?>
