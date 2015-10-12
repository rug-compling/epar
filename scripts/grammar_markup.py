#!/usr/bin/env python3

import markup
import sys

def m(cat):
    return markup.markup(cat, lambda x: x)

rules = []

for line in sys.stdin:
    try:
        left_cat, right_cat, cat, head, name = line.split()
        rule = '{}\t{}\t{}\t{}\t{}'.format(m(left_cat),
                m(right_cat), m(cat), head, name)
        if rule not in rules:
            rules.append(rule)
    except ValueError:
        old_cat, new_cat, name = line.split()
        rule = '{}\t{}\t{}'.format(m(old_cat), m(new_cat),
                name)
        if rule not in rules:
            rules.append(rule)

for rule in rules:
    print(rule)
