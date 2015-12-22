/*
Copyright 2015, [name of copyright owner, Johannes Mulder (Fraunhofer IOSB)"]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
