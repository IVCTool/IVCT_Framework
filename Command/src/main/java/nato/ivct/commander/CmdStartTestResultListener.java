package nato.ivct.commander;

public class CmdStartTestResultListener implements Command {

	private IvctTestResultListener listener;
	
	public CmdStartTestResultListener() {
		listener = new IvctTestResultListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE));

	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}
