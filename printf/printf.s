.equ SYS_WRITE, 1
.equ STDOUT, 1

.data
helloWorld: .asciz "The quick brown fox quickly jumps over the lazy dog!"
overflow_string: .asciz "-9223372036854775808"

.text
.global main
main:
	#prologue
	pushq %rbp
	movq %rsp, %rbp

	movq $helloWorld, %rdi
	call my_printf

	#epilogue
	movq %rbp, %rsp
	popq %rbp

	movq $60, %rax	
	movq $0, %rdi
	syscall

my_printf:
	# prologue
	pushq %rbp
	movq %rsp, %rbp

	# Pushing arguments to the stack
	pushq $-24		# Push argument index	
	pushq $-8		# Push argument offset
	pushq %rsi		# Push argument 1
	pushq %rdx		# Push argument 2
	pushq %rcx		# Push argument 3
	pushq %r8		# Push argument 4
	pushq %r9		# Push argument 5
	
	movq %rdi, %r8		# Store string address in R8
	movq $-8, %r9		# Store stack offset of next argument in R9
loop:
	cmpq $-56, -8(%rbp)
	jl switch_to_stack_arguments

	cmpb $0, (%r8)		# If char equals NULL
	je end			# then, end

	cmpb $37, (%r8)		# If char does not equal '%'
	jne print_char		# then, print_char

	# Handle format specifiers
	cmpb $0, 1(%r8)		# If next char equals NULL
	je end			# then, print current char

	cmpb $37, 1(%r8)	# If next char equals '%'
	je procent_format	# then, procent_format

	cmpb $117, 1(%r8)	# If next char equals 'u'
	je unsigned_format	# then, unsigned_format	

	cmpb $100, 1(%r8)	# If next char equals 'd'
	je signed_format	# then, signed_format

	cmpb $115, 1(%r8)	# If next char equals 's'
	je string_format	# then, string_format
	
	jmp print_char		# Print unknown format specifier

continue:	
	incq %r8		# Increment loop counter
	jmp loop	

string_format:
	movq -8(%rbp), %r11	# Get argument index
	movq (%rbp, %r11), %rax	# Store address of string in RAX
	
	movq -16(%rbp), %r11	# Store argument offset
	addq %r11, -8(%rbp)	# Update argument counter

string_loop:
	cmpb $0, (%rax)		# Check if character equals NULL
	je string_end		# then, string_end
	
	pushq %rax		# Store RAX 

	# print current character
	movq %rax, %rsi
	movq $SYS_WRITE, %rax
	movq $STDOUT, %rdi
	movq $1, %rdx
	syscall

	popq %rax		# Restore rax
	
	incq %rax		# Increment rax
	jmp string_loop

string_end:
	incq %r8
	jmp continue

unsigned_format:	
	movq -8(%rbp), %r11	# Move argument index in R11	
	movq (%rbp, %r11), %rax # Move argument into RAX

	cmpq $0, %rax
	je zero_edge_case

from_signed:	
	movq $10, %r10

	movq -16(%rbp), %r11
	addq %r11, -8(%rbp)	# Update argument index
	
	movq $0, %r11		# Character counter
	xorq %rdx, %rdx		# Clear rdx, where the remainder will be stored


	unsigned_loop:
		cmpq $0, %rax
		je unsigned_end

		divq %r10		# Divide rax by 10, store remainder in rdx

		addq $48, %rdx		# Increment rdx by '0'
		pushq %rdx		# Push character

		incq %r11		# Increment character counter
		xorq %rdx, %rdx		# Clear rdx
		jmp unsigned_loop

unsigned_end:
	digit_loop:
		cmpq $0, %r11		# Check if character counter <= 0
		jle digit_end		# then, digit_end

		pushq %r11		# Save r11

		# Print pushed character
		movq $SYS_WRITE, %rax
		movq $STDOUT, %rdi
		leaq 8(%rsp), %rsi
		movq $1, %rdx
		syscall

		popq %r11		# Restore r11

		addq $8, %rsp		# Remove character from stack
		decq %r11		# Decrement character count

		jmp digit_loop

	digit_end:	
		incq %r8
		jmp continue

zero_edge_case:
	pushq $48

	movq $SYS_WRITE, %rax
	movq $STDOUT, %rdi
	movq %rsp, %rsi
	movq $1, %rdx
	syscall

	addq $8, %rsp
	jmp digit_end
	

signed_format:
	movq -8(%rbp), %r11	# Get argument index
	movq (%rbp, %r11), %rax	# Get current argument

	movq $-9223372036854775808, %r11
	cmpq %r11, %rax
	je overflow_edge_case

	cmpq $0, %rax		# Check if argument is negative
	js handle_negative	# then, handle_negative
	
	jmp unsigned_format	# else, unsigned_format

handle_negative:
	negq %rax		# Negate RAX
	
	pushq %rax		# Store RAX
	pushq $45		# Store the character '-' in the stack
	
	# Print '-'
	movq $SYS_WRITE, %rax
 	movq $STDOUT, %rdi
 	movq %rsp, %rsi
 	movq $1, %rdx
	syscall

	addq $8, %rsp		# Remove character from the stack
	popq %rax		# Restore RAX

	jmp from_signed
	
overflow_edge_case:
	movq -16(%rbp), %r11
	addq %r11, -8(%rbp)

	movq $SYS_WRITE, %rax
	movq $STDOUT, %rdi
	movq $overflow_string, %rsi
	movq $20, %rdx
	syscall

	jmp digit_end

procent_format:
	# Print current character
	movq $SYS_WRITE, %rax
	movq $STDOUT, %rdi
	movq %r8, %rsi
	movq $1, %rdx
	syscall

	incq %r8		# Increment character
	jmp continue

print_char:
	movq $SYS_WRITE, %rax   # Use SYS_WRITE
	movq $STDOUT, %rdi      # First argument: To STDOUT
	movq %r8, %rsi          # Second argument: String address
	movq $1, %rdx           # Third argument: length is 1
	syscall

	jmp continue

switch_to_stack_arguments:
	movq $16, -8(%rbp)
	movq $8, -16(%rbp)
	jmp loop

end:
	# epilogue
	movq %rbp, %rsp
	popq %rbp

	ret
