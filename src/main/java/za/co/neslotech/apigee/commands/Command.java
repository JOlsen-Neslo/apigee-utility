package za.co.neslotech.apigee.commands;

import picocli.CommandLine;

public abstract class Command {

    @CommandLine.Option(names = {"--proxy-host"}, description = "hostname of the proxy you want to define")
    protected String host;

    @CommandLine.Option(names = {"--proxy-port"}, description = "port of the proxy you want to define", defaultValue = "8080")
    protected String port;

}
