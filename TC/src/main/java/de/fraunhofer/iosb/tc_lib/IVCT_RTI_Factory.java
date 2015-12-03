package de.fraunhofer.iosb.tc_lib;

import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import org.slf4j.Logger;


/**
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCT_RTI_Factory {

    /**
     * @param logger reference to the logger
     * @return reference to an ivct rti ambassador or a null
     */
    public static IVCT_RTIambassador getIVCT_RTI(final Logger logger) {

        // Connect to RTI
        try {
            final RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
            EncoderFactory encoderFactory;
            RTIambassador rtiAmbassador = null;

            rtiAmbassador = rtiFactory.getRtiAmbassador();
            encoderFactory = rtiFactory.getEncoderFactory();
            final IVCT_RTIambassador ivct_rti = new IVCT_RTIambassador(rtiAmbassador, encoderFactory, logger);
            return ivct_rti;
        }
        catch (final Exception e) {
            e.printStackTrace();
            logger.error("Unable to create RTI ambassador.");
            return null;
        }
    }
}
