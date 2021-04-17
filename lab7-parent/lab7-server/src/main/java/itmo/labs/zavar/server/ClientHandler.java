package itmo.labs.zavar.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import itmo.labs.zavar.commands.base.CommandPackage;
import itmo.labs.zavar.commands.base.Environment;

public class ClientHandler implements Callable<String> {

	private AsynchronousSocketChannel asyncChannel;
	private Environment clientEnv;
	private ExecutorService clientExecutor;
	private ForkJoinPool clientWriter;
	private Logger logger = LogManager.getLogger(ClientHandler.class.getName());

	public ClientHandler(AsynchronousSocketChannel asyncChannel, Environment clientEnv, ExecutorService clientExecutor, ForkJoinPool clientWriter) {
		this.asyncChannel = asyncChannel;
		this.clientEnv = clientEnv;
		this.clientExecutor = clientExecutor;
		this.clientWriter = clientWriter;
	}

	@Override
	public String call() throws Exception {
		String host = asyncChannel.getRemoteAddress().toString();
		logger.info("Incoming connection from: " + host.replace("/", ""));

		final ByteBuffer buffer = ByteBuffer.wrap(new byte[4096 * 4]);

		while (asyncChannel.read(buffer).get() != -1) {
			try {

				CommandPackage per = ClientReader.read(buffer);
				logger.info("Command from " + host.replace("/", "") + ": " + per.getName());
				
				Future<ByteBuffer> futureOutBuffer = clientExecutor.submit(() -> {
					return ClientCommandExecutor.executeCommand(per, clientEnv);
				});

				ByteBuffer outBuffer = futureOutBuffer.get();
				
				clientWriter.submit(() -> {
					try {
						ClientWriter.write(asyncChannel, outBuffer);
					} catch (InterruptedException | ExecutionException | IOException e) {
						logger.error("Error while writing output to " + host.replace("/", ""));
					}
				});
				
				logger.info("Send command's output to " + host.replace("/", ""));
				
				buffer.flip();
				buffer.put(new byte[buffer.remaining()]);
				buffer.clear();

			} catch (Exception e) {
				logger.error("Error while handling " + host.replace("/", ""));
			}
		}

		asyncChannel.close();
		logger.info("Client " + host.replace("/", "") + " was successfully served");
		return host;
	}
}
