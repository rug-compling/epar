epar
====

epar is a Java implementation by [Kilian Evang](http://kilian.evang.name/) of
Zhang and Clark (2011)'s shift-reduce CCG parser.

External dependencies
---------------------

Before you run the experiments described below, you will need to get some
external dependencies. Create a directory called `ext` and make sure it
contains the following repositories as subdirectories (you may also symlink
them):

* [supertagging](https://github.com/texttheater/supertagging/), some software
  for producing the POS-tagged and supertagged inputs
* [candc](http://svn.ask.it.usyd.edu.au/trac/candc/), the C&C tools, for
  evaluation scripts
* [zpar](https://github.com/frcchang/zpar/), Yue Zhangs ZPar, for evaluation
  scripts

The C&C tools need to be compiled, you can 

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

Preparation
-----------

Create a directory called `ext` and put the following required external
resources into it (you can use symlinks):

* `CCGbank1.2`
* [`candc`](http://svn.ask.it.usyd.edu.au/trac/candc/) (SVN version)
* [`zpar`](https://github.com/frcchang/zpar/)

...

Evaluation
----------

This part is held together with flimsy strings and chewing gum.
