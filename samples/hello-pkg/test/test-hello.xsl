<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:h="http://www.example.org/hello"
                exclude-result-prefixes="#all"
                version="2.0">

   <xsl:import href="http://www.example.org/hello.xsl"/>

   <xsl:output omit-xml-declaration="yes"/>

   <xsl:template match="/" name="main">
      <greetings>
         <xsl:sequence select="h:hello('world')"/>
      </greetings>
   </xsl:template>

</xsl:stylesheet>
