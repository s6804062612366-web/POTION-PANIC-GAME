package com.potionpanic.audio;

import javax.sound.sampled.*;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * จัดการเสียง Sound Effects (SFX) และดนตรีประกอบ (BGM)
 * อัปเกรดระบบดนตรีประกอบ Multi-Track Fantasy Soundtrack:
 * 1. Menu BGM: "Mystic Alchemist Chamber" - เพลงบรรยากาศห้องปรุงยาเวทมนตร์ยามค่ำคืน นุ่มนวล อบอุ่น ลึกลับ
 * 2. Battle BGM: "Potion Panic / Tavern Groove" - เพลงแนวสนุกสนานกระฉับกระเฉง จังหวะตื่นเต้นเร้าใจ
 * 3. ปุ่มเปิด-ปิดเสียง (Mute Toggle) กด [M] หรือคลิกบนหน้าจอ ตัดเสียงทันทีและเปิดกลับมาได้สมบูรณ์ 100%
 */
public class SoundManager {
    private static SoundManager instance;
    private final ExecutorService soundPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "PotionPanic-SoundPool");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean soundEnabled = true;

    public enum BgmType {
        NONE, MENU, BATTLE
    }

    private volatile BgmType desiredBgmType = BgmType.NONE;
    private volatile BgmType currentPlayingBgm = BgmType.NONE;
    private volatile SourceDataLine activeBgmLine = null;
    private volatile int bgmSessionId = 0;
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

    private boolean isAudioHardwareAvailable() {
        return !GraphicsEnvironment.isHeadless();
    }

    public synchronized void setSoundEnabled(boolean enabled) {
        if (this.soundEnabled == enabled) return;
        this.soundEnabled = enabled;

        if (!enabled) {
            // หยุดเสียงทันที แต่ยังคงจำ desiredBgmType ไว้
            haltBgmThread();
            currentPlayingBgm = BgmType.NONE;
            updateLowHpAlert(false);
        } else {
            // เมื่อเปิดเสียงกลับมา เล่น BGM ของฉากปัจจุบันต่อทันที
            if (desiredBgmType != BgmType.NONE) {
                playBgm(desiredBgmType);
            }
        }
    }

    public void toggleSound() {
        setSoundEnabled(!soundEnabled);
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public synchronized void playMenuBgm() {
        playBgm(BgmType.MENU);
    }

    public synchronized void startBgm() {
        playMenuBgm();
    }

    public synchronized void playBattleBgm() {
        playBgm(BgmType.BATTLE);
    }

    public synchronized void playBgm(BgmType type) {
        desiredBgmType = type;

        if (!soundEnabled || !isAudioHardwareAvailable() || type == BgmType.NONE) {
            haltBgmThread();
            currentPlayingBgm = BgmType.NONE;
            return;
        }

        if (currentPlayingBgm == type && bgmThread != null && bgmThread.isAlive()) {
            return; // เพลงเดิมกำลังเล่นอยู่แล้ว
        }

        haltBgmThread();
        currentPlayingBgm = type;
        final int sessionId = ++bgmSessionId;

        bgmThread = new Thread(() -> {
            SourceDataLine line = null;
            try {
                float sampleRate = 22050f;
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) return;

                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, (int) (sampleRate * 0.4 * 2)); // 0.4s buffer
                line.start();
                activeBgmLine = line;

                double phaseMelody = 0;
                double phaseChord1 = 0;
                double phaseChord2 = 0;
                double phaseBass = 0;

                int step = 0;
                int totalSteps = (type == BgmType.MENU) ? 32 : 32;

                while (soundEnabled && currentPlayingBgm == type && bgmSessionId == sessionId) {
                    double bpm = (type == BgmType.MENU) ? 88.0 : 138.0;
                    double stepDuration = (60.0 / bpm) / 2.0; // Eighth notes
                    int stepSamples = (int) (sampleRate * stepDuration);

                    double bassFreq;
                    double chordFreq1;
                    double chordFreq2;
                    double melodyFreq;

                    if (type == BgmType.MENU) {
                        // Progression: Am -> F -> C -> G (Warm Celtic Alchemy Chamber)
                        int chordGroup = (step / 8) % 4;
                        bassFreq = switch (chordGroup) {
                            case 0 -> 110.0; // A2
                            case 1 -> 87.31; // F2
                            case 2 -> 130.81; // C3
                            default -> 98.0; // G2
                        };
                        chordFreq1 = switch (chordGroup) {
                            case 0 -> 220.0; // A3
                            case 1 -> 174.61; // F3
                            case 2 -> 261.63; // C4
                            default -> 196.0; // G3
                        };
                        chordFreq2 = switch (chordGroup) {
                            case 0 -> 261.63; // C4
                            case 1 -> 220.0; // A3
                            case 2 -> 329.63; // E4
                            default -> 246.94; // B3
                        };

                        double[] menuMelody = {
                                440.0, 0, 523.25, 659.25, 587.33, 523.25, 440.0, 0,
                                349.23, 440.0, 523.25, 0, 440.0, 392.0, 349.23, 0,
                                523.25, 659.25, 783.99, 0, 659.25, 523.25, 587.33, 0,
                                392.0, 493.88, 587.33, 659.25, 587.33, 493.88, 440.0, 0
                        };
                        melodyFreq = menuMelody[step % menuMelody.length];
                    } else {
                        // Progression: Dm -> Bb -> F -> C (Upbeat Tavern / Alchemy Defense Groove)
                        int chordGroup = (step / 8) % 4;
                        bassFreq = switch (chordGroup) {
                            case 0 -> 146.83; // D3
                            case 1 -> 116.54; // Bb2
                            case 2 -> 87.31;  // F2
                            default -> 130.81; // C3
                        };
                        chordFreq1 = switch (chordGroup) {
                            case 0 -> 293.66; // D4
                            case 1 -> 233.08; // Bb3
                            case 2 -> 174.61; // F3
                            default -> 261.63; // C4
                        };
                        chordFreq2 = switch (chordGroup) {
                            case 0 -> 349.23; // F4
                            case 1 -> 293.66; // D4
                            case 2 -> 261.63; // C4
                            default -> 329.63; // E4
                        };

                        double[] battleMelody = {
                                587.33, 0, 587.33, 698.46, 587.33, 0, 523.25, 440.0,
                                466.16, 0, 587.33, 466.16, 523.25, 0, 440.0, 0,
                                698.46, 0, 698.46, 880.0, 783.99, 698.46, 587.33, 0,
                                523.25, 659.25, 783.99, 0, 659.25, 523.25, 587.33, 0
                        };
                        melodyFreq = battleMelody[step % battleMelody.length];
                    }

                    byte[] buffer = new byte[stepSamples * 2]; // 16-bit audio (2 bytes per sample)

                    for (int i = 0; i < stepSamples; i++) {
                        double envStep = Math.exp(-3.5 * ((double) i / stepSamples));

                        // 1. Bassline (warm sine + slight warmth)
                        phaseBass += (2.0 * Math.PI * bassFreq) / sampleRate;
                        double bassWave = (Math.sin(phaseBass) + 0.3 * Math.sin(phaseBass * 2)) * 0.28 * Math.exp(-2.2 * ((double) i / stepSamples));

                        // 2. Chord Harmony (Plucked acoustic feel)
                        phaseChord1 += (2.0 * Math.PI * chordFreq1) / sampleRate;
                        phaseChord2 += (2.0 * Math.PI * chordFreq2) / sampleRate;
                        double chordWave = (Math.sin(phaseChord1) + 0.8 * Math.sin(phaseChord2)) * 0.12 * envStep;

                        // 3. Melody (Soft lute / flute harmonic)
                        double melWave = 0;
                        if (melodyFreq > 10) {
                            phaseMelody += (2.0 * Math.PI * melodyFreq) / sampleRate;
                            double melDecay = (type == BgmType.MENU) ? Math.exp(-2.8 * ((double) i / stepSamples)) : Math.exp(-4.2 * ((double) i / stepSamples));
                            melWave = (Math.sin(phaseMelody) + 0.35 * Math.sin(phaseMelody * 2.0) + 0.15 * Math.sin(phaseMelody * 3.0)) * 0.22 * melDecay;
                        }

                        // 4. Subtle percussion tick on battle groove
                        double perc = 0;
                        if (type == BgmType.BATTLE && (step % 2 == 0) && i < 300) {
                            perc = (Math.random() * 2.0 - 1.0) * (1.0 - (double) i / 300.0) * 0.08;
                        }

                        double mixed = (bassWave + chordWave + melWave + perc) * 0.75;
                        mixed = Math.max(-1.0, Math.min(1.0, mixed));

                        short sampleVal = (short) (mixed * 32767);
                        buffer[i * 2] = (byte) (sampleVal & 0xff);
                        buffer[i * 2 + 1] = (byte) ((sampleVal >> 8) & 0xff);
                    }

                    line.write(buffer, 0, buffer.length);
                    step = (step + 1) % totalSteps;
                }
            } catch (Exception ignored) {
            } finally {
                if (line != null) {
                    try {
                        line.stop();
                        line.flush();
                        line.close();
                    } catch (Exception ignored) {}
                }
                if (activeBgmLine == line) {
                    activeBgmLine = null;
                }
            }
        }, "PotionPanic-BGM-Thread");

        bgmThread.setDaemon(true);
        bgmThread.start();
    }

    private synchronized void haltBgmThread() {
        bgmSessionId++;
        if (activeBgmLine != null) {
            try {
                activeBgmLine.stop();
                activeBgmLine.flush();
                activeBgmLine.close();
            } catch (Exception ignored) {}
            activeBgmLine = null;
        }
        if (bgmThread != null) {
            bgmThread.interrupt();
            bgmThread = null;
        }
    }

    public synchronized void stopBgm() {
        desiredBgmType = BgmType.NONE;
        currentPlayingBgm = BgmType.NONE;
        haltBgmThread();
    }

    public synchronized void shutdown() {
        soundEnabled = false;
        desiredBgmType = BgmType.NONE;
        currentPlayingBgm = BgmType.NONE;
        haltBgmThread();
        updateLowHpAlert(false);
        soundPool.shutdownNow();
    }

    // --- SFX (Sound Effects) ---

    public void playGrab() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> playMagicChime(659.25, 90, 0.45)); // E5
    }

    public void playBoil() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> {
            playBubbleTone(220, 360, 50, 0.35);
            try { Thread.sleep(35); } catch (InterruptedException ignored) {}
            playBubbleTone(300, 480, 55, 0.38);
            try { Thread.sleep(40); } catch (InterruptedException ignored) {}
            playBubbleTone(380, 580, 60, 0.40);
            playMagicChime(880.0, 110, 0.35);
        });
    }

    public void playThrow() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> {
            for (int f = 680; f >= 280; f -= 60) {
                playSynthTone(f, 22, 0.28, "triangle");
            }
        });
    }

    public void playHit() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> {
            playSynthTone(160, 75, 0.55, "noise");
            playSynthTone(220, 85, 0.45, "triangle");
        });
    }

    public void playExplosion() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> {
            playSynthTone(75, 260, 0.85, "noise");
            playSynthTone(60, 300, 0.75, "square");
        });
    }

    public void playCritical() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        soundPool.execute(() -> {
            playMagicChime(880.0, 120, 0.60); // A5
            playMagicChime(1318.5, 150, 0.45); // E6
        });
    }

    public void playCombo(int comboCount) {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
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
                playMagicChime(note * 1.5, 120, 0.45);
            }
        });
    }

    public synchronized void updateLowHpAlert(boolean active) {
        if (!soundEnabled || !isAudioHardwareAvailable()) {
            lowHpAlertRunning = false;
            return;
        }

        if (active && !lowHpAlertRunning) {
            lowHpAlertRunning = true;
            lowHpAlertThread = new Thread(() -> {
                while (lowHpAlertRunning) {
                    if (soundEnabled) {
                        playHeartbeatTone(58.0, 75, 0.65);
                        try { Thread.sleep(120); } catch (InterruptedException ignored) {}
                        playHeartbeatTone(52.0, 65, 0.50);
                    }
                    try {
                        Thread.sleep(950);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "PotionPanic-LowHPAlert");
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

    public void playVictory() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        updateLowHpAlert(false);
        soundPool.execute(() -> {
            double[] chords = {523.25, 659.25, 783.99, 1046.50, 1318.51};
            for (double n : chords) {
                playMagicChime(n, 180, 0.55);
                try { Thread.sleep(95); } catch (InterruptedException ignored) {}
            }
        });
    }

    public void playDefeat() {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        updateLowHpAlert(false);
        soundPool.execute(() -> {
            double[] notes = {392.00, 369.99, 349.23, 311.13, 261.63};
            for (double n : notes) {
                playSynthTone(n, 220, 0.55, "triangle");
                try { Thread.sleep(160); } catch (InterruptedException ignored) {}
            }
        });
    }

    private void playMagicChime(double freq, int ms, double volume) {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double t = i / sampleRate;
                double wave1 = Math.sin(2.0 * Math.PI * freq * t);
                double wave2 = Math.sin(2.0 * Math.PI * (freq * 2.756) * t) * 0.35;
                double wave3 = Math.sin(2.0 * Math.PI * (freq * 5.404) * t) * 0.15;
                double combined = (wave1 + wave2 + wave3) / 1.5;

                double decay = Math.exp(-4.2 * ((double) i / numSamples));
                combined *= decay * volume;

                buffer[i] = (byte) (Math.max(-127, Math.min(127, combined * 127.0)));
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    private void playBubbleTone(double startFreq, double endFreq, int ms, double volume) {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        try {
            float sampleRate = 22050f;
            int numSamples = (int) (ms * (sampleRate / 1000f));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double progress = (double) i / numSamples;
                double currentFreq = startFreq + (endFreq - startFreq) * progress;
                double t = i / sampleRate;
                double wave = Math.sin(2.0 * Math.PI * currentFreq * t);
                double envelope = Math.sin(progress * Math.PI);
                buffer[i] = (byte) (wave * envelope * volume * 127.0);
            }
            writeAudioBuffer(buffer, sampleRate);
        } catch (Exception ignored) {}
    }

    private void playHeartbeatTone(double freq, int ms, double volume) {
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
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
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
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
        if (!soundEnabled || !isAudioHardwareAvailable()) return;
        try {
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) return;
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, buffer.length);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception ignored) {}
    }
}
