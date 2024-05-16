# subroutines.asm
# Subroutines lab, implementing several
# functions in MIPS assembly including max,
# fibonacci, factorial, linked lists, etc.
#
# @author Grant Yang
# @version 2024.05.16

.macro print_int %reg
	move $a0 %reg
	li $v0 1
	syscall
	
	li $v0 11
	li $a0 10
	syscall
.end_macro

.text
main:
	li $a0 -10
	li $a1 10
	li $a2 12
	jal max3
	print_int $v0
	
	li $a0 213
	li $a1 123
	li $a2 -5
	jal max3
	print_int $v0
	
	li $a0 3
	li $a1 -2
	li $a2 10
	jal max3
	print_int $v0
	
	li $a0 5
	jal fact
	print_int $v0

	li $a0 6
	jal fib
	print_int $v0
	li $a0 7
	jal fib
	print_int $v0
	li $a0 8
	jal fib
	print_int $v0
	
	li $a0 12
	li $a1 0 # addr
	jal newlistnode
	
	li $a0 10
	move $a1 $v0
	jal newlistnode
	
	li $a0 56
	move $a1 $v0
	jal newlistnode
	
	move $a0 $v0
	jal sumlist
	print_int $v0

	li $v0 10
	syscall

# Finds the maximum of 2 integers
# $a0 and $a1 contain integers as arguments
# $v0 will contain return value
max2:
	slt $t0 $a0 $a1
	move $v0 $a0
	movn $v0 $a1 $t0
	jr $ra

# Finds the maximum of 3 integers
# $a0 $a1 and $a2 contain integers as arguments
# $v0 will contain return value
max3:
	addi $sp $sp -4
	sw $ra ($sp)
	jal max2
	move $v0 $a0
	move $a1 $a2
	jal max2
	lw $ra ($sp)
	addi $sp $sp 4
	jr $ra

# recursively calculates factorial
# $a0 contains input value
# $v0 will contain return value
# Postcondition: $a0 will have remained unchanged
fact:
	beqz $a0 fact_ret1
	addi $sp $sp -4
	sw $ra ($sp)
	addi $a0 $a0 -1
	jal fact
	addi $a0 $a0 1
	mul $v0 $a0 $v0
	lw $ra ($sp)
	addi $sp $sp 4
	jr $ra
fact_ret1:
	li $v0 1
	jr $ra

# recursively computes the nth fibonacci number in O(n^2)
# $a0 contains n, which fibonacci number to compute
# $v0 will contain the nth fibonacci number
# Postcondition: $a0 will have remained unchanged
fib:
	ble $a0 1 fib_retn
	addi $sp $sp -4
	sw $ra ($sp)

	addi $a0 $a0 -1
	jal fib
	
	addi $sp $sp -4
	sw $v0 ($sp)
	
	addi $a0 $a0 -1
	jal fib

	lw $t0 ($sp)
	add $v0 $v0 $t0
	lw $ra 4($sp)
	addi $sp $sp 8
	addi $a0 $a0 2
	
	jr $ra
fib_retn:
	move $v0 $a0
	jr $ra
	
# creates a linked list by prepending
# $a0 contains the value to prepend
# $a1 contains the address of the list to prepend to
# $v0 will contain the returned address of the new list
newlistnode:
	move $t0 $a0
	li $v0 9
	li $a0 8
	syscall
	
	sw $t0 ($v0)
	sw $a1 4($v0)
	jr $ra

# sums the elements in a linkedlist
# $a0 contains the address of the list
# $v0 will contain the returned calculated sum
sumlist:
	beqz $a0 sumlist_ret0
	addi $sp $sp -4
	sw $ra ($sp)
	
	lw $t0 ($a0) # get value & push
	addi $sp $sp -4
	sw $t0 ($sp)
	
	lw $a0 4($a0) # point to next
	
	jal sumlist
	lw $t0 ($sp) # pop, recover val
	addi $sp $sp 4
	add $v0 $v0 $t0
	
	lw $ra ($sp)
	addi $sp $sp 4
	jr $ra
sumlist_ret0:
	move $v0 $0
	jr $ra
