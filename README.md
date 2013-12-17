# EXPath Packaging for Java

Implementation of the EXPath [Packaging System](http://expath.org/modules/pkg/)
for Java, Saxon and Calabash.


## Installation

Download the latest `expath-repo-x.y.z-installer.jar` file from the
[download area](http://code.google.com/p/expath-pkg/downloads) and
execute it by double-clicking on it (or from the command-line: `java
-jar expath-repo-x.y.z-installer.jar`).  Follow the instructions,
that's it!

If you don't want a graphical installer, use the latest
`expath-repo-x.y.z.zip` file instead.  Unzip it and set it up.


## Setup

Add the script `xrepo` to your PATH.  To create an EXPath package
repository, simply use: `xrepo create <repo-dir>`.  You can set the
environment variable `EXPATH_REPO` if you don't want to provide the
repository directory everytime you use `xrepo`.

For more information on the repository manager: `xrepo help`.

The install directory contains also a script `saxon` and a script
`calabash`, that launch resp. Saxon and Calabash, by configuring them
with the repository, and adding useful options from the command line.
For more information: `saxon --help` and `calabash ++help`, or see the
[README](https://github.com/fgeorges/expath-pkg-java/tree/master/bin)
file in the sub-directory `bin/` in the source repository.
