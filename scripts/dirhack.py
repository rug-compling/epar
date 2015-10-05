#!/usr/bin/env python3

import collections
import markup

def straighten(cat):
    return cat.replace('\\', '/')

mapping = markup.mapping()
straightmapping = collections.defaultdict(set)
backmapping = {}

for raw, markedup in mapping.items():
    straightmapping[straighten(raw)].add(straighten(markedup))
    backmapping[straighten(markedup)] = raw

for raw, markedups in straightmapping.items():
    if len(markedups) > 1:
        print(raw)
        for markedup in markedups:
            print('{} {}'.format(backmapping[markedup], markedup))
        print()
