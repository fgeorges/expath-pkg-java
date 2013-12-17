# Processor scripts

Contains scripts to launch Saxon and Calabash on Windows and Unix-like
systems (including Linux and Mac OS X).  One of the scripts allows one
to manage a repository of packages on the disk, as defined by the
EXPath [Packaging System](http://expath.org/spec/pkg) specification.

Scripts for Saxon and Calabash can usually be used straight away.
Just use `calabash` or `saxon` commands, each with its own processor
command line interface: documented [here for Calabash](http://xmlcalabash.com/docs/)
and [here for Saxon](http://saxonica.com/documentation/html/using-xsl/commandline.html):

```
> saxon -s:source.xml -xsl:hello.xsl
<result>
   <hello>World!</hello>
</result>

> calabash -i source=source.xml hello.xproc
<result>
   <hello>World!</hello>
</result>
```

Scripts for Saxon and Calabash share the same idea: they set up the
classpath and a few other options, take the same options as the final
program (i.e. Saxon or Calabash) and add a few options of their own.
They consume their own options, in the same order as on the command
line, then when there is none anymore, set up the environment for Java
and call it with the remainder options.  For instance:

```
> saxon --repo /home/me/expath/repo -s:source.xml -xsl:style.xsl
...
```

In the above example, the option `--repo` (and its value) is consumed
by the script.  Because this is the only script-defined option, the
rest of the options are passed straight to Saxon.

By default, the repository manager looks at the environment variable
`EXPATH_REPO` for the repository directory, so you do not have to set
it explicitly every time you use `xrepo`, `calabash` or `saxon`.

## xrepo

The command `xrepo` (resp. `xrepo.bat` on Windows) helps you to manage
a local repository of packages.  The command takes first optional
options (e.g. a repository location), then a specific command name
(e.g. `install`), then the specific command options.  For instance, to
create a new repository in `/home/me/expath/repo` and install the
package `/tmp/my-package-1.0.0.xar`:

```
> xrepo create /home/me/expath/repo
...

> xrepo --repo /home/me/expath/repo install /tmp/my-package-1.0.0.xar
...
```

The available commands are displayed by the command `help`:

```
> xrepo help
Usage:
  xrepo [--repo <repo>|--verbose] help|list|install|remove|create|lookup ...

Commands:
  help
  version
  list
  install [-b|-f] <pkg>
      -b batch operations (no interaction)
      -f override a package if already installed (force)
  remove [-b] <pkg>
      -b batch operations (no interaction)
  create <repo>
  lookup <space> <uri>
```

## Calabash

The command `calabash` (resp. `calabash.bat` on Windows) helps you
calling Calabash and setting up the classpath and other options.  The
options starting with `++` are specific to this script, the rest is
passed as is to Calabash.  Only the options starting with `++` at the
beginning of the option list are taken into account, as soon as the
script sees another one it stops consuming them (or as soon as it sees
`++` by itself, which means "stop consuming options and pass all the
rest to Calabash").

The available options let you display a help message, set explicitly
the repository directory (instead of using the environment variable
`EXPATH_REPO`, pass options to the JVM, and add items to the
classpath.  For instance, to display the help:

```
> calabash ++help
Usage: calabash <script options> <processor options>

Processor options are any option accepted by the original command-line
Calabash frontend.  Script options are (all are optional, those marked with
an * are repeatable, those with ... require a parameter):

  ++help                    display this help message
  ++repo ...                the path to the packaging repository to use
  ++java ... *              add an option to the Java Virtual Machine
  ++add-cp ... *            add an entry to the classpath
```

If you want to set explicitly the repository directory to use, as well
as adding an option to the JVM command (i.e. to `java`, e.g. the
amount of available memory), as well as adding a specific JAR file to
the classpath, you can use the following command:

```
> calabash ++repo /.../repo ++java -Xmx512m ++add-cp /.../my.jar pipe.xproc
<result>
   <hello>World!</hello>
</result>
```

## Saxon

The command `saxon` (resp. `saxon.bat` on Windows) helps you calling
Saxon and setting up the classpath and other options.  The options
starting with `--` are specific to this script, the rest is passed as
is to Saxon.  Only the options starting with `--` at the beginning of
the option list are taken into account, as soon as the script sees
another one it stops consuming them (or as soon as it sees `--` by
itself, which means "stop consuming options and pass all the rest to
Saxon").

The available options let you display a help message, set explicitly
the repository directory (instead of using the environment variable
`EXPATH_REPO`, pass options to the JVM, add items to the classpath,
chose between the XSLT and XQuery processors, override the classpath,
set the amount of memory for the JVM, and setup an HTTP and HTTPS
proxy.  For instance, to display the help:

```
> saxon --help
Usage: saxon <script options> <processor options>

<processor options> are any option accepted by the original command-line
Saxon frontend.  Script options are (all are optional, those marked with
an * are repeatable):

  --help                    display this help message
  --xsl                     invoke Saxon as an XSLT processor (the default)
  --xq                      invoke Saxon as an XQuery processor
  --repo ...                set the EXPath Packaging repository dir
  --add-cp classpath *      add an entry to the classpath
  --cp classpath *          set the classpath (override the default classpath)
  --java ...                add an option to the Java Virtual Machine
  --mem ...                 set the memory (shortcut for --java=-Xmx...)
  --proxy [user:password@]host:port
                            HTTP and HTTPS proxy information
```

If you want to set explicitly the repository directory to use, set the
amount of available memory, and add a specific JAR file to the
classpath, you can use the following command:

```
> saxon --repo /.../repo --mem 512m --add-cp /.../my.jar -s:source.xml -xsl:style.xsl
<result>
   <hello>World!</hello>
</result>
```
