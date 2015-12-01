package de.fraunhofer.iosb.tc_lib;

/**
 *
 * @author mul (Fraunhofer IOSB)
 */
public class TcInconclusive  extends Exception {
    public TcInconclusive(String msg)
    {
       super(msg);
    }

    public TcInconclusive(String message, Throwable cause)
    {
       super(message, cause);
    }
}
