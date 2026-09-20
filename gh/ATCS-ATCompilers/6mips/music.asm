# music.asm
# @author Grant Yang
# @version 2024.04.16
# 
# Simple assembly program to play MIDI music using
# the syscalls. The data is stored as an array of 4-word objects,
# each of which represent a syscall. 
#
# If the first word is positive, the object represents a synchronous MIDI syscall.
# 	The 4 words are (pitch) (duration in milliseconds) (MIDI instrument number) and (volume)
# If the first word is negative value other than -1, the object represents a sleep() syscall.
# 	The second word is the duration in milliseconds to sleep, and the other 2 words are ignored.
# If the first word is equal to -1, the program terminates (this object marks the end of the stream).
# 	The following 3 words are ignored.
# 
# See genmusic.py for an tool to automatically generate this data array from a human-readable format.

.data
data: .word 74 954 74 127 72 136 74 127 71 136 74 127 69 136 74 127 71 136 74 127 72 136 74 127 74 136 74 127 72 136 74 127 71 409 74 127 72 136 74 127 71 136 74 127 69 136 74 127 72 136 74 127 71 136 74 127 69 136 74 127 65 409 74 127 65 68 74 127 -100 68 0 0 65 68 74 127 -100 68 0 0 65 136 74 127 -100 136 0 0 69 136 74 127 -100 136 0 0 72 136 74 127 69 136 74 127 71 136 74 127 67 136 74 127 65 272 74 127 65 68 74 127 -100 68 0 0 65 68 74 127 -100 68 0 0 65 136 74 127 -100 136 0 0 69 136 74 127 -100 136 0 0 71 136 74 127 67 136 74 127 69 136 74 127 65 136 74 127 62 272 74 127 62 136 74 127 60 136 74 127 62 818 74 127 62 68 74 127 -100 68 0 0 62 68 74 127 -100 68 0 0 62 136 74 127 -100 136 0 0 65 136 74 127 -100 136 0 0 69 136 74 127 65 136 74 127 67 136 74 127 64 136 74 127 62 272 74 127 62 136 74 127 60 136 74 127 62 818 74 127 62 136 74 127 60 136 74 127 62 272 74 127 64 136 74 127 65 136 74 127 67 1227 74 127 65 136 74 127 64 136 74 127 62 136 74 127 60 272 74 127 -1 0 0 0

.text 0x00400000
.globl main
main:
	la $t0 data
	li $t5 -10
	
next:
	lw $a0 ($t0)
	beq $a0 -1 end
	lw $a1 4($t0)
	lw $a2 8($t0)
	lw $a3 12($t0)
	
	li $v0 1
	syscall
	li $v0 33
	
	add $t0 $t0 16
	
	li $t2 32
	slt $t1 $a0 $t5
	movn $v0 $t2 $t1
	movn $a0 $a1 $t1
	syscall
	j next

end:
	li $v0 10
	syscall
