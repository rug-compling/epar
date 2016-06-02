#!/usr/bin/env python3

import collections
import sys
import util

mapping = collections.defaultdict(set)
mapping_straight = collections.defaultdict(set)

with open('ext/candc/src/data/ccg/cats/markedup') as f:
    for block in util.blocks(
            l for l in f if not (l.startswith('=') or l.startswith('#'))):
        if not block:
            continue
        block = block.split('\n')
        cat = block[0].rstrip()
        markedup = block[1].split()[1]
        cat_straight = cat.replace('\\', '/')
        markedup_straight = markedup.replace('\\', '/')
        mapping[cat].add(markedup)
        mapping_straight[cat_straight].add(markedup_straight)

print('# dependency-ambiguous original categories')
print()

for cat, markedups in mapping.items():
    if len(markedups) > 1:
        print(cat)
        for markedup in markedups:
            print(' ' + markedup)
        print()

print()

print('# dependency-ambiguous straight categories')
print()

for cat, markedups in mapping_straight.items():
    if len(markedups) > 1:
        print(cat)
        for markedup in markedups:
            print(' ' + markedup)
        print()

print()
