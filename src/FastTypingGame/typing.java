package FastTypingGame;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;


public class typing extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private Form levelForm;
    private ChoiceGroup levelChoice;
    private StringItem targetItem;
    private TextField inputField;
    private TextField nameField;
    private String targetSentence;
    private String[] sentences = {
        "(change this) level 1 sentence",
        "(change this) level 2 sentence, longer",
        "(change this) level 3 sentence, more complicated"
    };
    private long startTime;
    private float timeTaken;
    private int selectedIndex;
    private Command startCommand;
    private Command submitCommand;
    private Command selectLevelCommand;
    private Command backCommand;
    private Command highScoreCommand;
    private Command saveCommand;
    private StringItem resultItem;
    private boolean isStarted;
    private boolean formincomplete;
    private RecordStore recordStore;

    public typing() {
        display = Display.getDisplay(this);
        initializeForms();
    }

    private void initializeForms() {
        // Create level selection form
        levelForm = new Form("Level selection");
        levelChoice = new ChoiceGroup("Select level:", ChoiceGroup.EXCLUSIVE);
        for (int i = 1; i <= sentences.length; i++) {
            levelChoice.append("Level " + i, null);
        }
        levelForm.append(levelChoice);
        
        // Commands for level selection
        selectLevelCommand = new Command("Start", Command.BACK, 1);
        highScoreCommand = new Command("High Scores", Command.ITEM, 1);
        levelForm.addCommand(selectLevelCommand);
        levelForm.addCommand(highScoreCommand);
        levelForm.setCommandListener(this);
        
        // Main form for the typing game
        form = new Form("Fast typing");
        targetItem = new StringItem("Sentence:", "");
        form.append(targetItem);
        inputField = new TextField("Press Start and write the sentence:", "", 100, TextField.ANY);
        form.append(inputField);
        resultItem = new StringItem("Result: ", "");
        form.append(resultItem);
        nameField = new TextField("Your name:", "", 30, TextField.ANY);
        //nameField.setLabel(""); 
        formincomplete = false;
        // Commands for typing game
        startCommand = new Command("Start", Command.OK, 1);
        submitCommand = new Command("Done!", Command.OK, 2);
        backCommand = new Command("Back", Command.BACK, 1);
        saveCommand = new Command("Save", Command.OK, 2);
        form.addCommand(startCommand);
        form.addCommand(backCommand);
        form.setCommandListener(this);
        
        isStarted = false;

        // Initialize RecordStore for high scores
        try {
            recordStore = RecordStore.openRecordStore("highScoreStore", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startApp() {
       display.setCurrent(levelForm); // Start by showing the level selection
    }

    public void commandAction(Command c, Displayable d) {
        if (d == levelForm) {
            if (c == selectLevelCommand) {
                handleLevelSelection();
            } else if (c == highScoreCommand) {
                displayHighScores();
            }
        } else if (d == form) {
            handleGameCommands(c);
        }
        if (c == backCommand) {
            handleBack();
        }
    }

    private void handleLevelSelection() {
        resetGame();
        selectedIndex = levelChoice.getSelectedIndex();
        targetSentence = sentences[selectedIndex];
        targetItem.setText(targetSentence);
        display.setCurrent(form);
    }

    private void handleGameCommands(Command c) {
        if (c == startCommand) {
            startGame();
        } else if (c == submitCommand) {
            handleSubmit();
        } else if (c == saveCommand) {
            handleSave();
        }
    }

    private void startGame() {
        startTime = System.currentTimeMillis();
        inputField.setString("");
        resultItem.setText("");
        isStarted = true;
        form.delete(form.size() - 1);
        // Show submit button
        form.removeCommand(startCommand);
        form.addCommand(submitCommand);
    }

    private void handleSubmit() {
        long endTime = System.currentTimeMillis();
        String typedText = inputField.getString();
        timeTaken = (float)(endTime - startTime) / 1000;

        if (typedText.equals(targetSentence)) {
            form.delete(form.size() - 1);
            form.delete(form.size() - 1);
            formincomplete = true;
            resultItem.setText("Correct! Time: " + timeTaken + " seconds.");
            form.append(resultItem);
            if (!containsNameField()) {
                form.append(nameField);
            }
            form.removeCommand(submitCommand);
            form.addCommand(saveCommand);
            isStarted = false;
        } else {
            resultItem.setText("Incorrect. Try again!");
            form.append(resultItem);
            isStarted = false;

            form.removeCommand(submitCommand); // Remove submit button
            form.addCommand(startCommand); // Add start command again
        }
    }
    
    private void handleBack() {
        display.setCurrent(levelForm);
    }
    
    private void handleSave() {
        String playerName = nameField.getString();
        saveHighScore(selectedIndex,playerName, timeTaken);

        // Clean up after saving
        form.delete(form.size() - 1); // Remove the name field after saving
        form.removeCommand(saveCommand); // Remove save command
        displayHighScores(); // Display high scores
    }

    private void resetGame() {

        form.addCommand(startCommand);
        form.addCommand(backCommand);
        if (containsNameField()) {
            form.delete(form.size() - 1); // Remove nameField if it exists
        }
        if (formincomplete){
            form.delete(form.size()-1); // delete the results 
            form.append(targetItem);
            form.append(inputField);
            form.append(resultItem);
            formincomplete = false;
        }
        inputField.setString("");
        resultItem.setText("");
        nameField.setString("");
        isStarted = false; // Reset game state
    }

    private boolean containsNameField() {
        return form.size() > 0 && form.get(form.size() - 1) == nameField;
    }

    private void saveHighScore(int Level,String playerName, float timeT){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(Level);
            dos.writeUTF(playerName);
            dos.writeFloat(timeT);
            byte[] data = baos.toByteArray();

            recordStore.addRecord(data, 0, data.length);
            dos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayHighScores() {
        selectedIndex = levelChoice.getSelectedIndex(); 
        int lev;
        Form highScoreForm = new Form("High Scores for Level " + (selectedIndex+1),null);
        try {
            Vector highScores = new Vector();
            for (int i = 1; i <= recordStore.getNumRecords(); i++) {
                byte[] data = recordStore.getRecord(i);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                lev = dis.readInt();
                    if (lev == selectedIndex){
                    String name = dis.readUTF();
                    float time = dis.readFloat();
                    highScores.addElement(new HighScoreEntry(name, time));
                    }
                dis.close();
            }

            sortHighScores(highScores);

            for (int i = 0; i < highScores.size(); i++) {
                HighScoreEntry entry = (HighScoreEntry) highScores.elementAt(i);
                highScoreForm.append((i + 1) + ". " + entry.name + ": " + entry.time + " sec\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        highScoreForm.addCommand(backCommand);
        highScoreForm.setCommandListener(this);
        display.setCurrent(highScoreForm);
    }

    class HighScoreEntry {
        int level;
        String name;
        float time;
        //long time;
        HighScoreEntry(String name, float time){
            this.level = level;
            this.name = name;
            this.time = time;
        }
    }

    private void sortHighScores(Vector highScores) {
        for (int i = 0; i < highScores.size() - 1; i++) {
            for (int j = i + 1; j < highScores.size(); j++) {
                HighScoreEntry score1 = (HighScoreEntry) highScores.elementAt(i);
                HighScoreEntry score2 = (HighScoreEntry) highScores.elementAt(j);
                if (score1.time > score2.time) {
                    highScores.setElementAt(score2, i);
                    highScores.setElementAt(score1, j);
                }
            }
        }
    }

    protected void destroyApp(boolean unconditional) {
        if (recordStore != null) {
            try {
                recordStore.closeRecordStore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
        protected void pauseApp() {
        // You can save the state or perform any cleanup if necessary.
    }
}
