<?php
if (isset($_POST['sensor'])) {//['sensor']) === true && empty($_POST['sensor']) === false) {
	
//	$ch = curl_init();

//set the url, number of POST vars, POST data
//	curl_setopt($ch,CURLOPT_URL, $_GET['path']);
//	curl_setopt($ch,CURLOPT_POST, true);
//	curl_setopt($ch,CURLOPT_POSTFIELDS, $_POST);

//execute post
//	$result = curl_exec($ch);

//close connection
//	curl_close($ch);
//	print_r($_POST);
//	print_r($_GET['path']);
	http_post_data($_GET['path'], $_POST);

}
?>
