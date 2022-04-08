import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LexScanner {
    public static class LexClass {
        public ArrayList<String> startingState;
        public ArrayList<String> endingState;
        public ArrayList<String> wordType;
        public ArrayList<ArrayList<String>> rulesTable;
        public ArrayList<String> tokenResult;

        public LexClass() {
            this.startingState = new ArrayList<String>();
            this.endingState = new ArrayList<String>();
            this.wordType = new ArrayList<String>();
            this.rulesTable = new ArrayList<ArrayList<String>>();
            this.tokenResult = new ArrayList<String>();
        }

        public int getIndexStartingState(String state) {
            return this.startingState.indexOf(state);
        }

        public int getIndexEndingState(String state) {
            return this.endingState.indexOf(state);
        }

        public int getIndexWordType(String c) {
            int result = -1;
            for (String s : this.wordType) {
                if (Pattern.matches(s, c)) {
                    result = this.wordType.indexOf(s) + 1;
                }
            }
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("Start Lexical Scanner!");
        LexClass lexClass = new LexClass();
        //Read rules
        try {
            System.out.println("Reading Rules.dat...");
            File rulesFile = new File("Rules.dat");
            Scanner rulesScan = new Scanner(rulesFile);

            Scanner startingStateScan = new Scanner(rulesScan.nextLine());
            while (startingStateScan.hasNext()) {
                lexClass.startingState.add(startingStateScan.next());
            }
            startingStateScan.close();

            Scanner endingStateScan = new Scanner(rulesScan.nextLine());
            while (endingStateScan.hasNext()) {
                lexClass.endingState.add(endingStateScan.next());
            }
            endingStateScan.close();

            Scanner wordTypeScan = new Scanner(rulesScan.nextLine());
            while (wordTypeScan.hasNext()) {
                lexClass.wordType.add(wordTypeScan.next());
            }
            wordTypeScan.close();

            while (rulesScan.hasNextLine()) {
                Scanner stateScan = new Scanner(rulesScan.nextLine());
                ArrayList<String> row = new ArrayList<String>();
                while (stateScan.hasNext()) {
                    row.add(stateScan.next());
                }
                stateScan.close();
                lexClass.rulesTable.add(row);
            }

            rulesScan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        //Read Input and Handle data
        try {
            System.out.println("Reading Input.vc...");
            File inputFile = new File("Input.vc");
            Scanner inputScan = new Scanner(inputFile);
            inputScan.useDelimiter("");
            String s = "";
            String currentState = lexClass.rulesTable.get(0).get(0);
            while (inputScan.hasNext()) {
                String c = inputScan.next();
                if (currentState.equals("_")) {
                    s = c;
                    currentState = (lexClass.getIndexWordType(c) == -1) ? "_" : lexClass.rulesTable.get(0).get(lexClass.getIndexWordType(c));
                    continue;
                }
                String nextState = "_";
                if ((lexClass.getIndexStartingState(currentState) != -1) && (lexClass.getIndexWordType(c) != -1)) {
                    nextState = lexClass.rulesTable.get(lexClass.getIndexStartingState(currentState)).get(lexClass.getIndexWordType(c));
                }
                if (nextState.equals("_")) {
                    if (lexClass.getIndexEndingState(currentState) != -1) {
                        if (lexClass.tokenResult.indexOf(s) == -1) lexClass.tokenResult.add(s);
                    } else {
                        if (lexClass.tokenResult.indexOf("error '" + s + "'") == -1)
                            lexClass.tokenResult.add("error '" + s + "'");
                    }
                    s = c;
                    currentState = (lexClass.getIndexWordType(c) == -1) ? "_" : lexClass.rulesTable.get(0).get(lexClass.getIndexWordType(c));
                } else {
                    s += c;
                    currentState = nextState;
                }
            }
            inputScan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        //Write Output
        try {
            File outputFile = new File("Output.vctok");
            if (outputFile.createNewFile()) {
                System.out.println("Created " + outputFile.getName() + " file.");
            } else {
                System.out.println("Found " + outputFile.getName() + " file.");
            }

            System.out.println("Writing Output.vctok...");
            FileWriter outputWriter = new FileWriter("Output.vctok");
            for (String s : lexClass.tokenResult) {
                outputWriter.write(s + "\n");
            }
            outputWriter.close();
            System.out.println("Process is completed!");
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


}
