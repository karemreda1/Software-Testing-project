/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.swtesting;

/**
 *
 * @author DELL
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writefile {

    /**
     * Writes a list of strings to a file, each string on a new line
     * @param fileName Name/path of the file
     * @param lines List of strings to write
     */
   public static void writeToFile(String filename, String data) {
    try (FileWriter writer = new FileWriter(filename)) {
        writer.write(data);
        System.out.println("File written successfully: " + filename);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

    // Example main to test
