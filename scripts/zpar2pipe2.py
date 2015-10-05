#!/usr/bin/env python3

import sys

def convert(tokens):
    assert tokens.next() == '('
    cat = tokens.next()
    const_type = tokens.next()
    assert const_type in ('l', 'r', 'c', 's')
    if const_type == 'l':
        print('(<

for line in sys.stdin:
    print('###')
    tokens = iter(line.split())
    convert(tokens)
    print()
