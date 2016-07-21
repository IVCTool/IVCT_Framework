package de.fraunhofer.iosb.ivct;

public class QuitCmd implements Command {
	final String cmd;
	final IVCTcommander ivctCommander;
	final int counter;

	QuitCmd(final String cmd, final IVCTcommander ivctCommander, final int counter) {
		this.cmd = cmd;
		this.ivctCommander = ivctCommander;
		this.counter = counter;
	}

	public void execute() {
		String quitCmdString = IVCTcommander.printJson(this.cmd, this.counter);
		this.ivctCommander.sendToJms(quitCmdString);
	}
}
