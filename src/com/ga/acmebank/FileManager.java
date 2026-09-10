package com.ga.acmebank;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;


public class FileManager {
    BufferedWriter writer = null;

    public static void main(String[] args) {
        FileManager newWrite = new FileManager();
        newWrite.fileWriter();
        readConfigFile();
    }

    public void fileWriter() {
        final String USERS = "data/users.txt";
        try {
            File newFile = new File(USERS);
            if (newFile.createNewFile()) {
                System.out.println("File Created:" + newFile.getName());
            } else {

                try {
                    writer = new BufferedWriter(new FileWriter("data/users.txt"));
                    writer.write("=====Main Users DataBase=====");
                    writer.newLine();
                    writer.write("USERID || ROLE || FULL NAME || PASSWORD || CARD_TYPE");
                    writer.newLine();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readConfigFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("data/users.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Config: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



