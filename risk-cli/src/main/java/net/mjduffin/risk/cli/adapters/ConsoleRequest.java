package net.mjduffin.risk.cli.adapters;

public class ConsoleRequest {
    private final String request;

    public ConsoleRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }
}
