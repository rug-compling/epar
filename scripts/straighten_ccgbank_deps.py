#!/usr/bin/env python3

import sys

def is_empty(line):
    return not line.rstrip()

def is_comment(line):
    return line.startswith('#')

def is_stagged_line(line):
    return line.startswith('<c> ')

for line in sys.stdin:
    if not (is_empty(line) or is_comment(line) or is_stagged_line(line)):
        before, cat, after = line.split(' ', 2)
        cat = cat.replace('\\', '/')
        line = ' '.join((before, cat, after))
    sys.stdout.write(line)
