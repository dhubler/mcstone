package org.conf2.minecraft;

import java.io.*;

/**
 *
 */
public class CommandRunner implements Runnable {
    private BufferedWriter sink;
    private BufferedReader source;
    private boolean done;

    public CommandRunner(InputStream sourceStream, OutputStream sinkStream) {
        this.source = new BufferedReader(new InputStreamReader(sourceStream));
        this.sink = new BufferedWriter(new OutputStreamWriter(sinkStream));
    }

    public static CommandRunner replaceStdIn() throws IOException {
        PipedInputStream pipe = new PipedInputStream(2048);
        OutputStream proxy = new PipedOutputStream(pipe);
        CommandRunner commands = new CommandRunner(System.in, proxy);
        System.setIn(pipe);
        return commands;
    }

    public void sendCommand(String cmd) throws IOException {
        sink.write(cmd);
        sink.newLine();
        sink.flush();
    }

    @Override
    public void run() {
        done = false;
        while (!done) {
            try {
                String cmd = source.readLine();
                sendCommand(cmd);
            } catch (IOException e) {
                throw new CraftError("Cannot read from command input");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        CommandRunner runner = CommandRunner.replaceStdIn();
        new Thread(runner).start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = in.readLine();
            System.out.println("Got line : " + line);
        }
    }
}
