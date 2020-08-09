package net.mjduffin.risk.cli.adapters;

public interface ConsoleController {
    void request(ConsoleRequest request);
    void registerView(ConsoleView view);
}
