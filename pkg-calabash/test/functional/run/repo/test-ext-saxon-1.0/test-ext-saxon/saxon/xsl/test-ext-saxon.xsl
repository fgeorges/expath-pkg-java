<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       test-ext-saxon.xsl                                       -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2009-10-30                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2009 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ext="http://www.example.com/ext"
                xmlns:java="java:com.example.ext.Simple"
                exclude-result-prefixes="xs ext"
                version="2.0">

   <xsl:function name="ext:hello" as="xs:string">
      <xsl:param name="who" as="xs:string"/>
      <xsl:sequence select="java:hello($who)"/>
   </xsl:function>

</xsl:stylesheet>


<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.             -->
<!--                                                                       -->
<!-- The contents of this file are subject to the Mozilla Public License   -->
<!-- Version 1.0 (the "License"); you may not use this file except in      -->
<!-- compliance with the License. You may obtain a copy of the License at  -->
<!-- http://www.mozilla.org/MPL/.                                          -->
<!--                                                                       -->
<!-- Software distributed under the License is distributed on an "AS IS"   -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See  -->
<!-- the License for the specific language governing rights and limitations-->
<!-- under the License.                                                    -->
<!--                                                                       -->
<!-- The Original Code is: all this file.                                  -->
<!--                                                                       -->
<!-- The Initial Developer of the Original Code is Florent Georges.        -->
<!--                                                                       -->
<!-- Contributor(s): none.                                                 -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
