all: hello-1.1.xar hello-1.2.xar

hello-1.1.xar: expath-pkg.xml hello/hello.xq hello/hello.xsl
	zip "$@" expath-pkg.xml hello/hello.xq hello/hello.xsl

hello-1.2.xar: expath-pkg.xml hello/hello.xq hello/hello.xsl
	mkdir work
	mkdir work/content
	cat expath-pkg.xml | sed s/1.1/1.2/ > work/expath-pkg.xml
	cp hello/hello.xq hello/hello.xsl work/content
	( cd work; zip ../hello-1.2.xar expath-pkg.xml content/hello.xq content/hello.xsl )
	rm -r work

.PHONY: all
