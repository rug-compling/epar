#!/usr/bin/env python3

import grammar
import markup
import sys

def action_markup(action):
    print('Action to mark up: {}'.format(action), file=sys.stderr)
    fields = action.split()
    fields[-1] = markup.markup(fields[-1], lambda cat: cat)
    return ' '.join(fields)

try:
    _, binary_in, unary_in, binary_out, unary_out = sys.argv
except ValueError:
    print('Usage: grammar_markup.py BINARY_IN UNARY_IN BINARY_OUT UNARY_OUT',
            file=sys.stderr)
    sys.exit(1)

grammar_in = grammar.load(binary_in, unary_in)
grammar_out = grammar.Grammar()

for (left_cat, right_cat), action in grammar_in.get_binary_rules():
    left_cat = markup.markup(left_cat)
    right_cat = markup.markup(right_cat)
    action = action_markup(action)
    grammar_out.add_binary_rule(left_cat, right_cat, action)

for daughter_cat, action in grammar_in.get_unary_rules():
    daughter_cat = markup.markup(daughter_cat)
    action = action_markup(action)
    grammar_out.add_unary_rule(daughter_cat, action)

grammar_out.save(binary_out, unary_out)
