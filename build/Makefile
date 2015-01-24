# Makefile.local is commited, it must define the variable IZPACK, to
# point to Izpack's compile program, for instance:
#     IZPACK=/Applications/IzPack/bin/compile
#     IZPACK=c:/apps/izpack/bin/compile
include Makefile.local

VERSION=0.13.0
REVISION=$(shell git describe --always)
NAME=expath-repo-$(VERSION)
INSTALLER=expath-repo-installer-$(VERSION).jar

pkg-java=../pkg-java
pkg-saxon=../pkg-saxon
pkg-calabash=../pkg-calabash
bin=../bin
samples=../samples
prop-java=$(pkg-java)/src/org/expath/pkg/repo/tui/version.properties
prop-saxon=$(pkg-saxon)/src/java/org/expath/pkg/saxon/tools/version.properties
prop-calabash=$(pkg-calabash)/src/org/expath/pkg/calabash/version.properties

all: $(NAME).zip $(INSTALLER)

$(NAME).zip:
	rm -rf $(NAME)
	rm -rf $@
	mkdir $(NAME)
	cp README $(NAME)
	echo "Version: $(VERSION)"                          >  $(NAME)/VERSION
	echo "Subversion revision: $(REVISION)"             >> $(NAME)/VERSION
	echo "org.expath.pkg.repo.version=$(VERSION)"       >  $(prop-java)
	echo "org.expath.pkg.repo.revision=$(REVISION)"     >> $(prop-java)
	echo "org.expath.pkg.saxon.version=$(VERSION)"      >  $(prop-saxon)
	echo "org.expath.pkg.saxon.revision=$(REVISION)"    >> $(prop-saxon)
	echo "org.expath.pkg.calabash.version=$(VERSION)"   >  $(prop-calabash)
	echo "org.expath.pkg.calabash.revision=$(REVISION)" >> $(prop-calabash)
	( cd $(pkg-java)/ && ant )
	( cd $(pkg-saxon)/ && ant )
	( cd $(pkg-calabash)/ && ant )
	cp -r $(bin) $(NAME)
	mkdir $(NAME)/expath
	cp $(pkg-java)/dist/pkg-java.jar            $(NAME)/expath
	cp $(pkg-saxon)/dist/pkg-saxon.jar          $(NAME)/expath
	cp $(pkg-calabash)/dist/pkg-calabash.jar    $(NAME)/expath
	mkdir $(NAME)/sample
	cp $(samples)/hello-pkg/hello-1.2.xar       $(NAME)/sample
	cp $(samples)/hello-pkg/test/test-hello.xq  $(NAME)/sample
	cp $(samples)/hello-pkg/test/test-hello.xsl $(NAME)/sample
	cp -r notices $(NAME)
	zip -r "$@" $(NAME)

# Why using Perl, why not just Sed?
$(INSTALLER):
	perl -e "s|<appversion>([-.0-9a-z]+)</appversion>|<appversion>$(VERSION)</appversion>|g;" \
	    -pi izpack-config.xml
	"$(IZPACK)" izpack-config.xml -o "$@"

.PHONY: all $(NAME).zip $(INSTALLER)
