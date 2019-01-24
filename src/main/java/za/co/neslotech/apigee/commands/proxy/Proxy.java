package za.co.neslotech.apigee.commands.proxy;

import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Login;

@CommandLine.Command(
        name = "proxy",
        description = "proxy action to be performed",
        subcommands = {
                Import.class,
                Deploy.class,
                Undeploy.class,
                Delete.class
        })
public class Proxy implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

}
