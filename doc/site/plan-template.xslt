<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Language" content="en-us" />
				<meta name="keywords" content="Zekr, open source quranic project" />
				<meta name="name" content="Zekr plan" />
				<link href="site-style.css" type="text/css" rel="StyleSheet" />
				<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
				<title>Zekr 1.0 Plan</title>
			</head>
			<body style="background: url('img/zekr.jpg') fixed center center no-repeat">
				<table width="100%" border="0">
					<tr>
						<td width="80%">
							<h1>The Zekr Platform 1.0 Plan</h1>
						</td>
						<td align="right" width="20%">
							<a href="./">
								<img src="img/zekr-logo-small.png" alt="Zekr logo" />
							</a>
						</td>
					</tr>
				</table>
				This is the plan (road map, or <i>all in one</i> ;) ) 
				document for the Zekr project. Please note that this
				document is still incomplete.
				<br />
				<br />
				<table class="tab" width="95%" cellpadding="2" cellspacing="0">
					<tr>
						<th width="30%">Feature <span style="font-size: 0.7em">(name - % progress - milestone)</span></th>
						<th width="70%">Detail <span style="font-size: 0.7em">(name - % progress - milestone)</span></th>
					</tr>
					<xsl:apply-templates select="/plan/feature" />
				</table>
				<br />
				<br />
				<hr noshade="noshade" />
				This is a client-side, automatically generated list, using a 
				simple <a href="plan.xml">XML</a> as data source and an 
				<a href="plan-template.xslt">XSLT</a> file as the transformer engine.<br />
				Special thanks to <a href="http://siahe.com/blogs/mohammad">Mohammad Mehdi Saboorian</a>.


<!-- Start of StatCounter Code -->
<script type="text/javascript" language="javascript">
var sc_project=1162161; var sc_invisible=1; var sc_partition=10; var sc_security="b95f6b46"; 
</script>
<script type="text/javascript" language="javascript" src="http://www.statcounter.com/counter/counter.js"></script><noscript><a href="http://www.statcounter.com/" target="_blank"><img  src="http://c11.statcounter.com/counter.php?sc_project=1162161&amp;java=0&amp;security=b95f6b46&amp;invisible=1" alt="free webpage counters" border="0" /></a> </noscript>
<!-- End of StatCounter Code -->

			</body>
		</html>
	</xsl:template>
	<xsl:template match="feature">
		<tr>
			<td valign="top" nowrap="nowrap">
				<span style="font-size:1.2em">
					<xsl:value-of select="@title" />
				</span>
				<span style="font-size:1em">
					<xsl:text> - </xsl:text>
					<span title="Progress"
						style="cursor: default; font-weight: bold; color: red">
						<xsl:if test="@progress=100">
							<span class="imcomplete">
								<xsl:value-of select="@progress" />
							</span>
						</xsl:if>
						<xsl:if test="not(@progress=100)">
							<span class="completed">
								<xsl:value-of select="@progress" />
							</span>
						</xsl:if>
					</span>
					<xsl:text>% - </xsl:text>
					<span title="Estimated target milestone"
						style="cursor: default; font-weight: bold">
						<xsl:value-of select="@em" />
					</span>
				</span>
				<xsl:text> </xsl:text>
				<xsl:choose> 
					<xsl:when test="(@progress=100)"> 
						<img src="img/complete.gif" alt="complete" />
					</xsl:when> 
					<xsl:when test="(@progress &lt; 100) and (@progress > 0)"> 
						<img style="height: 6px" src="img/incomplete.gif" alt="incomplete" />
					</xsl:when> 
					<xsl:otherwise>
						<img src="img/notstarted.gif" alt="not started" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td valign="top">
				<ul>
					<xsl:for-each select="child::*">
						<xsl:call-template name="sub" />
					</xsl:for-each>
				</ul>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="sub">
		<li>
			<span style="font-size:0.95em">
				<xsl:value-of select="@title" />
			</span>
			<xsl:text> - </xsl:text>
			<span title="Progress"
				style="cursor: default; color: blue; font-weight: bold">
				<xsl:if test="@progress=100">
					<span class="incomplete">
						<xsl:value-of select="@progress" />
					</span>
				</xsl:if>
				<xsl:if test="not(@progress=100)">
					<span class="completed">
						<xsl:value-of select="@progress" />
					</span>
				</xsl:if>
			</span>
			<xsl:text>% - </xsl:text>
			<span title="Estimated target milestone" style="cursor: default">
				<xsl:value-of select="@em" />
			</span>
			<xsl:text> </xsl:text>
			<xsl:choose> 
				<xsl:when test="(@progress=100)"> 
					<img src="img/complete.gif" alt="complete" />
				</xsl:when> 
				<xsl:when test="(@progress &lt; 100) and (@progress > 0)"> 
					<img style="height: 6px" src="img/incomplete.gif" alt="incomplete" />
				</xsl:when> 
				<xsl:otherwise>
					<img src="img/notstarted.gif" alt="not started" />
				</xsl:otherwise>
			</xsl:choose>
			<ul>
				<xsl:for-each select="child::*">
					<xsl:call-template name="sub" />
				</xsl:for-each>
			</ul>
		</li>
	</xsl:template>
</xsl:stylesheet>
