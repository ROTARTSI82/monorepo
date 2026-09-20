"""
genmusic.py
@author Grant Yang
@version 2024.04.16

Simple tool for generating the array data for music.asm from a
human-readable format. The format is a space-seperated list of
note groupings. The number before the semicolon represents the
duration as a multiple of a quarter note. The comma-seperated
note names that follow are then played in sequence. An underscore
represents a rest. By default, notes all last for 90% of their
specified duration and rest for 10% so that notes remain distinct.

Tempo is the speed of quarter notes in BPM.
instr is the MIDI instrument (5 = piano)
vol is the volume.
"""

tempo = 110
instr = 74
vol = 127
transpose = -12
onRatio = 1.0
toTheMax = """
1;G3,G3 0.5;G3 1.5;D4 1.5;_ 1.5;D4 0.5;D4 1;Bb3,A3,G3,G3 0.5;G3 1.5;D4 1.5;_
 1.5;D4 0.5;D4 1;Bb3,A3,G3,G3 0.5;G3 0.5;A3,Bb3,A3,Bb3 1;G3,G3 0.5;G3
 0.5;A3,Bb3,A3,Bb3 2;G3
"""

bolero = """
1.75;D5 0.25;C5,B4,A4,B4,C5 0.25;D5,C5 0.75;B4 0.25;C5,B4,A4,C5,B4,A4
 0.75;F4 0.125;F4,_,F4,_ 0.25;F4,_,A4,_,C5,A4,B4,G4
 0.5;F4 0.125;F4,_,F4,_ 0.25;F4,_,A4,_,B4,G4,A4,F4
 0.5;D4 0.25;D4,C4 1.5;D4 0.125;D4,_,D4,_ 0.25;D4,_,F4,_,A4,F4,G4,E4
 0.5;D4 0.25;D4,C4 1.5;D4 0.25;D4,C4 0.5;D4 0.25;E4,F4 2.25;G4
 0.25;F4,E4,D4 0.5;C4
"""

test = """
1;C1,Eb1,F1,Gb1,F1,Eb1,C1
"""

laeteturIsreal = """
1;D3,F3,A3 1.5;D3 0.5;D3 1;D3,D3 2;Bb3,A3 1;F3
 1;C4,C4,G3,D4,D4 2;A3,Bb3,F3 1;F3 2;Bb3,G3,A3,D3 1;D3 1.5;G3
 0.5;G3 1;F#3,G3,G3,F3 1.5;Bb3 0.5;Bb3 1;A3,Bb3,Bb3,Bb3,G3 2;Eb3
 2;_ 1;Bb3,G3,Eb3,Eb3,Bb3,Bb3,F3 2;G3,C3,D3,G3 1;D3,G3,G3,F3,Bb3,Bb3,F3
 2;G3,C3,D3,G2
"""

data = bolero.replace('\n', '')

quarterMillis = 1000 * 60 / tempo
scale = "C _ D _ E F _ G _ A _ B".lower().split(' ')


def parseNote(x):
    if x == '_':
        return -100
    # print(x)
    x = x.lower()
    base = scale.index(x[0]) + int(x[1] == '#') - int(x[1] == 'b')
    num = float(x[2 if x[1] in "#b" else 1:])
    # print(base, num)
    return base + num * 12 + 24


for grouping in data.split(' '):
    dat = grouping.split(";")
    dur = float(dat[0])
    notes = list(map(parseNote, dat[1].split(',')))

    sub = quarterMillis * dur * (1 - onRatio)

    for i in notes:
        if int(i) < 0:
            print("-100", int(quarterMillis * dur), "0 0", end=' ')
            continue
        print(int(i + transpose), int(quarterMillis * dur - sub), instr, vol, end=' ')
        if int(sub) > 0:
            print('-100', int(sub), '0 0', end=' ')

print('-1 0 0 0', end=' ')
