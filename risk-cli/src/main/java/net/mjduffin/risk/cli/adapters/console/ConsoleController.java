package net.mjduffin.risk.cli.adapters.console;

public interface ConsoleController {
    void request(ConsoleRequest request);
    void registerView(ConsoleView view);
}
