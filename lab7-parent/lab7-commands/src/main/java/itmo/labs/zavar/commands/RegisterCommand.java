package itmo.labs.zavar.commands;

import java.io.InputStream;
import java.io.OutputStream;

import itmo.labs.zavar.commands.base.Command;
import itmo.labs.zavar.commands.base.Environment;
import itmo.labs.zavar.exception.CommandArgumentException;
import itmo.labs.zavar.exception.CommandException;

public class RegisterCommand extends Command {

	private RegisterCommand() {
		super("register", "login", "password");
	}

	@Override
	public void execute(ExecutionType type, Environment env, Object[] args, InputStream inStream, OutputStream outStream) throws CommandException {
		if (args instanceof String[] && args.length != 2 && (type.equals(ExecutionType.CLIENT) || type.equals(ExecutionType.INTERNAL_CLIENT))) {
			throw new CommandArgumentException("This command requires 2 arguments!\n" + getUsage());
		} else {
			
		}
		
	}

	@Override
	public String getHelp() {
		return "This command is using for registration!";
	}
	
	@Override
	public boolean isAuthorizationRequired() {
		return false;
	}

}
