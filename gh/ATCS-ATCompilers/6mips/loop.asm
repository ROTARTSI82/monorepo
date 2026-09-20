# loop.asm
# @author Grant Yang
# @versin 2024.04.30
#
# A simple MIPS program to print numbers as it loops from a lower bound
# to a higher bound, going by a step size. All three values
# are read from stdin from user input.

.data
promptLo: .asciiz "Enter hi bound: "
promptHi: .asciiz "Enter lo bound: "
promptStep: .asciiz "Enter step size: "

.text 0x00400000
.globl main
main:
	la $a0 promptLo
	jal promptInteger
	move $t0 $v0
	
	la $a0 promptHi
	jal promptInteger
	move $t1 $v0
	
	la $a0 promptStep
	jal promptInteger
	move $t4 $v0
	
	# swap bounds if not ordered correctly
	slt $t2 $t0 $t1
	movz $t3 $t0 $t2
	movz $t0 $t1 $t2
	movz $t1 $t3 $t2	

next:
	li $v0 1
	move $a0 $t0
	syscall
	
	li $v0 11
	li $a0 10
	syscall
	
	add $t0 $t0 $t4
	ble $t0 $t1 next
	
	li $v0 10
	syscall

promptInteger:
	li $v0 4
	syscall
	li $v0 5
	syscall
	jr $ra
	
