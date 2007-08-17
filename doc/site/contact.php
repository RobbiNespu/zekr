<html>

<head>

<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<meta http-equiv="Content-Language" content="en-us">
<meta name="keywords" content="Zekr, open source quranic project">
<meta name="name" content="Contact">
<meta http-equiv="Pragma" content="no-cache">

<link href="site-style.css" type="text/css" rel="StyleSheet">
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico">

<title>Contact Form</title>

</head>

<body>

<?php
//require("class.phpmailer.php");
error_reporting(0);

if (strcmp($_POST["contactMsg"], "") != 0 && strcmp($_POST["email"], "") != 0) {
	$from = $_POST["email"];
	$fullName = $_POST["fullName"];
	$msg = $_POST["contactMsg"];
	mailMe($from, $fullName, $msg);
}

function logIt($s) {
	echo $s;
}

function mailMe($from, $name, $message) {
	$mes = <<< HERE
Message from $name:
$message
HERE;

	$h = "MIME-Version: 1.0\r\n";
	$h .= "Content-type: text/plain; charset=UTF-8\r\n";
	$h .= "From: $name <$from>\r\nReply-to: $name <$from>\r\n";
	$h .= "CC: zekr@siahe.com\r\n";
	ini_set("smtp_port", "25");
	ini_set("SMTP", "localhost");
	if (!mail("mohsens@gmail.com", "Message from: $name", $mes, $h))
		logIt("<div style='color: red'>Could not send your message because of the following error. Please contact directly to <i>mohsens@gmail.com</i>.</div>");
/*
	$mailer = new PHPMailer();

	$mailer->SetLanguage("en");
	$mailer->CharSet = "utf-8";
	$mailer->From = $from;
	$mailer->FromName = $name;
	$mailer->Sender = "zekr@siahe.com";
	$mailer->Subject = "Message from : $name";
	$mailer->IsMail();
	$mailer->Body = $mes;
	$mailer->AddAddress("mohsens@gmail.com", "Mohsen Saboorian");
	$mailer->AddAddress("zekr@siahe.com", "Mohsen Saboorian");
	$mailer->Mailer = "smtp";
	$mailer->Host = "localhost";
	$mailer->SMTPAuth = true;
	$mailer->Username = "zekr@siahe.com";
	$mailer->Username = "*****";
	//$mailer->SMTPDebug = true;
	$mailer->AddReplyTo($from, $name);
	$res = $mailer->Send();
	if (!$res)
		logIt("<div style='color: red'>Could not send your message because of the following error. Please contact directly to <i>mohsens@gmail.com</i>.<br><b>" . $mailer->ErrorInfo . "</b></div>");
*/
}
?>
<table width="100%" height="90%">
<tr><td>
<div align="center" style="font-size: 30pt">Thank you.</div>
<br>
<div align="center">
Press your browser's back button to go back to the contact page.
</div>
</td></tr>
</table>


</body>
</html>
