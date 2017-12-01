package de.fraunhofer.iosb.testrunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

/**
 * Simple test environment. The TestRunner takes the classnames of the tests as
 * commandline arguments and then executes the tests in the given order.
 *
 * @author sen (Fraunhofer IOSB)
 */
public class TestRunner {

	/**
	 * Command line entry point for the TestRunner.
	 *
	 * @param args
	 *            command line parameters
	 */
	public static void main(final String[] args) {
		final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
		String paramJson = null;
		IVCT_Verdict verdicts[] = new IVCT_Verdict[1];
		new TestRunner().executeTests(LOGGER, "SuT", args, paramJson, verdicts);

	}

	/**
	 * execute the tests given as classnames.
	 *
	 * @param logger
	 *            The explicit logger to use
	 * @param sutName
	 *            The Name of the System under Test
	 * @param classnames
	 *            The classnames of the tests to execute
	 * @param paramJson
	 *            the test case parameters as a json value
	 * @param verdicts
	 *            the array of individual test case verdicts
	 */
	public void executeTests(final Logger logger, final String sutName, final String[] classnames, final String paramJson,
			final IVCT_Verdict verdicts[]) {
		int i = 0;

		for (final String classname : classnames) {
			AbstractTestCase testCase = null;
			try {
				testCase = (AbstractTestCase) Thread.currentThread().getContextClassLoader().loadClass(classname)
						.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
				logger.error("Could not instantiate " + classname + " !", ex);
			}
			if (testCase == null) {
				verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
				verdicts[i++].text = "Could not instantiate " + classname;
				continue;
			}
			testCase.setSutName(sutName);
			testCase.setTcName(classname);
			verdicts[i++] = testCase.execute(paramJson, logger);
		}
	}

}
