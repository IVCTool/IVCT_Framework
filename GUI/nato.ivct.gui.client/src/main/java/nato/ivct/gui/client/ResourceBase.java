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
	
	static public String PASSED = "00FF00";
	static public String FAILED = "FF6666";
	static public String INCONCLUSIVE = "E5CCFF";
	static public String RUNNING = "FFA500";
	
	static public String getVerdictColor (String verdict) {
		if (verdict.equalsIgnoreCase("PASSED"))
			return PASSED;
		else if (verdict.equalsIgnoreCase("FAILED"))
			return FAILED;
		else if (verdict.equalsIgnoreCase("INCONCLUSIVE"))
			return INCONCLUSIVE;
		else
			return "E0E0E0";
	}
}
