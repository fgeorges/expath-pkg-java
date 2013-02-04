<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:zip="http://expath.org/ns/zip"
                version="2.0">

   <xsl:import href="http://expath.org/ns/zip.xsl"/>

   <xsl:output indent="yes"/>

   <xsl:template name="main">
      <result>
         <binary-entry>
            <xsl:sequence select="zip:binary-entry('../test.zip', 'test.txt')"/>
         </binary-entry>
         <html-entry>
            <xsl:sequence select="zip:html-entry('../test.zip', 'sub/test.html')"/>
         </html-entry>
         <text-entry>
            <xsl:sequence select="zip:text-entry('../test.zip', 'test.txt')"/>
         </text-entry>
         <xml-entry>
            <xsl:sequence select="zip:xml-entry('../test.zip', 'test.xml')"/>
         </xml-entry>
         <empty-entry>
            <xsl:sequence select="zip:xml-entry('../test.zip', 'NOT-EXIST')"/>
         </empty-entry>
         <entries>
            <xsl:sequence select="zip:entries('../test.zip')"/>
         </entries>
      </result>
   </xsl:template>

</xsl:stylesheet>
