package za.co.neslotech.apigee.commands.app;

import picocli.CommandLine;

@CommandLine.Command(
        name = "app",
        description = "app action to be performed",
        subcommands = {
                Create.class,
                Delete.class
        })
public class App implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

}
