package de.fraunhofer.iosb.tc_lib;

/**
 *
 * @author mul (Fraunhofer IOSB)
 */
public class TcFailed  extends Exception {
    public TcFailed(String msg)
    {
       super(msg);
    }

    public TcFailed(String message, Throwable cause)
    {
       super(message, cause);
    }
}
