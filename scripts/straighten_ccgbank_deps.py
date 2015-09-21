#!/usr/bin/env python3

import sys

def is_empty(line):
    return not line.rstrip()

def is_comment(line):
    return line.startswith('#')

for line in sys.stdin:
    if not (is_empty(line) or is_comment(line)):
        before, cat, after = line.split(' ', 2)
        cat = cat.replace('\\', '/')
        line = ' '.join((before, cat, after))
    sys.stdout.write(line)
