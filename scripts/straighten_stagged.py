#!/usr/bin/env python3

import sys

def is_empty(line):
    return not line.rstrip()

def is_comment(line):
    return line.startswith('#')

def modify_token(token):
    word, pos, cat = token.rsplit('|', 2)
    cat = cat.replace('\\', '/')
    return '|'.join((word, pos, cat))

for line in sys.stdin:
    if not (is_empty(line) or is_comment(line)):
        line = ' '.join(modify_token(token) for token in line.split())
    print(line.rstrip())
