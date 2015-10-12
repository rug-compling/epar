#!/usr/bin/env python3

import grammar
import sys

try:
    _, binary_file, unary_file = sys.argv
except ValueError:
    print('Usage: zpargrammar2epargrammar.py BINARY UNARY', file=sys.stderr)
    sys.exit(1)

grammar = grammar.load(binary_file, unary_file)

for daughter_cat, action in grammar.get_unary_rules():
    _, _, mother_cat = action.split()
    print('{}\t{}\tdummy'.format(daughter_cat, mother_cat))

for (left_cat, right_cat), action in grammar.get_binary_rules():
    _, _, head_position, cat = action.split() 
    print('{}\t{}\t{}\t{}\tdummy'.format(left_cat, right_cat, cat,
            head_position))
