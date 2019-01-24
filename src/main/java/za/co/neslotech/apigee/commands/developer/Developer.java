package za.co.neslotech.apigee.commands.developer;

import picocli.CommandLine;

@CommandLine.Command(
        name = "developer",
        description = "developer action to be performed",
        subcommands = {
                Create.class,
                Delete.class
        })
public class Developer implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

}
