## Local Settings Designator
The value of `SETTINGS_DESIGNATOR` is a RTI-implementation specific string that the TC Runner uses when connecting to the RTI. The settings designator refers to a set of LRC settings that override the default settings for the LRC. For the format of the string please refer to the RTI documentation. If no value is provided then the settings designator defaults to an empty string, meaning that the default settings are used.

Note that the LRC image provides the possibility to set some of the default settings via LRC environment variables. These will be overriden by any settings provided in the settings designator.

### MaK RTI settings designator
For the MaK RTI the exact syntax of the settings designator is:

    RTI_RID_FILE /path/to/myRIDfile.mtl # {lisp_expression}*

Where:
* RTI_RID_FILE is optional. If included, the RTI_RID_FILE must be the first token in the string. The RID text following the token up to the '#' delimiter character is taken as the RID file name. If the # is omitted, then the remainder of the string text is used as the file name.
* The '#' deliminator is followed by zero or more lisp expressions.

If no RTI_RID_FILE is specified in the settings designator then the default RID file present in the LRC image is used. The default RID file is configured for strict RTI compliance and matches with the RID file in the RTI Exec image. So, when using an alternate RID file the settings in the file must be carefully reviewed.

To set an alternate MaK RID file (which must be volume mounted into the container) and to set the MaK RTI Exec address to host `rtiexec` and port `4000`, use the following string value: 

    RTI_RID_FILE /path/to/myRIDfile.mtl # (setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")

To just set the MaK RTI Exec address to `rtiexec` and port `4000`, use the following value:

    (setqb RTI_tcpPort 4000) (setqb RTI_tcpForwarderAddr \"rtiexec\")

Note that if no MaK RTI Exec address is provided in the settings designator, the default address is used. The default address can be set via the MAK LRC environment variables.

### Pitch RTI settings designator
For the Pitch RTI the settings designator refers to either the Central RTI Component (CRC), or to a local settings designator file.

Depending on the CRC mode, the format of the CRC reference is:
* Direct mode: ``crcAddress=<CRC-address>:<port>``
* Booster mode: ``crcAddress=<CRC-name>@<local-booster-address>:<port>``

For example, to set the Pitch CRC address to hostname `crc` and port `8989`, use the following value: 

    crcAddress=crc:8989

Note that if no CRC address is provided in the settings designator, the default address is used. The default address can be set via the Pitch LRC environment variables.

When specifying a local settings designator file, such as "MySpecialSettings", the LRC will search for additional settings as follows:
* First, it will look for a file named "MySpecialSettings.lsd" in ``/etc/prti1516e``.
* Then it will look for a file named "MySpecialSettings.lsd" in the home directory of the user ``/root``.
* Then it will look for a file named "MySpecialSettings.lsd" in the working directory of the federate application.

Any settings that it manages to read from the file in those three locations will be overriding the default settings, and in case there are settings available in several of these locations they will override each other in the same order as the files are read. If no local settings designator file is specified, the LRC will look for a file named “default.lsd”, in the same search order as above.
