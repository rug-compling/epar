#!/usr/bin/env python3

import markup
import sys

mapping = markup.mapping()

def markup(cat):
    try:
        return mapping[cat]
    except KeyError:
        return cat

for line in sys.stdin:
    line = line.rstrip()
    if line:
        line = line.split()
        line = line[:2] + [markup(cat) for cat in line[2:]]
        line = '\t'.join(line)
    print(line)
