#!/bin/sh

saxon() {
    OUTPUT=`
    java -cp ${SAXON_CP}:${REPO_CP} \
        -Dorg.expath.pkg.saxon.repo=${1}repo \
        net.sf.saxon.$2 \
        -init:org.expath.pkg.saxon.PkgInitializer \
        $3 $4 2>${1}${2}.stderr \
    | tee ${1}${2}.stdout \
    | java -cp ${SAXON_CP} net.sf.saxon.Query -s:- \
        "-qs:deep-equal(., doc('${1}result.xml'))" "!method=text"`
    if [ "$OUTPUT" = "true" ]; then
        echo "       " Passed.
    else
        echo "        [**]" FAILED!
    fi
}

xslt() {
    echo "       " Running XSLT test
    saxon $1 Transform -xsl:${1}main.xsl -it:main
}

xquery() {
    echo "       " Running XQuery test
    saxon $1 Query -q:${1}main.xq
}

# TODO: From options...
SAXON_VERSION=9.4.0.6
REPO_VERSION=0.10.0

echo Using Saxon ${SAXON_VERSION}
for jar in saxon/${SAXON_VERSION}/*.jar; do
    SAXON_CP=${SAXON_CP}:${jar}
done

echo Using repository manager ${REPO_VERSION}
for jar in repo/${REPO_VERSION}/*.jar; do
    REPO_CP=${REPO_CP}:${jar}
done

for mod in tests/*/; do
    echo Running tests for module $mod
    for test in ${mod}*/; do
        echo "   " Running tests for version $test
        xslt $test
        xquery $test
    done
done
