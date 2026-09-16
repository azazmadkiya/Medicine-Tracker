import wave
import struct
import math
import os

sample_rate = 44100

def generate_tone(freq, duration, decay=3.0):
    num_samples = int(sample_rate * duration)
    return [int(30000.0 * math.sin(2.0 * math.pi * freq * i / sample_rate) * math.exp(-decay * i / num_samples)) for i in range(num_samples)]

# Create a pleasant arpeggio for a medical alert (C6, E6, G6)
s1 = generate_tone(1046.50, 0.15, decay=5.0)
s2 = generate_tone(1318.51, 0.15, decay=5.0)
s3 = generate_tone(1567.98, 0.70, decay=3.0)

samples = s1 + s2 + s3

os.makedirs('app/src/main/res/raw', exist_ok=True)
with wave.open('app/src/main/res/raw/medication_alert.wav', 'w') as f:
    f.setnchannels(1)
    f.setsampwidth(2)
    f.setframerate(sample_rate)
    for s in samples:
        f.writeframesraw(struct.pack('<h', s))
