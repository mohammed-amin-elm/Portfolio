.text
.global brainfuck

invalid_argument_string: .asciz "Invalid argument: missing file\n"
invalid_file_string: .asciz "Invalid file: cannot open file\n"

READ: .asciz "r"

brainfuck:
	# prologue
	pushq %rbp
	movq %rsp, %rbp

	# Store callee-saved registers
	pushq %r14		# file pointer
	pushq %rbx		# cell-pointer
	pushq %r12		# program-counter
	pushq %r13		# bracket-counter

	# Initialize registers
	movq %rdi, %r14		# Set file-pointer
	movq $cells, %rbx	# Set cell-pointer to index zero
	movq $0, %r12		# Set program-counter to zero	
loop:
	# Get character
	movzb (%r14, %r12), %rax	
	
	cmpq $0, %rax	# Check if the character equals EOF
	je end_loop  	# then, end_loop

	cmpq $62, %rax			# Check if RAX = '>'
	je handle_data_pointer_right	# then, handle_data_pointer_right
	
	cmpq $60, %rax			# Check if RAX = '<'
	je handle_data_pointer_left	# then, handle_data_pointer_left

	cmpq $43, %rax			# Check if RAX = '+'
	je handle_plus			# then, handle_plus

	cmpq $45, %rax			# Check if RAX = '-'
	je handle_minus			# then, handle_minus

	cmpq $46, %rax			# Check if RAX = '.'
	je handle_dot			# then, handle_dot 

	cmpq $44, %rax			# Check if RAX = ','
	je handle_comma			# then, handle_comma

	cmpq $91, %rax			# Check if RAX = '['
	je handle_opening_bracket	# then, handle_opening_bracket

	cmpq $93, %rax			# Check if RAX = ']'
	je handle_closing_bracket	# then, handle_closing_bracket

continue:
	incq %r12			# Increment program counter
	jmp loop

end_loop:
	popq %r13
	popq %r12
	popq %rbx
	popq %r14
	
	# epilogue
	movq %rbp, %rsp
	popq %rbp

	ret

handle_data_pointer_right:
	incq %rbx			# Increment cell pointer
	jmp continue

handle_data_pointer_left:
	decq %rbx			# Decrement cell pointer
	jmp continue

handle_plus:
	incb (%rbx)			# Increment value on cell
	jmp continue

handle_minus:
	decb (%rbx)			# Decrement value on cell
	jmp continue

handle_dot:
	movzb (%rbx), %rdi
	call putchar
	jmp continue

handle_comma:
	call getchar
	movb %al, (%rbx)
	jmp continue
	
handle_opening_bracket:
	cmpb $0, (%rbx)		# Check if value at current cell equals 0
	je stop_loop		# then, jump to corrosponding ']'

	cmpq (%rsp), %r12
	je already_in_loop
	
	pushq %r12		# Mark the start of the loop in the stack

already_in_loop:
	jmp continue

stop_loop:
	incq %r12
	movq $0, %r13

char_loop:
	movzb (%r14, %r12), %rax

	cmpq $91, %rax
	je increment_bracket_counter

	cmpq $93, %rax		# Check if character equals ']'
	je check_bracket

continue_char_loop:
	incq %r12
	jmp char_loop

increment_bracket_counter:
	incq %r13
	jmp continue_char_loop	

check_bracket:
	cmpq $0, %r13
	je continue

	decq %r13
	jmp continue_char_loop
	 

handle_closing_bracket:
	cmpb $0, (%rbx)		# If current value of cell equals 0
	je end_loop_bracket	# then, end_loop_bracket

	movq (%rsp), %r12
	
	jmp loop	

end_loop_bracket:
	popq %r12
	
	jmp loop

invalid_arguments:
	movq $invalid_argument_string, %rdi
	movq $0, %rax
	call printf

	jmp end

invalid_file:
	movq $invalid_file_string, %rdi
	movq $0, %rax
	call printf

	jmp end

.data
cells: .skip 30000, 0

