package de.fraunhofer.iosb.tc_lib;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcParamTmr;
import org.slf4j.Logger;


public interface IVCT_BaseModelFactory {

    /**
     * @param logger reference to the logger
     * @param ivct_rti reference to the ivct rti
     * @return a local cache or null in case of a problem
     */
    IVCT_BaseModel getLocalCache(final IVCT_RTIambassador ivct_rti, final Logger logger, final TcParamTmr tcParam);
}
