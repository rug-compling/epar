#!/usr/bin/env python3

import markup
import sys

def straighten(cat):
    return cat.replace('\\', '/')

for line in sys.stdin:
    line = line.rstrip()
    if line:
        line = line.split()
        line = line[:2] + [straighten(cat) for cat in line[2:]]
        line = ' '.join(line)
    print(line)
