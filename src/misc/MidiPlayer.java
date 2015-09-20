package com.almasb.java.audio;

import javafx.beans.property.SimpleIntegerProperty;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * Very simple player that can play midi sounds
 * Be sure to call close() once you are done with the player
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class MidiPlayer {

    private Synthesizer synthesizer;

    /**
     * Volume [0..128]
     *
     * @defaultValue 100% (128)
     */
    private SimpleIntegerProperty volume = new SimpleIntegerProperty(128);

    public MidiPlayer() throws Exception {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
    }

    /**
     * Plays a single note at specified channel
     *
     * @param noteNumber [0..127]
     * @param channel   [0..15]
     */
    public void playNote(int noteNumber, int channel) {
        if (channel < 0 || channel >= 16
                || noteNumber < 0 || noteNumber >= 128) return;

        if (synthesizer.getChannels()[channel] != null) {
            synthesizer.getChannels()[channel].noteOn(noteNumber, volume.get());
            synthesizer.getChannels()[channel].noteOff(noteNumber);
        }
    }

    /**
     * Release the resources
     */
    public void close() {
        synthesizer.close();
    }

    public SimpleIntegerProperty volumeProperty() {
        return volume;
    }
}
