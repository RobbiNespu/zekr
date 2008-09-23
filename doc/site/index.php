<html>
<head>
<title>Zekr: Quran for Mac, PC, Linux</title>
<link rel="shortcut icon" href="favicon.ico" />
</head>
<body>
<script type="text/javascript">
//<![CDATA[ 
var BrowserDetect = {
	init: function () {
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();
//]]>
</script>

<script type="text/javascript">
//<![CDATA[ 

var platform=navigator.platform;

if ((BrowserDetect.OS=="Mac"||platform=="MacIntel"))
{
window.location = "http://zekr.org/quran/quran-for-mac"
}

//]]>
</script>

<script type="text/javascript">
//<![CDATA[ 

var platform=navigator.platform;

if ((BrowserDetect.OS=="Windows"||platform=="Win32"||platform=="Win64"))
{
window.location = "http://zekr.org/quran/quran-for-windows"
}

//]]>
</script>

<script type="text/javascript">
//<![CDATA[ 

var platform=navigator.platform;

if ((BrowserDetect.OS=="Linux"))
{
window.location = "http://zekr.org/quran/quran-for-linux"
}

//]]>
</script>
<script type="text/javascript">
//<![CDATA[ 

window.location = "http://zekr.org/quran/quran-for-windows"

//]]>
</script>
</body>
</html>
