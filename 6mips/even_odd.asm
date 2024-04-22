# even_odd.asm
# @author Grant Yang
# @version 2024.04.16
#
# Assembly program to check if the inputted integer is even or odd.

.data
msg: .ascii "Even\n\0Odd\n\0"
prompt: .asciiz "Enter Number: "
done: .asciiz "Done!\n"

.text 0x00400000
.globl main
main:
	li $v0 4
	la $a0 prompt
	syscall
	
	li $v0 5
	syscall
	beqz $v0 exit
	
	andi $t0 $v0 1
	move $t1 $0
	
	li $t2 6
	movn $t1 $t2 $t0
	
	li $v0 4
	la $a0 msg($t1)
	syscall
	
	j main
	
exit:
	li $v0 4
	la $a0 done
	syscall
	
	li $v0 10
	syscall
