
# standard library functions, to be inserted
# at the end of compiled programs as needed.
# Functions are marked by `# func` and `# endfunc`
# other stuff is ignored

.stdlib_main_test:
	li $t0 65546
	li $t1 78897
	mult $t0 $t1
	jal lltoa
	
	move $a0 $v0
	li $v0 4
	syscall
	
	li $v0 10
	syscall

# long long to ascii: lltoa
# convert a 64-bit integer stored in $hi and $lo into an ascii string
# $v0 will contain a pointer to the newly malloc'd string
# func
lltoa:
	mfhi $t0
	mflo $t1
	li $t2 10 # base 10
	
	li $v0 9 # malloc
	li $a0 21 # 64-bit integers are at most 20 decimal digits long
	syscall
	
	addi $v0 $v0 20
	sb $0 ($v0)
	
lltoa_loop:
	div $t0 $t2
	mfhi $t3 # t3 = hi mod 10
	mflo $t4 # t4 = floor(hi / 10)  <- to be treated as new hi
	
	div $t1 $t2
	mflo $t5 # t5 = floor(lo / 10) <- used as accumulator & treated as new lo
	mfhi $t0 # t0 = lo mod 10
	
	# t5 += floor(2^32 / 10) * (hi mod 10) => t5
	li $t6 0x19999999 # floor(2^32 / 10)
	mul $t7 $t6 $t3
	add $t5 $t5 $t7
	
	# t5 += floor(6 * (hi mod 10) / 10)
	li $t6 6 # 2^32 mod 10
	mul $t7 $t6 $t3 # t7 = 6 * (hi mod 10)
	div $t7 $t2
	mflo $t1 # t1 = t7 / 10
	add $t5 $t5 $t1

	# t5 += floor( [ lo mod 10 + (6*hi) mod 10 ] / 10)
	div $t7 $t2
	mfhi $t7 # t7 = (6 * hi) mod 10
	add $t3 $t7 $t0 # t3 = t7 + lo mod 10 = ((6*hi) mod 10 + lo mod 10)
	div $t3 $t2
	mflo $t7 # t7 = t3 / 10 = ((6*hi) mod 10 + lo mod 10) / 10
	mfhi $t3 # t3 %= 10
	add $t5 $t5 $t7
	
	move $t1 $t5 # t5 becomes new lo
	move $t0 $t4 # t4 becomes new hi
	
	addi $t3 $t3 48 # ord('0')
	addi $v0 $v0 -1
	sb $t3 ($v0)
	
	or $t5 $t5 $t4
	bnez $t5 lltoa_loop
	jr $ra
# endfunc

# $a0 contains string. It is preserved during the function call.
# after calling: $v0 contains length
# func
strlen:
	move $v0 $0
	move $t1 $a0
strlen_loop:
	lb $t0 ($t1)
	beqz $t0 strlen_end
	addi $t1 $t1 1
	addi $v0 $v0 1
	j strlen_loop	
strlen_end:
	jr $ra
# endfunc

# $t0 contains number of bytes to copy
# $t1 contains source memory location
# $t2 contains dest memory location
# The source and destination memory locations must not overlap.
# func
memcpy:
	beqz $t0 memcpy_end
	lb $t3 ($t1)
	sb $t3 ($t2)
	addi $t2 $t2 1
	addi $t1 $t1 1
	addi $t0 $t0 -1
	j memcpy
memcpy_end:
	jr $ra
# endfunc

