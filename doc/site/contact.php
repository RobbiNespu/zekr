<html>

<head>

<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<meta http-equiv="Content-Language" content="en-us">
<meta name="keywords" content="Zekr, open source quranic project">
<meta name="name" content="Contact">

<link href="site-style.css" type="text/css" rel="StyleSheet">
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico">

<title>Contact Form</title>

</head>

<body>

<?php
if (strcmp($_POST["contactMsg"], "") != 0 && strcmp($_POST["email"], "") != 0) {
	$from = $_POST["email"];
	$fullName = $_POST["fullName"];
	$msg = $_POST["contactMsg"];
	mailMe($from, $fullName, $msg);
}

function mailMe($from, $name, $message) {
	$h = "MIME-Version: 1.0\n";
	$h .= "Content-type: text/plain; charset=UTF-8\n";
	$h .= ("From:$from\nReply-to:$from");

	$mes = <<< HERE
Message from $name:
______________________________
$message
______________________________
HERE;
mail("zekr@siahe.com,m.saboorian@ece.ut.ac.ir", "Zekr contact from $fullName", $mes, $h);
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
