/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package de.fraunhofer.iosb.testrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.IVCT_Verdict;

/**
 * Simple test environment. The TestRunner takes the classnames of the tests as
 * commandline arguments and then executes the tests in the given order.
 *
 * @author sen (Fraunhofer IOSB)
 */
public class TestRunner {

	protected boolean health;
	protected String myClassName;

	public TestRunner() {
		myClassName = this.getClass().getSimpleName();
	}

	/**
	 * Command line entry point for the TestRunner.
	 *
	 * @param args command line parameters
	 */
	public static void main(final String[] args) {
		final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
		String paramJson = null;
		IVCT_Verdict verdicts[] = new IVCT_Verdict[1];
		// new TestRunner().executeTests(LOGGER, "SuT", args, paramJson, verdicts);
		TestRunner testrunner = new TestRunner();
		testrunner.executeTests(LOGGER, "SuT", args, paramJson, verdicts);
	}

	/**
	 * execute the tests given as classnames.
	 *
	 * @param logger     The explicit logger to use
	 * @param sutName    The Name of the System under Test
	 * @param classnames The classnames of the tests to execute
	 * @param paramJson  the test case parameters as a json value
	 * @param verdicts   the array of individual test case verdicts
	 */
	public void executeTests(final Logger logger, final String sutName, final String[] classnames,
			final String paramJson, final IVCT_Verdict verdicts[]) {
		int i = 0;

		for (final String classname : classnames) {
			AbstractTestCaseIf testCase = null;
			try {
				testCase = (AbstractTestCaseIf) Thread.currentThread().getContextClassLoader().loadClass(classname)
						.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
				logger.error("Could not instantiate " + classname + " !", ex);
			}
			if (testCase == null) {
				verdicts[i] = new IVCT_Verdict();
				verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
				verdicts[i].text = "Could not instantiate " + classname;
				i++;
				continue;
			}
			testCase.setSutName(sutName);
			testCase.setTcParam(paramJson);
			testCase.setTcName(classname);

			verdicts[i++] = testCase.execute(logger);
		}
	}

}
