epar
====

epar is a Java implementation by [Kilian Evang](http://kilian.evang.name/) of
Zhang and Clark (2011)'s shift-reduce CCG parser.

It differs in some details, such as using a hash kernel (Bohnet 2010) for
weight lookup, but gives very similar evaluation results.

External dependencies
---------------------

Before you run the experiment described below, you will need to get some
external dependencies. Create a directory called `ext` and make sure it
contains the following repositories as subdirectories (you may also symlink
them):

* [supertagging](https://github.com/texttheater/supertagging/), some software
  for producing the POS-tagged and supertagged inputs
* [candc](http://svn.ask.it.usyd.edu.au/trac/candc/), the C&C tools, for
  evaluation scripts
* [zpar](https://github.com/frcchang/zpar/), Yue Zhangs ZPar, for evaluation
  scripts

Follow the instructions in the `supertagging` README file for producing the
needed POS-tagged and supertagged data.

The C&C tools need to be compiled, this can be done by running

    make all bin/generate

from the `candc` directory.

To follow the steps below, you will also need:

* [Apache Ant](https://ant.apache.org/)
* Java 7 or higher
* [Produce](https://github.com/texttheater/produce/)

Make sure `ant`, `java` and `produce` are on your `PATH`.

Compiling
---------

To compile epar, run:

    ant

Training the parser model
-------------------------

To train the parser on the CCGbank sections 02-21 for 10 iterations, run:

    produce output/wsj/train.model10

Parsing the development test corpus
-----------------------------------

To parse the development test corpus with the above model, run:

    produce output/wsj/dev.trees10

Evaluation
----------

For dependency evaluation:

    produce output/wsj/dev.depeval10

For PARSEVAL evaluation:

    produce output/wsj/dev.eval10

Bug reports
-----------

Bug reports are very welcome, preferably as GitHub issues.

Literature
----------

Bernd Bohnet (2010): Very High Accuracy and Fast Dependency Parsing Is Not a
Contradiction. In _Proceedings of the 23rd International Conference on
Computational Linguistics_, pages 89–97. Association for Computational
Linguistics.
 
Yue Zhang and Stephen Clark (2011):
[Shift-reduce CCG Parsing](https://dl.acm.org/citation.cfm?id=2002559). In
_Proceedings of the 49th Annual Meeting of the Association for Computational
Linguistics: Human Language Technologies – Volume 1_, pages 683–692.
Association for Computational Linguistics.
