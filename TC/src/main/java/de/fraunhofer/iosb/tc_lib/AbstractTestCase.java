package de.fraunhofer.iosb.tc_lib;

import org.slf4j.Logger;


/**
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase {

    protected abstract void performTest() throws TcInconclusive, TcFailed;


    protected abstract void preambleAction() throws TcInconclusive;


    protected abstract void postambleAction() throws TcInconclusive;


    /**
     * @param tcParam test case parameters
     * @param ivct_BaseModel reference to the local cache factory
     * @param logger The {@link Logger} to use
     */
    public void execute(final IVCT_TcParam tcParam, final IVCT_BaseModel ivct_BaseModel, final Logger logger) {

        // Print out test case parameters
        // logger.info(tcParam.toString());

        // preamble block
        try {
            // Test case phase
            logger.info("TEST CASE PREAMBLE");

            // Publish interaction / object classes
            // Subscribe interaction / object classes

            this.preambleAction();
        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti(tcParam);
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            this.performTest();

        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti(tcParam);
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }
        catch (final TcFailed ex) {
            ivct_BaseModel.terminateRti(tcParam);
            logger.info("TC FAILED " + ex.getMessage());
            return;
        }

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            this.postambleAction();
            logger.info("TC PASSED");
        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti(tcParam);
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }
    }
}
