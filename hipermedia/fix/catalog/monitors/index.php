<?php

ini_set('display_errors', '1');	

// Include the main Propel script
require_once '../vendor/propel/runtime/lib/Propel.php';

// Initialize Propel with the runtime configuration
Propel::init("../build/conf/HiperDB-conf.php");

// Add the generated 'classes' directory to the include path
set_include_path("../build/classes" . PATH_SEPARATOR . get_include_path());
//echo $_POST['param'] ;

	$request = file_get_contents('php://input');
//	$request = '{  
  //          "name": "ala msssa kota",
    //        "href": "/monitor1"
      //  }';
	header('Content-type: application/json');
	switch ($_SERVER['REQUEST_METHOD']) {
		
		case 'POST' :
			try {
				$monitorObject = new MonitorObjects();
				$monitorObject -> fromJSON($_POST['param']);	
				echo $monitorObject -> getName();
				$monitorObject -> save();
				echo $monitorObject->toJSON();
			}
			catch (Exception $e) {
				 http_response_code(400);
				 echo '{"message":"Bad request", "messageDescription":"Bad request"}';
			}
			break;
		
		case 'GET' : 
			$monitors = MonitorObjectsQuery::create()->find();
			$out = '{ "monitors":[';
			foreach ($monitors as $monitor) {
					$out .= $monitor->toJSON();
					$out .= ',';
			}
			if ($out[strlen($out)-1] == ",")
				$out = substr_replace($out ,"",-1);
			$out .= ']}';
			echo $out;
			break;
		
		case 'DELETE' :
			$monitorObject = new MonitorObjects();
			$monitorObject -> fromJSON($request);	
			$monitorObject -> delete();
			break;
	}



?>
