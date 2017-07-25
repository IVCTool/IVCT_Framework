<jsp:useBean id="TcRunner" class="de.fraunhofer.iosb.tc.TcRunner"/>
<html>
<h1>Test Case Runner</h1>

<p>Current Log Level: ${TcRunner.logLevel}</p>
<p>Current Test Case: ${TcRunner.activeTestCase}</p>
</html>
