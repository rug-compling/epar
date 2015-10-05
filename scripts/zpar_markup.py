#!/usr/bin/env python3

import markup
import sys

for line in sys.stdin:
    line = line.split()
    line = (markup.markup(t, lambda x: x) for t in line)
    line = ' '.join(line)
    print(line)
