all: hello-1.3.xar

hello-1.3.xar: expath-pkg.xml content/hello.xq content/hello.xsl
	zip "$@" expath-pkg.xml content/hello.xq content/hello.xsl

.PHONY: all
