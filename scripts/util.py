def blocks(input):
    """
    Reads blocks of lines separated by an empty line from input and returns a sequence of blocks.
    """
    current_block = ''
    for line in input:
        if line.rstrip():
            current_block += line
        else:
            yield current_block
            current_block = ''
    if current_block:
        yield current_block
