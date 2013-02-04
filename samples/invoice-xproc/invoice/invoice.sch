<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       invoice.sch                                              -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2009-10-19                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2009 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<schema xmlns="http://purl.oclc.org/dsdl/schematron"
        queryBinding="xslt2">

   <ns prefix="i" uri="http://fgeorges.org/test/external-invoice"/>

   <pattern>
      <rule context="i:total">
         <assert test="number(@tax-excl) eq sum(../i:line/@price)">
            Total must be equal to the sum of price of lines (<value-of select="@tax-excl"/> / <value-of select="sum(../i:line/@price)"/>)
         </assert>
      </rule>
   </pattern>

</schema>


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
