#!/usr/bin/env python3

import grammar
import sys

def straighten(cat):
    return cat.replace('\\', '/')

def flip(action):
    if 'LEFT' in action:
        return action.replace('LEFT', 'RIGHT')
    else:
        return action.replace('RIGHT', 'LEFT')

try:
    _, binary_in, unary_in, binary_out, unary_out = sys.argv
except ValueError:
    print('Usage: grammar_straighten.py BINARY_IN UNARY_IN BINARY_OUT '
            'UNARY_OUT', file=sys.stderr)
    sys.exit(1)

grammar_in = grammar.load(binary_in, unary_in)
grammar_out = grammar.Grammar()

for (left_cat, right_cat), action in grammar_in.get_binary_rules():
    left_cat = straighten(left_cat)
    right_cat = straighten(right_cat)
    action = straighten(action)
    grammar_out.add_binary_rule(left_cat, right_cat, action)
    grammar_out.add_binary_rule(right_cat, left_cat, flip(action))

for daughter_cat, action in grammar_in.get_unary_rules():
    daughter_cat = straighten(daughter_cat)
    action = straighten(action)
    grammar_out.add_unary_rule(daughter_cat, action)

grammar_out.save(binary_out, unary_out)
