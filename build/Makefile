IZPACK=/Applications/IzPack/bin/compile

VERSION=0.11.1
# TODO: Check whether REVISION ends with an 'M' (then it should be an
# error: everything has to be checked in before a release)
REVISION=$(shell svnversion)
NAME=expath-repo-$(VERSION)

pkg-java=../pkg-java
pkg-saxon=../pkg-saxon
pkg-calabash=../pkg-calabash
bin=../bin
samples=../samples
version-prop=$(pkg-java)/src/org/expath/pkg/repo/tui/version.properties

all: $(NAME).zip $(NAME)-install.jar

$(NAME).zip:
	rm -rf $(NAME)
	rm -rf $@
	mkdir $(NAME)
	cp README $(NAME)
	echo "Version: $(VERSION)" > $(NAME)/VERSION
	echo "Subversion revision: $(REVISION)" >> $(NAME)/VERSION
	echo "org.expath.pkg.repo.version=$(VERSION)" > $(version-prop)
	echo "org.expath.pkg.repo.revision=$(REVISION)" >> $(version-prop)
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
$(NAME)-install.jar:
	perl -e "s|<appversion>([-.0-9a-z]+)</appversion>|<appversion>$(VERSION)</appversion>|g;" \
	    -pi izpack-config.xml
	"$(IZPACK)" izpack-config.xml -o "$@"

.PHONY: all $(NAME).zip $(NAME)-install.jar
