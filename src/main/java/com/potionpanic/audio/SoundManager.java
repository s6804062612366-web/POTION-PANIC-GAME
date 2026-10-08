package com.potionpanic.audio;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * จัดการเสียง Sound Effects (SFX) และดนตรีประกอบ (BGM)
 * ออกแบบระบบเสียงสังเคราะห์เวทมนตร์ (Magical Synthesizer Audio Engine)
 * รองรับ:
 * - เสียงแก้วผลึกและระฆังเวทมนตร์ (Harmonic Crystal Chimes)
 * - เสียงคอมโบไต่ระดับความสูงของตัวโน้ตตาม Combo Streak
 * - ระบบเสียงชีพจรเต้นบีบคั้น (Pulsing Heartbeat Tension) เมื่อพลังชีวิตหอคอยต่ำกว่า 35%
 */
public class SoundManager {
    private static SoundManager instance;
    private final ExecutorService soundPool = Executors.newCachedThreadPool();
    private boolean soundEnabled = true;

    private volatile boolean bgmRunning = false;
    private Thread bgmThread;

    // ระบบเสียงเตือนเมื่อเลือดหอคอยใกล้หมด (Low HP Warning Loop)
    private volatile boolean lowHpAlertRunning = false;
    private Thread lowHpAlertThread;

    private SoundManager() {}

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBgm();
            updateLowHpAlert(false);
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void playWavFile(String path) {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            try {
                InputStream is = getClass().getResourceAsStream(path);
                if (is != null) {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    clip.start();
                }
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * เสียงหยิบวัตถุดิบ: เสียงประกายแก้วเวทมนตร์ (Crystal Chime Pop)
     */
    public void playGrab() {
        if (!soundEnabled) return;
        soundPool.execute(() -> playMagicChime(659.25, 90, 0.45)); // E5
    }

    /**
     * เสียงหม้อต้มยาเดือดปุดๆ (Mystical Bubbling Modulation)
     */
    public void playBoil() {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            // ฟองอากาศเดือดผุดไล่ความถี่ขึ้น
            playBubbleTone(220, 360, 50, 0.35);
            try { Thread.sleep(35); } catch (InterruptedException ignored) {}
            playBubbleTone(300, 480, 55, 0.38);
            try { Thread.sleep(40); } catch (InterruptedException ignored) {}
            playBubbleTone(380, 580, 60, 0.40);
            // ประกายแก้วผสมสำเร็จ
            playMagicChime(880.0, 110, 0.35);
        });
    }

