# numguess.asm
# @author Grant Yang
# @version 2024.04.16
#
# Assembly program to pick a random integer between 0 and 100
# and then play a game with the user to guess it, giving feedback of
# "too high" or "too low" until they get it.

.data
prompt: .asciiz "What is your guess? "
feedback: .ascii "Too low\n\0Correct!\n\0Too high\n\0"

.text 0x00400000
.globl main
main:
	li $a0 0
	li $a1 100
	li $v0 42
	syscall
	
	# li $v0 1
	# syscall
	
	move $t0 $a0
	
retry:
	li $v0 4 # puts
	la $a0 prompt
	syscall
	
	li $v0 5 # read int
	syscall
	
	li $t2 9 # feedback[9] -> "correct!"
	li $t5 19 # feedback[19] -> "too high"
	
	sgt  $t3 $v0 $t0
	movn $t2 $t5 $t3 # t2 = 19 if too high
	slt  $t3 $v0 $t0
	movn $t2 $0  $t3 # t2 = 0 if too low
	# t2 stays 9 if correct
	
	move $t1 $v0
	li $v0 4 # puts
	la $a0 feedback($t2)
	syscall

	bne $t1 $t0 retry
	
	li $v0 10
	syscall
