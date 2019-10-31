package de.fraunhofer.iosb.tc_lib;

public class IVCTVersionCheckFailed extends Exception {
  private static final long serialVersionUID = 1L;

  public IVCTVersionCheckFailed() {
    super("IVCTVersionCheck without exception text");
  }

  public IVCTVersionCheckFailed(String exceptionText) {
    super(exceptionText);
  }
  
  public IVCTVersionCheckFailed(String exceptionText, Throwable cause) {
    super(exceptionText, cause);
}
  
}
