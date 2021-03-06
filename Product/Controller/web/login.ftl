<HTML>
<HEAD>
<TITLE>OpenRemote Controller</TITLE>
<link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
<link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
<META http-equiv=Content-Type content="text/html; charset=UTF-8">
<link type="text/css" href="css/index.css" rel="stylesheet" />
<link type="text/css" href="css/admin.css" rel="stylesheet" />
<script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
<script type="text/javascript" src="jslib/jquery.form-2.24.js"></script>
<script type="text/javascript" src="js/index.js"></script>
</HEAD>
<BODY>
<div id="context">
	<TABLE height="95%" cellSpacing=0 cellPadding=0 width="100%"
		align=center border=0>
		<TBODY>
			<TR vAlign="center" align="middle">
				<TD>
				<TABLE cellSpacing=0 cellPadding=0 width=468 bgColor=#ffffff border=0>
					<TBODY>
						<TR>
							<TD width=20 background="image/rbox_1.gif" height=20></TD>
							<TD width=108 background="image/rbox_2.gif" height=20></TD>
							<TD width=56><IMG height=20 src="image/rbox_2.gif" width=56></TD>
							<TD width=100 background="image/rbox_2.gif"></TD>
							<TD width=56><IMG height=20 src="image/rbox_2.gif" width=56></TD>
							<TD width=108 background="image/rbox_2.gif"></TD>
							<TD width=20 background="image/rbox_3.gif" height=20></TD>
						</TR>
						<TR>
							<TD align=left background="image/rbox_4.gif" rowSpan=2></TD>
							<TD style="border-bottom: 1px solid #ccc" colSpan=5 height=50>
							<a href="http://www.openremote.org/"><img alt=""
								src="image/global.logo.gif"></a></TD>
							<TD align=left background="image/rbox_6.gif" rowSpan=2></TD>
						</TR>
						<TR>
							<TD align=left colSpan=5 height=140>
							<p><a href="index.html"><img src="image/back.png" alt="Back" border=0 /> Back</a></p>
							
							<p class="welcome">Login</p>					
							<p>
								Here you can login to the administrator panel using the credentials you used to sync the configuration from 
								the OpenRemote Composer.
							</p>
							<#if errorMessage?has_content>
								<p class="activeErrMsg" style="display:block">${errorMessage}</p>
							</#if>	
							<div id="online-cont">
								<p>This requires your <b>Modeler</b> account.</p>
								<form action="login" method="post">
									<p>
										<label for="username">username : </label><br/> <input id="username" name="username" type="text" />
									</p>
									<p> 
										<label for="password">password : </label><br/> <input id="password" name="password" type="password" />
									</p>
									<p>
										<input type="submit" value="Login" />
									</p> 
								</form>
								<span>Don't have OpenRemote Designer account? <a href="http://composer.openremote.org/demo/">Create account now!</a></span>
							</div>
							<br />
	
							<p><a href="index.html"><img src="image/back.png" alt="Back" border=0 /> Back</a></p>
							</TD>
						</TR>
						<TR>
							<TD align=left background="image/rbox_7.gif" height=20></TD>
							<TD align=left background="image/rbox_8.gif" colSpan=5 height=20></TD>
							<TD align=left background="image/rbox_9.gif" height=20></TD>
						</TR>
					</TBODY>
				</TABLE>
				<span class="copyright">Copyright &copy; 2008-2012 OpenRemote.
				Licensed under Affero General Public License.</span> <span id="version" class="version">Version:</span></TD>
			</TR>
		</TBODY>
	</TABLE>
</div>
</BODY>
</HTML>
