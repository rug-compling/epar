#!/usr/bin/env python3

import sys

def straighten(cat):
    return cat.replace('\\', '/')

def flip(action):
    if 'LEFT' in action:
        return action.replace('LEFT', 'RIGHT')
    else:
        return action.replace('RIGHT', 'LEFT')

rules = []

for line in sys.stdin:
    try:
        left_cat, right_cat, cat, head, name = line.split()
        rule = '{}\t{}\t{}\t{}\t{}'.format(straighten(left_cat),
                straighten(right_cat), straighten(cat), head, name)
        if rule not in rules:
            rules.append(rule)
        rule = '{}\t{}\t{}\t{}\t{}'.format(straighten(right_cat),
                straighten(left_cat), straighten(cat), flip(head), name)
        if rule not in rules:
            rules.append(rule)
    except ValueError:
        old_cat, new_cat, name = line.split()
        rule = '{}\t{}\t{}'.format(straighten(old_cat), straighten(new_cat),
                name)
        if rule not in rules:
            rules.append(rule)

for rule in rules:
    print(rule)
