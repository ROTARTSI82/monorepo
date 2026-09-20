.data
# 10num.asm
# @author Grant Yang
# @version 2024.04.16
#
# Assembly program to compute the sum, average, maximum, and minimum
# of a sequence of 10 doubles read from stdin at runtime. 
# The values are also stored into an array.

prompt: .asciiz "Enter number (0x of 10): "
sum: .asciiz "Sum: "
avg: .asciiz "Average: "
min: .asciiz "Minimum: "
max: .asciiz "Maximum: "
numvals: .double 10
arr: .double 0 -100 100 0 0 0 0 0 0 0 # 10 values

.text 0x00400000
.globl main
main:
	# t0 contains loop index
	
	# f16 contains total sum
	# f18 contains max
	# f20 contains min
	
	li $t0 0 # zero
	l.d $f16 arr
	l.d $f18 arr+8
	l.d $f20 arr+16
	
	li $t3 48 # ord('0') 
	
next:
	add $t2 $t0 49  # 49 = ord('1')
	seq $t4 $t0 9 # loop index 9 corresponds to 10th loop
	movn $t2 $t3 $t4 # t3 is ord('1') + t0 if t0 < 9, otherwise it is ord('0')
	add $t3 $t3 $t4 # t3 increments from ord('0') to ord('1') at t0=9
	
	li $t1 15
	sb $t2 prompt($t1)
	li $t1 14
	sb $t3 prompt($t1)
	
	li $v0 4 # puts
	la $a0 prompt
	syscall
	
	li $v0 7 # read double -> $f0
	syscall
	
	sll $t1 $t0 3 # << 3 equivalent to * sizeof(double)
	s.d $f0 arr($t1)
	
	c.lt.d $f0 $f20
	movt.d $f20 $f0
	
	c.lt.d $f0 $f18
	movf.d $f18 $f0
	
	add.d $f16 $f16 $f0
	add $t0 $t0 1
	
	sub $t1 $t0 10
	bltz $t1 next
	
	
	mov.d $f12 $f16
	la $a0 sum
	jal reportValue
	
	l.d $f14 numvals
	div.d $f12 $f12 $f14
	la $a0 avg
	jal reportValue
	
	mov.d $f12 $f20
	la $a0 min
	jal reportValue
	
	mov.d $f12 $f18
	la $a0 max
	jal reportValue
	
	li $v0 10
	syscall
	
reportValue:
	li $v0 4 # print string
	syscall
	
	li $v0 3 # print double
	syscall

	# print newline
	li $v0 11
	li $a0 10
	syscall
	jr $ra

	