    /**
     * เสียงปาขวดยาลอยไปในอากาศ (Wind Whoosh)
     */
    public void playThrow() {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            for (int f = 680; f >= 280; f -= 60) {
                playSynthTone(f, 22, 0.28, "triangle");
            }
        });
    }

    /**
     * เสียงขวดยาแตกกระทบเป้าหมาย (Elemental Shatter)
     */
    public void playHit() {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            playSynthTone(160, 75, 0.55, "noise");
            playSynthTone(220, 85, 0.45, "triangle");
        });
    }

    /**
     * เสียงหม้อต้มระเบิด (Cauldron Overheat Explosion)
     */
    public void playExplosion() {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            playSynthTone(75, 260, 0.85, "noise");
            playSynthTone(60, 300, 0.75, "square");
        });
    }

    /**
     * เสียงโจมตีคริติคอลตรงจุดอ่อนธาตุ (Resonant Glass Strike)
     */
    public void playCritical() {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            playMagicChime(880.0, 120, 0.60); // A5
            playMagicChime(1318.5, 150, 0.45); // E6
        });
    }

    /**
     * เสียงคอมโบตามระดับ Combo Streak (Ascending Scale)
     */
    public void playCombo(int comboCount) {
        if (!soundEnabled) return;
        soundPool.execute(() -> {
            double note = switch (comboCount) {
                case 2 -> 523.25; // C5
                case 3 -> 659.25; // E5
                case 4 -> 783.99; // G5
                default -> 1046.50; // C6+
            };
            playMagicChime(note, 130, 0.55);
            if (comboCount >= 4) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                playMagicChime(note * 1.5, 120, 0.45); // คู่ 5 เสริมความอลังการ
            }
        });
    }

    /**
     * ควบคุมระบบเสียงชีพจรเต้นบีบคั้น (Pulsing Heartbeat) เมื่อพลังชีวิตหอคอยต่ำกว่า 35%
     */
    public synchronized void updateLowHpAlert(boolean active) {
        if (!soundEnabled) {
            lowHpAlertRunning = false;
            return;
        }

        if (active && !lowHpAlertRunning) {
            lowHpAlertRunning = true;
            lowHpAlertThread = new Thread(() -> {
                while (lowHpAlertRunning) {
                    if (soundEnabled) {
                        // จังหวะหัวใจ "ตึก-ตัก" สองจังหวะต่ำ
                        playHeartbeatTone(58.0, 75, 0.65);
                        try { Thread.sleep(120); } catch (InterruptedException ignored) {}
                        playHeartbeatTone(52.0, 65, 0.50);
                    }
                    try {
                        Thread.sleep(950); // เว้นช่วง 1 วินาที
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            lowHpAlertThread.setDaemon(true);
            lowHpAlertThread.start();
        } else if (!active && lowHpAlertRunning) {
            lowHpAlertRunning = false;
            if (lowHpAlertThread != null) {
                lowHpAlertThread.interrupt();
                lowHpAlertThread = null;
            }
        }
    }

    /**
     * สังเคราะห์เสียงชนะ (Victory Fanfare Chime)
     */
    public void playVictory() {
        if (!soundEnabled) return;
        updateLowHpAlert(false);
        soundPool.execute(() -> {
            double[] chords = {523.25, 659.25, 783.99, 1046.50, 1318.51}; // C Major Arpeggio
            for (double n : chords) {
                playMagicChime(n, 180, 0.55);
                try { Thread.sleep(95); } catch (InterruptedException ignored) {}
            }
        });
    }

    /**
     * สังเคราะห์เสียงพ่ายแพ้ (Defeat)
     */
    public void playDefeat() {
        if (!soundEnabled) return;
        updateLowHpAlert(false);
        soundPool.execute(() -> {
            double[] notes = {392.00, 369.99, 349.23, 311.13, 261.63}; // Descending Minor
            for (double n : notes) {
                playSynthTone(n, 220, 0.55, "triangle");
                try { Thread.sleep(160); } catch (InterruptedException ignored) {}
            }
        });
    }

    /**
     * เริ่มเพลงประกอบ BGM บรรยากาศเวทมนตร์ (Ambient Magic Harp)
     */
    public synchronized void startBgm() {
        if (!soundEnabled || bgmRunning) return;
        bgmRunning = true;
        bgmThread = new Thread(() -> {
            double[] melody = {261.63, 329.63, 392.00, 440.00, 392.00, 329.63, 293.66, 349.23};
            int idx = 0;
            while (bgmRunning) {
                if (soundEnabled) {
                    playMagicChime(melody[idx % melody.length], 300, 0.16);
                    idx++;
                }
                try {
                    Thread.sleep(620);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        bgmThread.setDaemon(true);
        bgmThread.start();
    }

    public synchronized void stopBgm() {
        bgmRunning = false;
        if (bgmThread != null) {
            bgmThread.interrupt();
            bgmThread = null;
        }
    }

    // --- ส่วนงานสังเคราะห์เสียงขั้นสูง (Acoustic Waveform Generators) ---

    /**
     * สังเคราะห์เสียงระฆังแก้วเวทมนตร์ (Inharmonic Crystal Bells)
     */
    private void playMagicChime(double freq, int ms, double volume) {
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double t = i / sampleRate;
                // ฮาร์โมนิกคู่ความถี่เสียงแก้ว (Fundamental + Inharmonic Overtone 2.76x)
                double wave1 = Math.sin(2.0 * Math.PI * freq * t);
                double wave2 = Math.sin(2.0 * Math.PI * (freq * 2.756) * t) * 0.35;
                double wave3 = Math.sin(2.0 * Math.PI * (freq * 5.404) * t) * 0.15;
                double combined = (wave1 + wave2 + wave3) / 1.5;

                // หางเสียงลดลงแบบเอกซ์โพเนนเชียล (Exponential Decay)
                double decay = Math.exp(-4.2 * ((double) i / numSamples));
                combined *= decay * volume;

                buffer[i] = (byte) (Math.max(-127, Math.min(127, combined * 127.0)));
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    /**
     * สังเคราะห์เสียงฟองอากาศเดือดผุด (Bubble Pitch Slide)
     */
    private void playBubbleTone(double startFreq, double endFreq, int ms, double volume) {
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double progress = (double) i / numSamples;
                double currentFreq = startFreq + (endFreq - startFreq) * progress;
                double t = i / sampleRate;
                double wave = Math.sin(2.0 * Math.PI * currentFreq * t);
                double envelope = Math.sin(progress * Math.PI); // Smooth arc
                buffer[i] = (byte) (wave * envelope * volume * 127.0);
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    /**
     * สังเคราะห์เสียงชีพจรเต้นทุ้มต่ำ (Low-Frequency Heartbeat Thump)
     */
    private void playHeartbeatTone(double freq, int ms, double volume) {
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double t = i / sampleRate;
                double progress = (double) i / numSamples;
                double wave = Math.sin(2.0 * Math.PI * freq * t);
                double envelope = Math.sin(Math.pow(progress, 0.4) * Math.PI);
                buffer[i] = (byte) (wave * envelope * volume * 127.0);
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    private void playSynthTone(double freq, int ms, double volume, String waveType) {
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double time = i / sampleRate;
                double angle = 2.0 * Math.PI * freq * time;
                double sample;

                switch (waveType) {
                    case "triangle" -> sample = (2.0 / Math.PI) * Math.asin(Math.sin(angle));
                    case "square" -> sample = Math.sin(angle) >= 0 ? 0.6 : -0.6;
                    case "noise" -> sample = (Math.random() * 2.0 - 1.0) * (1.0 - (double) i / numSamples);
                    default -> sample = Math.sin(angle);
                }

                double envelope = 1.0 - ((double) i / numSamples);
                sample *= envelope * volume;
                buffer[i] = (byte) (sample * 127.0);
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    private void writeAudioBuffer(byte[] buffer, float sampleRate) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, buffer.length);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception ignored) {}
    }
}
