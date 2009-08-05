Deployment Instructions.
------------------------------------
The Map2PDF service uses the Java Servlet technology and can be deployed any Servlet / J2EE container including
Apache Tomcat 6, IBM Websphere, Oracle Weblogic etc. 

The download (.zip file), from the code gallery consists of a deployable WAR or a Web Archive. To deploy it on Tomcat do the following:

a. Rename the downloaded .zip file to Map2PDF.war.
b. Copy this .war file to your Tomcat's "webapps" directory. 
   It is normally located at "%Program Files%\Apache Software Foundation\Tomcat 6.0\webapps"
c. To start Tomcat, go to Start->All Programs->Apache Tomcat 6.0->Monitor Tomcat (If Tomcat is already running skip steps c & d)
   (Note: The Tomcat's service runs on a specific port. This port number is chosen by the user when Tomcat is being installed. )
d. Click on "Start" to start the service.
e. Once Tomcat is started, it will now automatically extract the contents of the .war file that you copied and deploy it on the server. 
f. To access the deployed application on the server, use the following url : http://[your host name]:[tomcat's port]/Map2PDF

If you do not have any Servlet/J2EE container deployed you can download Apache Tomcat from (http://tomcat.apache.org) and install it on your computer.
Tomcat requires Java to be installed on the computer as well, so if you do not have Java installed, download Java Development Kit 6.0 from
(http://java.sun.com/downloads/index.jsp)

If you primarily use IIS for deploying web applications, you can use the Tomcat IIS connector. This will enable access of web applications that are 
deployed on Tomcat via IIS. For more information, check out: (http://blogs.msdn.com/silverlight_plus_java/archive/2008/08/12/tip-make-iis-serving-jsps-and-servlets-through-tomcat.aspx)
