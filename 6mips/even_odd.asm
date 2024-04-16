# even_odd.asm
# @author Grant Yang
# @version 2024.04.16
#
# Assembly program to check if the inputted integer is even or odd.

.data
msg: .ascii "Even\0Odd\0"
prompt: .asciiz "Enter Number: "

.text 0x00400000
.globl main
main:
	li $v0 4
	la $a0 prompt
	syscall
	
	li $t2 5
	move $v0 $t2
	syscall
	
	andi $t0 $v0 1
	move $t1 $0
	movn $t1 $t2 $t0
	
	li $v0 4
	la $a0 msg($t1)
	syscall
	
	li $v0 10
	syscall
