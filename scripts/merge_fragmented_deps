#!/usr/bin/env python3

import sys

_, depsfile, pipefile, catfile = sys.argv

fragment_lengths = [0]

with open(pipefile) as f:
    depth = 0
    for line in f:
        if line.startswith('('):
            depth += 1
            if line.startswith('(<L '):
                fragment_lengths[-1] += 1
        elif line.startswith(')'):
            depth -= 1
            assert depth >= 0
            if depth == 0:
                fragment_lengths.append(0)

with open(depsfile) as f:
   line = f.readline()
   assert line.startswith('#')
   sys.stdout.write(line)
   line = f.readline()
   assert line.startswith('#')
   sys.stdout.write(line)
   line = f.readline()
   assert line.strip() == ''
   sys.stdout.write(line)
   current_fragment = 0
   offset = 0
   for line in f:
       if not line.rstrip():
           offset += fragment_lengths[current_fragment]
           current_fragment += 1
       else:
           dep, cat, arg, head, x = line.split(' ', 4)
           dep, depnum = dep.rsplit('_')
           head, headnum = head.rsplit('_')
           depnum = int(depnum)
           headnum = int(headnum)
           depnum += offset
           headnum += offset
           line = '{}_{} {} {} {}_{} {}'.format(dep, depnum, cat, arg, head,
                   headnum, x)
           sys.stdout.write(line)

print

