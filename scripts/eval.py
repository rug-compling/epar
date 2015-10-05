#!/usr/bin/env python3

import sys

def extract_constituents(line, constituents, sentence_number):
    suffix = line.split()
    start = 0
    while suffix:
        start = consume_constituent(start, suffix, constituents, sentence_number)

def consume_constituent(start, suffix, constituents, sentence_number):
    assert suffix[0] == '('
    label = suffix[1]
    ctype = suffix[2]
    suffix[:3] = []
    assert ctype in ('l', 'r', 'c', 's')
    if ctype in ('l', 'r'): # TODO distinguish binary constituents by headedness?
        mid = consume_constituent(start, suffix, constituents, sentence_number)
        end = consume_constituent(mid, suffix, constituents, sentence_number)
    elif ctype == 's':
        end = consume_constituent(start, suffix, constituents, sentence_number)
    elif ctype == 'c':
        suffix[:2] = []
        end = start + 1
    constituents.add((sentence_number, label, ctype == 'c', start, end))
    assert suffix[0] == ')'
    suffix[:1] = []
    return end

try:
    _, goldfile, testfile = sys.argv
except ValueError:
    print('Usage: python eval.py GOLDFILE TESTFILE', file=sys.stderr)
    sys.exit(1)

gold_constituents = set()
test_constituents = set()

with open(goldfile) as f:
    for sentence_number, line in enumerate(f, start=1):
        extract_constituents(line, gold_constituents, sentence_number)

with open(testfile) as f:
    for sentence_number, line in enumerate(f, start=1):
        extract_constituents(line, test_constituents, sentence_number)

def is_lexical(const):
    _, _, lexical, start, end = const
    return lexical

gold_words = set(const for const in gold_constituents if is_lexical(const))
test_words = set(const for const in test_constituents if is_lexical(const))
assert len(gold_words) == len(test_words)
gold_constituents = set(const for const in gold_constituents if not is_lexical(const))
test_constituents = set(const for const in test_constituents if not is_lexical(const))

unlabeled_gold_constituents = set((sentence_number, is_lexical, start, end) for
        (sentence_number, label, is_lexical, start, end) in gold_constituents)
unlabeled_test_constituents = set((sentence_number, is_lexical, start, end) for
        (sentence_number, label, is_lexical, start, end) in test_constituents)

intersection = test_constituents.intersection(gold_constituents)
unlabeled_intersection = unlabeled_test_constituents.intersection(
        unlabeled_gold_constituents)
lex_intersection = test_words.intersection(gold_words)

precision = len(intersection) / len(gold_constituents)
recall = len(intersection) / len(test_constituents)
f1 = 2 * precision * recall / (precision + recall)

lexcat_accuracy = len(lex_intersection) / len(gold_words)

unlabeled_precision = len(unlabeled_intersection) / len(unlabeled_gold_constituents)
unlabeled_recall = len(unlabeled_intersection) / len(unlabeled_test_constituents)
unlabeled_f1 = 2 * unlabeled_precision * unlabeled_recall / (unlabeled_precision + unlabeled_recall)

print("""\
Lexical category accuracy: {}

Labeled recall:            {}
Labeled precision:         {}
Labeled f-score:           {}

Unlabeled recall:          {}
Unlabeled precision:       {}
Unlabeled f-score:         {}""".format(lexcat_accuracy, recall, precision, f1,
        unlabeled_recall, unlabeled_precision, unlabeled_f1))
