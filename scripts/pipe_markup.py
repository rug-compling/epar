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
    fields = line.split()
    if len(fields) > 2:
        fields[2] = markup(fields[2])
    print(' '.join(fields))
