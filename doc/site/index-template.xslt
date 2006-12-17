<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Language" content="en-us" />
				<meta name="keywords" content="Zekr, open source quranic project" />
				<meta name="name" content="Zekr home page" />
				<link href="site-style.css" type="text/css" rel="StyleSheet" />
				<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
				<title>The Zekr Project Homepage</title>
			</head>
			<body style="background: url('img/zekr.jpg') fixed center center no-repeat">
				<table style="margin:0px; padding:0" cellspacing="3">
					<tr>
						<td id="idMenue" width="130" valign="top">
							<div style="height: 90px">
								<img src="img/zekr-logo-small.png" alt="Zekr small logo" />
							</div>
							<div class="menuItem">
								<a href="./" title="Zekr homepage">Home</a>
								<a href="glossary.html" title="Quranic terms glossary">
									Glossary
								</a>
								<a href="download.html" title="Zek download page">
									Download
								</a>
								<a href="resources.html" title="Zek resources for download">
									Resources
								</a>
								<a href="screenshot/index.html" title="Screenshots">
									Screenshots
								</a>
								<a href="participation.html"
									title="Participation and help">
									Participation
								</a>
								<a href="doc.html" title="Zekr documentation">
									Documentation
								</a>
								<a href="faq.html" title="Zekr FAQ">FAQ</a>
								<a href="contact.html" title="Go to the contact page">
									Contact
								</a>
							</div>
							<div class="buttons">
							<xsl:apply-templates select="/index/buttons" />								
							<a href="http://fusion.google.com/add?moduleurl=http%3A//base.google.com/base/a/1096661/D12554424364135394330">
							<img src="http://buttons.googlesyndication.com/fusion/add.gif" width="104" height="17" border="0" alt="Add to Google" /></a>
							</div>
						</td>
						<td class="right">
							<div id="right" class="right">
								<div id="header" class="header" align="center">
									<img src="img/bism.gif" alt="In the name of Allah"
										title="In the name of Allah" />
								</div>
								<div class="intro" id="intro">
									<p>
										<div style="float: right">
											<span style="padding: 4px">
												<a href="img/mixed-en.png"
													target="_blank">
													<img class="headerScreens"
														src="img/mixed-en-small.png" alt="Sura view (English: left to right)"
														title="Mixed sura view (English: left to right)" />
												</a>
											</span>
											<span style="padding: 4px">
												<a href="img/mixed-fa.png"
													target="_blank">
													<img class="headerScreens"
														src="img/mixed-fa-small.png" alt="Mixed sura view (Farsi: right to left)"
														title="Sura view (Farsi: right to left)" />
												</a>
											</span>
										</div>
										Zekr is an open platform for research and
										development on the Holy Quran. It is a Quran based
										project, planned to be a universal, open source,
										cross-platform application to perform most of the
										usual refers to Quran. The main idea is to build
										an
										<i>as generic as possible</i>
										platform to be capable of having different
										<i>add-ins</i>
										for its tasks.
									</p>
									<p>
										The current release, 0.4.0 supports	scoped search through bot Quran and translations.
										See <a href="plan.xml">Zekr plan</a> for more details on the Zekr plan.
									</p>
									<p>
										The program is written in Java, on
										<a href="http://www.eclipse.org">Eclipse IDE</a>, and
										<a href="http://www.eclipse.org/swt/" title="Standard Widget Toolkit">SWT</a>
										as the widget toolkit. Please refer to <a href="relnotes.html">release notes</a>
										page for more technical details.
									</p>
								</div>
								<xsl:apply-templates select="/index/allnews" />
							</div>
						</td>
					</tr>
				</table>
				<br />

				<div class="footer" id="footer">
				<table border="0" width="100%" class="footer">
					<tr>
						<td>Copyright (c) 2004-2006 <a href="http://siahe.com">Siahe.com</a>.</td>
						<td width="10%" valign="top"><a href="http://sourceforge.net"><img
							src="http://sourceforge.net/sflogo.php?group_id=128414&amp;type=2"
							width="125" height="37" alt="SourceForge.net Logo" /></a></td>
						<td width="10%"><a href="http://www.eclipse.org"><img alt="Built on Eclipse"
							src="img/builtoneclipse.png" /></a></td>
						<td width="10%" valign="top"><a href="http://velocity.apache.org"><img
							src="img/powered-by-velocity.gif" border="0" alt="Powered by Velocity" /></a></td>
					</tr>
				</table>
				</div>

<script src="http://www.google-analytics.com/urchin.js" type="text/javascript"></script>
<script type="text/javascript">_uacct = "UA-336966-1";urchinTracker();</script>
<!-- stat counter -->
<script language="JavaScript" type="text/javascript" src="stat.js"></script>
<script language="JavaScript" type="text/javascript">persianstat(10004449, 0);</script>
<!-- /stat counter -->


			</body>
		</html>
	</xsl:template>
	<xsl:template match="news">
		<div class="news">
			<fieldset>
				<legend><xsl:value-of select="@title" /> 
				(<xsl:value-of select="@date" />)</legend>
				
				<xsl:if test="@icon">
				<div style="float: right;">
				<img>
				<xsl:attribute name="src">
					<xsl:text>img/</xsl:text>
					<xsl:value-of select="@icon"/>
				</xsl:attribute>
				</img>
				</div>
				</xsl:if>
				
				<xsl:copy-of select="." />
			</fieldset>
		</div>
	</xsl:template>
	<xsl:template match="button">
		<div class="button">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
				<img>
					<xsl:attribute name="src">
						<xsl:text>img/b/</xsl:text>
						<xsl:value-of select="@icon"/>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="@title"/>
					</xsl:attribute>
					<xsl:attribute name="title">
						<xsl:value-of select="@title"/>
					</xsl:attribute>
				</img>
			</a>
		</div>
	</xsl:template>

	
</xsl:stylesheet>
