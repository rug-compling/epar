#!/usr/bin/env python3

import re
import sys

def remove_markup(cat):
    length = len(cat)
    cat = re.sub(r'<\d+>|{[A-Z_]+}', '', cat)
    # Compare length to see if anything was removed: if there was markup, then
    # there is also an extra pair of parentheses around the whole category,
    # which we need to remove as well.
    if len(cat) < length and cat.startswith('('):
        assert cat.endswith(')')
        cat = cat[1:-1]
    return cat

for line in sys.stdin:
    fields = line.split()
    if len(fields) > 2:
        if fields[0] == '(<L':
            fields.insert(2, remove_markup(fields[2]))
        else:
            fields[2] = remove_markup(fields[2])
    print(' '.join(fields))
