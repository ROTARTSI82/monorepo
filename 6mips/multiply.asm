# multiply.asm
# @author Grant Yang
# @version 2024.04.16
#
# Assembly program to read in two doubles from stdin
# and compute their product.

.data
msg: .ascii "Enter number: \0"

.text 0x00400000
.globl main
main:
	li $v0 4
	la $a0 msg
	syscall

	li $v0 7
	syscall
	
	mov.d $f2 $f0
	li $v0 4
	syscall
	
	li $v0 7
	syscall
	
	mul.d $f12 $f2 $f0
	li $v0 3
	syscall
	
	

	
