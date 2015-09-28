def mapping():
    mapping = {}
    with open('ext/candc/src/data/ccg/cats/markedup') as f:
        for line in f:
            line = line.rstrip()
            if not line or line.startswith('=') or line.startswith('#'):
                continue
            if line.startswith('  '):
                if cat not in mapping:
                    _, markedup = line.split()
                    mapping[cat] = markedup
            else:
                cat = line
    return mapping
