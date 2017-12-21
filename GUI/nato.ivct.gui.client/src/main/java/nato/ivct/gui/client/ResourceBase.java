package nato.ivct.gui.client;

/**
 * <h3>{@link ResourceBase}</h3> This class is the base for resources contained
 * in this module.<br>
 * Do not move it to another package, the package is part of the resource
 * name.<br>
 * Usage: <code>ResourceBase.class.getResource("relativeFolder/file.ext")</code>
 *
 * @author hzg
 */
public final class ResourceBase {
	private ResourceBase() {
	}
	
	static public String PASSED = "CCFFCC";
	static public String FAILED = "DD5143";
	static public String INCONCLUSIVE = "8D6CAB";
	static public String RUNNING = "E68523";
	
	static public String getVerdictColor (String verdict) {
		if ("PASSED".equalsIgnoreCase(verdict))
			return PASSED;
		else if ("FAILED".equalsIgnoreCase(verdict))
			return FAILED;
		else if ("INCONCLUSIVE".equalsIgnoreCase(verdict))
			return INCONCLUSIVE;
		else
			return "E0E0E0";
	}
}
