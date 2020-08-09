package net.mjduffin.risk.cli.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInput implements RawInput {
    BufferedReader input;

    public ConsoleInput() {
        InputStreamReader in = new InputStreamReader(System.in);
        input = new BufferedReader(in);
    }

    @Override
    public String get() {
        String result = null;
        try {
            result = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
