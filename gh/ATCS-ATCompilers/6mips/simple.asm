# simple.asm
# @author Grant Yang
# @version 2024.04.16
#
# Simple MIPS assembly hello world

.data

str: .ascii "Hello World!\n\0"

.text 0x00400000
.globl main

main:
	li $t0 2
	li $t1 3

	addu $t2 $t0 $t1
	
	li $v0, 4
	la $a0, str
	syscall

	li $v0 10
	syscall
