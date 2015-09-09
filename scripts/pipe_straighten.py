#!/usr/bin/env python3

import re
import sys

for line in sys.stdin:
    fields = line.split()
    if len(fields) > 2:
        fields[2] = fields[2].replace('\\', '/')
    print(' '.join(fields))
