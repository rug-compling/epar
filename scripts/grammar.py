import collections
import itertools
import sys

class Grammar:

    def __init__(self):
        self.unary_rules_by_daughter_cat = collections.defaultdict(list)
        self.binary_rules_by_daughter_cats = collections.defaultdict(list)

    def add_unary_rule(self, daughter_cat, action):
        actions = self.unary_rules_by_daughter_cat[daughter_cat]
        if action not in actions:
            actions.append(action)

    def add_binary_rule(self, left_cat, right_cat, action):
        actions = self.binary_rules_by_daughter_cats[(left_cat, right_cat)]
        if action not in actions:
            actions.append(action)

    def get_binary_rules(self):
        for (left_cat, right_cat), actions in \
                self.binary_rules_by_daughter_cats.items():
            for action in actions:
                yield (left_cat, right_cat), action

    def get_unary_rules(self):
        for daughter_cat, actions in self.unary_rules_by_daughter_cat.items():
            for action in actions:
                yield daughter_cat, action

    def save(self, binary_rules_file, unary_rules_file):
        with open(binary_rules_file, 'w') as f:
            for (left_cat, right_cat), actions in \
                    self.binary_rules_by_daughter_cats.items():
                print('{} , {} : [ {} ]'.format(left_cat, right_cat,
                        ' , '.join(actions)), file=f)
        with open(unary_rules_file, 'w') as f:
            for daughter_cat, actions in \
                    self.unary_rules_by_daughter_cat.items():
                print('{} : [ {} ]'.format(daughter_cat, ' , '.join(actions)),
                        file=f)

def load(binary_rules_file, unary_rules_file):
    grammar = Grammar()
    with open(binary_rules_file) as f:
       for line in f:
          print(line, file=sys.stderr, end='')
          fields = line.split()
          left_cat = fields[0]
          assert fields[1] == ','
          right_cat = fields[2]
          assert fields[3] == ':'
          assert fields[4] == '['
          for i in itertools.count(start=5, step=5):
              assert fields[i] == 'REDUCE'
              assert fields[i + 1] == 'BINARY'
              assert fields[i + 2] in ('LEFT', 'RIGHT')
              mother_cat = fields[i + 3]
              grammar.add_binary_rule(left_cat, right_cat,
                      ' '.join(fields[i:i+4]))
              if fields[i + 4] == ']':
                  assert len(fields) == i + 5
                  break
              assert fields[i + 4] == ','
    with open(unary_rules_file) as f:
        for line in f:
            fields = line.split()
            daughter_cat = fields[0]
            assert fields[1] == ':'
            assert fields[2] == '['
            for i in itertools.count(start=3, step=4):
                assert fields[i] == 'REDUCE'
                assert fields[i + 1] == 'UNARY'
                mother_cat = fields[i + 2]
                grammar.add_unary_rule(daughter_cat, ' '.join(fields[i:i+3]))
                if fields[i + 3] == ']':
                    assert len(fields) == i + 4
                    break
                assert fields[i + 3] == ','
    return grammar
