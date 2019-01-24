package za.co.neslotech.apigee;

import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Login;
import za.co.neslotech.apigee.commands.app.App;
import za.co.neslotech.apigee.commands.developer.Developer;
import za.co.neslotech.apigee.commands.product.Product;
import za.co.neslotech.apigee.commands.proxy.Proxy;

@CommandLine.Command(
        name = "action", version = "v0.0.1",
        description = "action to be performed",
        mixinStandardHelpOptions = true,
        subcommands = {
                Login.class,
                Proxy.class,
                Product.class,
                App.class,
                Developer.class
        })
public class ApigeeUtility implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public static void main(String[] args) {
        CommandLine.run(new ApigeeUtility(), args);
    }

}
