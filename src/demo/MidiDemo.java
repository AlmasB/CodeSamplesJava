package com.almasb.java.framework.demo;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import com.almasb.java.ui.FXWindow;

public class MidiDemo extends FXWindow {

    private MidiChannel midiChannel;
    private int volume = 128;

    @Override
    protected void createContent(Pane root) {
        root.setPrefSize(400, 300);

        Synthesizer synthesizer;
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midiChannel = synthesizer.getChannels()[9];

            if (midiChannel == null)
                throw new NullPointerException("");

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected void initScene(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode().ordinal() >= 0 && event.getCode().ordinal() <= 125) {
                midiChannel.noteOn(event.getCode().ordinal(), volume);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode().ordinal() >= 0 && event.getCode().ordinal() <= 125) {
                midiChannel.noteOff(event.getCode().ordinal());
            }
        });
    }

    @Override
    protected void initStage(Stage primaryStage) {
        primaryStage.show();
    }

    public static void main(String[] args) {
        MidiDemo midi = new MidiDemo();
        midi.init();
    }
}
