package de.fraunhofer.iosb.testrunner;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcBaseModel;
import de.fraunhofer.iosb.tc_lib.TcBaseModelFactory;
import de.fraunhofer.iosb.tc_lib.TcFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.TcParamTmr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple test enviroment. The TestRunner takes the classnames of the tests as
 * commandline arguments and then executes the tests in the given order.
 *
 * @author sen (Fraunhofer IOSB)
 */
public class TestRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);


    /**
     * Command line entry point for the TestRunner.
     *
     * @param args command line parameters
     */
    public static void main(final String[] args) {
        new TestRunner().executeTests(args);

    }


    /**
     * execute the tests given as classnames.
     *
     * @param classnames The classnames of the tests to execute
     */
    public void executeTests(final String[] classnames) {
        for (final String classname: classnames) {
            AbstractTestCase testCase = null;
            try {
                testCase = (AbstractTestCase) Class.forName(classname).newInstance();
            }
            catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                LOGGER.error("Could not instantiate " + classname + " !", ex);
            }
            if (testCase == null) {
                continue;
            }
            // initialize LOGGER, localcache, federeateAmbassador and tcparam
            final TcParamTmr tcParam = new TcParamTmr();
            final Logger testLogger = LoggerFactory.getLogger(testCase.getClass());
            final TcBaseModelFactory tcBaseModelFactory = new TcBaseModelFactory();
            final IVCT_RTIambassador ivct_rti = IVCT_RTI_Factory.getIVCT_RTI(testLogger);
            final TcBaseModel tcBaseModel = (TcBaseModel) tcBaseModelFactory.getLocalCache(ivct_rti, testLogger, tcParam);
            final TcFederateAmbassador tcFederateAmbassador = new TcFederateAmbassador(tcBaseModel, testLogger);
            testCase.execute(tcParam, tcBaseModel, testLogger);
        }
    }
}
