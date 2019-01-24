package za.co.neslotech.apigee.commands.product;

import picocli.CommandLine;

@CommandLine.Command(
        name = "product",
        description = "product action to be performed",
        subcommands = {
                Create.class,
                Delete.class
        })
public class Product implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

}
